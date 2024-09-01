package org.antarcticgardens.faunamail.mailman;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;

import java.util.UUID;

public class EntityReferenceHelper {

    public record EntityReferenceInfo(ChunkPos pos, UUID uuid, ServerLevel level, MinecraftServer server, BlockPos bPos) {
        public static EntityReferenceInfo fromReference(CompoundTag nbt, MinecraftServer server) {
            UUID uuid = nbt.getUUID("uuid");
            int chunkX = nbt.getInt("chunkX");
            int chunkZ = nbt.getInt("chunkZ");
            ChunkPos pos = new ChunkPos(chunkX, chunkZ);

            BlockPos bPos = new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));

            ServerLevel level = server.getLevel(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(nbt.getString("level"))));

            if (level == null) {
                return null;
            }

            return new EntityReferenceInfo(pos, uuid, level, server, bPos);
        }

        public Entity get() {
            return level.getEntity(uuid);
        }

        public CompoundTag toNBT() {
            CompoundTag nbt = new CompoundTag();

            nbt.putUUID("uuid", uuid);
            nbt.putInt("chunkX", pos.x);
            nbt.putInt("chunkZ", pos.z);
            nbt.putString("level", level().dimension().location().toString());
            BlockPos pos = bPos;
            nbt.putInt("x", pos.getX());
            nbt.putInt("y", pos.getY());
            nbt.putInt("z", pos.getZ());
            return nbt;
        }

        public void forceLoad() {
            level.getChunkSource().addRegionTicket(TicketType.PORTAL, pos, 3, bPos);
        }

    }

    public static CompoundTag toNBTReference(Entity entity) {
        CompoundTag nbt = new CompoundTag();
        nbt.putUUID("uuid", entity.getUUID());
        nbt.putInt("chunkX", entity.chunkPosition().x);
        nbt.putInt("chunkZ", entity.chunkPosition().z);
        nbt.putString("level", entity.level().dimension().location().toString());
        BlockPos pos = entity.blockPosition();
        nbt.putInt("x", pos.getX());
        nbt.putInt("y", pos.getY());
        nbt.putInt("z", pos.getZ());
        return nbt;
    }

}
