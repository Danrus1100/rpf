package com.danrus.rpf;

import com.danrus.rpf.compat.rprenames.impl.RpRenamesCompat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Rpf implements ClientModInitializer {

    public static CompletableFuture<List<RpfClientItemInfoLoader.LoadedClientInfos>> rpf$currentItemLayersFuture;

    @Override
    public void onInitializeClient() {
        if (FabricLoader.getInstance().isModLoaded("rprenames")) {
            RpRenamesCompat.init();
        }
    }
}
