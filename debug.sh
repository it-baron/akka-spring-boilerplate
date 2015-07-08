#!/bin/sh
PROFILE=dev
mvn clean spring-boot:run -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=${PROFILE} -Dakka.remote.netty.tcp.port=2551
