# flow

## syntax

```
<R>
Flow
   [.exec(() -> ?|Monad<?>)]*
    .returnPtr(Ptr<R>)|.returnValue(() -> R)|.returnFuture(() -> Monad<R>)|.returnNull()

// exec result is FlowStmt without any generic info. each `exec` is considered as a separated statement.
// return result will be Monad<R>
```

## usage

```java
int id = xxx;                                                           // some id
Ptr<Server> server = Ptr.nil();                                         // create a nil pointer for holding server
Ptr<ServerGroup> group = Ptr.nil();                                     // create a nil pointer for holding group
Flow.exec(() -> server.assign(getServer(id)))                           // get and assign server
    .exec(() -> group.assign(getServerGroup(server.value.groupId)))     // get and assign group
    .returnValue(() -> new Tuple2<>(server.value, group.value))         // return a tuple of server and group
```
