package org.antarcticgardens.faunamail.mailman.movement;

import java.util.Map;

public abstract class Movement {

    public interface movementCreator {
        Movement create(Map<String, Object> json);
    }
}
