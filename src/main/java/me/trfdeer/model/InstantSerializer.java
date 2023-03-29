package me.trfdeer.model;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class InstantSerializer implements JsonSerializer<Instant> {
    public JsonElement serialize(Instant src, Type typeOfSrc, JsonSerializationContext context) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("dd MMM yyyy")
                .withZone(ZoneOffset.UTC);

        return new JsonPrimitive(LocalDate.ofInstant(src, ZoneOffset.UTC).format(formatter));
    }
}