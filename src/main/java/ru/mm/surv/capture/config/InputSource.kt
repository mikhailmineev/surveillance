package ru.mm.surv.capture.config;

import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class InputSource {
    private static final Pattern MAC_PATTERN = Pattern.compile("\\[.*] \\[(\\d)] (.*)");
    private static final Pattern WIN_PATTERN = Pattern.compile("\\[.*]  \"(.*)\"");

    public static InputSource fromMacLine(InputType inputType, String line) {
        Matcher m = MAC_PATTERN.matcher(line);
        if (!m.find()) {
            throw new IllegalArgumentException("Can't parse line " + line);
        };
        String id = m.group(1);
        String name = m.group(2);
        return new InputSource(inputType, id, name, Collections.emptyList());
    }

    public static InputSource fromWinLine(InputType inputType, String line) {
        Matcher m = WIN_PATTERN.matcher(line);
        if (!m.find()) {
            throw new IllegalArgumentException("Can't parse line " + line);
        };
        String id = m.group(1);
        return new InputSource(inputType, id, id, Collections.emptyList());
    }

    private final InputType type;
    private final String id;
    private final String name;
    private final List<InputFormat> formats;
}
