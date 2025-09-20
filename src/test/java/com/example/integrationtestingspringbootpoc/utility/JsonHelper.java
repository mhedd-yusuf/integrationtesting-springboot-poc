package com.example.integrationtestingspringbootpoc.utility;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class JsonHelper {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String json(String path) {
        try {
            var res = new ClassPathResource(path);
            return Files.readString(res.getFile().toPath(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            // Fallback for resources inside JAR (CI)
            try (var is = new ClassPathResource(path).getInputStream()) {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            } catch (Exception ex) {
                throw new RuntimeException("Cannot read resource: " + path, ex);
            }
        }
    }

    public static <T> T jsonAs(String path, Class<T> type, ObjectMapper objectMapper) {
        try (var is = new ClassPathResource(path).getInputStream()) {
            return objectMapper.readValue(is, type);
        } catch (Exception e) {
            throw new RuntimeException("Cannot map resource: " + path, e);
        }
    }

    public static <T> T jsonAs(String path, TypeReference<T> typeRef, ObjectMapper objectMapper) {
        try (var is = new ClassPathResource(path).getInputStream()) {
            return objectMapper.readValue(is, typeRef);
        } catch (Exception e) {
            throw new RuntimeException("Cannot map resource: " + path, e);
        }
    }
}
