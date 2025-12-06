package com.danrus.rpf.mixin;

import com.danrus.rpf.RpfClientItemInfoLoader;
import com.danrus.rpf.duck.RpfBakingResult;
import com.danrus.rpf.duck.RpfItemModel;
import com.danrus.rpf.duck.RpfModelBakery;
import com.danrus.rpf.duck.RpfModelManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SpecialBlockModelRenderer;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.Zone;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(value = ModelManager.class, priority = 2000)
public abstract class ModelManagerMixin implements RpfModelManager {

    @Unique
    private static final Logger LOGGER = LoggerFactory.getLogger("RpfModelManager");

    @Unique
    private List<Map<ResourceLocation, ItemModel>> rpf$bakedItemStackModels;

    @Unique
    private static CompletableFuture<List<RpfClientItemInfoLoader.LoadedClientInfos>> rpf$currentItemLayersFuture;

    @Unique
    private static final ClientItemInfoLoader.LoadedClientInfos EMPTY_LOADED_INFOS =
            new ClientItemInfoLoader.LoadedClientInfos(Map.of());

    @Shadow
    private ModelBakery.MissingModels missingModels;

    @Shadow
    @Final
    private AtlasSet atlases;

    @Shadow
    private int maxMipmapLevels;

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
    private static CompletableFuture<ModelManager.ReloadState> loadModels(Map<ResourceLocation, AtlasSet.StitchResult> atlases, ModelBakery modelBakery, Object2IntMap<BlockState> modelGroups, EntityModelSet entityModelSet, SpecialBlockModelRenderer specialBlockModelRenderer, Executor executor) {
        return null;
    }

    @Shadow
    protected abstract void apply(ModelManager.ReloadState reloadState, ProfilerFiller profiler);

    @WrapOperation(
            method = "reload",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/model/ClientItemInfoLoader;scheduleLoad(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"
            )
    )
    private CompletableFuture<ClientItemInfoLoader.LoadedClientInfos> rpf$wrapClientItemInfoLoader(ResourceManager resourceManager, Executor executor, Operation<CompletableFuture<ClientItemInfoLoader.LoadedClientInfos>> original) {
        LOGGER.info("[RPF] Start to schedule items!");
        rpf$currentItemLayersFuture = RpfClientItemInfoLoader.scheduleLoad(resourceManager, executor);
        return CompletableFuture.completedFuture(EMPTY_LOADED_INFOS);
    }

    @WrapOperation(
            method = "net/minecraft/client/resources/model/ModelManager.method_65747(Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/lang/Void;)Lnet/minecraft/client/resources/model/ModelManager$ResolvedModels;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/ModelManager;discoverModelDependencies(Ljava/util/Map;Lnet/minecraft/client/resources/model/BlockStateModelLoader$LoadedModels;Lnet/minecraft/client/resources/model/ClientItemInfoLoader$LoadedClientInfos;)Lnet/minecraft/client/resources/model/ModelManager$ResolvedModels;")
    )
    private static ModelManager.ResolvedModels rpf$wrapDiscovery(Map<ResourceLocation, UnbakedModel> inputModels, BlockStateModelLoader.LoadedModels loadedModels, ClientItemInfoLoader.LoadedClientInfos loadedClientInfos, Operation<ModelManager.ResolvedModels> original) {
        return rpf$discoverModelDependencies(inputModels, loadedModels, rpf$currentItemLayersFuture.join());
    }

    @WrapOperation(
            method = "net/minecraft/client/resources/model/ModelManager.method_65753(Ljava/util/Map;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/CompletableFuture;Ljava/util/concurrent/Executor;Ljava/lang/Void;)Ljava/util/concurrent/CompletionStage;",
            at = @At(value = "NEW", target = "(Lnet/minecraft/client/model/geom/EntityModelSet;Ljava/util/Map;Ljava/util/Map;Ljava/util/Map;Lnet/minecraft/client/resources/model/ResolvedModel;)Lnet/minecraft/client/resources/model/ModelBakery;")
    )
    private static ModelBakery rpf$redirectModelBakeryConstructor(EntityModelSet entityModelSet, Map unbakedBlockStateModels, Map clientInfos, Map resolvedModels, ResolvedModel missingModel, Operation<ModelBakery> original) {
        List<Map<ResourceLocation, ClientItem>> rawLayers = new ArrayList<>();
        for (RpfClientItemInfoLoader.LoadedClientInfos layer : rpf$currentItemLayersFuture.join()) {
            rawLayers.add(layer.contents());
        }
        return ((RpfModelBakery) original.call(entityModelSet, unbakedBlockStateModels, clientInfos, resolvedModels, missingModel)).rpf$setClientItems(rawLayers);
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

            // Регистрируем корни из блоков
            loadedModels.models().values().forEach(modelDiscovery::addRoot);

            // Регистрируем корни из предметов (проходим по всем слоям)
            for (RpfClientItemInfoLoader.LoadedClientInfos layer : itemLayers) {
                layer.contents().values().forEach((clientItem) -> modelDiscovery.addRoot(clientItem.model()));
            }

            return new ModelManager.ResolvedModels(modelDiscovery.missingModel(), modelDiscovery.resolve());
        }
    }

    @Inject(
            method = "getItemModel",
            at = @At("HEAD"),
            cancellable = true
    )
    private void rpf$getItemModel(ResourceLocation modelLocation, CallbackInfoReturnable<ItemModel> cir) {
        if (this.rpf$bakedItemStackModels == null) return;

        ItemModel fallback = null;
        /*
        Несмотря на то что фолбэки определяются правильно, работа логики должна быть измениена в:
           1) SelectItemModel.update (и в RangeSelect тоже надо примерно то же самое)
           2) ItemModelResolver.appendItemLayers

        я думаю, что можно в SelectItemModel передавать ссылку на rpf$bakedItemStackModels, и там если она фолбэчит то брать ItemModel от туда
         */
        for (Map<ResourceLocation, ItemModel> bakedItemStackModels : rpf$bakedItemStackModels) {
            ItemModel model = bakedItemStackModels.get(modelLocation);
            if (model != null && !((RpfItemModel) model).rpf$isFallback()) {
                cir.setReturnValue(model);
                return;
            }
            if (model != null && ((RpfItemModel) model).rpf$isFallback() && fallback == null) {
                fallback = model;
            }
        }
        if (fallback != null) {
            cir.setReturnValue(fallback);
        }
    }

    @Inject(
            method = "apply",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/ModelBakery$BakingResult;itemStackModels()Ljava/util/Map;")
    )
    private void rpf$apply(ModelManager.ReloadState reloadState, ProfilerFiller profiler, CallbackInfo ci, @Local ModelBakery.BakingResult bakingResult) {
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
}
