# todo-list-vertx

This project is a fork from marcorotondi's project [todo-list-vertx](https://github.com/marcorotondi/todo-list-vertx.git). I use thsi repository as a good reference of mid-size project using Vertx.


## History of the branches

| Branch | Description | Status | Last update date |
|--------|-------------|--------|------------------|
| feature/structure | Create the Eclipse structure and guarantee the successful execution of the buidl package | Open | 2021-04-21 |
| feature/documentation-code | Explicate the source code in Vertx | Closed | 2021-04-21 |
| develop | Reference branch for every dev branch, except _hotfix_ | Open | 2021-04-21 |
| master | Reference branch for stable version | Open | 2021-04-21 |



# Weird error and solution

| Message | Solution |
|---------|----------| 
| java.lang.IllegalAccessException: class io.netty.util.internal.PlatformDependent0$6 cannot access class jdk.internal.misc.Unsafe (in module java.base) because module java.base does not export jdk.internal.misc to unnamed module | https://stackoverflow.com/questions/57885828/netty-cannot-access-class-jdk-internal-misc-unsafe | 
