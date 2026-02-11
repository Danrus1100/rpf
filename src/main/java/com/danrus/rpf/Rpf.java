package com.danrus.rpf;

import com.danrus.rpf.compat.rprenames.impl.RpRenamesCompat;
import com.danrus.rpf.core.RpfClientItemInfoLoader;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Rpf implements ClientModInitializer {

    public static CompletableFuture<List<RpfClientItemInfoLoader.LoadedClientInfos>> rpf$currentItemLayersFuture;
    public static boolean debug;

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, commandBuildContext) -> {
            dispatcher.register(ClientCommandManager.literal("rpf_toggle_debug").executes(ctx -> {
                debug = !debug;
                ctx.getSource().sendFeedback(Component.literal("RPF debug mode is now " + debug));
                return 1;
            }));
        });

        if (FabricLoader.getInstance().isModLoaded("rprenames")) {
            RpRenamesCompat.init();
        }
    }
}
