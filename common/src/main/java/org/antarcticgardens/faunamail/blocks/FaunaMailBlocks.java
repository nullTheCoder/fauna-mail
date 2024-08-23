package org.antarcticgardens.faunamail.blocks;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.antarcticgardens.faunamail.blocks.mailbox.MailBoxBlock;

import java.util.List;

public class FaunaMailBlocks {

    public static List<MailboxTogetherStrong> mailboxes = List.of(
        new MailboxTogetherStrong("wooden_mailbox", new MailBoxBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LECTERN)))
    );

    public record MailboxTogetherStrong(String id, MailBoxBlock block) {}

}
