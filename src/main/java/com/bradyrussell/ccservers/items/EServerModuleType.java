package com.bradyrussell.ccservers.items;

import com.bradyrussell.ccservers.items.modules.*;

import java.util.function.Function;

public enum EServerModuleType {
    F(ItemTestConsumptionModule::new, 1000, 10),
    TEST_POWER_BATTERY(ItemTestCapacityModule::new),
    TEST_POWER_OUTPUT(ItemEnergyOutputModule::new),
    TEST_BACKUP_POWER_SUPPLY(ItemBackupPowerSupplyModule::new, 5000, 20),
    CREATIVE_POWER(ItemCreativePowerModule::new);

    public final String registryName;
    public ItemServerModuleBase moduleInstance;
    public final int energyBuffer, maxDraw;

    private final Function<EServerModuleType,ItemServerModuleBase> instantiator;

    EServerModuleType(Function<EServerModuleType,ItemServerModuleBase> instantiator) {
        this.instantiator = instantiator;
        this.registryName = "module_"+name().toLowerCase();
        energyBuffer = 0;
        maxDraw = 0;
    }

    EServerModuleType(Function<EServerModuleType,ItemServerModuleBase> instantiator, int buffer, int maxDraw) {
        this.instantiator = instantiator;
        this.registryName = "module_"+name().toLowerCase();
        energyBuffer = buffer;
        this.maxDraw = maxDraw;
    }

    public static void InstantiateModules(){
        for(EServerModuleType type:EServerModuleType.values()) {
            type.moduleInstance = type.instantiator.apply(type);
            type.moduleInstance.setUnlocalizedName(type.registryName + "_unlocalised_name");
            type.moduleInstance.setRegistryName(type.registryName);
        }
    }

}
