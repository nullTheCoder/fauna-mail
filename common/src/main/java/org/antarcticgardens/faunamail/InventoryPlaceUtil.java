package org.antarcticgardens.faunamail;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class InventoryPlaceUtil {


    public static void insertOrDrop(ItemStack stack, Inventory inventory, BlockPos position, Level world) {
        if (stack.isEmpty()){return;}
        inventory.add(stack);
        if (!stack.isEmpty()) {
            Containers.dropItemStack(world, position.getX(), position.getY(), position.getZ(), stack);
        }
    }

    public static void insertOrDrop(List<ItemStack> items, Inventory inventory, BlockPos position, Level world) {
        for (ItemStack item : items) {
            insertOrDrop(item, inventory, position, world);
        }
    }

}
