package dev.cakestudio.cakenear.util;

import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class HexColor {

    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .hexColors()
            .character('&')
            .build();

    public static @NonNull Component deserialize(String input) {
        return SERIALIZER.deserialize(input);
    }
}
