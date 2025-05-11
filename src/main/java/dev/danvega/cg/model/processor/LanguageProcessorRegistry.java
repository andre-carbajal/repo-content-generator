package dev.danvega.cg.model.processor;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Registry for language processors, providing methods to access processors
 * by language type.
 */
@Component
public class LanguageProcessorRegistry {

    private final Map<String, LanguageProcessor> processors;

    /**
     * Creates a new registry from the provided list of language processors.
     *
     * @param languageProcessors the list of language processors to register
     */
    public LanguageProcessorRegistry(List<LanguageProcessor> languageProcessors) {
        Map<String, LanguageProcessor> processorMap = new HashMap<>();

        for (LanguageProcessor processor : languageProcessors) {
            String languageType = processor.getLanguageType().toLowerCase();
            processorMap.put(languageType, processor);
        }

        this.processors = Collections.unmodifiableMap(processorMap);
    }

    /**
     * Gets a processor for the specified language type.
     *
     * @param type the language type
     * @return an Optional containing the language processor, or empty if not found
     */
    public Optional<LanguageProcessor> getProcessor(String type) {
        if (type == null || type.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(processors.get(type.toLowerCase()));
    }

    /**
     * Gets a list of all supported language types.
     *
     * @return a list of supported language types
     */
    public List<String> getSupportedLanguages() {
        return processors.keySet().stream().sorted().toList();
    }
}
