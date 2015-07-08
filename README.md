# Start with parameters

```sh
PROFILE=dev && mvn clean spring-boot:run -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=${PROFILE}
```

# Linux service boilerblate

```sh
service.sh
```