package org.antarcticgardens.faunamail;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import java.util.logging.Level;

public record LevelBlockPos(BlockPos pos, ServerLevel level) {

    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("x", pos.getX());
        tag.putInt("y", pos.getY());
        tag.putInt("z", pos.getZ());
        tag.putString("level", level.dimension().location().toString());
        return tag;
    }

    public static LevelBlockPos fromNbt(CompoundTag tag, MinecraftServer server) {
        try {
            return new LevelBlockPos(
                    new BlockPos(
                            tag.getInt("x"),
                            tag.getInt("y"),
                            tag.getInt("z")
                    ), server.getLevel(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(tag.getString("level"))))
            );
        } catch (Exception e) {
            return null;
        }
    }

}
