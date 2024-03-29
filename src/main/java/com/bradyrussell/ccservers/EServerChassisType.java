package com.bradyrussell.ccservers;

public enum EServerChassisType {
    IRON("Iron Server Chassis",4, 10000 * CCServers.RF_MULTIPLIER),
    GOLD("Gold Server Chassis",8, 50000 * CCServers.RF_MULTIPLIER),
    DIAMOND("Diamond Server Chassis",16, 100000 * CCServers.RF_MULTIPLIER);

    final public String registryName;
    final public int availableSlots, baseCapacity;
    final public String title;

    EServerChassisType(String title, int availableSlots, int baseCapacity) {
        this.title = title;
        this.baseCapacity = baseCapacity;
        this.registryName = "server_"+name().toLowerCase();
        this.availableSlots = availableSlots;
    }
}

