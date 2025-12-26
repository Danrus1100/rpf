package com.danrus.rpf.mixin.load;

import com.danrus.rpf.Rpf;
import com.danrus.rpf.RpfClientItemInfoLoader;
import com.danrus.rpf.compat.rprenames.impl.RenamesBridge;
import com.danrus.rpf.duck.load.RpfBakingResult;
import com.danrus.rpf.duck.load.RpfModelBakery;
import com.danrus.rpf.duck.load.RpfModelManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SpecialBlockModelRenderer;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.Zone;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(value = ModelManager.class, priority = 2000)
public abstract class ModelManagerMixin implements RpfModelManager {

    @Unique
    private static final Logger LOGGER = LoggerFactory.getLogger("RpfModelManager");

    @Unique
    private List<Map<ResourceLocation, ItemModel>> rpf$bakedItemStackModels;

    @Unique
    private static final ClientItemInfoLoader.LoadedClientInfos EMPTY_LOADED_INFOS =
            new ClientItemInfoLoader.LoadedClientInfos(Map.of());

    @Shadow
    private ModelBakery.MissingModels missingModels;

    //? if <= 1.21.8 {
    @Shadow
    @Final
    private AtlasSet atlases;
    //? } else {
    /*@Shadow
    @Final
    private AtlasManager atlasManager;
    *///?}

