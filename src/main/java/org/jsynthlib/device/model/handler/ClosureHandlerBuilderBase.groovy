package org.jsynthlib.device.model.handler

import org.jsynthlib.xmldevice.HandlerDefinitionBase

public abstract class ClosureHandlerBuilderBase<T extends ClosureHandlerBase> {
    HandlerDefinitionBase handlerDefinition
    Properties propertyValues

    public T build() {
        if (handlerDefinition == null || propertyValues == null) {
            throw new IllegalStateException("Either handlerDefinition or propertyValues was not set")
        } else if (handlerDefinition.getClosure() == null || handlerDefinition.getClosure().isEmpty()) {
            throw new IllegalArgumentException("Closure definition is invalid")
        }
        def propertyArray = handlerDefinition.getPropertyArray()

        StringBuilder sb = new StringBuilder()
        sb.append("{ ")
        boolean first = true
        propertyArray.each {
            if (first) {
                first = false
            } else {
                sb.append(", ")
            }
            sb.append(it.key)
        }
        sb.append(" -> ").append(handlerDefinition.getClosure()).append(" }")
        println sb.toString()
        Closure closure = new GroovyShell().evaluate(sb.toString())

        propertyArray.each {
            if (propertyValues.containsKey(it.key)) {
                closure = closure.curry(propertyValues.getProperty(it.key))
            }
        }


        T newInstance = newInstance()
        newInstance.setClosure(closure)
        return newInstance
    }

    abstract T newInstance();
}
