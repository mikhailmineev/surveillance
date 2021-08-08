# Сервис стриминга веб камер
## Запуск
### Через maven

```bash
mvnw package
run
```

или

```bash
mvn clean
mvn spring-boot:run
```

## Первый запуск

Создать контейнер с серверным сертификатом

```bash
keytool -genkeypair -alias selfsigned -keyalg RSA -validity 365 --keystore keystore.jks
```

Далее указать пароль и данные сертификата

```bash
Enter keystore password:  
Re-enter new password: 
What is your first and last name?
  [Unknown]:  
What is the name of your organizational unit?
  [Unknown]:  
What is the name of your organization?
  [Unknown]:  
What is the name of your City or Locality?
  [Unknown]:  
What is the name of your State or Province?
  [Unknown]:  
What is the two-letter country code for this unit?
  [Unknown]:  
Is CN=Unknown, OU=Unknown, O=Unknown, L=Unknown, ST=Unknown, C=Unknown correct?
  [no]:  yes
```

Создать в корне проекта файл `application.properties`. Заполнить файл содержимым ниже, заменив `******` на пароли

```properties
server.ssl.key-store=keystore.jks
server.ssl.key-store-password=******
server.ssl.key-alias=selfsigned

auth.users.user1.username=publisher
auth.users.user1.password=*******
auth.users.user1.roles=PUBLISHER
auth.users.user2.username=consumer
auth.users.user2.password=******
auth.users.user2.roles=CONSUMER,ADMIN
```

Команда для создания файла

```bash
server.ssl.key-store=keystore.jks
server.ssl.key-store-password=******
server.ssl.key-alias=selfsigned

auth.users.user1.username=publisher
auth.users.user1.password=*******
auth.users.user1.role=PUBLISHER
auth.users.user2.username=consumer
auth.users.user2.password=******
auth.users.user2.role=CONSUMER" >> application.properties

```

Запустить проект

Сервис будет доступен по https://localhost:8443

Зайти через администратора на сайт

Зайти в панель "configure"

В таблице доступных устройств взять названия нужных

Добавить в `application.properties` конфигурацию захвата. Ниже пример для одной из камер

```properties
ffmpeg.recorder.first.name=first
ffmpeg.recorder.first.audio=Microphone (USB Audio Device)
ffmpeg.recorder.first.video=USB Video Device
ffmpeg.recorder.first.input_resolution=320x240
ffmpeg.recorder.first.input_framerate=16
ffmpeg.publisher=user1
```

Перезапустить приложение