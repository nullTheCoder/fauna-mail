package org.antarcticgardens.faunamail.tracker;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.antarcticgardens.faunamail.FaunaMail;
import org.antarcticgardens.faunamail.LevelBlockPos;

import java.util.HashMap;
import java.util.Map;

public class StateManager extends SavedData {
    private static StateManager instance;
    private final MinecraftServer server;

    public static void addMailbox(String name, LevelBlockPos pos) {
        if (instance == null) {
            FaunaMail.logger.info("Unable to add mailbox {} at {} this early.", name, pos.pos());
            return;
        }
        if (getInstance().mailBoxes.containsKey(name)) {
            return;
        }
        getInstance().mailBoxes.put(name, pos);
        getInstance().setDirty();
        FaunaMail.logger.info("New mailbox {} at {}", name, pos.pos());
    }

    public static void removeMailbox(String name) {
        if (instance == null) {
            FaunaMail.logger.info("Unable to remove mailbox {} this early.", name);
            return;
        }
        getInstance().mailBoxes.remove(name);
        FaunaMail.logger.info("Removed mailbox {}", name);
    }

    public static Map<String, LevelBlockPos> getMailBoxes() {
        if (instance == null) {
            FaunaMail.logger.info("Unable to retrieve mailboxes this early.");
            return new HashMap<>();
        }
        return getInstance().mailBoxes;
    }

    public static StateManager getInstance() {
        return instance;
    }

    public StateManager(MinecraftServer server) {
        instance = this;
        this.server = server;
    }

    public Map<String, LevelBlockPos> mailBoxes = new HashMap<>();

    public static StateManager load(CompoundTag tag, HolderLookup.Provider registries, MinecraftServer server) {
        StateManager instance = new StateManager(server);

        var boxes = tag.getCompound("mailboxes");
        for (String key : boxes.getAllKeys()) {
            instance.mailBoxes.put(key, LevelBlockPos.fromNbt(boxes.getCompound(key), server));
        }

        return instance;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {

        CompoundTag boxesTag = new CompoundTag();
        for (String key : mailBoxes.keySet()) {
            boxesTag.put(key, mailBoxes.get(key).toNbt());
        }
        tag.put("mailboxes", boxesTag);

        return tag;
    }

    public static void setServerState(MinecraftServer server) {
        var type = new Factory<>(
                () -> new StateManager(server),
                (a, b) -> load(a, b, server),
                null
        );

        DimensionDataStorage storage = server.overworld().getDataStorage();

        StateManager state = storage.computeIfAbsent(type, FaunaMail.MOD_ID);
        state.setDirty();
    }

}
