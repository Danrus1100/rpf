package com.danrus.rpf;

import com.danrus.rpf.api.DelegateItemModel;
import com.danrus.rpf.api.codec.RpfModelsCodecsExtends;
import com.danrus.rpf.duck.item.RpfCompositeModel;
import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;

import java.util.Locale;

public class RpfCodecs {
    private static boolean initialized;

    private RpfCodecs() {}
    public static final Identifier COMPOSITE_ID = Identifier.withDefaultNamespace("composite");
    public static final Identifier RANGE_ID = Identifier.withDefaultNamespace("range_dispatch");
    public static final Identifier SELECT_ID = Identifier.withDefaultNamespace("select");

    static {
        init();
    }

    public static void registerDelegate(Identifier location) {
        RpfModelsCodecsExtends.getInstance().register(
                location,
                Codec.BOOL.optionalFieldOf("delegate", true),
                (model, val) -> ((DelegateItemModel.Unbaked) model).rpf$setDeligation(val),
                (model) -> ((DelegateItemModel.Unbaked) model).rpf$getDelegation()
        );
    }

    // if you want to add your own extends for codec, the best idea to mixin your codec here
    public static void init() {
        if (initialized) return;
        initialized = true;

        registerDelegate(SELECT_ID);
        registerDelegate(RANGE_ID);

        RpfModelsCodecsExtends.getInstance().register(
                COMPOSITE_ID,
                Codec.STRING.optionalFieldOf("delegate_strategy", "one_do_delegate"),
                (model, val) -> {
                    var strategy = RpfCompositeModel.DelegateStrategy.valueOf(val.toUpperCase(Locale.ROOT));
                    ((RpfCompositeModel.Unbaked) model).rpf$setDelegateStrategy(strategy);
                },
                (model) -> ((RpfCompositeModel.Unbaked) model).rpf$getDelegateStrategy().name().toLowerCase(Locale.ROOT)
        );
    }

}
