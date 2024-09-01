package org.antarcticgardens.faunamail.tracker;

import net.minecraft.server.MinecraftServer;
import org.antarcticgardens.faunamail.FaunaMail;
import org.antarcticgardens.faunamail.mailman.Delivery;

import java.util.List;

public class Ticker {

    public static void tick(MinecraftServer server) {
        List<Delivery> deliveryList = StateManager.getDeliveries();
        for (int i = deliveryList.size() - 1; i >= 0; i--) {
            try {
                if (deliveryList.get(i).tick(server) != Delivery.TickResult.CONTINUE) {
                    deliveryList.remove(i);
                }
            } catch (Exception e) {
                FaunaMail.logger.info("Error ticking delivery", e);
                deliveryList.remove(i);
            }
        }
        StateManager.getInstance().setDirty(true);
    }

}
