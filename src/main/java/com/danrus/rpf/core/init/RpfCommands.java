package com.danrus.rpf.core.init;

import com.danrus.rpf.Rpf;
import com.danrus.rpf.core.item.RpfResolversManager;
import com.danrus.rpf.debug.RpfDebugSystem;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

import java.awt.*;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RpfCommands<T> {

    private final CommandDispatcher<T> dispatcher;

    public RpfCommands(CommandDispatcher<T> dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void init() {
        dispatcher.register(literal("rpf")
            .then(buildResolverCommand())
            .then(buildDebugCommand())
        );
    }

    private LiteralArgumentBuilder<T> buildResolverCommand() {
        return literal("resolver")
                .executes(this::executePrintCurrentResolver)
                .then(literal("set")
                        .then(argument("id", IdentifierArgument.id())
                                .suggests(this::suggestAvailableResolvers)
                                .executes(this::executeSetCurrentResolver)
                        )
                );
    }

    private LiteralArgumentBuilder<T> buildDebugCommand() {
        return literal("debug")
                .then(literal("toggle").executes(this::executeToggleDebug))
                .then(literal("clear").executes(this::executeClearDebug))
                .then(literal("export")
                        .then(argument("item", IdentifierArgument.id())
                                .suggests(this::suggestLoggedItems)
                                .executes(this::executeExportDebugItem)
                        )
                );
    }

    private int executeToggleDebug(CommandContext<T> ctx) {
        RpfDebugSystem.getInstance().toggleDebug();
        sendFeedback(Component.translatable("rpf.debug", RpfDebugSystem.getInstance().isDebug()));
        return 1;
    }

    private int executeClearDebug(CommandContext<T> ctx) {
        RpfDebugSystem.getInstance().clear();
        sendFeedback(Component.translatable("rpf.debug.clear", RpfDebugSystem.getInstance().isDebug()));
        return 1;
    }

    private int executeExportDebugItem(CommandContext<T> ctx){
        Identifier id = ctx.getArgument("item", Identifier.class);
        Path saved = RpfDebugSystem.getInstance().exportDump(id);
        if (saved == null) {
            sendError(Component.translatable("rpf.debug.export.error", id));
            return 0;
        }
        sendFeedback(Component.translatable("rpf.debug.export", id).append(
                Component.translatable("rpf.debug.export.open").withStyle(
                        style ->  style
                                .withUnderlined(true)
                                .withClickEvent(new ClickEvent.OpenFile(saved))
                                .withHoverEvent(new HoverEvent.ShowText(Component.translatable("rpf.debug.export.open")))
                )
        ));
        return 1;
    }

    private int executePrintCurrentResolver(CommandContext<T> ctx){
        sendFeedback(Component.translatable("rpf.resolver.current", RpfResolversManager.getInstance().getCurrent()));
        return 1;
    }

    private int executeSetCurrentResolver(CommandContext<T> ctx){
        Identifier id = ctx.getArgument("id", Identifier.class);
        if (!RpfResolversManager.getInstance().getAvailable().contains(id)) {
            sendError(Component.translatable("rpf.resolver.unknown", id));
            return 0;
        }
        RpfResolversManager.getInstance().setPendingResolver(id);
        Minecraft.getInstance().reloadResourcePacks();
        Rpf.getConfig().setResolver(id);
        Rpf.getConfig().save();
        sendFeedback(Component.translatable("rpf.resolver.changed", id));
        return 1;
    }

    private CompletableFuture<Suggestions> suggestAvailableResolvers(CommandContext<T> ctx, SuggestionsBuilder b) {
        List<Identifier> list = RpfResolversManager.getInstance().getAvailable();
        list.sort(Comparator.naturalOrder());
        for (Identifier l : list) {
            b.suggest(l.toString());
        }
        return b.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestLoggedItems(CommandContext<T> ctx, SuggestionsBuilder b) {
        List<Identifier> list = RpfDebugSystem.getInstance().getDatabaseKeys();
        for (Identifier l : list) {
            b.suggest(l.toString());
        }
        return b.buildFuture();
    }
    
    private void sendFeedback(MutableComponent component) {
        Minecraft.getInstance().gui.hud.getChat().addClientSystemMessage(component);
    }

    private void sendError(MutableComponent component) {
        Minecraft.getInstance().gui.hud.getChat().addClientSystemMessage(component.setStyle(component.getStyle().withColor(Color.RED.getRGB())));
    }

    private LiteralArgumentBuilder<T> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    private <C> RequiredArgumentBuilder<T, C> argument(String name, ArgumentType<C> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

}
