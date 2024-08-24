package org.antarcticgardens.faunamail.mailman.displays;

import java.util.Map;

public abstract class MailItemDisplay {

    public interface displayCreator {
        MailItemDisplay create(Map<String, Object> json);
    }
}
