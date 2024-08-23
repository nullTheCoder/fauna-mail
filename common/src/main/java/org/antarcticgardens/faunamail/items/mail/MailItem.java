package org.antarcticgardens.faunamail.items.mail;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.antarcticgardens.faunamail.FaunaMail;
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

    public ResourceLocation BG() {
        return ResourceLocation.tryBuild("minecraft", "textures/gui/container/cartography_table.png");
    }

    public int rows() {
        return 2;
    }

    public int columns() {
        return 2;
    }

    public int[][] textRowCoords() {
        return new int[][] {
                {11, 0},
                {11, 13}
        };
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        Boolean sealed = stack.get(Components.SEALED);
        if (sealed == null || !sealed) {
            if (!level.isClientSide()) {
                open(player, stack);
            }
            return InteractionResultHolder.success(stack);
        }
        return super.use(level, player, usedHand);
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
}
