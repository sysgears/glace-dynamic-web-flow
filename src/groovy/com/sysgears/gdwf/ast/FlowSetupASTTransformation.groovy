package com.sysgears.gdwf.ast

import com.sysgears.gdwf.FlowStage
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

/**
 * Handles code generation for the {@code @FlowSetup} annotation.
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class FlowSetupASTTransformation implements ASTTransformation {

    /**
     * Handles flow methods injection.
     *
     * @param nodes the ast nodes
     * @param source the source unit for the nodes
     */
    @Override
    void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
        if (!FlowASTUtils.validateNodes(nodes)) {
            FlowASTUtils.reportError('AST Transformation arguments are invalid', nodes[0], sourceUnit)
            return
        }
        FlowASTInjections.injectFlowMethod(FlowStage.FLOW_SETUP.boundMethodName, nodes[1], nodes[0], sourceUnit)
    }
}
