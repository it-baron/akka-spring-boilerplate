# Akka && Spring REST Micro-services Boilerplate

## Start with parameters

```sh
PROFILE=dev && mvn clean spring-boot:run -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=${PROFILE}
```

## Check
```sh
curl localhost:8181/ping
```

## Linux service boilerplate

```sh
service.sh
```

## Create seed node

```
-Dakka.remote.netty.tcp.port=2551
```

## Joining seed nodes

```
-Dakka.cluster.seed-nodes.0=akka.tcp://akka@host1:2551
-Dakka.cluster.seed-nodes.1=akka.tcp://akka@host2:2552
```