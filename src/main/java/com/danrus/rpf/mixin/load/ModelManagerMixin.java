package com.danrus.rpf.mixin.load;

import com.danrus.rpf.Rpf;
import com.danrus.rpf.api.event.type.ModelDiscoveryEvent;
import com.danrus.rpf.core.item.RpfResolversManager;
import com.danrus.rpf.core.load.ResourceLoadManager;
import com.danrus.rpf.core.load.RpfClientItemInfoLoader;
import com.danrus.rpf.compat.rprenames.RenamesBridge;
import com.danrus.rpf.core.item.RpfModelIdentity;
import com.danrus.rpf.core.item.SignedItemModel;
import com.danrus.rpf.debug.RpfDebugSystem;
import com.danrus.rpf.duck.load.RpfBakingResult;
import com.danrus.rpf.duck.load.RpfModelBakery;
import com.danrus.rpf.duck.load.RpfModelManager;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.model.geom.EntityModelSet;
//? <26.1
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
//? >=26.1
//import net.minecraft.client.resources.model.cuboid.ItemModelGenerator;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.*;
//? <26.1
import net.minecraft.client.resources.model.SpriteGetter;
//? >=26.1
//import net.minecraft.client.resources.model.cuboid.MissingBlockModel;
//? >=26.1
//import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.Zone;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(value = ModelManager.class)
public abstract class ModelManagerMixin implements RpfModelManager {

    @Unique
    private static final Logger LOGGER = LoggerFactory.getLogger("RpfModelManager");

    @Unique
//    private List<Map<ResourceLocation, ItemModel>> rpf$bakedItemStackModels;
    private List<Map<ResourceLocation, SignedItemModel>> rpf$bakedItemStackSignetModels;

    @Unique
    private List<Map<ResourceLocation, ClientItem.Properties>> rpf$itemProperties;

    @Unique
    private Map<RpfModelIdentity, ClientItem.Properties> rpf$itemPropertiesByIdentity;

    @Unique
    private static final ClientItemInfoLoader.LoadedClientInfos EMPTY_LOADED_INFOS =
            new ClientItemInfoLoader.LoadedClientInfos(Map.of());

    @Shadow
    private ModelBakery.MissingModels missingModels;

    // The ModelManager atlas fields moved around between versions; this mixin doesn't use them.

    @Shadow
    @Final
    private BlockColors blockColors;

    @Shadow
    private static CompletableFuture<Map<ResourceLocation, UnbakedModel>> loadBlockModels(ResourceManager resourceManager, Executor executor) {
        return null; // Shadowed implementation
    }

    @Shadow
    private static Object2IntMap<BlockState> buildModelGroups(BlockColors blockColors, BlockStateModelLoader.LoadedModels loadedModels) {
        return null; // Shadowed implementation
    }

    @Shadow
    protected abstract void apply(ModelManager.ReloadState reloadState
            //? if <=1.21.8
            , ProfilerFiller profiler
    );

    @WrapOperation(
            method = "reload",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/model/ClientItemInfoLoader;scheduleLoad(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"
            )
    )
    private CompletableFuture<ClientItemInfoLoader.LoadedClientInfos> rpf$wrapClientItemInfoLoader(ResourceManager resourceManager, Executor executor, Operation<CompletableFuture<ClientItemInfoLoader.LoadedClientInfos>> original) {
        LOGGER.info("[RPF] Start to schedule items!");
        Rpf.getResourceLoadManager().setCurrentFuture(RpfClientItemInfoLoader.scheduleLoad(resourceManager, executor));
        return CompletableFuture.completedFuture(EMPTY_LOADED_INFOS);
    }

    @WrapOperation(
            method =
                    //? <26.1 {
                    "net/minecraft/client/resources/model/ModelManager.method_65747(Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/lang/Void;)Lnet/minecraft/client/resources/model/ModelManager$ResolvedModels;",
                    //?} else {
                    /*"lambda$reload$1",
                    *///?}
            at = @At(value = "INVOKE", target =
                    "Lnet/minecraft/client/resources/model/ModelManager;discoverModelDependencies(Ljava/util/Map;Lnet/minecraft/client/resources/model/BlockStateModelLoader$LoadedModels;Lnet/minecraft/client/resources/model/ClientItemInfoLoader$LoadedClientInfos;)Lnet/minecraft/client/resources/model/ModelManager$ResolvedModels;")
    )
    private static ModelManager.ResolvedModels rpf$wrapDiscovery(Map<ResourceLocation, UnbakedModel> inputModels, BlockStateModelLoader.LoadedModels loadedModels, ClientItemInfoLoader.LoadedClientInfos loadedClientInfos, Operation<ModelManager.ResolvedModels> original) {
        return rpf$discoverModelDependencies(inputModels, loadedModels, Rpf.getResourceLoadManager().getCurrentFuture().join(), () -> original.call(inputModels, loadedModels, loadedClientInfos));
    }

