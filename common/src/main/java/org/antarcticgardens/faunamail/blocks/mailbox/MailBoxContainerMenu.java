package org.antarcticgardens.faunamail.blocks.mailbox;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class MailBoxContainerMenu extends AbstractContainerMenu {

    public final MailBoxBlock block;
    public Container container;

    public MailBoxContainerMenu(MenuType<?> type, int id, Inventory player, MailBoxBlock block) {
        this(type, id, player, new SimpleContainer(block.slots()[2]*block.slots()[3]), block);
    }

    public MailBoxContainerMenu(MenuType<?> type, int id, Inventory player, Container container, MailBoxBlock block) {
        super(type, id);

        this.container = container;
        container.startOpen(player.player);
        this.block = block;

        int[] dims = block.slots();
        for (int i = 0; i < dims[2]; i++) {
            for (int j = 0; j < dims[3]; j++) {
                this.addSlot(new Slot(container, i + j * dims[2], i * 18 + dims[0], j * 18 + dims[1]));
            }
        }

        int m;
        int l;
        //The player container
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(player, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }
        //The player Hotbar
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(player, m, 8 + m * 18, 142));
        }

    }



    @Override
    public ItemStack quickMoveStack(Player player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            newStack = originalStack.copy();
            if (invSlot < this.container.getContainerSize()) {
                if (!this.moveItemStackTo(originalStack, this.container.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(originalStack, 0, this.container.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return newStack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }
}
