package com.danrus.rpf.core.init;

import com.danrus.rpf.Rpf;
import com.danrus.rpf.RpfConfig;
import com.danrus.rpf.core.item.RpfResolversManager;
import com.danrus.rpf.debug.RpfDebugSystem;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class RpfCommands {

    public static void init() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, commandBuildContext) -> {
            dispatcher.register(literal("rpf")
                .then(buildResolverCommand())
                .then(buildDebugCommand())
            );
        });
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> buildResolverCommand() {
        return literal("resolver")
                .executes(RpfCommands::executePrintCurrentResolver)
                .then(literal("set")
                        .then(argument("id", ResourceLocationArgument.id())
                                .suggests(RpfCommands::suggestAvailableResolvers)
                                .executes(RpfCommands::executeSetCurrentResolver)
                        )
                );
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> buildDebugCommand() {
//        return literal("debug").executes(RpfCommands::executeToggleDebug);
        return literal("debug")
                .then(literal("toggle").executes(RpfCommands::executeToggleDebug))
                .then(literal("clear").executes(RpfCommands::executeClearDebug))
                .then(literal("export")
                        .then(argument("item", ResourceLocationArgument.id())
                                .suggests(RpfCommands::suggestLoggedItems)
                                .executes(RpfCommands::executeExportDebugItem)
                        )
                );
    }

    private static int executeToggleDebug(CommandContext<FabricClientCommandSource> ctx) {
        RpfDebugSystem.getInstance().toggleDebug();
        ctx.getSource().sendFeedback(Component.translatable("rpf.debug", RpfDebugSystem.getInstance().isDebug()));
        return 1;
    }

    private static int executeClearDebug(CommandContext<FabricClientCommandSource> ctx) {
        RpfDebugSystem.getInstance().clear();
        ctx.getSource().sendFeedback(Component.translatable("rpf.debug.clear", RpfDebugSystem.getInstance().isDebug()));
        return 1;
    }

    private static int executeExportDebugItem(CommandContext<FabricClientCommandSource> ctx){
        ResourceLocation id = ctx.getArgument("item", ResourceLocation.class);
        Path saved = RpfDebugSystem.getInstance().exportDump(id);
        if (saved == null) {
            ctx.getSource().sendError(Component.translatable("rpf.debug.export.error", id));
            return 0;
        }
        ctx.getSource().sendFeedback(Component.translatable("rpf.debug.export", id).append(
                Component.translatable("rpf.debug.export.open").withStyle(
                        style ->  style
                                .withUnderlined(true)
                                .withClickEvent(new ClickEvent.OpenFile(saved))
                                .withHoverEvent(new HoverEvent.ShowText(Component.translatable("rpf.debug.export.open")))
                )
        ));
        return 1;
    }

    private static int executePrintCurrentResolver(CommandContext<FabricClientCommandSource> ctx){
        ctx.getSource().sendFeedback(Component.translatable("rpf.resolver.current", RpfResolversManager.getInstance().getCurrent()));
        return 1;
    }

    private static int executeSetCurrentResolver(CommandContext<FabricClientCommandSource> ctx){
        ResourceLocation id = ctx.getArgument("id", ResourceLocation.class);
        RpfResolversManager.getInstance().setPendingResolver(id);
        Minecraft.getInstance().reloadResourcePacks();
        RpfConfig.getInstance().setResolver(id);
        RpfConfig.save(Rpf.CONFIG_PATH);
        ctx.getSource().sendFeedback(Component.translatable("rpf.resolver.changed", id));
        return 1;
    }

    private static CompletableFuture<Suggestions> suggestAvailableResolvers(CommandContext<FabricClientCommandSource> ctx, SuggestionsBuilder b) {
        List<ResourceLocation> list = RpfResolversManager.getInstance().getAvailable();
        list.sort(Comparator.naturalOrder());
        for (ResourceLocation l : list) {
            b.suggest(l.toString());
        }
        return b.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestLoggedItems(CommandContext<FabricClientCommandSource> ctx, SuggestionsBuilder b) {
        List<ResourceLocation> list = RpfDebugSystem.getInstance().getDatabaseKeys();
        for (ResourceLocation l : list) {
            b.suggest(l.toString());
        }
        return b.buildFuture();
    }

}
