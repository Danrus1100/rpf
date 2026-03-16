//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.danrus.rpf.compat;

import com.danrus.rpf.debug.RpfLogger;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public abstract class RpfCompatPlugin implements IMixinConfigPlugin {
    private static final Map<String, String> COMPAT_CLASSES = Map.of(
            "rprenames", "com.danrus.rpf.compat.rprenames.RpRenamesCompat",
            "my-totem-doll", "com.danrus.rpf.compat.mtd.MtdCompat"
    );
    private static boolean loaded = false;

    public static void loadCompats() {
        if (!loaded) {
            for(Map.Entry<String, String> entry : COMPAT_CLASSES.entrySet()) {
                String modId = (String)entry.getKey();
                String className = (String)entry.getValue();
                if (FabricLoader.getInstance().isModLoaded(modId)) {
                    try {
                        Class<?> clazz = Class.forName(className);
                        RpfCompatInitializer compat = (RpfCompatInitializer)clazz.getDeclaredConstructor().newInstance();
                        compat.init();
                        RpfLogger.get().info("Successfully loaded compatibility for: " + modId);
                    } catch (Exception e) {
                        RpfLogger.get().error("Failed to load compatibility for mod: " + modId, e);
                    }
                }
            }

            loaded = true;
        }
    }

    public void onLoad(String mixinPackage) {
    }

    public String getRefMapperConfig() {
        return "";
    }

    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return FabricLoader.getInstance().isModLoaded(this.modId());
    }

    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    public List<String> getMixins() {
        return List.of();
    }

    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    protected abstract String modId();
}
