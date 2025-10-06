package no.webstep.ai.mcp.core.tool.invocation;

import no.webstep.ai.mcp.core.tool.OptionalParam;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.List;

@Component
public class OptionalParamDetector {


    private static final List<String> OPTIONAL_FQNS = List.of(
            OptionalParam.class.getName(),
            "org.springframework.lang.Nullable",
            "org.jetbrains.annotations.Nullable",
            "jakarta.annotation.Nullable",
            "javax.annotation.Nullable");

    public boolean isOptional(Parameter p) {
        for (Annotation a : p.getAnnotations()) {
            final String fullyQualifiedName = a.annotationType().getName();
            if (OPTIONAL_FQNS.contains(fullyQualifiedName)) {
                return true;
            }
        }
        return false;
    }

}
