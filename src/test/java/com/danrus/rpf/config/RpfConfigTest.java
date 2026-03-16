package com.danrus.rpf.config;

import com.danrus.rpf.core.init.config.RpfConfig;
import com.danrus.rpf.core.item.RpfResolversManager;
import com.google.gson.JsonPrimitive;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.lang.reflect.Constructor;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class RpfConfigTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("ResourceLocationAdapter serializes correctly")
    void resourceLocationAdapter_serialize_correctFormat() {
        RpfConfig.ResourceLocationAdapter adapter = new RpfConfig.ResourceLocationAdapter();
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath("test", "path");
        
        JsonPrimitive result = (JsonPrimitive) adapter.serialize(location, null, null);
        
        assertEquals("test:path", result.getAsString());
    }

    @Test
    @DisplayName("ResourceLocationAdapter deserializes correctly")
    void resourceLocationAdapter_deserialize_correctResult() {
        RpfConfig.ResourceLocationAdapter adapter = new RpfConfig.ResourceLocationAdapter();
        JsonPrimitive json = new JsonPrimitive("test:path");
        
        ResourceLocation result = adapter.deserialize(json, null, null);
        
        assertEquals("test", result.getNamespace());
        assertEquals("path", result.getPath());
    }

    @Test
    @DisplayName("ResourceLocationAdapter throws on invalid format")
    void resourceLocationAdapter_deserialize_invalidFormat_throws() {
        RpfConfig.ResourceLocationAdapter adapter = new RpfConfig.ResourceLocationAdapter();
        JsonPrimitive json = new JsonPrimitive("invalid string with spaces");
        
        assertThrows(ResourceLocationException.class, () -> adapter.deserialize(json, null, null));
    }

    @Test
    @DisplayName("ResourceLocationAdapter handles vanilla locations")
    void resourceLocationAdapter_vanillaLocation_correctFormat() {
        RpfConfig.ResourceLocationAdapter adapter = new RpfConfig.ResourceLocationAdapter();
        ResourceLocation location = ResourceLocation.withDefaultNamespace("stone");
        
        JsonPrimitive result = (JsonPrimitive) adapter.serialize(location, null, null);
        
        assertEquals("minecraft:stone", result.getAsString());
    }

    @Test
    @DisplayName("ResourceLocationAdapter deserializes vanilla locations")
    void resourceLocationAdapter_deserializeVanilla_correctResult() {
        RpfConfig.ResourceLocationAdapter adapter = new RpfConfig.ResourceLocationAdapter();
        JsonPrimitive json = new JsonPrimitive("minecraft:stone");
        
        ResourceLocation result = adapter.deserialize(json, null, null);
        
        assertEquals("minecraft", result.getNamespace());
        assertEquals("stone", result.getPath());
    }

    @Test
    @DisplayName("Config can be created via reflection for testing")
    void config_canBeCreatedViaReflection() throws Exception {
        RpfConfig config = RpfConfig.create(tempDir);
        
        assertNotNull(config);
        assertEquals(RpfResolversManager.DEFAULT_RESOLVER, config.getResolver());
    }

    @Test
    @DisplayName("Config setResolver works")
    void config_setResolver_works() throws Exception {
        RpfConfig config = RpfConfig.create(tempDir);
        
        ResourceLocation newResolver = ResourceLocation.fromNamespaceAndPath("test", "custom");
        config.setResolver(newResolver);
        
        assertEquals(newResolver, config.getResolver());
    }

    @Test
    @DisplayName("Config isDebug defaults to false")
    void config_isDebug_defaultsToFalse() throws Exception {
        RpfConfig config = RpfConfig.create(tempDir);
        
        assertFalse(config.isDebug());
    }

    @Test
    @DisplayName("Config setDebug works")
    void config_setDebug_works() throws Exception {
        RpfConfig config = RpfConfig.create(tempDir);
        
        config.setDebug(true);
        
        assertTrue(config.isDebug());
    }

    @Test
    @DisplayName("Config file name is correct")
    void config_fileName_isCorrect() {
        assertEquals("rpf.json", RpfConfig.CONFIG_FILE_NAME);
    }

    @Test
    @DisplayName("Default resolver matches RpfResolversManager")
    void config_defaultResolver_matchesManager() throws Exception {
        RpfConfig config = RpfConfig.create(tempDir);
        
        assertEquals(RpfResolversManager.DEFAULT_RESOLVER, config.getResolver());
    }
}
