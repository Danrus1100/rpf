package com.danrus.rpf;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.multiplayer.ClientRegistryLayer;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.PlaceholderLookupProvider;
import net.minecraft.util.StrictJsonParser;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class RpfClientItemInfoLoader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final FileToIdConverter LISTER = FileToIdConverter.json("items");

    // === 1. Основной входной метод ===
    public static CompletableFuture<List<LoadedClientInfos>> scheduleLoad(ResourceManager resourceManager, Executor executor) {
        RegistryAccess.Frozen registryAccess = ClientRegistryLayer.createRegistryAccess().compositeAccess();

        // Используем listMatchingResourceStacks вместо listMatchingResources
        return CompletableFuture.supplyAsync(() -> LISTER.listMatchingResourceStacks(resourceManager), executor)
                .thenCompose(stacks -> loadAllStacks(stacks, registryAccess, executor))
                .thenApply(RpfClientItemInfoLoader::buildLayeredInfos);
    }

    // === 2. Оркестратор загрузки всех стеков ===
    private static CompletableFuture<List<PendingStack>> loadAllStacks(
            Map<ResourceLocation, List<Resource>> stacks,
            RegistryAccess.Frozen registryAccess,
            Executor executor
    ) {
        List<CompletableFuture<PendingStack>> futures = new ArrayList<>(stacks.size());

        stacks.forEach((fileLocation, resourceStack) -> {
            CompletableFuture<PendingStack> future = CompletableFuture.supplyAsync(
                    () -> processSingleStack(fileLocation, resourceStack, registryAccess),
                    executor
            );
            futures.add(future);
        });

        return Util.sequence(futures);
    }

    // === 3. Обработка одного стека ресурсов (один ID, несколько версий) ===
    private static PendingStack processSingleStack(
            ResourceLocation fileLocation,
            List<Resource> resources,
            RegistryAccess.Frozen registryAccess
    ) {
        ResourceLocation id = LISTER.fileToId(fileLocation);
        List<ClientItem> loadedItems = new ArrayList<>(resources.size());

        for (Resource resource : resources) {
            ClientItem item = parseResource(id, resource, registryAccess);
            if (item != null) {
                loadedItems.add(item);
            }
        }

        return new PendingStack(id, loadedItems);
    }

    // === 4. Парсинг конкретного ресурса ===
    @Nullable
    private static ClientItem parseResource(
            ResourceLocation id,
            Resource resource,
            RegistryAccess.Frozen registryAccess
    ) {
        try (Reader reader = resource.openAsReader()) {
            PlaceholderLookupProvider placeholders = new PlaceholderLookupProvider(registryAccess);
            DynamicOps<JsonElement> ops = placeholders.createSerializationContext(JsonOps.INSTANCE);

            return ClientItem.CODEC
                    .parse(ops, StrictJsonParser.parse(reader))
                    .ifError(error -> LOGGER.error("Couldn't parse item model '{}' from pack '{}': {}",
                            id, resource.sourcePackId(), error.message()))
                    .result()
                    .map(item -> placeholders.hasRegisteredPlaceholders()
                            ? item.withRegistrySwapper(placeholders.createSwapper())
                            : item)
                    .orElse(null);

        } catch (Exception e) {
            LOGGER.error("Failed to open item model {} from pack '{}'", id, resource.sourcePackId(), e);
            return null;
        }
    }

    // === 5. Сборка результатов в слои ===
    private static List<LoadedClientInfos> buildLayeredInfos(List<PendingStack> loadedStacks) {
        // Определяем максимальную глубину стека
        int maxDepth = 0;
        for (PendingStack stack : loadedStacks) {
            maxDepth = Math.max(maxDepth, stack.items().size());
        }

        // Создаем список карт (слоев)
        List<Map<ResourceLocation, ClientItem>> layers = new ArrayList<>(maxDepth);
        for (int i = 0; i < maxDepth; i++) {
            layers.add(new HashMap<>());
        }

        // Заполняем слои
        for (PendingStack stack : loadedStacks) {
            List<ClientItem> items = stack.items();
            for (int i = 0; i < items.size(); i++) {
                // i-й элемент кладем в i-й слой
                layers.get(i).put(stack.id(), items.get(i));
            }
        }

        // Оборачиваем каждый слой в LoadedClientInfos
        List<LoadedClientInfos> result = new ArrayList<>(maxDepth);
        for (Map<ResourceLocation, ClientItem> layer : layers) {
            result.add(new LoadedClientInfos(layer));
        }

        return result;
    }

    @Environment(EnvType.CLIENT)
    public record LoadedClientInfos(Map<ResourceLocation, ClientItem> contents) {
    }

    // Вспомогательная запись для хранения списка распаршенных предметов для одного ID
    private record PendingStack(ResourceLocation id, List<ClientItem> items) {
    }
}
