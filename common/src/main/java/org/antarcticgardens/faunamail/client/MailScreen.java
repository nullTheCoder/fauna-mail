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

public class MailScreen extends AbstractContainerScreen<MailContainerMenu> {

    private static final WidgetSprites SPRITES = new WidgetSprites(
            ResourceLocation.tryBuild("faunamail", "textures/gui/sign.png"),
            ResourceLocation.tryBuild("faunamail", "textures/gui/sign_disabled.png"),
            ResourceLocation.tryBuild("faunamail", "textures/gui/sign_focused.png")
    );

    private static final WidgetSprites TURN_SPRITES = new WidgetSprites(
            ResourceLocation.tryBuild("faunamail", "textures/gui/turn.png"),
            ResourceLocation.tryBuild("faunamail", "textures/gui/turn_disabled.png"),
            ResourceLocation.tryBuild("faunamail", "textures/gui/turn_focused.png")
    );
    private final Inventory playerInventory;

    private boolean back = true;

    private Button turn;
    private Button seal;

    private EditBox text1;
    private EditBox text2;
    private EditBox address;
    private EditBox recipient;

    public MailScreen(MailContainerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.playerInventory = playerInventory;
    }

    @Override
    protected void init() {
        super.init();
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;

        turn = new ImageButton(i - 24, j - 24, 16, 16, TURN_SPRITES, button -> {
            back = !back;
            addWidgets();
            if (back) {
                setFocused(text1);
            } else {
                setFocused(turn);
            }
        }, Component.translatable("faunamail.turn"));

        seal = new ImageButton(i + 11, j - 24, this.imageWidth - 11, 12, SPRITES, button -> {

        }, Component.translatable("faunamail.seal"));

        this.text1 = new EditBox(this.font, i + 11, j , this.imageWidth - 22, 12, Component.translatable("faunamail.message.line.1"));
        this.text1.setTextColor(0xffffff);
        this.text1.setTextColorUneditable(0x999999);
        this.text1.setMaxLength(26);
        this.text1.setHint(Component.translatable("faunamail.message.line.1"));

        this.text2 = new EditBox(this.font, i + 11, j + 13, this.imageWidth - 22, 12, Component.translatable("faunamail.message.line.2"));
        this.text2.setTextColor(0xffffff);
        this.text2.setTextColorUneditable(0x999999);
        this.text2.setMaxLength(26);
        this.text2.setHint(Component.translatable("faunamail.message.line.2"));

        this.address = new EditBox(this.font, i + 25, j + 50, this.imageWidth - 11 - 25, 12, Component.translatable("faunamail.address"));
        this.address.setTextColor(0xffffff);
        this.address.setTextColorUneditable(0x999999);
        this.address.setMaxLength(20);
        this.address.setHint(Component.translatable("faunamail.address"));

        this.recipient = new EditBox(this.font, i + 25, j + 63, this.imageWidth - 11 - 25, 12, Component.translatable("faunamail.player"));
        this.recipient.setTextColor(0xffffff);
        this.recipient.setTextColorUneditable(0x999999);
        this.recipient.setMaxLength(20);
        this.recipient.setHint(Component.translatable("faunamail.player"));

        this.setFocused(text1);
        addWidgets();
    }

    public void addWidgets() {
        clearWidgets();
        if (this.back) {
            this.addWidget(this.text1);
            this.addWidget(this.text2);
            this.addWidget(this.address);
            this.addWidget(this.recipient);
        } else {
            this.addWidget(this.seal);
        }
        this.addWidget(this.turn);
    }

    public void resize(Minecraft minecraft, int width, int height) {
        String string = this.text1.getValue();
        String string2 = this.text2.getValue();
        String address = this.address.getValue();
        String recipient = this.recipient.getValue();
        this.init(minecraft, width, height);
        this.text1.setValue(string);
        this.text2.setValue(string2);
        this.address.setValue(address);
        this.recipient.setValue(recipient);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        if (this.text1.isFocused()) {
            this.setFocused(seal);
            return false;
        }
        return true;
    }

    protected int backgroundWidth = 176;
    protected int backgroundHeight = 166;
    @Override
    protected void renderBg(GuiGraphics context, float partialTick, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        //context.blitSprite(menu.container.getMailItem().BG(), x, y, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!this.back) {
            super.render(guiGraphics, mouseX, mouseY, partialTick);
            seal.render(guiGraphics, mouseX, mouseY, partialTick);

            turn.render(guiGraphics, mouseX, mouseY, partialTick);

            this.renderTooltip(guiGraphics, mouseX, mouseY);
        } else {
            text1.render(guiGraphics, mouseX, mouseY, partialTick);
            text2.render(guiGraphics, mouseX, mouseY, partialTick);
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
