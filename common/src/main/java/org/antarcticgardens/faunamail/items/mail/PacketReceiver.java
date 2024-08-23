package org.antarcticgardens.faunamail.items.mail;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.antarcticgardens.faunamail.items.Components;

import java.util.ArrayList;
import java.util.List;

public class PacketReceiver {

    public static void handle(ServerPlayer player, List<String> text, String address, String receiver) {
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
                            .build());

            menu.selfItem = item;
            menu.container.clearContent();
            player.closeContainer();
        }
    }

}
