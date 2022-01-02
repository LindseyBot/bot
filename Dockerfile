FROM azul/zulu-openjdk-alpine:17

LABEL maintainer="git@notfab.net"

COPY build/libs/*.jar /opt/

CMD ["java", "-jar", "/opt/bot.jar"]
