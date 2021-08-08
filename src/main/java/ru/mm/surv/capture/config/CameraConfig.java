package ru.mm.surv.capture.config;

import lombok.Data;

@Data
public class CameraConfig {

    private String name;

    private String video;

    private String audio;

    private String inputFramerate;

    private String inputResolution;
}
