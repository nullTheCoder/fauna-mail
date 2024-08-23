package org.antarcticgardens.faunamail;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record UnsealPayload() implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, UnsealPayload> CODEC = StreamCodec.unit(new UnsealPayload());

    public static final ResourceLocation LOCATION = ResourceLocation.tryBuild("fauna_mail", "unseal_envelope");

    public static final Type<UnsealPayload> ID = new Type<>(LOCATION);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
