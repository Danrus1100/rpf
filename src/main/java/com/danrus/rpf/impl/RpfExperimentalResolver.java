package com.danrus.rpf.impl;

import com.danrus.rpf.api.AbstractTestResultCollector;
import com.danrus.rpf.api.RpfItemModel;
import com.danrus.rpf.api.RpfItemModelResolver;
import com.danrus.rpf.api.TestsResultCollector;
import com.danrus.rpf.core.item.RpfModelIdentity;
import com.danrus.rpf.core.item.SignedItemModel;
import com.danrus.rpf.duck.load.RpfModelManager;
import com.danrus.rpf.logging.DummyTestsResultsCollector;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.*;

public class RpfExperimentalResolver implements RpfItemModelResolver {

    private static final TestsResultCollector DUMMY_COLLECTOR = new DummyTestsResultsCollector();


    @Override
    public void resolveAndAppendLayer(ItemStackRenderState renderState, ItemStack stack, ItemDisplayContext displayContext, Level level, LivingEntity entity, int seed, Operation<Void> vanilla, ItemModelResolver mcResolver) {
        ResourceLocation resourceLocation = stack.get(DataComponents.ITEM_MODEL);
        if (resourceLocation == null) return;

        ClientLevel clientLevel = level instanceof ClientLevel cl ? cl : null;

        RpfModelManager rpfModelManager = RpfItemModelResolver.getModelManager();
        List<Map<ResourceLocation, SignedItemModel>> packs = rpfModelManager.rpf$getSignedModels();

        List<SignedItemModel> candidates = new ArrayList<>();
        for (Map<ResourceLocation, SignedItemModel> currentPack : packs) {
            SignedItemModel model = currentPack.get(resourceLocation);
            if (model != null) {
                candidates.add(model);
            }
        }

        int packCounter = 0;
        Map<SignedItemModel, Integer> results = new HashMap<>(candidates.size());
        for (int i = 0; i < candidates.size(); i++) {
            try {
                SignedItemModel model = candidates.get(i);
                if (!(model.model() instanceof RpfItemModel)) {
                    model.model().update(renderState, stack, mcResolver, displayContext, clientLevel, entity, seed);
                    return;
                }
                ExperimentalModelTestCollector collector = new ExperimentalModelTestCollector(resourceLocation);
                collector.resetShift();
                model.doDelegate(renderState, stack, mcResolver, displayContext, clientLevel, entity, seed, resourceLocation, collector);
                collector.addAdditionalScore(packCounter);
                packCounter -= 1;
                results.put(model, collector.calculateResult());

            } catch (Exception e) {
                // e.printStackTrace(); //TODO: remove
            }
        }
        SignedItemModel modelToUpdate = null;
        int highestScore = -999999;
        for (Map.Entry<SignedItemModel, Integer> entry : results.entrySet()) {
            if (entry.getValue() > highestScore) {
                highestScore = entry.getValue();
                modelToUpdate = entry.getKey();
            }
        }

        if (modelToUpdate == null) {
            RpfV1ModelResolver.updateMissingModel(resourceLocation, rpfModelManager, renderState, DUMMY_COLLECTOR, stack, displayContext, clientLevel, entity, seed, mcResolver);
            return;
        }

        RpfModelIdentity identity = new RpfModelIdentity(resourceLocation, modelToUpdate.name());
        ClientItem.Properties properties = rpfModelManager.rpf$getProperties(identity);
        if (properties == null) {
            properties = ClientItem.Properties.DEFAULT;
        }
        renderState.setOversizedInGui(properties.oversizedInGui());
//        this.componentsToProperties.put(stack.getComponents(), properties);
        renderState.appendModelIdentityElement(identity); // for correct GUI rendering
        modelToUpdate.update(renderState, stack, mcResolver, displayContext, clientLevel, entity, seed);
    }

    @Override
    public boolean shouldPlaySwapAnimation(ItemStack stack, Operation<Boolean> vanilla) {
        return true; //TODO: implement
    }

    private static class ExperimentalModelTestCollector extends AbstractTestResultCollector {

        private static final Map<Class<?>, Integer> REWARDS_BY_CLASS = Map.of(
                BlockModelWrapper.class, 1,
                BundleSelectedItemSpecialRenderer.class, 1,
                CompositeModel.class, 2,
                ConditionalItemModel.class, 3,
                EmptyModel.class, 0,
                MissingItemModel.class, -1,
                RangeSelectItemModel.class, 4,
                SelectItemModel.class, 3,
                SpecialModelWrapper.class, 1
        );

        private static final Map<TestResultType, Integer> REWARDS_BY_RESULT = Map.of(
                TestResultType.ALLOW_UPDATE, 5,
                TestResultType.DELEGATE, -3,
                TestResultType.NEXT_TEST, 2,
                TestResultType.NEXT_TEST_FALLBACK, -2,
                TestResultType.INFO, 0,
                TestResultType.ERROR,-5
        );

        protected final List<ExperimentalResultUnit> eUnits = new LinkedList<>();

        public ExperimentalModelTestCollector(ResourceLocation modelLocation) {
            super(modelLocation);
        }

        @Override
        public void touch(Class<?> clazz, String desc, String packName, TestResultType resultType) {
            eUnits.add(new ExperimentalResultUnit(clazz, desc, packName, resultType, currentShift));
        }

        public void addAdditionalScore(int value) {
            eUnits.add(new ExperimentalResultUnit(null, "", "", TestResultType.INFO, 0, value));
        }


        public int calculateResult() {
            int result = 0;
            for (ExperimentalResultUnit unit : eUnits) {
                result += unit.score;
            }
            return result;
        }

        @Override
        public List<String> getStringsToLog() {
            List<String> strings = new ArrayList<>(eUnits.size());
            for (ExperimentalResultUnit unit : eUnits) {
                strings.add(unit.toString());
            }
            return strings;
        }

        protected record ExperimentalResultUnit(Class<?> clazz ,String itemModelType, String packName, TestResultType resultType, int shift, int score) {
            public ExperimentalResultUnit(Class<?> clazz ,String itemModelType, String packName, TestResultType resultType, int shift) {
                this(clazz, itemModelType, packName, resultType, shift, calculateScore(clazz, resultType));
            }

            private static int calculateScore(Class<?> clazz, TestResultType resultType) {
                return REWARDS_BY_CLASS.get(clazz) + REWARDS_BY_RESULT.get(resultType);
            }
        }
    }
}
