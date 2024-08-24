package org.antarcticgardens.faunamail.items.mail;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.antarcticgardens.faunamail.client.PacketSender;
import org.antarcticgardens.faunamail.client.ScreenOpener;
import org.antarcticgardens.faunamail.items.Components;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MailItem extends Item {

    public static List<MailItem> items = new ArrayList<>();
    private final String name;

    public MenuType<MailContainerMenu> menu;

    public MailItem(Properties properties, String name) {
        super(properties);
        items.add(this);
        this.name = name;
    }

    public ResourceLocation bgFront() {
        return ResourceLocation.tryBuild("fauna_mail", "textures/gui/envelope/bg_front.png");
    }

    public ResourceLocation bgFrontSealed() {
        return ResourceLocation.tryBuild("fauna_mail", "textures/gui/envelope/bg_front_sealed.png");
    }

    public ResourceLocation bgBack() {
        return ResourceLocation.tryBuild("fauna_mail", "textures/gui/envelope/bg_back.png");
    }

    public ResourceLocation[] sealImage() {
        return new ResourceLocation[] {
                ResourceLocation.tryBuild("fauna_mail", "textures/gui/envelope/seal.png"),
                ResourceLocation.tryBuild("fauna_mail", "textures/gui/envelope/seal_selected.png"),
                ResourceLocation.tryBuild("fauna_mail", "textures/gui/envelope/seal_disabled.png")
        };
    }

    public ResourceLocation[] flipImage() {
        return new ResourceLocation[] {
                ResourceLocation.tryBuild("fauna_mail", "textures/gui/envelope/flip.png"),
                ResourceLocation.tryBuild("fauna_mail", "textures/gui/envelope/flip_selected.png"),
                ResourceLocation.tryBuild("fauna_mail", "textures/gui/envelope/flip.png")
        };
    }

    public int[] slotPositions() {
        return new int[] {62, 51};
    }

    public int columns() {
        return 3;
    }

    public int rows() {
        return 1;
    }

    /**
     * @return array of {posX, posY, width, height, char_length}
     */
    public int[][] textRows() {
        return new int[][] {
                {11, 33, backgroundWidth() - 22, 12, 24},
                {11, 46, backgroundWidth() - 22, 12, 24}
        };
    }

    public int textColor() {
        return 0x000000;
    }

    /**
     * @return {posX, posY, width, height}
     */
    public int[] address() {
        return new int[] {25, 73, backgroundWidth() - 11 - 25, 12};
    }

    /**
     * @return {posX, posY, width, height}
     */
    public int[] player() {
        return new int[] {25, 86, backgroundWidth() - 11 - 25, 12};
    }

    public int[] flip() {
        return new int[] {171, 54, 16, 14};
    }

    public int[] seal() {
        return new int[] {77, 32, 22, 15};
    }

    public int[] sealedSeal() {
        return new int[] {77, 48, 22, 15};
    }

    public int backgroundWidth() {return 176; }
    public int backgroundHeight() {return 166; }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        Boolean sealed = stack.get(Components.SEALED);
        Boolean used = stack.get(Components.USED);
        if ((sealed == null || !sealed) && (used == null || !used)) {
            if (!level.isClientSide()) {
                open(player, stack);
            }
            return InteractionResultHolder.success(stack);
        } else {
            if (used == null) {
                used = false;
            }
            if (level.isClientSide()) {
                if (Boolean.TRUE.equals(sealed)) {
                    var result = Minecraft.getInstance().hitResult;
                    if (result != null) {
                        if (result.getType() == HitResult.Type.ENTITY && result instanceof EntityHitResult ent) {
                            Entity entity = ent.getEntity();
                            PacketSender.mail(entity.getId());
                            return InteractionResultHolder.success(stack);
                        } else if (result.getType() == HitResult.Type.BLOCK && result instanceof BlockHitResult block) {
                            BlockPos pos = block.getBlockPos();
                            List<Entity> entities = level.getEntities(null, new AABB(pos.above(1)));
                            for (Entity entity : entities) {
                                PacketSender.mail(entity.getId());
                                return InteractionResultHolder.success(stack);
                            }
                        }
                    }
                }

                ScreenOpener.open(this, stack.get(Components.TEXT), stack.get(Components.ADDRESS), stack.get(Components.PLAYER), used);
                return InteractionResultHolder.success(stack);
            }
            return InteractionResultHolder.success(stack);
        }
    }

    public void open(Player player, ItemStack itemStack) {
        player.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.empty();
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                var menu_ = menu.create(i, inventory);
                menu_.selfItem = itemStack.copyWithCount(1);
                itemStack.shrink(1);
                return menu_;
            }
        });
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        if (stack.getComponents().has(Components.SEALED)) {
            return InteractionResult.PASS;
        }
        return super.interactLivingEntity(stack, player, interactionTarget, usedHand);
    }

    public String getName() {
        return name;
    }

    public ResourceLocation[] unsealImage() {
        return sealImage();
    }
}
