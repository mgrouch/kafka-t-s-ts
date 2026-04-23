
```java

enum Dir {
    D, R
}

enum TT {
    BY, SL, CS, SS
}

record T(String id, String pid, TT tt, long q, long qs) {}
record S(String id, String pid, Dir dir, long q, long used) {}
record TS(String tid, String sid, String pid, long q) {}
```

```
process(event):
  pid = event.pid

  if event is T:
      put T into tById
      add T.id to openTByPid[pid] if (T.q - T.qs) > 0
      for each openS.id in openSByPid[pid] in order by offset:
          emit TS

  if event is S:
      put S into sById
      add S.id to openSByPid[pid] if (S.q - S.qs) > 0
      for each openT.id in openTradesByPid[pid] in business order:
          emit TS
```
