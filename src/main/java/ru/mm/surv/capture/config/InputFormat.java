package ru.mm.surv.capture.config;

import lombok.Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class InputFormat {
    private static final Pattern WIN_PATTERN = Pattern.compile(".*min s=(\\d+)x(\\d+) fps=(\\d+) max s=(\\d+)x(\\d+) fps=(\\d+)");

    public static InputFormat fromWinLine(String line) {
        Matcher m = WIN_PATTERN.matcher(line);
        if (!m.find()) {
            throw new IllegalArgumentException("Can't parse line " + line);
        };
        String hmax = m.group(4);
        String vmax = m.group(5);
        String resmax = hmax + "x" + vmax;
        String fmax = m.group(6);
        return new InputFormat(resmax, fmax);
    }

    public final String resolution;
    public final String fps;
}
