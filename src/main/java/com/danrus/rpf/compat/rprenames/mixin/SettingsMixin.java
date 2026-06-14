package com.danrus.rpf.compat.rprenames.mixin;

import com.hiword9.rprenames.mod.Settings;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.nio.file.Path;

@Mixin(Settings.class)
public class SettingsMixin {
    @Shadow
    static Path configDir;

    @WrapMethod(method = "setConfigDir")
    private static void rpf$ignoreDuplicateSet(Path path, Operation<Void> original){
        // RPF may set configDir early (during compat init); ignore the later
        // duplicate set from RPRenames instead of throwing IllegalStateException.
        if (configDir == null) original.call(path);
    }
}
