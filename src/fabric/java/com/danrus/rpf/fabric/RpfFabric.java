package com.danrus.rpf.fabric;

import com.danrus.rpf.Rpf;
import com.danrus.rpf.compat.RpfCompatPlugin;
import com.danrus.rpf.core.init.RpfCommands;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;

public final class RpfFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RpfCompatPlugin.setModLoadedPredicate(FabricLoader.getInstance()::isModLoaded);
        Rpf.init(FabricLoader.getInstance().getConfigDir());

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext) -> {
            new RpfCommands<>(dispatcher).init();
        });
    }
}
