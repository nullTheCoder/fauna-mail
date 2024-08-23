package org.antarcticgardens.faunamail;

import com.google.common.collect.Lists;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.antarcticgardens.faunamail.blocks.FaunaMailBlocks;
import org.antarcticgardens.faunamail.blocks.mailbox.MailBoxBlock;
import org.antarcticgardens.faunamail.blocks.mailbox.MailBoxContainerMenu;
import org.antarcticgardens.faunamail.client.MailBoxScreen;
import org.antarcticgardens.faunamail.client.MailScreen;
import org.antarcticgardens.faunamail.client.PacketSender;
import org.antarcticgardens.faunamail.items.Components;
import org.antarcticgardens.faunamail.items.mail.MailItem;

import java.util.ArrayList;

public class FaunaMailFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        for (MailItem item : MailItem.items) {
            MenuScreens.register(item.menu, MailScreen::new);
        }

        for (FaunaMailBlocks.MailboxTogetherStrong mailbox : FaunaMailBlocks.mailboxes) {
            MenuScreens.register(mailbox.block().menuType, MailBoxScreen::new);
        }

        PacketSender.implementation = new PacketSender() {
            @Override
            public void sendUnsealPacket_() {
                ClientPlayNetworking.send(new UnsealPayload());
            }

            @Override
            public void sendSealPacket_(String[] text, String address, String player) {
                ClientPlayNetworking.send(new SealPayload(Lists.newArrayList(text), address, player));
            }

            @Override
            public void mail_(int entityId) {
                ClientPlayNetworking.send(new MailPayload(entityId));
            }
        };

        ResourceLocation sealed = ResourceLocation.parse("fauna_mail:sealed");
        ResourceLocation used = ResourceLocation.parse("fauna_mail:used");

        ClampedItemPropertyFunction sealedFunction = (itemStack, clientWorld, livingEntity, seed) -> itemStack.getOrDefault(Components.SEALED, false) ? 1 : 0;
        ClampedItemPropertyFunction usedFunction = (itemStack, clientWorld, livingEntity, seed) -> itemStack.getOrDefault(Components.USED, false) ? 1 : 0;

        for (MailItem item : MailItem.items) {
            ItemProperties.register(item, sealed, sealedFunction);
            ItemProperties.register(item, used, usedFunction);
        }



    }
}
