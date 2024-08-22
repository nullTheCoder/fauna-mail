package org.antarcticgardens.faunamail.items;

import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import org.antarcticgardens.faunamail.items.mail.MailItem;

import java.util.function.Supplier;

public class Items {

    public static Tuple<String, Item>[] items = new Tuple[] {
        new Tuple<String, Item>("envelope", new MailItem(new Item.Properties(), "envelope"))
    };

}
