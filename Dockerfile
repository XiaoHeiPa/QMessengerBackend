FROM ubuntu:latest
LABEL authors="cubewhy"

RUN apt add --no-cache git

ARG USERNAME
ARG PASSWORD

RUN git clone https://$USERNAME:$PASSWORD@github.com/qbychat/QMessengerBackend .
RUN bash ./gradlew build


ENTRYPOINT ["java", "-jar", "./build/libs/QMessengerBackend.jar"]
CMD ["--server.port=42229"]

