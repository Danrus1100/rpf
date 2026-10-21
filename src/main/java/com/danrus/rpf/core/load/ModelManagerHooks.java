package com.danrus.rpf.core.load;

import com.danrus.rpf.Rpf;
import com.danrus.rpf.api.event.type.ModelDiscoveryEvent;
import com.danrus.rpf.duck.load.RpfModelBakery;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.ClientItemInfoLoader;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelDiscovery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.cuboid.ItemModelGenerator;
import net.minecraft.client.resources.model.cuboid.MissingCuboidModel;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.Zone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

/**
 * Platform-neutral implementation of the ModelManager/ModelBakery hooks.
 *
 * <p>The actual injection points differ between Fabric and NeoForge (NeoForge 26.3
 * adds standalone models and pending animations to the vanilla signatures), so the
 * platform-specific mixins only capture their arguments and delegate here.</p>
 */
public final class ModelManagerHooks {
    private static final Logger LOGGER = LoggerFactory.getLogger("RpfModelManager");
    private static final ClientItemInfoLoader.LoadedClientInfos EMPTY_LOADED_INFOS =
            new ClientItemInfoLoader.LoadedClientInfos(Map.of());

    private ModelManagerHooks() {}

    public static CompletableFuture<ClientItemInfoLoader.LoadedClientInfos> scheduleClientItems(
            ResourceManager resourceManager,
            Executor executor
    ) {
        Rpf.getResourceLoadManager().setCurrentFuture(RpfClientItemInfoLoader.scheduleLoad(resourceManager, executor));
        return CompletableFuture.completedFuture(EMPTY_LOADED_INFOS);
    }

    public static List<RpfClientItemInfoLoader.LoadedClientInfos> currentItemLayers() {
        CompletableFuture<List<RpfClientItemInfoLoader.LoadedClientInfos>> future =
                Rpf.getResourceLoadManager().getCurrentFuture();
        return future == null ? List.of() : future.join();
    }

    public static void onBakeryCreated(ModelBakery bakery) {
        List<Map<Identifier, ClientItem>> rawLayers = new ArrayList<>();
        for (RpfClientItemInfoLoader.LoadedClientInfos layer : currentItemLayers()) {
            rawLayers.add(layer.contents());
        }
        ((RpfModelBakery) bakery).rpf$setClientItems(rawLayers);
    }

    public static ModelManager.ResolvedModels discoverModelDependencies(
            Map<Identifier, UnbakedModel> blockModels,
            BlockStateModelLoader.LoadedModels loadedModels,
            List<RpfClientItemInfoLoader.LoadedClientInfos> itemLayers,
            List<? extends ResolvableModel> extraRoots,
            Function<ClientItemInfoLoader.LoadedClientInfos, ModelManager.ResolvedModels> original
    ) {
        try (Zone zone = Profiler.get().zone("dependencies")) {
            ModelDiscovery modelDiscovery = new ModelDiscovery(blockModels, MissingCuboidModel.missingModel());
            ModelDiscoveryEvent preEvent = new ModelDiscoveryEvent(
                    ModelDiscoveryEvent.Stage.PRE, modelDiscovery, blockModels, loadedModels, itemLayers
            );
            Rpf.getEventBus().post(preEvent);
            if (preEvent.isCancelled()) {
                LOGGER.info("[RPF] Model discovery cancelled during PRE stage; using flattened item models from the highest-priority packs.");
                return original.apply(flatten(itemLayers));
            }

            modelDiscovery.addSpecialModel(ItemModelGenerator.GENERATED_ITEM_MODEL_ID, new ItemModelGenerator());
            loadedModels.models().values().forEach(modelDiscovery::addRoot);
            for (RpfClientItemInfoLoader.LoadedClientInfos layer : itemLayers) {
                layer.contents().values().forEach(clientItem -> modelDiscovery.addRoot(clientItem.model()));
            }
            extraRoots.forEach(modelDiscovery::addRoot);

            ModelDiscoveryEvent postEvent = new ModelDiscoveryEvent(
                    ModelDiscoveryEvent.Stage.POST, modelDiscovery, blockModels, loadedModels, itemLayers
            );
            Rpf.getEventBus().post(postEvent);
            if (postEvent.isCancelled()) {
                LOGGER.info("[RPF] Model discovery cancelled during POST stage; using flattened item models from the highest-priority packs.");
                return original.apply(flatten(itemLayers));
            }

            return new ModelManager.ResolvedModels(modelDiscovery.missingModel(), modelDiscovery.resolve());
        }
    }

    private static ClientItemInfoLoader.LoadedClientInfos flatten(
            List<RpfClientItemInfoLoader.LoadedClientInfos> itemLayers
    ) {
        Map<Identifier, ClientItem> merged = new HashMap<>();
        for (RpfClientItemInfoLoader.LoadedClientInfos layer : itemLayers) {
            merged.putAll(layer.contents());
        }
        return new ClientItemInfoLoader.LoadedClientInfos(merged);
    }
}
