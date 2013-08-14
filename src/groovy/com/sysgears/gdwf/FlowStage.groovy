package com.sysgears.gdwf

import com.sysgears.gdwf.annotation.FlowSetup
import com.sysgears.gdwf.annotation.FlowState

import java.lang.annotation.Annotation

/**
 * Holds Web Flow stages.
 */
public enum FlowStage {

    /**
     * Flow setup stage.
     */
    FLOW_SETUP('executeGDWFSetupStage', FlowSetup.class),

    /**
     * Flow state stage.
     */
    FLOW_STATE('executeGDWFStateStage', FlowState.class)

    /**
     * Name of the method that is injected on this stage.
     */
    final String boundMethodName

    /**
     * Annotation type that specifies the flow stage.
     */
    final Class<? extends Annotation> annotationType

    /**
     * Creates new instance.
     *
     * @param boundMethodName name of the method that is injected this stage
     * @param annotationType annotation type that specifies the flow stage
     */
    private FlowStage(String boundMethodName,
                      Class<? extends Annotation> annotationType) {
        this.boundMethodName = boundMethodName
        this.annotationType = annotationType
    }
}