    //? if <=1.21.8
    @Shadow private int maxMipmapLevels;

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

//    //? if <=1.21.8 {
//    /*@Shadow
//    private static CompletableFuture<ModelManager.ReloadState> loadModels(Map<ResourceLocation, AtlasSet.StitchResult> atlases, ModelBakery modelBakery, Object2IntMap<BlockState> modelGroups, EntityModelSet entityModelSet, SpecialBlockModelRenderer specialBlockModelRenderer, Executor executor) {
//        return null;
//    }
//    *///? } else {
//    @Shadow
//    private static CompletableFuture<ModelManager.ReloadState> loadModels(final SpriteLoader.Preparations preperations, ModelBakery modelBakery, Object2IntMap<BlockState> modelGroups, EntityModelSet entityModelSet, SpecialBlockModelRenderer specialBlockModelRenderer, Executor executor) {
//        return null;
//    }
//    //? }

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
        Rpf.rpf$currentItemLayersFuture = RpfClientItemInfoLoader.scheduleLoad(resourceManager, executor);
        return CompletableFuture.completedFuture(EMPTY_LOADED_INFOS);
    }

    @WrapOperation(
            method = "net/minecraft/client/resources/model/ModelManager.method_65747(Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/lang/Void;)Lnet/minecraft/client/resources/model/ModelManager$ResolvedModels;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/ModelManager;discoverModelDependencies(Ljava/util/Map;Lnet/minecraft/client/resources/model/BlockStateModelLoader$LoadedModels;Lnet/minecraft/client/resources/model/ClientItemInfoLoader$LoadedClientInfos;)Lnet/minecraft/client/resources/model/ModelManager$ResolvedModels;")
    )
    private static ModelManager.ResolvedModels rpf$wrapDiscovery(Map<ResourceLocation, UnbakedModel> inputModels, BlockStateModelLoader.LoadedModels loadedModels, ClientItemInfoLoader.LoadedClientInfos loadedClientInfos, Operation<ModelManager.ResolvedModels> original) {
        return rpf$discoverModelDependencies(inputModels, loadedModels, Rpf.rpf$currentItemLayersFuture.join());
    }

    @Unique
    private static final String redirectModelBakeryConstructorTarget =
            //? if 1.21.8
            "net/minecraft/client/resources/model/ModelManager.method_65753(Ljava/util/Map;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/Executor;Ljava/lang/Void;)Ljava/util/concurrent/CompletionStage;";
            //? if 1.21.10
            //"net/minecraft/client/resources/model/ModelManager.method_65753(Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/Executor;Ljava/lang/Void;)Ljava/util/concurrent/CompletionStage;";
            //? if 1.21.11
            //"net/minecraft/client/resources/model/ModelManager.method_65753(Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/Executor;Ljava/lang/Void;)Ljava/util/concurrent/CompletionStage;";

    @Unique
    private static final String redirectModelBakeryConstructorBakeryTarget =
            //? if 1.21.8
            "(Lnet/minecraft/client/model/geom/EntityModelSet;Ljava/util/Map;Ljava/util/Map;Ljava/util/Map;Lnet/minecraft/client/resources/model/ResolvedModel;)Lnet/minecraft/client/resources/model/ModelBakery;";
            //? if >=1.21.10
            //"(Lnet/minecraft/client/model/geom/EntityModelSet;Lnet/minecraft/client/resources/model/MaterialSet;Lnet/minecraft/client/renderer/PlayerSkinRenderCache;Ljava/util/Map;Ljava/util/Map;Ljava/util/Map;Lnet/minecraft/client/resources/model/ResolvedModel;)Lnet/minecraft/client/resources/model/ModelBakery;";

    @WrapOperation(
            method = redirectModelBakeryConstructorTarget,
            at = @At(value = "NEW", target = redirectModelBakeryConstructorBakeryTarget)
    )
    private static ModelBakery rpf$redirectModelBakeryConstructor
            //? if 1.21.8
            (EntityModelSet entityModelSet, Map unbakedBlockStateModels, Map clientInfos, Map resolvedModels, ResolvedModel missingModel, Operation<ModelBakery> original)
            //? if >=1.21.10
            //(EntityModelSet entityModelSet, MaterialSet materials, net.minecraft.client.renderer.PlayerSkinRenderCache playerSkinRenderCache, Map unbakedBlockStateModels, Map clientInfos, Map resolvedModels, ResolvedModel missingModel, Operation<ModelBakery> original)
    {
        List<Map<ResourceLocation, ClientItem>> rawLayers = new ArrayList<>();
        for (RpfClientItemInfoLoader.LoadedClientInfos layer : Rpf.rpf$currentItemLayersFuture.join()) {
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
                //? if >=1.21.10
                //(entityModelSet, materials, playerSkinRenderCache, unbakedBlockStateModels, clientInfos, resolvedModels, missingModel)
        ).rpf$setClientItems(rawLayers);
    }

    @Unique
    private static ModelManager.ResolvedModels rpf$discoverModelDependencies(
            Map<ResourceLocation, UnbakedModel> blockModels,
            BlockStateModelLoader.LoadedModels loadedModels,
            List<RpfClientItemInfoLoader.LoadedClientInfos> itemLayers
    ) {
        try (Zone zone = Profiler.get().zone("dependencies")) {
            ModelDiscovery modelDiscovery = new ModelDiscovery(blockModels, MissingBlockModel.missingModel());
            modelDiscovery.addSpecialModel(ItemModelGenerator.GENERATED_ITEM_MODEL_ID, new ItemModelGenerator());

            loadedModels.models().values().forEach(modelDiscovery::addRoot);

            for (RpfClientItemInfoLoader.LoadedClientInfos layer : itemLayers) {
                layer.contents().values().forEach((clientItem) -> modelDiscovery.addRoot(clientItem.model()));
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
            this.rpf$bakedItemStackModels = ((RpfBakingResult) (Object) bakingResult).rpf$geItemModels().reversed(); // "reversed" to put vanilla RP down of list
        } catch (ClassCastException e) {
            throw new IllegalStateException("ModelBakery.BakingResult bakingResult is not instance of RpfBakingResult!");
        } catch (Exception e2) {
            throw new RuntimeException("Unknown error! {}", e2);
        }
    }

    @Override
    public List<Map<ResourceLocation, ItemModel>> rpf$getModelMaps() {
        return this.rpf$bakedItemStackModels;
    }

    @Override
    public @Nullable ItemModel rpf$saveGetNextModel(int currentIndex, ResourceLocation location) {
        if (this.rpf$bakedItemStackModels == null) {
            return null;
        }
        int nextIndex = currentIndex + 1;
        if (nextIndex >= this.rpf$bakedItemStackModels.size()) {
            return null;
        }
        Map<ResourceLocation, ItemModel> nextLayer = this.rpf$bakedItemStackModels.get(nextIndex);
        ItemModel model = nextLayer.get(location);
        if (model == null) {
            return null;
        }
        return model;
    }
}
