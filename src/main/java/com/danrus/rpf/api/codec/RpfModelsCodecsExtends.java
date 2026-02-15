package com.danrus.rpf.api.codec;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class RpfModelsCodecsExtends {
    private static final RpfModelsCodecsExtends INSTANCE = new RpfModelsCodecsExtends();
    private RpfModelsCodecsExtends() {}
    public static RpfModelsCodecsExtends getInstance() {return INSTANCE;}

    private final Map<ResourceLocation, List<ItemModelCodecExtend<?, ?>>> extensions = new HashMap<>();
    private final Map<MapCodec<?>, MapCodec<?>> codecMap = new IdentityHashMap<>();

    public MapCodec<?> getWrapped(MapCodec<?> original) {
        return codecMap.getOrDefault(original, original);
    }
    public <T, V> void register(ResourceLocation location, MapCodec<V> fieldCodec, BiConsumer<T, V> setter, Function<T, V> getter) {
        extensions.computeIfAbsent(location, l -> new ArrayList<>())
                .add(new ItemModelCodecExtend<>(fieldCodec, setter, getter));
    }

    @SuppressWarnings("unchecked")
    public <T> MapCodec<T> wrap(ResourceLocation location, MapCodec<T> baseCodec) {
        List<ItemModelCodecExtend<?, ?>> list = extensions.get(location);
        if (list == null || list.isEmpty()) {
            return baseCodec;
        }

        RpfCodecBuilder<T> builder = RpfCodecBuilder.of(baseCodec);
        for (ItemModelCodecExtend<?, ?> ext : list) {
            MapCodec<Object> fCodec = (MapCodec<Object>) ext.fieldCodec;
            BiConsumer<T, Object> setter = (BiConsumer<T, Object>) ext.setter;
            Function<T, Object> getter = (Function<T, Object>) ext.getter;

            builder.withField(fCodec, setter, getter);
        }
        MapCodec<T> wrapped = builder.build();
        codecMap.put(baseCodec, wrapped);
        return wrapped;
    }

    private record ItemModelCodecExtend<T, V>(
            MapCodec<V> fieldCodec,
            BiConsumer<T, V> setter,
            Function<T, V> getter
    ) {}
}