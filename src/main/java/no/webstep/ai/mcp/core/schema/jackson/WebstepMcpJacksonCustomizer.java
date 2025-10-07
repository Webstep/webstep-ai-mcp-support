package no.webstep.ai.mcp.core.schema.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.TimeZone;

@Conditional(JacksonCustomizerCondition.class)
@Slf4j
public class WebstepMcpJacksonCustomizer implements Jackson2ObjectMapperBuilderCustomizer {

    @Override
    public void customize(Jackson2ObjectMapperBuilder builder) {
        builder.modules(
                new JavaTimeModule(),
                new SimpleModule().addSerializer(BigDecimal.class, new JsonSerializer<>() {
                    @Override
                    public void serialize(BigDecimal bigDecimal,
                                          JsonGenerator jsonGenerator,
                                          SerializerProvider serializerProvider) throws IOException {
                        jsonGenerator.writeString(bigDecimal.toPlainString());
                    }
                }));
        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        builder.featuresToEnable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);
        builder.timeZone(TimeZone.getTimeZone("UTC"));
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);
        builder.featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        builder.featuresToDisable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        builder.featuresToEnable(SerializationFeature.INDENT_OUTPUT);
        builder.postConfigurer(mapper ->
                mapper.getFactory().configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true)
        );

    }
}
