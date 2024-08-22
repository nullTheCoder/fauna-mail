package org.antarcticgardens.faunamail;

import com.google.common.collect.Lists;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.antarcticgardens.faunamail.client.MailScreen;
import org.antarcticgardens.faunamail.client.PacketSender;
import org.antarcticgardens.faunamail.items.mail.MailItem;

import java.util.ArrayList;

public class FaunaMailFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        for (MailItem item : MailItem.items) {
            MenuScreens.register(item.menu, MailScreen::new);
        }
        PacketSender.implementation = new PacketSender() {
            @Override
            public void sendSealPacket_(String[] text, String address, String player) {
                ClientPlayNetworking.send(new SealPayload(Lists.newArrayList(text), address, player));
            }
        };
    }
}
