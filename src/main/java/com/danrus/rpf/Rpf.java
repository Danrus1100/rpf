package com.danrus.rpf;

import com.danrus.rpf.api.event.RpfEventBus;
import com.danrus.rpf.compat.rprenames.impl.RpRenamesCompat;
import com.danrus.rpf.core.RpfClientItemInfoLoader;
import com.danrus.rpf.logging.ItemModelsSelectLogger;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class Rpf implements ClientModInitializer {

    public static String MOD_ID = "rpf";
    public static CompletableFuture<List<RpfClientItemInfoLoader.LoadedClientInfos>> rpf$currentItemLayersFuture;
    private static final ItemModelsSelectLogger ITEM_LOGGER = new ItemModelsSelectLogger();
    private static final RpfEventBus EVENT_BUS = new RpfEventBus();
    public static boolean debug;
    public static boolean toggle = true;

    @Override
    public void onInitializeClient() {
        RpfCodecs.init();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, commandBuildContext) -> {
            dispatcher.register(
                    literal("rpf")
                            .then(
                                    literal("toggle").executes(ctx -> {
                                        toggle = !toggle;
                                        MutableComponent issues = Component.literal("issues").withStyle(s -> s
                                                .withUnderlined(true)
                                                .withHoverEvent(new HoverEvent.ShowText(Component.literal("Github issues page")))
                                                .withClickEvent(new ClickEvent.OpenUrl(URI.create("https://github.com/Danrus1100/rpf/issues")))
                                        );
                                        String key = "rpf.toggle." + (toggle ? "on" : "off");
                                        Minecraft.getInstance().reloadResourcePacks();
                                        ctx.getSource().sendFeedback(Component.translatable(key, issues));
                                        return 1;
                                    })
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
