package com.bradyrussell.ccservers;

public enum EEnergyUnit {
    NONE("",1),
    THOUSAND("k",1000),
    MILLION("M",1000000),
    BILLION("B",1000000000);

    final String suffix;
    final long unit;

    EEnergyUnit(String suffix, long unit) {
        this.suffix = suffix;
        this.unit = unit;
    }

    @Override
    public String toString() {
        return suffix;
    }
}
