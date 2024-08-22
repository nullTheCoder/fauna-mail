package org.antarcticgardens.faunamail;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record SealPayload(List<String> text, String address, String player) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, SealPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(Codec.list(Codec.STRING)), SealPayload::text,
            ByteBufCodecs.STRING_UTF8, SealPayload::address,
            ByteBufCodecs.STRING_UTF8, SealPayload::player,
            SealPayload::new
    );

    public static final ResourceLocation LOCATION = ResourceLocation.tryBuild("fauna_mail", "seal_envelope");

    public static final CustomPacketPayload.Type<SealPayload> ID = new CustomPacketPayload.Type<>(LOCATION);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