    @Unique
    private static final String redirectModelBakeryConstructorTarget =
            //? if 1.21.8
            "net/minecraft/client/resources/model/ModelManager.method_65753(Ljava/util/Map;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/Executor;Ljava/lang/Void;)Ljava/util/concurrent/CompletionStage;";
            //? if 1.21.10
            //"net/minecraft/client/resources/model/ModelManager.method_65753(Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/Executor;Ljava/lang/Void;)Ljava/util/concurrent/CompletionStage;";
            //? if 1.21.11
            //"net/minecraft/client/resources/model/ModelManager.method_65753(Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/Executor;Ljava/lang/Void;)Ljava/util/concurrent/CompletionStage;";
            //? if 26.1
            //"lambda$reload$4";

    @Unique
    private static final String redirectModelBakeryConstructorBakeryTarget =
            //? if 1.21.8
            "(Lnet/minecraft/client/model/geom/EntityModelSet;Ljava/util/Map;Ljava/util/Map;Ljava/util/Map;Lnet/minecraft/client/resources/model/ResolvedModel;)Lnet/minecraft/client/resources/model/ModelBakery;";
            //? if >=1.21.10 && <26.1
            //"(Lnet/minecraft/client/model/geom/EntityModelSet;Lnet/minecraft/client/resources/model/MaterialSet;Lnet/minecraft/client/renderer/PlayerSkinRenderCache;Ljava/util/Map;Ljava/util/Map;Ljava/util/Map;Lnet/minecraft/client/resources/model/ResolvedModel;)Lnet/minecraft/client/resources/model/ModelBakery;";
            //? if >=26.1
            //"(Lnet/minecraft/client/model/geom/EntityModelSet;Lnet/minecraft/client/resources/model/sprite/SpriteGetter;Lnet/minecraft/client/renderer/PlayerSkinRenderCache;Ljava/util/Map;Ljava/util/Map;Ljava/util/Map;Lnet/minecraft/client/resources/model/ResolvedModel;)Lnet/minecraft/client/resources/model/ModelBakery;";

    @WrapOperation(
            method = redirectModelBakeryConstructorTarget,
            at = @At(value = "NEW", target = redirectModelBakeryConstructorBakeryTarget)
    )
    private static ModelBakery rpf$redirectModelBakeryConstructor
            //? if 1.21.8
            (EntityModelSet entityModelSet, Map unbakedBlockStateModels, Map clientInfos, Map resolvedModels, ResolvedModel missingModel, Operation<ModelBakery> original)
            //? if >=1.21.10 && <26.1
            //(EntityModelSet entityModelSet, MaterialSet materials, net.minecraft.client.renderer.PlayerSkinRenderCache playerSkinRenderCache, Map unbakedBlockStateModels, Map clientInfos, Map resolvedModels, ResolvedModel missingModel, Operation<ModelBakery> original)
            //? if >=26.1
            //(final EntityModelSet entityModelSet, final SpriteGetter sprites, final net.minecraft.client.renderer.PlayerSkinRenderCache playerSkinRenderCache, final Map<BlockState, net.minecraft.client.renderer.block.dispatch.BlockStateModel.UnbakedRoot> unbakedBlockStateModels, final Map<ResourceLocation, ClientItem> clientInfos, final Map<ResourceLocation, ResolvedModel> resolvedModels, final ResolvedModel missingModel, Operation<ModelBakery> original)
    {
        List<Map<ResourceLocation, ClientItem>> rawLayers = new ArrayList<>();
        for (RpfClientItemInfoLoader.LoadedClientInfos layer : Rpf.getResourceLoadManager().getCurrentFuture().join()) {
            rawLayers.add(layer.contents());
        }
        if (RenamesBridge.active) {
            RenamesBridge.itemSetter.accept(rawLayers);
            RenamesBridge.parser.accept(
                    Minecraft.getInstance().getResourceManager(),
                    Profiler.get()
            );
            RenamesBridge.update();
        }
        return ((RpfModelBakery) original.call
                //? if 1.21.8
                (entityModelSet, unbakedBlockStateModels, clientInfos, resolvedModels, missingModel)
                //? if >=1.21.10 && <26.1
                //(entityModelSet, materials, playerSkinRenderCache, unbakedBlockStateModels, clientInfos, resolvedModels, missingModel)
                //? if >=26.1
                //(entityModelSet, sprites, playerSkinRenderCache, unbakedBlockStateModels, clientInfos, resolvedModels, missingModel)
        ).rpf$setClientItems(rawLayers);
    }

