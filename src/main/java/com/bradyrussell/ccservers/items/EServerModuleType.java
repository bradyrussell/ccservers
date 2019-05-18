package com.bradyrussell.ccservers.items;

import com.bradyrussell.ccservers.items.modules.*;

import java.util.function.Function;

public enum EServerModuleType {
    F(ItemTestConsumptionModule::new),
    TEST_POWER_BATTERY(ItemTestCapacityModule::new),
    TEST_POWER_OUTPUT(ItemEnergyOutputModule::new),
    TEST_BACKUP_POWER_SUPPLY(ItemBackupPowerSupplyModule::new),
    CREATIVE_POWER(ItemCreativePowerModule::new);

    public final String registryName;
    public ItemServerModuleBase moduleInstance;

    private final Function<EServerModuleType,ItemServerModuleBase> instantiator;

    EServerModuleType(Function<EServerModuleType,ItemServerModuleBase> instantiator) {
        this.instantiator = instantiator;
        this.registryName = "module_"+name().toLowerCase();
    }

    public static void InstantiateModules(){
        for(EServerModuleType type:EServerModuleType.values()) {
            type.moduleInstance = type.instantiator.apply(type);
            type.moduleInstance.setUnlocalizedName(type.registryName + "_unlocalised_name");
            type.moduleInstance.setRegistryName(type.registryName);
        }
    }

}
