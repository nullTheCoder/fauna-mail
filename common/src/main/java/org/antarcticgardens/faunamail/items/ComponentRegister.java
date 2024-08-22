package org.antarcticgardens.faunamail.items;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.Tuple;

import java.util.function.Supplier;

import static org.antarcticgardens.faunamail.items.Components.SEALED;

public class ComponentRegister {
    public static Tuple<String, Supplier<DataComponentType<?>>>[]components = new Tuple[] {
            new Tuple<String, Supplier<DataComponentType<?>>>("sealed", () -> SEALED)
    };
}
