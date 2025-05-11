package dev.danvega.cg.model.processor;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class LanguageProcessorRegistry {
    private final Map<String, LanguageProcessor> processors = new HashMap<>();

    public LanguageProcessorRegistry(List<LanguageProcessor> languageProcessors) {
        for (LanguageProcessor processor : languageProcessors) {
            processors.put(processor.getLanguageType().toLowerCase(), processor);
        }
    }

    public Optional<LanguageProcessor> getProcessor(String type) {
        if (type == null || type.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(processors.get(type.toLowerCase()));
    }

    public List<String> getSupportedLanguages() {
        return processors.keySet().stream().toList();
    }
}
