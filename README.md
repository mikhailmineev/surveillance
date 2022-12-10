# Сервис стриминга веб камер
## Запуск
### Через maven

```bash
run
```

## Первый запуск

```bash
sh first_configure.sh
```

Создать файл `application.properties`. Заполнить файл содержимым ниже, заменив `******` на пароли

Команда для создания файла, запускать в корне проекта

```bash
"auth.users.user1.username=publisher
auth.users.user1.password=*******
auth.users.user1.role=PUBLISHER" >> backend/application.properties
```

Запустить проект

Сервис будет доступен по https://localhost:443

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
