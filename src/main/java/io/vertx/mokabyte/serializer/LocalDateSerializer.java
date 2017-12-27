package io.vertx.mokabyte.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateSerializer extends StdSerializer<LocalDateTime> {

    protected LocalDateSerializer() {
        super(LocalDateTime.class);
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String format = value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        jsonGenerator.writeString(format);
    }
}
