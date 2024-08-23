package org.antarcticgardens.faunamail;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.List;

public record MailPayload(int id) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, MailPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(Codec.INT), MailPayload::id,
            MailPayload::new
    );

    public static final ResourceLocation LOCATION = ResourceLocation.tryBuild("fauna_mail", "mail");

    public static final Type<MailPayload> ID = new Type<>(LOCATION);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
