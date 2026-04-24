package com.example.txcache.app;

import org.springframework.core.env.Environment;

public record AppProperties(String bootstrapServers, String inputTopic, String processedTopic, int topicPartitions,
                            String streamsApplicationId, String streamsStateDir, int streamsNumThreads) {
    public static AppProperties from(Environment e) {
        return new AppProperties(e.getRequiredProperty("app.bootstrap-servers"), e.getRequiredProperty("app.topic.input"), e.getRequiredProperty("app.topic.processed"), Integer.parseInt(e.getRequiredProperty("app.topic.partitions")), e.getRequiredProperty("app.streams.application-id"), e.getRequiredProperty("app.streams.state-dir"), Integer.parseInt(e.getRequiredProperty("app.streams.num-threads")));
    }
}
