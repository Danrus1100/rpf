package com.danrus.rpf;

import com.danrus.rpf.api.event.RpfEventBus;
import com.danrus.rpf.compat.rprenames.impl.RpRenamesCompat;
import com.danrus.rpf.core.init.RpfCommands;
import com.danrus.rpf.core.item.RpfResolversManager;
import com.danrus.rpf.core.load.ResourceLoadManager;
import com.danrus.rpf.impl.RpfExperimentalResolver;
import com.danrus.rpf.impl.RpfV1ModelResolver;
import com.danrus.rpf.impl.VanillaModelResolver;
import com.danrus.rpf.debug.ItemModelsSelectLogger;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;

public class Rpf implements ClientModInitializer {

    public static String MOD_ID = "rpf";
    private static final ResourceLoadManager RESOURCE_LOAD_MANAGER = new ResourceLoadManager();
    private static final ItemModelsSelectLogger ITEM_LOGGER = new ItemModelsSelectLogger();
    private static final RpfEventBus EVENT_BUS = new RpfEventBus();
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir();
    public static boolean debug;

    @Override
    public void onInitializeClient() {
        RpfConfig.init(CONFIG_PATH);
        registerResolvers();
        RpfCommands.init();

        if (FabricLoader.getInstance().isModLoaded("rprenames")) {
            RpRenamesCompat.init();
        }
    }

    private static void registerResolvers() {
        RpfResolversManager.getInstance().register(RpfResolversManager.DEFAULT_RESOLVER, new RpfV1ModelResolver());
        RpfResolversManager.getInstance().register(RpfResolversManager.VANILLA_RESOLVER, new VanillaModelResolver());
        RpfResolversManager.getInstance().register(ResourceLocation.fromNamespaceAndPath("rpf", "experimental"), new RpfExperimentalResolver());
        RpfResolversManager.getInstance().setPendingResolver(RpfConfig.getInstance().getResolver());
    }

    public static RpfEventBus getEventBus() {
        return EVENT_BUS;
    }

    public static ItemModelsSelectLogger getItemLogger() {
        return ITEM_LOGGER;
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
}
