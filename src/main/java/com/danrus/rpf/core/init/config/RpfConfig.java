package com.danrus.rpf.core.init.config;

import com.danrus.rpf.Rpf;
import com.danrus.rpf.core.item.RpfResolversManager;
import com.google.gson.*;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;

public class RpfConfig {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Identifier.class, new IdentifierAdapter())
            .create();
    private static final Logger log = LoggerFactory.getLogger(RpfConfig.class);

    @RpfConfigField
    private Identifier resolver;
    @RpfConfigField
    private boolean isDebug;

    public static final String CONFIG_FILE_NAME = "rpf.json";

    public RpfConfig() {
        this.resolver = RpfResolversManager.DEFAULT_RESOLVER;
        this.isDebug = false;
    }

    public Identifier getResolver() {
        return resolver;
    }

    public void setResolver(Identifier resolver) {
        this.resolver = resolver;
    }

    public static RpfConfig create(Path configPath) {
        Path configFile = configPath.resolve(CONFIG_FILE_NAME);

        try {
            if (Files.exists(configFile)) {
                return load(configFile);
            } else {
                RpfConfig config = new RpfConfig();
                save(config, configFile);
                return config;
            }
        } catch (Exception e) {
            log.error("Failed to load config, using defaults", e);
            return new RpfConfig();
        }
    }


    public void reload(Path configPath) {
        Path configFile = configPath.resolve(CONFIG_FILE_NAME);
        if (Files.exists(configFile)) {
            try {
                RpfConfig loaded = load(configFile);
                copyAnnotatedFields(loaded, this);
                log.info("[RPF] Config reloaded successfully.");
            } catch (Exception e) {
                log.error("Failed to reload config", e);
            }
        }
    }

    private void copyAnnotatedFields(RpfConfig from, RpfConfig to) throws IllegalAccessException {
        for (Field field : RpfConfig.class.getDeclaredFields()) {
            if (field.isAnnotationPresent(RpfConfigField.class)) {
                field.setAccessible(true);

                Object value = field.get(from);
                field.set(to, value);

                log.debug("[RPF] Updated config field: {} = {}", field.getName(), value);
            }
        }
    }

    public void save() {
        save(Rpf.CONFIG_PATH);
    }

    public void save(Path configDir) {
        Path configFile = configDir.resolve(CONFIG_FILE_NAME);
        try {
            save(this, configFile);
        } catch (IOException e) {
            log.error("Failed to save config", e);
        }
    }

    private static RpfConfig load(Path path) throws IOException {
        try (Reader reader = Files.newBufferedReader(path)) {
            RpfConfig config = GSON.fromJson(reader, RpfConfig.class);
            if (config == null) {
                throw new IOException("Config file is empty or invalid");
            }
            return config;
        }
    }

    private static void save(RpfConfig config, Path path) throws IOException {
        Files.createDirectories(path.getParent());
        try (Writer writer = Files.newBufferedWriter(path)) {
            GSON.toJson(config, writer);
        }
    }

    public void setDebug(boolean debugOutput) {
        this.isDebug = debugOutput;
    }

    public boolean isDebug() {
        return isDebug;
    }

    public static class IdentifierAdapter implements JsonSerializer<Identifier>, JsonDeserializer<Identifier> {
        @Override
        public JsonElement serialize(Identifier src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public Identifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            // Используем tryParse или parse в зависимости от версии MC
            return Identifier.parse(json.getAsString());
        }
    }
}