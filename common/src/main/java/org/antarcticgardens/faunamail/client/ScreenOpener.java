package org.antarcticgardens.faunamail.client;

import net.minecraft.client.Minecraft;
import org.antarcticgardens.faunamail.items.mail.MailItem;

import java.util.List;

public class ScreenOpener {

    public static void open(MailItem item, List<String> text, String address, String player, Boolean used) {
        Minecraft.getInstance().setScreen(new MailOpeningScreen(item, text, address, player, used));
    }

}
