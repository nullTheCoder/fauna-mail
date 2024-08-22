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
import org.antarcticgardens.faunamail.items.mail.MailContainerMenu;

import java.util.ArrayList;
import java.util.List;

public class MailScreen extends AbstractContainerScreen<MailContainerMenu> {

    private static final WidgetSprites SPRITES = new WidgetSprites(
            ResourceLocation.tryBuild("faunamail", "textures/gui/sign.png"),
            ResourceLocation.tryBuild("faunamail", "textures/gui/sign_disabled.png"),
            ResourceLocation.tryBuild("faunamail", "textures/gui/sign_focused.png")
    );
    private final Inventory playerInventory;

    private EditBox text;
    private Button seal;

    public static final List<String> SUGGESTIONS = new ArrayList<>() {
        {
            add("Here is a mail");
            add("Here be mail");
            add("This be a mail!");
            add("Hope you have a great day!");
            add("From me with love!");
            add("I hope for a blahaj in return :3");
            add(":3");
            add("");
        }
    };

    public MailScreen(MailContainerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.playerInventory = playerInventory;
    }

    @Override
    protected void init() {
        super.init();
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;

        seal = new ImageButton(i + 22, j - 24, this.imageWidth - 22, 12, SPRITES, button -> {

        }, Component.translatable("faunamail.seal"));
        this.addWidget(seal);

        this.text = new EditBox(this.font, i + 22, j + 24, this.imageWidth - 22, 12, Component.translatable("faunamail.message"));
        this.text.setTextColor(0xffffff);
        this.text.setTextColorUneditable(0x999999);
        this.text.setMaxLength(26);
        this.text.setHint(Component.translatable("faunamail.message"));
        this.addWidget(this.text);

        this.setFocused(text);
    }

    public void resize(Minecraft minecraft, int width, int height) {
        String string = this.text.getValue();
        this.init(minecraft, width, height);
        this.text.setValue(string);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        if (this.text.isFocused()) {
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
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        text.render(guiGraphics, mouseX, mouseY, partialTick);
        seal.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
