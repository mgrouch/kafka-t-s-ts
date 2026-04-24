package com.example.txcache.app.domain;
import com.example.txcache.core.*;
import java.time.Instant; import java.util.*;
public final class SampleBatchProcessor implements BatchProcessor<TEntity,SEntity,TSEntity>{
  public ProcessResult<TEntity,SEntity,TSEntity> process(ProductState<TEntity,SEntity,TSEntity> current,List<InputEvent<TEntity,SEntity,TSEntity>> batch){
    var state=current.copy(); var out=new ArrayList<ProcessedEvent<TEntity,SEntity,TSEntity>>();
    for(var event:batch){ var e=event.entity();
      switch(e.kind()){
        case T -> { if(e.tombstone()) state.tById().remove(e.entityId()); else state.tById().put(e.entityId(), e.tValue()); }
        case S -> { if(e.tombstone()) state.sById().remove(e.entityId()); else state.sById().put(e.entityId(), e.sValue()); }
        case TS -> { if(e.tombstone()) state.tsById().remove(e.entityId()); else state.tsById().put(e.entityId(), e.tsValue()); }
      }
      out.add(new ProcessedEvent<>(event.eventId(), Instant.now(), e));
    }
    return new ProcessResult<>(state,out);
  }
}
