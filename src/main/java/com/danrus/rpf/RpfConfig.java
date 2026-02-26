package com.danrus.rpf;

import com.danrus.rpf.core.item.RpfResolversManager;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;

public class RpfConfig {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocationAdapter())
            .create();
    private static final Logger log = LoggerFactory.getLogger(RpfConfig.class);

    private ResourceLocation resolver;
    private boolean isDebug;

    public static final String CONFIG_FILE_NAME = "rpf.json";
    private static RpfConfig INSTANCE = null;

    private RpfConfig() {
        this.resolver = RpfResolversManager.DEFAULT_RESOLVER;
    }

    public ResourceLocation getResolver() {
        return resolver;
    }

    public void setResolver(ResourceLocation resolver) {
        this.resolver = resolver;
    }

    public static RpfConfig getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Config not initialized yet!");
        }
        return INSTANCE;
    }

    public static void init(Path configPath) {
        if (INSTANCE != null) {
            return;
        }

        Path configFile = configPath.resolve(CONFIG_FILE_NAME);

        try {
            if (Files.exists(configFile)) {
                INSTANCE = load(configFile);
            } else {
                INSTANCE = new RpfConfig();
                save(INSTANCE, configFile);
            }
        } catch (Exception e) {
            log.error("Failed to load config, using defaults", e);
            INSTANCE = new RpfConfig(); // fallback
            try {
                save(INSTANCE, configFile);
            } catch (IOException ex) {
                log.error("Failed to save fallback config", ex);
            }
        }
    }

    public static void reload(Path configPath) {
        Path configFile = configPath.resolve(CONFIG_FILE_NAME);
        if (Files.exists(configFile)) {
            try {
                INSTANCE = load(configFile);
            } catch (IOException | JsonParseException e) {
                log.error("Failed to reload config", e);
            }
        }
    }

    public static void save(Path configDir) {
        if (INSTANCE == null) return;
        Path configFile = configDir.resolve(CONFIG_FILE_NAME);
        try {
            save(INSTANCE, configFile);
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

    public static class ResourceLocationAdapter implements JsonSerializer<ResourceLocation>, JsonDeserializer<ResourceLocation> {
        @Override
        public JsonElement serialize(ResourceLocation src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public ResourceLocation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return ResourceLocation.parse(json.getAsString());
        }
    }
}