package com.sysgears.gdwf.ast

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.control.messages.SyntaxErrorMessage
import org.codehaus.groovy.syntax.SyntaxException

/**
 * Provides methods that help to manage AST transformations.
 */
class FlowASTUtils {

    /**
     * Checks whether the nodes that are passed to the ASTTransformation visit() method are correct.
     *
     * @param nodes nodes that are passed to the {@link org.codehaus.groovy.transform.ASTTransformation#visit} method as arguments
     * @return true if argument types are correct, false otherwise
     */
    static boolean validateNodes(ASTNode[] nodes) {
        (nodes && nodes[0] && nodes[1] && nodes[0] instanceof AnnotationNode)
    }

    /**
     * Reports AST transformation error.
     *
     * @param message error message
     * @param node annotation node that triggered the transformation
     * @param source source unit for the nodes
     */
    static void reportError(String message, ASTNode node, SourceUnit source) {
        SyntaxException exception = new SyntaxException(message + '\n', node.lineNumber, node.columnNumber)
        SyntaxErrorMessage errorMessage = new SyntaxErrorMessage(exception, source)
        source.errorCollector.addErrorAndContinue(errorMessage)
    }
}
