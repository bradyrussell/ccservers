package com.bradyrussell.ccservers.entities;

public enum EServerRedstoneBehavior {
    DO_NOTHING,
    NORMAL,
    INVERTED,
    ENABLE_ONLY,
    DISABLE_ONLY,
    TOGGLE_ON_CHANGE;

    public static EServerRedstoneBehavior getNext(EServerRedstoneBehavior current){
        return EServerRedstoneBehavior.values()[(current.ordinal()+1)%EServerRedstoneBehavior.values().length];
    }
}
