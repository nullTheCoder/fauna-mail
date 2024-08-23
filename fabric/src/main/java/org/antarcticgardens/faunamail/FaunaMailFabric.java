package org.antarcticgardens.faunamail;

import com.google.gson.Gson;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Tuple;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.antarcticgardens.faunamail.blocks.FaunaMailBlocks;
import org.antarcticgardens.faunamail.blocks.mailbox.MailBoxBlock;
import org.antarcticgardens.faunamail.blocks.mailbox.MailBoxBlockEntity;
import org.antarcticgardens.faunamail.blocks.mailbox.MailBoxContainerMenu;
import org.antarcticgardens.faunamail.client.MailBoxScreen;
import org.antarcticgardens.faunamail.items.ComponentRegister;
import org.antarcticgardens.faunamail.items.Items;
import org.antarcticgardens.faunamail.items.mail.MailContainer;
import org.antarcticgardens.faunamail.items.mail.MailContainerMenu;
import org.antarcticgardens.faunamail.items.mail.MailItem;
import org.antarcticgardens.faunamail.items.mail.PacketReceiver;
import org.antarcticgardens.faunamail.mailman.MailmanRegister;
import org.antarcticgardens.faunamail.tracker.Names;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            PacketReceiver.handle(context.player(), payload.text(), payload.address(), payload.player());
        });

        PayloadTypeRegistry.playC2S().register(UnsealPayload.ID, UnsealPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(UnsealPayload.ID, (payload, context) -> {
            PacketReceiver.unseal(context.player());
        });

        PayloadTypeRegistry.playC2S().register(MailPayload.ID, MailPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(MailPayload.ID, (payload, context) -> {
            PacketReceiver.mail(context.player(), payload.id());
        });

        int i = 0;
        MailBoxBlock[] blocks = new MailBoxBlock[FaunaMailBlocks.mailboxes.size()];

        for (FaunaMailBlocks.MailboxTogetherStrong mailbox : FaunaMailBlocks.mailboxes) {
            Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.tryBuild(FaunaMail.MOD_ID, mailbox.id()), mailbox.block());
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.tryBuild(FaunaMail.MOD_ID, mailbox.id()),
                    new BlockItem(mailbox.block(), new Item.Properties()));
            blocks[i] = mailbox.block();

            mailbox.block().menuType = Registry.register(BuiltInRegistries.MENU, "box_" + mailbox.id(),
                    new MenuType<>(
                            (a, b) -> new MailBoxContainerMenu(mailbox.block().menuType, a, b, mailbox.block()), FeatureFlagSet.of()
                    ));

            i++;
        }

        MailBoxBlockEntity.TYPE = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
                ResourceLocation.tryBuild("fauna_mail", "mailbox"),
                BlockEntityType.Builder.of((blockPos, blockState) -> new MailBoxBlockEntity(MailBoxBlockEntity.TYPE, blockPos, blockState), blocks).build()
                );

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return ResourceLocation.tryBuild(FaunaMail.MOD_ID, "fauna_mail");
            }

            @Override
            public void onResourceManagerReload(ResourceManager manager) {
                MailmanRegister.mailmanMap.clear();
                Gson gson = new Gson();
                for(var entry : manager.listResources("fauna_mail/animals", path -> path.toString().endsWith(".json")).entrySet()) {
                    try(InputStream stream = entry.getValue().open()) {
                        String string = new String(stream.readAllBytes());
                        Map<String, Object> json = gson.fromJson(string, HashMap.class);

                        MailmanRegister.parse(json);

                    } catch (Exception e) {
                        FaunaMail.logger.error("Error occurred while loading resource json{}", entry.getKey().toString(), e);
                    }
                }
                Names.names.clear();
                for(var entry : manager.listResources("fauna_mail/mail_box_names", path -> path.toString().endsWith(".json")).entrySet()) {
                    try(InputStream stream = entry.getValue().open()) {
                        String string = new String(stream.readAllBytes());
                        List<String> names = gson.fromJson(string, ArrayList.class);
                        Names.names.addAll(names);
                    } catch (Exception e) {
                        FaunaMail.logger.error("Error occurred while loading resource json{}", entry.getKey().toString(), e);
                    }
                }
            }
        });

    }
}
