package com.danrus.rpf;

import com.danrus.rpf.api.event.RpfEventBus;
import com.danrus.rpf.compat.rprenames.impl.RpRenamesCompat;
import com.danrus.rpf.core.item.RpfResolversManager;
import com.danrus.rpf.core.load.RpfClientItemInfoLoader;
import com.danrus.rpf.impl.RpfExperimentalResolver;
import com.danrus.rpf.impl.RpfV1ModelResolver;
import com.danrus.rpf.impl.VanillaModelResolver;
import com.danrus.rpf.logging.ItemModelsSelectLogger;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class Rpf implements ClientModInitializer {

    public static String MOD_ID = "rpf";
    public static CompletableFuture<List<RpfClientItemInfoLoader.LoadedClientInfos>> rpf$currentItemLayersFuture;
    private static final ItemModelsSelectLogger ITEM_LOGGER = new ItemModelsSelectLogger();
    private static final RpfEventBus EVENT_BUS = new RpfEventBus();
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir();
    public static boolean debug;

    @Override
    public void onInitializeClient() {
        RpfConfig.init(CONFIG_PATH);
        RpfCodecs.init();

        RpfResolversManager.getInstance().register(RpfResolversManager.DEFAULT_RESOLVER, new RpfV1ModelResolver());
        RpfResolversManager.getInstance().register(RpfResolversManager.VANILLA_RESOLVER, new VanillaModelResolver());
        RpfResolversManager.getInstance().register(ResourceLocation.fromNamespaceAndPath("rpf", "experimental"), new RpfExperimentalResolver());
        RpfResolversManager.getInstance().setPendingResolver(RpfConfig.getInstance().getResolver());

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, commandBuildContext) -> {
            dispatcher.register(
                    literal("rpf")
                            .then(
                                    literal("resolver")
                                            .executes(context -> {
                                                context.getSource().sendFeedback(Component.translatable("rpf.resolver.current", RpfResolversManager.getInstance().getCurrent()));
                                                return 1;
                                            })
                                            .then(
                                                    literal("set")
                                                            .then(
                                                                    argument("id", ResourceLocationArgument.id())
                                                                            .suggests((ctx, b) -> {
                                                                                List<ResourceLocation> list = RpfResolversManager.getInstance().getAvailable();
                                                                                list.sort(Comparator.naturalOrder());
                                                                                for (ResourceLocation l : list) {
                                                                                    b.suggest(l.toString());
                                                                                }
                                                                                return b.buildFuture();
                                                                            })
                                                                            .executes(context -> {
                                                                                ResourceLocation id = context.getArgument("id", ResourceLocation.class);
                                                                                RpfResolversManager.getInstance().setPendingResolver(id);
                                                                                Minecraft.getInstance().reloadResourcePacks();
                                                                                RpfConfig.getInstance().setResolver(id);
                                                                                RpfConfig.save(CONFIG_PATH);
                                                                                context.getSource().sendFeedback(Component.translatable("rpf.resolver.changed", id));
                                                                                return 1;
                                                                            })
                                                            )
                                            )
                            )
                            .then(
                            literal("debug").executes(ctx -> {
                                debug = !debug;
                                ctx.getSource().sendFeedback(Component.translatable("rpf.debug", debug));
                                return 1;
                            })

                    )
            );
        });

        if (FabricLoader.getInstance().isModLoaded("rprenames")) {
            RpRenamesCompat.init();
        }
    }

    public static RpfEventBus getEventBus() {
        return EVENT_BUS;
    }

    public static ItemModelsSelectLogger getItemLogger() {
        return ITEM_LOGGER;
    }
}
