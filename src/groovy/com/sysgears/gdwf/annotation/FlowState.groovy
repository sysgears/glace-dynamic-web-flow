package com.sysgears.gdwf.annotation

import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Annotates a controller action (method or field).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.METHOD, ElementType.FIELD])
@GroovyASTTransformationClass('com.sysgears.gdwf.ast.FlowStateASTTransformation')
@interface FlowState {
    String view() default ""
}