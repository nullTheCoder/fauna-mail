package org.antarcticgardens.faunamail.mailman.movement;

import org.antarcticgardens.faunamail.mailman.MailmanRegister;

import java.util.Map;

public class FlyUpDownMovement extends Movement {

    public final float distance;

    public FlyUpDownMovement(float distance) {
        this.distance = distance;
    }

    public static Movement creator(Map<String, Object> properties) {
        return new FlyUpDownMovement(MailmanRegister.getFloat(properties, "distance", 3));
    }

}
