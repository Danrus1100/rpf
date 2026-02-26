package com.danrus.rpf.impl;

import com.danrus.rpf.api.AbstractTestResultCollector;
import com.danrus.rpf.api.RpfItemModel;
import com.danrus.rpf.api.RpfItemModelResolver;
import com.danrus.rpf.api.TestsResultCollector;
import com.danrus.rpf.core.item.ModelUpdateContext;
import com.danrus.rpf.core.item.SignedItemModel;
import com.danrus.rpf.duck.load.RpfModelManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.renderer.item.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RpfExperimentalResolver implements RpfItemModelResolver {

    private static final TestsResultCollector DUMMY_COLLECTOR = new DummyTestsResultsCollector();


    @Override
    public void resolveAndAppendLayer(ModelUpdateContext context, ItemStack stack, ItemOwner entity, Operation<Void> vanilla) {

        RpfModelManager rpfModelManager = RpfItemModelResolver.getModelManager();
        List<Map<Identifier, SignedItemModel>> packs = rpfModelManager.rpf$getSignedModels();

        List<SignedItemModel> candidates = new ArrayList<>();
        for (Map<Identifier, SignedItemModel> currentPack : packs) {
            SignedItemModel model = currentPack.get(context.location());
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
                    model.update(context, stack, entity);
                    return;
                }
                ExperimentalModelTestCollector collector = new ExperimentalModelTestCollector(context.location(), model.name());
                collector.resetShift();
                model.doDelegate(context, stack, entity, collector);
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
            RpfItemModelResolver.updateMissingModel(context, DUMMY_COLLECTOR, stack, entity);
            return;
        }

        RpfItemModelResolver.appendModelLayer(context, stack, entity, modelToUpdate);
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

        public ExperimentalModelTestCollector(Identifier modelLocation, String packName) {
            super(modelLocation, packName);
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
        public void touch(@Nullable Class<?> clazz, String description, TestResultType resultType) {
            eUnits.add(new ExperimentalResultUnit(clazz, description, this.packName, resultType, currentShift));
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
