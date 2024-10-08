package org.antarcticgardens.faunamail.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.antarcticgardens.faunamail.items.mail.MailContainerMenu;
import org.antarcticgardens.faunamail.items.mail.MailItem;

public class MailScreen extends AbstractContainerScreen<MailContainerMenu> {

    private static WidgetSprites sealImage;

    public static WidgetSprites turnImage;

    private final Inventory playerInventory;

    private boolean back = true;

    private Button turn;
    private Button seal;

    private EditBox[] text;
    private EditBox address;
    private EditBox recipient;
    private final MailItem item;

    public MailScreen(MailContainerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.playerInventory = playerInventory;
        item = menu.container.getMailItem();
    }

    @Override
    protected void init() {
        super.init();

        var images = item.sealImage();
        sealImage = new WidgetSprites(
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

        int i = (this.width - item.backgroundWidth()) / 2;
        int j = (this.height - item.backgroundHeight()) / 2;

        turn = new ImageButtonButBetter(i+ item.flip()[0], j + item.flip()[1], item.flip()[2], item.flip()[3], turnImage, button -> {
            back = !back;
            addWidgets();
            if (back) {
                this.clearFocus();
                setFocused(text[0]);
            } else {
                this.clearFocus();
                setFocused(turn);
            }
        }, Component.translatable("faunamail.turn"));

        seal = new ImageButtonButBetter(i + item.seal()[0], j + item.seal()[1], item.seal()[2], item.seal()[3], sealImage, button -> {
            if (address.getValue().isBlank() && recipient.getValue().isBlank()) {
                return;
            }

            String[] text = new String[this.text.length];
            for (int a = 0 ; a < text.length ; a++) {
                text[a] = this.text[a].getValue();
            }
            PacketSender.sendSealPacket(text, address.getValue(), recipient.getValue());
        }, Component.translatable("faunamail.seal"));

        text = new EditBox[item.textRows().length];

        int a = 0;
        for (int[] tex : item.textRows()) {
            this.text[a] = new EditBox(this.font, i + tex[0], j + tex[1], tex[2], tex[3], Component.translatable("faunamail.message.line." + a));
            this.text[a].setTextColor(0xffffff);
            this.text[a].setTextColorUneditable(0x999999);
            this.text[a].setMaxLength(tex[4]);
            this.text[a].setHint(Component.translatable("faunamail.message.line." + a));
            a++;
        }

        var addr = item.address();
        this.address = new EditBox(this.font, i + addr[0], j + addr[1], addr[2], addr[3], Component.translatable("faunamail.address"));
        this.address.setTextColor(0xffffff);
        this.address.setTextColorUneditable(0x999999);
        this.address.setMaxLength(20);
        this.address.setHint(Component.translatable("faunamail.address"));

        var pla = item.player();
        this.recipient = new EditBox(this.font, i + pla[0], j + pla[1], pla[2], pla[3], Component.translatable("faunamail.player"));
        this.recipient.setTextColor(0xffffff);
        this.recipient.setTextColorUneditable(0x999999);
        this.recipient.setMaxLength(16);
        this.recipient.setHint(Component.translatable("faunamail.player"));

        this.clearFocus();
        this.setFocused(text[0]);
        addWidgets();
    }

    public void addWidgets() {
        clearWidgets();
        if (this.back) {
            for (var text : this.text) {
                this.addWidget(text);
            }
            this.addWidget(this.address);
            this.addWidget(this.recipient);
        } else {
            this.addWidget(this.seal);
        }
        this.addWidget(this.turn);
    }

    public void resize(Minecraft minecraft, int width, int height) {
        String[] strings = new String[this.text.length];
        for (int a = 0 ; a < strings.length ; a++) {
            strings[a] = this.text[a].getValue();
        }
        String address = this.address.getValue();
        String recipient = this.recipient.getValue();
        this.init(minecraft, width, height);
        for (int a = 0 ; a < strings.length ; a++) {
            this.text[a].setValue(strings[a]);
        }
        this.address.setValue(address);
        this.recipient.setValue(recipient);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        for (EditBox editBox : text) {
            if (editBox.isFocused()) {
                this.clearFocus();
                this.setFocused(this.turn);
                return false;
            }
        }
        if (this.address.isFocused() || this.recipient.isFocused()) {
            this.clearFocus();
            this.setFocused(this.turn);
            return false;
        }
        return true;
    }

    @Override
    protected void renderBg(GuiGraphics context, float partialTick, int mouseX, int mouseY) {
        int x = (width - item.backgroundWidth()) / 2;
        int y = (height - item.backgroundHeight()) / 2;
        if (this.back) {
            context.blit(menu.container.getMailItem().bgBack(), x, y, 0, 0, item.backgroundWidth(), item.backgroundHeight());
        } else {
            context.blit(menu.container.getMailItem().bgFront(), x, y, 0, 0, item.backgroundWidth(), item.backgroundHeight());
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.minecraft.options.keyInventory.matches(keyCode, scanCode)) {
            if (getFocused() instanceof EditBox && back) {
                return getFocused().keyPressed(keyCode, scanCode, modifiers);
            }
            return false;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!this.back) {
            super.render(guiGraphics, mouseX, mouseY, partialTick);
            seal.render(guiGraphics, mouseX, mouseY, partialTick);

            turn.render(guiGraphics, mouseX, mouseY, partialTick);

            this.renderTooltip(guiGraphics, mouseX, mouseY);
        } else {
            this.renderTransparentBackground(guiGraphics);
            this.renderBg(guiGraphics, partialTick, mouseX, mouseY);

            for (EditBox editBox : text) {
                editBox.render(guiGraphics, mouseX, mouseY, partialTick);
            }
            address.render(guiGraphics, mouseX, mouseY, partialTick);
            recipient.render(guiGraphics, mouseX, mouseY, partialTick);

            turn.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
        if (back) {
            return;
        }
        super.slotClicked(slot, slotId, mouseButton, type);
    }
}
