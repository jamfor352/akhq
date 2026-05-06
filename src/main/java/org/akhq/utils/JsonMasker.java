package org.akhq.utils;

import org.akhq.configs.DataMasking;
import org.akhq.configs.JsonMaskingFilter;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class JsonMasker implements Masker {
    private static final String[][] EMPTY_PATH_ARRAYS = new String[0][];

    private final Map<String, List<String>> topicToKeysMap;
    private final Map<String, Set<String>> topicToKeysSetMap;
    private final Map<String, String[][]> topicToPathArraysMap;
    protected final String jsonMaskReplacement;

    public JsonMasker(DataMasking dataMasking) {
        this.jsonMaskReplacement = dataMasking.getJsonMaskReplacement();
        this.topicToKeysMap = buildTopicKeysMap(dataMasking);
        this.topicToKeysSetMap = buildTopicKeysSetMap();
        this.topicToPathArraysMap = buildTopicPathArraysMap();
    }

    private Map<String, List<String>> buildTopicKeysMap(DataMasking dataMasking) {
        return dataMasking.getJsonFilters().stream()
            .collect(Collectors.toMap(
                JsonMaskingFilter::getTopic,
                JsonMaskingFilter::getKeys,
                (a, b) -> a,
                HashMap::new
            ));
    }

    private Map<String, Set<String>> buildTopicKeysSetMap() {
        return topicToKeysMap.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> new HashSet<>(entry.getValue()),
                (a, b) -> a,
                HashMap::new
            ));
    }

    private Map<String, String[][]> buildTopicPathArraysMap() {
        return topicToKeysMap.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream()
                    .map(key -> key.split("\\."))
                    .toArray(String[][]::new),
                (a, b) -> a,
                HashMap::new
            ));
    }

    protected List<String> getKeysForTopic(String topic) {
        return topicToKeysMap.getOrDefault(topic.toLowerCase(), Collections.emptyList());
    }

    protected Set<String> getKeySetForTopic(String topic) {
        return topicToKeysSetMap.getOrDefault(topic.toLowerCase(), Collections.emptySet());
    }

    protected String[][] getPathArraysForTopic(String topic) {
        return topicToPathArraysMap.getOrDefault(topic.toLowerCase(), EMPTY_PATH_ARRAYS);
    }
}