    @Unique
    private static ModelManager.ResolvedModels rpf$discoverModelDependencies(
            Map<ResourceLocation, UnbakedModel> blockModels,
            BlockStateModelLoader.LoadedModels loadedModels,
            List<RpfClientItemInfoLoader.LoadedClientInfos> itemLayers,
            Supplier<ModelManager.ResolvedModels> originalDiscoverModelDependencies
    ) {
        try (Zone zone = Profiler.get().zone("dependencies")) {
            ModelDiscovery modelDiscovery = new ModelDiscovery(blockModels, MissingBlockModel.missingModel());
            ModelDiscoveryEvent preEvent = new ModelDiscoveryEvent(ModelDiscoveryEvent.Stage.PRE, modelDiscovery, blockModels, loadedModels, itemLayers);
            Rpf.getEventBus().post(preEvent);
            if (preEvent.isCancelled()) {
                LOGGER.info("[RPF] Model discovery was canceled during PRE stage, skipping discovery and using vanilla models only.");
                return originalDiscoverModelDependencies.get();
            }
            modelDiscovery.addSpecialModel(ItemModelGenerator.GENERATED_ITEM_MODEL_ID, new ItemModelGenerator());

            loadedModels.models().values().forEach(modelDiscovery::addRoot);

            for (RpfClientItemInfoLoader.LoadedClientInfos layer : itemLayers) {
                layer.contents().values().forEach((clientItem) -> modelDiscovery.addRoot(clientItem.model()));
            }

            ModelDiscoveryEvent postEvent = new ModelDiscoveryEvent(ModelDiscoveryEvent.Stage.POST, modelDiscovery, blockModels, loadedModels, itemLayers);
            Rpf.getEventBus().post(postEvent);
            if (postEvent.isCancelled()) {
                LOGGER.info("[RPF] Model discovery was canceled during POST stage, skipping discovery and using vanilla models only.");
                return originalDiscoverModelDependencies.get();
            }

            return new ModelManager.ResolvedModels(modelDiscovery.missingModel(), modelDiscovery.resolve());
        }
    }

    @Inject(
            method = "apply",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/ModelBakery$BakingResult;itemStackModels()Ljava/util/Map;")
    )
    private void rpf$apply(
            ModelManager.ReloadState reloadState,
            //? if <= 1.21.8
            ProfilerFiller profiler,
            CallbackInfo ci,
            @Local ModelBakery.BakingResult bakingResult
    ) {
        try {
            RpfBakingResult result = ((RpfBakingResult) (Object) bakingResult);
            this.rpf$bakedItemStackSignetModels = new ArrayList<>(result.rpf$getItemSignedModels().reversed());// "reversed" to put vanilla RP down of list
            this.rpf$itemProperties = new ArrayList<>(result.rpf$getItemPropertiesById().reversed());
            this.rpf$itemPropertiesByIdentity = result.rpf$getItemPropertiesByIdentity();
            
            // Clear vanilla model cache on resource reload
            try {
                ResourceLoadManager.vanillaModelCache.clear();
            } catch (Exception e) {
                LOGGER.warn("Failed to do stuff with vanilla cache: ", e);
            }
            
            RpfResolversManager.getInstance().applyPendingResolver();
            RpfDebugSystem.getInstance().clear();
        } catch (ClassCastException e) {
            throw new IllegalStateException("ModelBakery.BakingResult bakingResult is not instance of RpfBakingResult!");
        } catch (Exception e2) {
            throw new RuntimeException("Unknown error! ", e2);
        }
    }

    @WrapMethod(
            method = "getItemModel"
    )
    private ItemModel rpf$wrapGetItemModel(ResourceLocation modelLocation, Operation<ItemModel> original) {
        for (Map<ResourceLocation, SignedItemModel> layer : this.rpf$bakedItemStackSignetModels) {
            SignedItemModel model = layer.get(modelLocation);
            if (model != null) {
                return model.model();
            }
        }
        return original.call(modelLocation);
    }

    @Override
    public List<Map<ResourceLocation, SignedItemModel>> rpf$getSignedModels() {
        return rpf$bakedItemStackSignetModels;
    }

    @Override
    public List<Map<ResourceLocation, ClientItem.Properties>> rpf$getItemPropertiesMaps() {
        return this.rpf$itemProperties;
    }

    @Override
    public SignedItemModel rpf$getVanillaModel(ResourceLocation location) {
        // Check cache first for O(1) lookup
        SignedItemModel cached = ResourceLoadManager.vanillaModelCache.get(location);
        if (cached != null) {
            return cached;
        }

        // Cache miss - search through packs
        for (Map<ResourceLocation, SignedItemModel> map : rpf$getSignedModels()) {
            SignedItemModel model = map.get(location);
            if (model != null && "vanilla".equals(model.name())) {
                // Cache the result before returning
                ResourceLoadManager.vanillaModelCache.put(location, model);
                return model;
            }
        }
        return null;
    }

    @Override
    @NotNull
    public ClientItem.Properties rpf$getProperties(RpfModelIdentity identity) {
        ClientItem.Properties properties = rpf$itemPropertiesByIdentity.get(identity);
        if (properties == null) {
            properties = ClientItem.Properties.DEFAULT;
        }
        return properties;
    }

    @Override
    public ItemModel rpf$getMissingModel() {
        return missingModels.item();
    }
}
