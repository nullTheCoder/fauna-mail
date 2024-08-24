package org.antarcticgardens.faunamail.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.antarcticgardens.faunamail.items.mail.MailItem;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static org.antarcticgardens.faunamail.client.MailScreen.turnImage;

public class MailOpeningScreen extends Screen {
    private final MailItem item;
    private final List<String> textStrings;
    private final String playerString;
    private final String addressString;
    private final boolean used;
    private boolean back = false;

    private static WidgetSprites unsealImage;
    public static WidgetSprites turnImage;

    public MailOpeningScreen(MailItem item, @Nullable List<String> text, @Nullable String address, @Nullable String player, Boolean used) {
        super(Component.translatable("faunamail.opening_screen.title"));
        this.item = item;
        this.textStrings = text;
        this.addressString = address;
        this.playerString = player;
        if (text == null) {
            text = new ArrayList<>();
        }
        if (address == null) {
            address = "######";
        }
        if (player == null) {
            player = "######";
        }
        if (used == null) {
            used = false;
        }
        this.used = used;

        var images = item.unsealImage();
        unsealImage = new WidgetSprites(
                images[0],
                images[2],
                images[1]
        );

        images = item.flipImage();
        turnImage = new WidgetSprites(
                images[0],
                images[2],
                images[1]
        );
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
    }

    private Button turn;
    private Button unseal;


    @Override
    protected void init() {
        super.init();
        int i = (this.width - item.backgroundWidth()) / 2;
        int j = (this.height - item.backgroundHeight()) / 2;

        turn = new ImageButtonButBetter(i+ item.flip()[0], j + item.flip()[1], item.flip()[2], item.flip()[3], turnImage, button -> {
            back = !back;
            addWidgets();
            this.clearFocus();
            setFocused(turn);
        }, Component.translatable("faunamail.turn"));

        unseal = new ImageButtonButBetter(i + item.sealedSeal()[0], j + item.sealedSeal()[1], item.sealedSeal()[2], item.sealedSeal()[3], unsealImage, button -> {
            unseal.active = false;
            PacketSender.sendUnsealPacket();
        }, Component.translatable("faunamail.unseal"));
        unseal.active = !used;
        addWidgets();
    }

    public void addWidgets() {
        clearWidgets();
        if (!this.back) {
            this.addWidget(this.unseal);
        }
        this.addWidget(this.turn);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        int i = (this.width - item.backgroundWidth()) / 2;
        int j = (this.height - item.backgroundHeight()) / 2;
        if (this.back) {
            guiGraphics.blit(item.bgBack(), i, j, 0, 0, item.backgroundWidth(), item.backgroundHeight());
        } else {
            guiGraphics.blit(item.bgFrontSealed(), i, j, 0, 0, item.backgroundWidth(), item.backgroundHeight());
        }
        if (this.back) {
            var text = item.textRows();
            for (int a = 0; a < text.length; a++) {
                String str = "#UNKNOWN ;-;";
                if (textStrings.size() > a) {
                    str = textStrings.get(a);
                }
                guiGraphics.drawString(this.font, str, i + text[a][0], j  + text[a][1], item.textColor(), false);
            }
            var address = item.address();
            guiGraphics.drawString(this.font, addressString, i + address[0], j  + address[1], item.textColor(), false);
            var player = item.player();
            guiGraphics.drawString(this.font, playerString, i + player[0], j  + player[1], item.textColor(), false);
        } else {
            unseal.render(guiGraphics, mouseX, mouseY, partialTick);
        }
        turn.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
