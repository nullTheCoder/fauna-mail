package org.antarcticgardens.faunamail.items.mail;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.antarcticgardens.faunamail.InventoryPlaceUtil;
import org.antarcticgardens.faunamail.items.Components;

import java.util.ArrayList;
import java.util.List;

public class PacketReceiver {

    public static void unseal(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof MailItem)) {
            stack = player.getOffhandItem();
        }
        if (!(stack.getItem() instanceof MailItem)) {
            return;
        }
        Boolean used = stack.get(Components.USED);
        Boolean sealed = stack.get(Components.SEALED);
        if ((used != null && used) || (sealed == null || !sealed)) {
            return;
        }

        var plp = player.position();
        NonNullList<ItemStack> items = NonNullList.create();
        for (Object stac : stack.getOrDefault(Components.ITEMS, List.of())) {
            if (stac instanceof ItemStack && !((ItemStack) stac).isEmpty()) {
                items.add(((ItemStack) stac).copy());
            }
        }

        InventoryPlaceUtil.insertOrDrop(items, player.getInventory(), new BlockPos((int) plp.x(), (int) plp.y(), (int) plp.z()), player.level());
        stack.applyComponents(DataComponentMap.builder()
                .addAll(stack.getComponents())
                .set(Components.USED, !stack.has(DataComponents.UNBREAKABLE))
                .set(Components.SEALED, false)
                .set(Components.ITEMS, null)
                .build());

    }

    public static void handle(ServerPlayer player, List<String> text, String address, String receiver) {
        if (address.length() > 16 || receiver.length() > 16) {
            return;
        }
        for (String s : text) {
            if (s.length() > 16) {
                return;
            }
        }

        if (player.containerMenu instanceof MailContainerMenu menu) {
            ItemStack item = menu.selfItem;
            List<ItemStack> items = new ArrayList<>();
            for (ItemStack i : menu.container.items) {
                if (!i.isEmpty()) {
                    items.add(i);
                }
            }

            item.applyComponents(DataComponentMap
                            .builder()
                            .addAll(item.getComponents())
                            .set(Components.SEALED, true)
                            .set(Components.TEXT, text)
                            .set(Components.ADDRESS, address)
                            .set(Components.PLAYER, receiver)
                            .set(Components.ITEMS, items)
                            .set(DataComponents.MAX_STACK_SIZE, 1)
                            .build());

            menu.selfItem = item;
            menu.container.clearContent();
            player.closeContainer();
        }
    }

}
