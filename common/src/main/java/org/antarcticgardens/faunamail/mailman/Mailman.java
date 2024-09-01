package org.antarcticgardens.faunamail.mailman;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import org.antarcticgardens.faunamail.mailman.displays.MailItemDisplay;
import org.antarcticgardens.faunamail.mailman.movement.Movement;

import java.util.Map;

public record Mailman(
        ResourceLocation id,

        float speedBpt,
        float maxDistance,

        Returns returns,
        float returnJourneyMinSpeedMultiplier,
        float returnJourneyMaxSpeedMultiplier,

        int dimensionalTravelCost,
        boolean canLocatePlayer,
        boolean canLocateMailbox,

        Movement movement,
        MailItemDisplay display

) {

}
