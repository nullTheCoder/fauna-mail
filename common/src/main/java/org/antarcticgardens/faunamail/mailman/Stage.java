package org.antarcticgardens.faunamail.mailman;

public enum Stage {
    ANIMATION_OUT(0),
    DELIVERING(1),
    ANIMATION_IN(2),
    ANIMATION_OUT_RETURN(3),
    ANIMATION_IN_RETURN(4);

    private final int i;
    Stage(int i) {
        this.i = i;
    }

    public int getValue() {
        return i;
    }

    public static Stage fromInt(int value) {
        switch (value) {
            case 0 -> {return ANIMATION_OUT;}
            case 1 -> {return DELIVERING;}
            case 2 -> {return ANIMATION_IN;}
            case 3 -> {return ANIMATION_OUT_RETURN;}
            case 4 -> {return ANIMATION_IN_RETURN;}
        };
        return ANIMATION_OUT;
    }

}
