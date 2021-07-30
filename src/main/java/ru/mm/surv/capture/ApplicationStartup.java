package ru.mm.surv.capture;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartup
        implements ApplicationListener<ApplicationReadyEvent> {

    private final FfmpegStreamManager ffmpeg;

    public ApplicationStartup(FfmpegStreamManager ffmpeg) {
        this.ffmpeg = ffmpeg;
    }

    /**
     * This event is executed as late as conceivably possible to indicate that
     * the application is ready to service requests.
     */
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        ffmpeg.start();
    }
}
