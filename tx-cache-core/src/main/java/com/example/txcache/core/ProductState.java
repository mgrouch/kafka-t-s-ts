package com.example.txcache.core;
import java.util.*;
public record ProductState<T,S,TS>(String productId, Map<String,T> tById, Map<String,S> sById, Map<String,TS> tsById) {
  public static <T,S,TS> ProductState<T,S,TS> empty(String productId){ return new ProductState<>(productId,new LinkedHashMap<>(),new LinkedHashMap<>(),new LinkedHashMap<>()); }
  public ProductState<T,S,TS> copy(){ return new ProductState<>(productId,new LinkedHashMap<>(tById),new LinkedHashMap<>(sById),new LinkedHashMap<>(tsById)); }
}
