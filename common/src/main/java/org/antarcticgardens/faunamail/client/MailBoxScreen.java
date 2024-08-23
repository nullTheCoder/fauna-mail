package org.antarcticgardens.faunamail.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.antarcticgardens.faunamail.blocks.mailbox.MailBoxContainerMenu;

public class MailBoxScreen extends AbstractContainerScreen<MailBoxContainerMenu> {

    private final ResourceLocation texture;

    public MailBoxScreen(MailBoxContainerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = menu.block.getImageWidth();
        imageHeight = menu.block.getImageHeight();
        texture = ResourceLocation.parse(menu.block.texture());
    }


    @Override
    protected void renderBg(GuiGraphics context, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        context.blit(texture, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
