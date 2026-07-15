package com.felipepassada.outsera.infra.data.parser;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class ProducerParserRegex implements ProducerParser {
    @Override
    public Set<String> parse(String producersString) {
        if (producersString == null || producersString.isBlank()) {
            return Set.of();
        }

        String cleanedString = producersString.replaceAll("(?i),?\\s*producers?\\s*$", "");
        String normalizedString = cleanedString.replaceAll("(?<=\\p{Ll})and(?=\\s*\\p{Lu})", " and ")
                .replaceAll("(?i)\\s*\\band\\b\\s*", ", ")
                .replaceAll("\\s*&\\s*", ", ");

        return Arrays.stream(normalizedString.split(","))
                .map(name -> name.trim().replaceAll("\\s+", " "))
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toSet());
    }
}
