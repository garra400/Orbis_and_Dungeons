package com.garra400.racas.color;

import com.hypixel.hytale.server.core.Message;

import java.awt.Color;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ColorConverter {

    private static final Pattern CODE_PATTERN =
            Pattern.compile("&(#?[0-9a-fA-F]{6}|[0-9a-flomr])");

    private static final Map<String, Color> COLORS = Map.ofEntries(
            Map.entry("&0", new Color(0, 0, 0)),
            Map.entry("&1", new Color(0, 0, 170)),
            Map.entry("&2", new Color(0, 170, 0)),
            Map.entry("&3", new Color(0, 170, 170)),
            Map.entry("&4", new Color(170, 0, 0)),
            Map.entry("&5", new Color(170, 0, 170)),
            Map.entry("&6", new Color(255, 170, 0)),
            Map.entry("&7", new Color(170, 170, 170)),
            Map.entry("&8", new Color(85, 85, 85)),
            Map.entry("&9", new Color(85, 85, 255)),
            Map.entry("&a", new Color(85, 255, 85)),
            Map.entry("&b", new Color(85, 255, 255)),
            Map.entry("&c", new Color(255, 85, 85)),
            Map.entry("&d", new Color(255, 85, 255)),
            Map.entry("&e", new Color(255, 255, 85)),
            Map.entry("&f", new Color(255, 255, 255))
    );

    private ColorConverter() {}

    public static Message message(String input) {
        Message base = Message.raw("");

        if (input == null || input.isEmpty()) {
            return base;
        }

        int lastIndex = 0;
        Color color = Color.WHITE;
        boolean bold = false;
        boolean italic = false;
        boolean mono = false;

        Matcher matcher = CODE_PATTERN.matcher(input);

        while (matcher.find()) {
            if (matcher.start() > lastIndex) {
                base.insert(
                        Message.raw(input.substring(lastIndex, matcher.start()))
                                .color(color)
                                .bold(bold)
                                .italic(italic)
                                .monospace(mono)
                );
            }

            String code = matcher.group().toLowerCase();

            if (code.startsWith("&#")) {
                try {
                    color = Color.decode(code.substring(1));
                } catch (NumberFormatException ignored) {}
            } else {
                switch (code) {
                    case "&l" -> bold = true;
                    case "&o" -> italic = true;
                    case "&m" -> mono = true;
                    case "&r" -> {
                        color = Color.WHITE;
                        bold = italic = mono = false;
                    }
                    default -> color = COLORS.getOrDefault(code, color);
                }
            }

            lastIndex = matcher.end();
        }

        if (lastIndex < input.length()) {
            base.insert(
                    Message.raw(input.substring(lastIndex))
                            .color(color)
                            .bold(bold)
                            .italic(italic)
                            .monospace(mono)
            );
        }

        return base;
    }
}
