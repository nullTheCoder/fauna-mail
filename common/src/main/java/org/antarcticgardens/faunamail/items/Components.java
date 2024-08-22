package org.antarcticgardens.faunamail.items;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Tuple;
import net.minecraft.util.Unit;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Components {

    public static DataComponentType<?> SEALED = DataComponentType.builder().persistent(
            Codec.unit(Unit.INSTANCE)
    ).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)).build();

}
