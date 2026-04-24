package com.example.txcache.app;

import org.apache.kafka.streams.KafkaStreams;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;

public final class TxCacheApplication {
    public static void main(String[] args) throws Exception {
        try (var ctx = new SpringApplicationBuilder()
                .sources(Empty.class)
                .web(WebApplicationType.NONE)
                .initializers(new TxCacheInitializer())
                .run(args);
            var streams = ctx.getBean(StreamsApp.class).buildAndStart()) {
            Thread.currentThread().join();
        }
    }

    public static final class Empty {
        private Empty() {
        }
    }
}
