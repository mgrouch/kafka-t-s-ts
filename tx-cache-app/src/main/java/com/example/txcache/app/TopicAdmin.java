package com.example.txcache.app;
import org.apache.kafka.clients.admin.*; import java.util.*;
public final class TopicAdmin{
  private TopicAdmin(){}
  public static void ensureTopics(AppProperties p){
    try(var admin=AdminClient.create(Map.of("bootstrap.servers",p.bootstrapServers()))){
      admin.createTopics(List.of(new NewTopic(p.inputTopic(),p.topicPartitions(),(short)1),new NewTopic(p.processedTopic(),p.topicPartitions(),(short)1))).all().get();
    }catch(Exception ignored){}
  }
}
