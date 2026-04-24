package com.example.txcache.app;

import com.example.txcache.app.domain.*;
import com.example.txcache.core.BatchProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

public final class TxCacheInitializer implements ApplicationContextInitializer<GenericApplicationContext> {
    public void initialize(GenericApplicationContext ctx) {
        var props = AppProperties.from(ctx.getEnvironment());
        ctx.registerBean(AppProperties.class, () -> props);
        ctx.registerBean(ObjectMapper.class, () -> {
            var m = new ObjectMapper();
            m.registerModule(new JavaTimeModule());
            return m;
        });
        ctx.registerBean(BatchProcessor.class, SampleBatchProcessor::new);
        ctx.registerBean(StreamsApp.class, () -> new StreamsApp(ctx.getBean(AppProperties.class), ctx.getBean(ObjectMapper.class), ctx.getBean(BatchProcessor.class)));
    }
}
