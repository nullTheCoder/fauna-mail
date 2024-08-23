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

import static org.antarcticgardens.faunamail.client.MailScreen.TURN_SPRITES;

public class MailOpeningScreen extends Screen {
    private final MailItem item;
    private final List<String> textStrings;
    private final String playerString;
    private final String addressString;
    private final boolean used;
    private boolean back = false;

    private static final WidgetSprites SPRITES = new WidgetSprites(
            ResourceLocation.tryBuild("faunamail", "textures/gui/sign.png"),
            ResourceLocation.tryBuild("faunamail", "textures/gui/sign_disabled.png"),
            ResourceLocation.tryBuild("faunamail", "textures/gui/sign_focused.png")
    );

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

        turn = new ImageButton(i - 24, j - 24, 16, 16, TURN_SPRITES, button -> {
            back = !back;
            addWidgets();
            this.clearFocus();
            setFocused(turn);
        }, Component.translatable("faunamail.turn"));

        unseal = new ImageButton(i + 11, j - 24, item.backgroundWidth() - 11, 12, SPRITES, button -> {
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
            var text = item.textRows();
            for (int a = 0; a < text.length; a++) {
                String str = "#UNKNOWN ;-;";
                if (textStrings.size() > a) {
                    str = textStrings.get(a);
                }
                guiGraphics.drawString(this.font, str, i + text[a][0], j  + text[a][1], item.textColor());
            }
            var address = item.address();
            guiGraphics.drawString(this.font, addressString, i + address[0], j  + address[1], item.textColor());
            var player = item.player();
            guiGraphics.drawString(this.font, playerString, i + player[0], j  + player[1], item.textColor());
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
