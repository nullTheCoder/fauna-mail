package org.antarcticgardens.faunamail.blocks.mailbox;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TickingTracker;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.antarcticgardens.faunamail.LevelBlockPos;
import org.antarcticgardens.faunamail.tracker.Names;
import org.antarcticgardens.faunamail.tracker.StateManager;

public class MailBoxBlockEntity extends BaseContainerBlockEntity {

    public static BlockEntityType<MailBoxBlockEntity> TYPE;
    private final MailBoxBlock block;
    private final int size;

    public NonNullList<ItemStack> items;
    public String name = "";


    public MailBoxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
        if (getBlockState().getBlock() instanceof MailBoxBlock block) {
            this.block = block;
            this.size = block.slots()[2] * block.slots()[3];
            items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        } else {
            throw new IllegalArgumentException("Can't place mail box block entity on a non mailbox block");
        }
    }

    @Override
    public void startOpen(Player player) {
        super.startOpen(player);
        updateBlockState(getBlockState().setValue(MailBoxBlock.OPEN, true));
    }

    @Override
    public void stopOpen(Player player) {
        super.stopOpen(player);
        updateBlockState(getBlockState().setValue(MailBoxBlock.OPEN, false));
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("faunamail.mailbox");
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        var a = super.removeItem(slot, amount);
        checkFullness();
        return a;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        var a = super.removeItemNoUpdate(slot);
        checkFullness();
        return a;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        super.setItem(slot, stack);
        checkFullness();
    }

    private void checkFullness() {
        boolean full = false;
        for (int i = 0; i < getContainerSize(); i++) {
            if (!getItem(i).isEmpty()) {
                full = true;
                break;
            }
        }
        updateBlockState(getBlockState().setValue(MailBoxBlock.FULL,  full));
    }

    void updateBlockState(BlockState state) {
        this.level.setBlock(this.getBlockPos(), state, 3);
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new MailBoxContainerMenu(block.menuType, containerId, inventory, this, block);
    }

    @Override
    public int getContainerSize() {
        return size;
    }

    @Override
    public Component getName() {
        return Component.literal(name);
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        var cname = componentInput.get(DataComponents.CUSTOM_NAME);
        if (cname != null) {
            this.name = cname.getString();
        }
        super.applyImplicitComponents(componentInput);
    }

    private boolean generateOrTestForNewName = true;
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.name = tag.getString("Name");
        if (!name.isEmpty()) {
            generateOrTestForNewName = false;
        }
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if (level instanceof ServerLevel sl && generateOrTestForNewName) {
            level.scheduleTick(getBlockPos(), getBlockState().getBlock(), 0);
        }
    }

    public void testGen() {
        if (generateOrTestForNewName && level instanceof ServerLevel serverLevel) {
            if (name == null || name.isEmpty()) {
                name = Names.getName(Names.names.get(level.random.nextInt(Names.names.size())), level.random.nextInt(1, 700));
            } else {
                name = Names.getName(name);
            }
            StateManager.addMailbox(name, new LevelBlockPos(getBlockPos(), serverLevel));
            generateOrTestForNewName = false;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
        tag.putString("Name", name);
    }
}
