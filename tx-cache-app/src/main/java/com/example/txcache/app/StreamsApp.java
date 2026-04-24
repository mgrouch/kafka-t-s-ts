package com.example.txcache.app;

import com.example.txcache.app.domain.*;
import com.example.txcache.core.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.processor.*;
import org.apache.kafka.streams.state.*;

import java.util.*;

@SuppressWarnings("deprecation")
public final class StreamsApp {
    static final String STORE = "product-state-store";
    private final AppProperties props;
    private final ObjectMapper mapper;
    private final BatchProcessor<TEntity, SEntity, TSEntity> processor;

    public StreamsApp(AppProperties props, ObjectMapper mapper, BatchProcessor<TEntity, SEntity, TSEntity> processor) {
        this.props = props;
        this.mapper = mapper;
        this.processor = processor;
    }

    public KafkaStreams buildAndStart() {
        TopicAdmin.ensureTopics(props);
        var inputSerde = new JsonSerde<InputEvent<TEntity, SEntity, TSEntity>>(mapper, new TypeReference<>() {
        });
        var outSerde = new JsonSerde<ProcessedEvent<TEntity, SEntity, TSEntity>>(mapper, new TypeReference<>() {
        });
        var stateSerde = new JsonSerde<ProductState<TEntity, SEntity, TSEntity>>(mapper, new TypeReference<>() {
        });
        StoreBuilder<KeyValueStore<String, ProductState<TEntity, SEntity, TSEntity>>> store = Stores.keyValueStoreBuilder(Stores.persistentKeyValueStore(STORE), Serdes.String(), stateSerde);
        Topology t = new Topology();
        t.addSource("source", Serdes.String().deserializer(), inputSerde.deserializer(), props.inputTopic())
                .addProcessor("processor", () -> new ProductProcessor(processor), "source")
                .addStateStore(store, "processor")
                .addSink("sink", props.processedTopic(), Serdes.String().serializer(), outSerde.serializer(), "processor");
        Properties c = new Properties();
        c.put(StreamsConfig.APPLICATION_ID_CONFIG, props.streamsApplicationId());
        c.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, props.bootstrapServers());
        c.put(StreamsConfig.STATE_DIR_CONFIG, props.streamsStateDir());
        c.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);
        c.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, props.streamsNumThreads());
        KafkaStreams streams = new KafkaStreams(t, c);
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
        streams.start();
        return streams;
    }

    static final class ProductProcessor implements Processor<String, InputEvent<TEntity, SEntity, TSEntity>> {
        private final BatchProcessor<TEntity, SEntity, TSEntity> processor;
        private ProcessorContext context;
        private KeyValueStore<String, ProductState<TEntity, SEntity, TSEntity>> store;

        ProductProcessor(BatchProcessor<TEntity, SEntity, TSEntity> processor) {
            this.processor = processor;
        }

        public void init(ProcessorContext context) {
            this.context = context;
            this.store = context.getStateStore(STORE);
        }

        public void process(String key, InputEvent<TEntity, SEntity, TSEntity> value) {
            String productId = key != null ? key : value.entity().productId();
            ProductState<TEntity, SEntity, TSEntity> current = store.get(productId);
            if (current == null) current = ProductState.empty(productId);
            var result = processor.process(current, List.of(value));
            store.put(productId, result.newState());
            for (var out : result.processedEvents()) context.forward(productId, out);
        }

        public void close() {
        }
    }
}
