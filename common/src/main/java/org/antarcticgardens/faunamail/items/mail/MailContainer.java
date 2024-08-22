package org.antarcticgardens.faunamail.items.mail;

import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.antarcticgardens.faunamail.items.Components;

public class MailContainer implements Container {

    private final MailItem mailItem;
    NonNullList<ItemStack> items;

    public MailContainer (int rows, int columns, MailItem item) {
        items = NonNullList.createWithCapacity(columns*rows);
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                items.add(ItemStack.EMPTY);
            }
        }
        this.mailItem = item;
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack item : items) {
            if (!item.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(items, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack item = items.get(slot);
        if (!item.isEmpty()) {
            items.set(slot, ItemStack.EMPTY);
        }
        return item;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (canPlaceItem(slot, stack)) {
            items.set(slot, stack);
        }
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        for (int i = 0 ; i < items.size(); i++) {
            items.set(i,ItemStack.EMPTY);
        }
    }

    public MailItem getMailItem() {
        return mailItem;
    }

    public static TagKey<Item> BLACKLIST = TagKey.create(Registries.ITEM, ResourceLocation.tryBuild("fauna_mail", "non_mailable"));

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (stack.getComponents().has(Components.SEALED) || stack.is(BLACKLIST)) {
            return false;
        }
        return Container.super.canPlaceItem(slot, stack);
    }
}
