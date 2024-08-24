package org.antarcticgardens.faunamail.mailman.displays;

import org.antarcticgardens.faunamail.mailman.Mailman;
import org.antarcticgardens.faunamail.mailman.MailmanRegister;

import java.util.Map;

public class ItemDisplayDisplay extends MailItemDisplay {

    float x,  y,  z,  scale,  rotX,  rotY,  rotZ;

    public ItemDisplayDisplay(float x, float y, float z, float scale, float rotX, float rotY, float rotZ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.scale = scale;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
    }

    public static MailItemDisplay creator(Map<String, Object> properties) {
        return new ItemDisplayDisplay(
                MailmanRegister.getFloat(properties, "x", 0.0f),
                MailmanRegister.getFloat(properties, "y", 0.0f),
                MailmanRegister.getFloat(properties, "z", 0.0f),

                MailmanRegister.getFloat(properties, "scale", 0.0f),

                MailmanRegister.getFloat(properties, "rot_x", 0.0f),
                MailmanRegister.getFloat(properties, "rot_y", 0.0f),
                MailmanRegister.getFloat(properties, "rot_z", 0.0f)
        );
    }


}
