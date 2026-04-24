package com.example.txcache.app;
import com.fasterxml.jackson.core.type.TypeReference; import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.*; import java.util.Map;
public final class JsonSerde<T> implements Serde<T>{
  private final ObjectMapper mapper; private final TypeReference<T> type;
  public JsonSerde(ObjectMapper mapper, TypeReference<T> type){this.mapper=mapper;this.type=type;}
  public Serializer<T> serializer(){ return (topic,data)->{ try{return data==null?null:mapper.writeValueAsBytes(data);}catch(Exception e){throw new RuntimeException(e);} }; }
  public Deserializer<T> deserializer(){ return (topic,data)->{ try{return data==null?null:mapper.readValue(data,type);}catch(Exception e){throw new RuntimeException(e);} }; }
  public void configure(Map<String,?> configs, boolean isKey){} public void close(){}
}
