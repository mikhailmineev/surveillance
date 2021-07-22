package ru.mm.surv.capture.config;

import lombok.Data;

@Data
public class CameraConfig {

    private String selector;

    private String inputFramerate;

    private String inputResolution;
}
