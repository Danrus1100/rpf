package com.danrus.rpf.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public record ItemDump(Type type, Identifier item, List<String> strings) {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    private static Path exportPath() {
        return Minecraft.getInstance().gameDirectory.toPath().resolve("debug/rpf");
    }

    private static String sanitize(Identifier item) {
        // Identifiers may contain '/', '..' or Windows-reserved names; keep each dump
        // inside debug/rpf and use a single, predictable file name.
        return (item.getNamespace() + "_" + item.getPath()).replaceAll("[^a-z0-9._-]", "_");
    }

    public void save() {
        try {
            Path exportPath = exportPath();
            Files.createDirectories(exportPath);
            String timestamp = LocalDateTime.now().format(TIME_FORMATTER);

            String fileName = String.format("%s_%s_%s.log",
                    sanitize(item),
                    type.name().toLowerCase(Locale.ROOT),
                    timestamp
            );

            Path toSave = exportPath.resolve(fileName);
            Files.write(toSave, strings, StandardCharsets.UTF_8);
            RpfLogger.get().info("Debug log saved to: " + toSave.toAbsolutePath());

        } catch (IOException e) {
            RpfLogger.get().error("Failed to save item debug log", e);
        }
    }

    @Nullable
    public static Path saveCombined(Identifier item, List<ItemDump> dumps) {
        if (dumps.isEmpty()) return null;

        try {
            Path exportPath = exportPath();
            Files.createDirectories(exportPath);
            String timestamp = LocalDateTime.now().format(TIME_FORMATTER);

            String fileName = String.format("%s_%s.log",
                    sanitize(item),
                    timestamp
            );

            Path toSave = exportPath.resolve(fileName);

            List<String> allLines = new ArrayList<>();
            for (ItemDump dump : dumps) {
                allLines.add("=== TYPE: " + dump.type() + " ===");
                allLines.addAll(dump.strings());
                allLines.add("");
            }

            Files.write(toSave, allLines, StandardCharsets.UTF_8);
            RpfLogger.get().info("Combined debug log saved to: " + toSave.toAbsolutePath());

            return toSave;

        } catch (IOException e) {
            RpfLogger.get().error("Failed to save combined debug log", e);
        }
        return null;
    }

    protected enum Type {
        ERROR,
        INFO
    }
}
