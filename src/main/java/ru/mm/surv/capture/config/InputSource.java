package ru.mm.surv.capture.config;

import lombok.Data;

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
        return new InputSource(inputType, id, name);
    }

    public static InputSource fromWinLine(InputType inputType, String line) {
        Matcher m = WIN_PATTERN.matcher(line);
        if (!m.find()) {
            throw new IllegalArgumentException("Can't parse line " + line);
        };
        String id = m.group(1);
        return new InputSource(inputType, id, id);
    }

    public final InputType type;
    public final String id;
    public final String name;
}
