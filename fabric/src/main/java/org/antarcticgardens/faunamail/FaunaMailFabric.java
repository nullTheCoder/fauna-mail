package org.antarcticgardens.faunamail;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import org.antarcticgardens.faunamail.items.ComponentRegister;
import org.antarcticgardens.faunamail.items.Items;
import org.antarcticgardens.faunamail.items.mail.MailContainer;
import org.antarcticgardens.faunamail.items.mail.MailContainerMenu;
import org.antarcticgardens.faunamail.items.mail.MailItem;

import java.util.function.Supplier;

public class FaunaMailFabric implements ModInitializer {
    @Override
    public void onInitialize() {

        for (Tuple<String, Supplier<DataComponentType<?>>> component : ComponentRegister.components) {
            Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
                    ResourceLocation.tryBuild(FaunaMail.MOD_ID, component.getA()),
                    component.getB().get());
        }

        for (Tuple<String, Item> item : Items.items) {
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.tryBuild(FaunaMail.MOD_ID, item.getA()), item.getB());
        }

        for (MailItem mailItem : MailItem.items) {
            mailItem.menu = Registry.register(BuiltInRegistries.MENU, "mail_screen_type_" + mailItem.getName(),
                    new MenuType<>(
                            (a, b) -> new MailContainerMenu(mailItem.menu, a, b,
                                        new MailContainer(mailItem.rows(), mailItem.columns(), mailItem)
                                    , mailItem.rows(), mailItem.columns()), FeatureFlagSet.of()
                    ));
        }

        PayloadTypeRegistry.playC2S().register(SealPayload.ID, SealPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(SealPayload.ID, (payload, context) -> {
            System.out.println("Got SealPayload " + payload);
        });

    }
}
