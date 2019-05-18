package com.bradyrussell.ccservers.entities;

public enum EServerRedstoneBehavior {
    DO_NOTHING,
    TURN_ON,
    TURN_OFF;

    public static EServerRedstoneBehavior getNext(EServerRedstoneBehavior current){
        return EServerRedstoneBehavior.values()[(current.ordinal()+1)%EServerRedstoneBehavior.values().length];
    }
}
