package org.antarcticgardens.faunamail.mailman;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.antarcticgardens.faunamail.FaunaMail;
import org.antarcticgardens.faunamail.LevelBlockPos;
import org.antarcticgardens.faunamail.tracker.StateManager;

import java.util.UUID;
import java.util.function.Consumer;

public class Delivery {

    private Mob mob;
    private EntityReferenceHelper.EntityReferenceInfo mobReference;
    private Mailman mailman;
    private Stage stage;
    private int ticksIn;
    private LevelBlockPos origin;
    private MinecraftServer server;

    public Delivery(Mob mob, Mailman mailman, BlockPos origin) {
        this.mailman = mailman;
        this.mob = mob;
        stage = Stage.ANIMATION_OUT;
        this.origin = new LevelBlockPos(origin, (ServerLevel) mob.level());

        mob.isAlive();
    }

    public Delivery() { }

    public enum TickResult {CONTINUE, DONE}

    public void force() {
        mobReference.forceLoad();
    }

    public TickResult tick(MinecraftServer server) {
        if (mobReference != null) {
            mobReference.forceLoad();
        }
        if (mob != null && mob.isAlive()) {
            mobReference = new EntityReferenceHelper.EntityReferenceInfo(mob.chunkPosition(), mob.getUUID(), (ServerLevel) mob.level(), server, mob.blockPosition());
        } else if (!makeEntitySafer()) {
            if (ticksIn > 200) {
                FaunaMail.logger.info("Lost track of mob for delivery which started at {}", origin);
                return TickResult.DONE;
            }
            return TickResult.CONTINUE;
        }
        this.server = server;
        ticksIn++;

        mob.stopInPlace();
        mob.setNoAi(true);
        mob.setInvulnerable(true);
        mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 5, 255, false, false));

        if (ticksIn > 50 && stage == Stage.ANIMATION_OUT) {
            ticksIn = 0;
            stage = Stage.DELIVERING;
            mob.moveTo(10000, 500, 10000);
        }

        if (ticksIn > 100) {
            cleanEntity();
            dimensionalTP(origin.level(), origin.pos().getCenter());
            return TickResult.DONE;
        }

        return TickResult.CONTINUE;
    }

    public void dimensionalTP(ServerLevel world, Vec3 pos) {

        mob.changeDimension(new DimensionTransition(world, pos, Vec3.ZERO, mob.getYRot(), mob.getXRot(), entity -> {
            world.getChunkSource().removeEntity(entity);
            mob = (Mob) entity;
            world.sendParticles(ParticleTypes.PORTAL, pos.x(), pos.y(), pos.z(), 15, 1, 1, 1, 0.2);
            world.getChunkSource().addEntity(entity);
        }));
    }

    public void cleanEntity() {
        mob.setNoAi(false);
        mob.setInvulnerable(false);
        mob.setSpeed(mob.getSpeed());
    }

    int lostTicks = 0;

    public boolean makeEntitySafer() {
        if (mob == null) {
            updateEntity();
        } else if (!mob.isAlive()) {
            updateEntity();
        }

        if (mob == null) {
            lostTicks += 1;
            return false;
        }
        lostTicks = 0;
        return true;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();

        tag.putInt("stage", stage.getValue());
        if (mobReference != null) {
            tag.put("entity", mobReference.toNBT());
        }
        tag.put("origin", origin.toNbt());

        return tag;
    }

    public void updateEntity() {
        mob = (Mob) mobReference.get();
        if (mob == null) {
            return;
        }
        mailman = MailmanRegister.mailmanMap.get(
                BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType())
        );
    }

    public static Delivery getDeliveryFromNBT(CompoundTag tag, MinecraftServer server) {
        try {
            Delivery tracker = new Delivery();
            tracker.stage = Stage.fromInt(tag.getInt("stage"));
            tracker.mobReference = EntityReferenceHelper.EntityReferenceInfo.fromReference(tag.getCompound("entity"), server);
            tracker.origin = LevelBlockPos.fromNbt(tag.getCompound("origin"), server);

            if (tracker.mailman == null) {
                tracker.mailman = (Mailman) MailmanRegister.mailmanMap.values().toArray()[0];
            }

            return tracker;
        } catch (Exception e) {
            FaunaMail.logger.info("Error while loading delivery", e);
            return null;
        }
    }

}
