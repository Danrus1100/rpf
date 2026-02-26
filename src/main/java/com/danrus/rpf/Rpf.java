package com.danrus.rpf;

import com.danrus.rpf.api.event.RpfEventBus;
import com.danrus.rpf.compat.rprenames.impl.RpRenamesCompat;
import com.danrus.rpf.core.init.RpfCommands;
import com.danrus.rpf.core.init.config.RpfConfig;
import com.danrus.rpf.core.item.RpfResolversManager;
import com.danrus.rpf.core.load.ResourceLoadManager;
import com.danrus.rpf.impl.RpfExperimentalResolver;
import com.danrus.rpf.impl.RpfV1ModelResolver;
import com.danrus.rpf.impl.VanillaModelResolver;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

import java.nio.file.Path;

public class Rpf implements ClientModInitializer {

    public static String MOD_ID = "rpf";
    private static final ResourceLoadManager RESOURCE_LOAD_MANAGER = new ResourceLoadManager();
    private static final RpfEventBus EVENT_BUS = new RpfEventBus();
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir();
    private static final RpfConfig CONFIG = RpfConfig.create(CONFIG_PATH);

    @Override
    public void onInitializeClient() {
        registerResolvers();
        RpfCommands.init();

        if (FabricLoader.getInstance().isModLoaded("rprenames")) {
            RpRenamesCompat.init();
        }
    }

    private static void registerResolvers() {
        RpfResolversManager.getInstance().register(RpfResolversManager.DEFAULT_RESOLVER, new RpfV1ModelResolver());
        RpfResolversManager.getInstance().register(RpfResolversManager.VANILLA_RESOLVER, new VanillaModelResolver());
        RpfResolversManager.getInstance().register(Identifier.fromNamespaceAndPath("rpf", "experimental"), new RpfExperimentalResolver());
        RpfResolversManager.getInstance().setPendingResolver(CONFIG.getResolver());
    }

    public static RpfEventBus getEventBus() {
        return EVENT_BUS;
    }

    /**
     * Gets the resource load manager for thread-safe access to loading futures.
     * 
     * @return The resource load manager instance
     * @since 1.4.0
     */
    public static ResourceLoadManager getResourceLoadManager() {
        return RESOURCE_LOAD_MANAGER;
    }

    public static RpfConfig getConfig() {
        return CONFIG;
    }
}
