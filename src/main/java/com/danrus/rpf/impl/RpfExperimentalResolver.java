//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.danrus.rpf.impl;

import com.danrus.rpf.api.AbstractTestResultCollector;
import com.danrus.rpf.api.RpfItemModel;
import com.danrus.rpf.api.RpfItemModelResolver;
import com.danrus.rpf.api.TestsResultCollector;
import com.danrus.rpf.api.TestsResultCollector.TestResultType;
import com.danrus.rpf.core.item.ModelUpdateContext;
import com.danrus.rpf.core.item.SignedItemModel;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.item.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class RpfExperimentalResolver extends AbstractResolverWithCandidates {
    private static final TestsResultCollector DUMMY_COLLECTOR = new DummyTestsResultsCollector();

    public void resolveAndAppendLayer(ModelUpdateContext context, List<SignedItemModel> candidates, ItemStack stack, LivingEntity entity, Operation<Void> vanilla) {
        int packCounter = 0;
        Map<SignedItemModel, Integer> results = new HashMap(candidates.size());

        for(int i = 0; i < candidates.size(); ++i) {
            try {
                SignedItemModel model = (SignedItemModel)candidates.get(i);
                if (!(model.model() instanceof RpfItemModel)) {
                    model.update(context, stack, entity);
                    return;
                }

                ExperimentalModelTestCollector collector = new ExperimentalModelTestCollector(context.location(), model.info().id());
                collector.resetShift();
                model.doDelegate(context, stack, entity, collector);
                collector.addAdditionalScore(packCounter);
                --packCounter;
                results.put(model, collector.calculateResult());
            } catch (Exception var12) {
            }
        }

        SignedItemModel modelToUpdate = null;
        int highestScore = -999999;

        for(Map.Entry<SignedItemModel, Integer> entry : results.entrySet()) {
            if ((Integer)entry.getValue() > highestScore) {
                highestScore = (Integer)entry.getValue();
                modelToUpdate = (SignedItemModel)entry.getKey();
            }
        }

        if (modelToUpdate == null) {
            RpfItemModelResolver.updateMissingModel(context, DUMMY_COLLECTOR, stack, entity);
        } else {
            RpfItemModelResolver.appendModelLayer(context, stack, entity, modelToUpdate);
        }
    }

    public boolean shouldPlaySwapAnimation(ItemStack stack, Operation<Boolean> vanilla) {
        return true;
    }

    private static class ExperimentalModelTestCollector extends AbstractTestResultCollector {
        private static final Map<Class<?>, Integer> REWARDS_BY_CLASS = Map.of(
                //~ block_rename
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
        private static final Map<TestsResultCollector.TestResultType, Integer> REWARDS_BY_RESULT;
        protected final List<ExperimentalResultUnit> eUnits = new LinkedList();

        public ExperimentalModelTestCollector(ResourceLocation modelLocation, String packName) {
            super(modelLocation, packName);
        }

        public void addAdditionalScore(int value) {
            this.eUnits.add(new ExperimentalResultUnit((Class)null, "", "", TestResultType.INFO, 0, value));
        }

        public int calculateResult() {
            int result = 0;

            for(ExperimentalResultUnit unit : this.eUnits) {
                result += unit.score;
            }

            return result;
        }

        public void touch(@Nullable Class<?> clazz, String description, TestsResultCollector.TestResultType resultType) {
            this.eUnits.add(new ExperimentalResultUnit(clazz, description, this.packName, resultType, this.currentShift));
        }

        public void touch(String modelType, String description, TestsResultCollector.TestResultType resultType) {
        }

        public List<String> getStringsToLog() {
            List<String> strings = new ArrayList(this.eUnits.size());

            for(ExperimentalResultUnit unit : this.eUnits) {
                strings.add(unit.toString());
            }

            return strings;
        }

        static {
            REWARDS_BY_RESULT = Map.of(TestResultType.ALLOW_UPDATE, 5, TestResultType.DELEGATE, -3, TestResultType.NEXT_TEST, 2, TestResultType.NEXT_TEST_FALLBACK, -2, TestResultType.INFO, 0, TestResultType.ERROR, -5);
        }

        protected static record ExperimentalResultUnit(Class<?> clazz, String itemModelType, String packName, TestsResultCollector.TestResultType resultType, int shift, int score) {
            public ExperimentalResultUnit(Class<?> clazz, String itemModelType, String packName, TestsResultCollector.TestResultType resultType, int shift) {
                this(clazz, itemModelType, packName, resultType, shift, calculateScore(clazz, resultType));
            }

            private static int calculateScore(Class<?> clazz, TestsResultCollector.TestResultType resultType) {
                return (Integer)RpfExperimentalResolver.ExperimentalModelTestCollector.REWARDS_BY_CLASS.get(clazz) + (Integer)RpfExperimentalResolver.ExperimentalModelTestCollector.REWARDS_BY_RESULT.get(resultType);
            }
        }
    }
}
