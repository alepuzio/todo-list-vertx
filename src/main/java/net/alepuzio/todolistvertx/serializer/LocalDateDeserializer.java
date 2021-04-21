package net.alepuzio.todolistvertx.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 * @overview: fix problem in /deserialize datetime
 * */
public class LocalDateDeserializer extends StdDeserializer<LocalDateTime> {

	private static final long serialVersionUID = 1L;

	protected LocalDateDeserializer() {
        super(LocalDateTime.class);
    }

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String text = jsonParser.readValueAs(String.class);
        if (text == null) {
            return LocalDateTime.MAX;
        } else {
            return LocalDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }
}
