package com.danrus.rpf.core.init;

import com.danrus.rpf.Rpf;
import com.danrus.rpf.RpfConfig;
import com.danrus.rpf.core.item.RpfResolversManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

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
        return literal("debug").executes(RpfCommands::executeToggleDebug);
    }

    private static int executeToggleDebug(CommandContext<FabricClientCommandSource> ctx) {
        Rpf.debug = !Rpf.debug;
        ctx.getSource().sendFeedback(Component.translatable("rpf.debug", Rpf.debug));
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

}
