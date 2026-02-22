package com.danrus.rpf.api.codec;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class RpfCodecBuilder<T> {
    private MapCodec<T> currentCodec;

    private RpfCodecBuilder(MapCodec<T> base) {
        this.currentCodec = base;
    }

    public static <T> RpfCodecBuilder<T> of(MapCodec<T> base) {
        return new RpfCodecBuilder<>(base);
    }

    /**
     * adds a field to the codec, using the provided getter and setter to read and write the value from the model
     */
    public <V> RpfCodecBuilder<T> withField(MapCodec<V> fieldCodec, BiConsumer<T, V> setter, Function<T, V> getter) {
        final MapCodec<T> captured = this.currentCodec;

        this.currentCodec = RecordCodecBuilder.mapCodec(inst -> inst.group(
                captured.forGetter(t -> t),
                fieldCodec.forGetter(getter)
        ).apply(inst, (model, value) -> {
            setter.accept(model, value);
            return model;
        }));
        return this;
    }

    public MapCodec<T> build() {
        return currentCodec;
    }
}
