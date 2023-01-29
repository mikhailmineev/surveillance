# Webcam streaming service
## Launch

```bash
sh run.sh
```

Service should become accessible from https://localhost:443

## First launch

```bash
sh first_configure.sh
sh run.sh
```

### Add webcam

- Open "configure" page
- Find needed webcams
- Add to `application.properties` config that looks like this:

```properties
ffmpeg.recorder.first.name=first
ffmpeg.recorder.first.audio=Microphone (USB Audio Device)
ffmpeg.recorder.first.video=USB Video Device
ffmpeg.recorder.first.input_resolution=320x240
ffmpeg.recorder.first.input_framerate=16
ffmpeg.publisher=user1
```

- Restart app
