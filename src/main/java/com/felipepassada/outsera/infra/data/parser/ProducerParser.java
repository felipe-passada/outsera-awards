package com.felipepassada.outsera.infra.data.parser;

import java.util.Set;

public interface ProducerParser {
    Set<String> parse(String producersString);
}
