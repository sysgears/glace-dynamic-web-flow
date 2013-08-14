package com.sysgears.gdwf.ast

import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.ast.*

/**
 * Provides various AST injections for the GDWF plugin.
 */
class FlowASTInjections {

    /**
     * Injects flow method to the node.
     * <p>
     * Transforms the node in order to inject flow method call in it.<br>
     * For instance:<br>
     * <code>def foo = { // do something... }</code><br>
     * will be transformed to:<br>
     * <code>def foo = { bar { // do something... }}</code><br>
     * where: <code>foo</code> -- AST node, <code>bar</code> -- name of the flow method
     *
     * @param methodName name of the flow method
     * @param node AST node, valid node types: {@link MethodNode}, {@link FieldNode}
     * @param annotationNode annotation node that triggered the transformation
     * @param source source unit for the nodes
     */
    static void injectFlowMethod(String methodName, ASTNode node, ASTNode annotationNode, SourceUnit source) {

        if (node instanceof FieldNode) {
            if (!(node.initialExpression instanceof ClosureExpression)) {
                FlowASTUtils.reportError('Field value is invalid, expected: closure expression', node, source)
                return
            }
            node.initialExpression.code = wrapStatement(node.initialExpression.code, methodName)
        }

        // Grails 2.0+
        if (node instanceof MethodNode) {
            node.code = wrapStatement(node.code as BlockStatement, methodName)
        }
    }

    /**
     * Places the block statement into a closure and passes it to the method as an argument.
     * <p>
     * Resulting statement:<br>
     * <code>method { block statement }</code>
     *
     * @param statement block statement
     * @param methodName name of the method
     * @return block statement which contains the method call
     */
    private static Statement wrapStatement(Statement statement, String methodName) {
        if (!(statement instanceof BlockStatement)) return

        def shareVariables = { scope ->
            for (Iterator<Variable> vars = scope.referencedLocalVariablesIterator; vars.hasNext();) {
                Variable var = vars.next()
                var.setClosureSharedVariable(true)
            }
        }

        // create new block
        BlockStatement block = new BlockStatement()
        VariableScope blockScope = statement.variableScope.copy()
        shareVariables(blockScope)
        block.variableScope = blockScope

        // create closure expression, use statement as an argument
        ClosureExpression closure = new ClosureExpression(Parameter.EMPTY_ARRAY, statement)
        closure.variableScope = statement.variableScope.copy()

        // create method call expression, use the closure as an argument
        MethodCallExpression methodCall = new MethodCallExpression(new VariableExpression('this'),
                methodName, new ArgumentListExpression(closure))

        // add method call to the block statement
        block.addStatement(new ExpressionStatement(methodCall))

        block
    }
}
