# Сервис стриминга веб камер
## Запуск

TBD

## Запуск из исходников

Создать контейнер с серверным сертификатом

```bash
keytool -keystore clientkeystore -genkey -alias client
```

Создать в корне проекта файл `application.properties`

```properties
ffmpeg.recorder.first.selector=video=USB Video Device:audio=Microphone (USB Audio Device)
ffmpeg.recorder.first.input_resolution=320x240
ffmpeg.recorder.first.input_framerate=16
ffmpeg.publisher=user1

server.ssl.key-store=keystore.jks
server.ssl.key-store-password=******
server.ssl.key-alias=selfsigned

auth.users.user1.username=publisher
auth.users.user1.password=*******
auth.users.user1.role=PUBLISHER
auth.users.user2.username=consumer
auth.users.user2.password=******
auth.users.user2.role=CONSUMER
```

Запустить проект

```properties
mvn spring-boot:run
```

Сервис будет доступен по https://localhost:8443