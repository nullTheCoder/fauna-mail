package org.antarcticgardens.faunamail.items.mail;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.antarcticgardens.faunamail.InventoryPlaceUtil;

public class MailContainerMenu extends AbstractContainerMenu {

    public MailContainer container;
    public ItemStack selfItem;

    public MailContainerMenu(MenuType<?> type, int id, Inventory player, MailContainer container, int rows, int columns) {
        super(type, id);

        this.container = container;
        container.startOpen(player.player);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                this.addSlot(new Slot(container, i + j * rows, i * 18, j * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return container.canPlaceItem(index, stack);
                    }
                });
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
        container.stopOpen(player);
        var plp = player.position();
        if (selfItem != null) {
            InventoryPlaceUtil.insertOrDrop(selfItem, player.getInventory(), new BlockPos((int) plp.x, (int) plp.y, (int) plp.z), player.level());
        }
        InventoryPlaceUtil.insertOrDrop(container.items, player.getInventory(), new BlockPos((int) plp.x, (int) plp.y, (int) plp.z), player.level());
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }
}
