package org.antarcticgardens.faunamail.tracker;

import java.util.ArrayList;
import java.util.List;

public class Names {
    public static List<String> names = new ArrayList<>();

    public static String getName(String base) {
        var mailboxes = StateManager.getMailBoxes();
        if (mailboxes.containsKey(base)) {
            return getName(base, 1);
        }
        return base;
    }

    public static String getName(String base, int num) {
        String original = base;
        base = base + " " + num;
        if (num > 999) {
            return base + "+";
        }
        var mailboxes = StateManager.getMailBoxes();
        if (mailboxes.containsKey(base)) {
            return getName(original, num + 1);
        }
        return base;
    }
}
