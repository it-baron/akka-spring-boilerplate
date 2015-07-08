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