package org.antarcticgardens.faunamail.mailman;

import org.antarcticgardens.faunamail.mailman.movement.Movement;

import java.util.Map;

public record Mailman(
        float speedBpt,
        float maxDistance,

        Returns returns,
        float returnJourneyMinSpeedMultiplier,
        float returnJourneyMaxSpeedMultiplier,

        int dimensionalTravelCost,
        boolean canLocatePlayer,
        boolean canLocateMailbox,

        Movement movement,
        Map<String, Object> renderer

) {

}
