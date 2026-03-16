//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.danrus.rpf.impl;

import com.danrus.rpf.api.RpfItemModelResolver;
import com.danrus.rpf.compat.mtd.MtdBridge;
import com.danrus.rpf.core.item.ModelUpdateContext;
import com.danrus.rpf.core.item.SignedItemModel;
import com.danrus.rpf.duck.load.RpfModelManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractResolverWithCandidates implements RpfItemModelResolver {
    public void resolveAndAppendLayer(ModelUpdateContext context, ItemStack stack, LivingEntity holder, Operation<Void> vanilla) {
        MtdBridge.capturer.captureEntity(stack, holder == null ? null : holder, context.renderState());
        RpfModelManager rpfModelManager = RpfItemModelResolver.getModelManager();
        List<Map<ResourceLocation, SignedItemModel>> packs = rpfModelManager.rpf$getSignedModels();
        List<SignedItemModel> candidates = new ArrayList<>();

        for(Map<ResourceLocation, SignedItemModel> currentPack : packs) {
            SignedItemModel model = currentPack.get(context.location());
            if (model != null) {
                candidates.add(model);
            }
        }

        this.resolveAndAppendLayer(context, candidates, stack, holder, vanilla);
    }

    abstract void resolveAndAppendLayer(ModelUpdateContext context, List<SignedItemModel> candidates, ItemStack stack, LivingEntity holder, Operation<Void> vanilla);
}
