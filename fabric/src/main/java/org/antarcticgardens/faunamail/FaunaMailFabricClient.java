package org.antarcticgardens.faunamail;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import org.antarcticgardens.faunamail.client.MailScreen;
import org.antarcticgardens.faunamail.items.mail.MailItem;

public class FaunaMailFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        for (MailItem item : MailItem.items) {
            MenuScreens.register(item.menu, MailScreen::new);
        }
    }
}
