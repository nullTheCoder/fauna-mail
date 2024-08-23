package org.antarcticgardens.faunamail.items;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class Components {

    public static DataComponentType<Boolean> SEALED = new DataComponentType.Builder<Boolean>().persistent(
            Codec.BOOL
    ).networkSynchronized(ByteBufCodecs.fromCodec(Codec.BOOL)).build();

    public static DataComponentType<Boolean> USED = new DataComponentType.Builder<Boolean>().persistent(
            Codec.BOOL
    ).networkSynchronized(ByteBufCodecs.fromCodec(Codec.BOOL)).build();

    public static DataComponentType<List<ItemStack>> ITEMS = new DataComponentType.Builder<List<ItemStack>>().persistent(
            Codec.list(ItemStack.CODEC)
    ).build();

    public static DataComponentType<List<String>> TEXT = new DataComponentType.Builder<List<String>>().persistent(
            Codec.list(Codec.STRING)
    ).networkSynchronized(ByteBufCodecs.fromCodec(Codec.list(Codec.STRING))).build();

    public static DataComponentType<String> ADDRESS = new DataComponentType.Builder<String>().persistent(
            Codec.STRING
    ).networkSynchronized(ByteBufCodecs.fromCodec(Codec.STRING)).build();

    public static DataComponentType<String> PLAYER = new DataComponentType.Builder<String>().persistent(
            Codec.STRING
    ).networkSynchronized(ByteBufCodecs.fromCodec(Codec.STRING)).build();

}
