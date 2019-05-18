package com.bradyrussell.ccservers.items;

import com.bradyrussell.ccservers.CCServers;
import com.bradyrussell.ccservers.items.modules.*;
import com.bradyrussell.ccservers.items.modules.powered.ItemBackupPowerSupplyModule;
import com.bradyrussell.ccservers.items.modules.powered.ItemCryptoAuthenticationDatabaseModule;
import com.bradyrussell.ccservers.items.modules.powered.ItemCryptoHashModule;
import com.bradyrussell.ccservers.items.modules.powered.ItemTestConsumptionModule;

import java.util.function.Function;

public enum EServerModuleType {
    F(ItemTestConsumptionModule::new, 1000 * CCServers.RF_MULTIPLIER, 10 * CCServers.RF_MULTIPLIER),
    CRYPTO_HASH_PROVIDER(ItemCryptoHashModule::new, 2560 * CCServers.RF_MULTIPLIER, 10 * CCServers.RF_MULTIPLIER),
    CRYPTO_AUTHENTICATION_DATABASE(ItemCryptoAuthenticationDatabaseModule::new, 4096 * CCServers.RF_MULTIPLIER, 20 * CCServers.RF_MULTIPLIER),
    TEST_POWER_BATTERY(ItemTestCapacityModule::new),
    TEST_POWER_OUTPUT(ItemEnergyOutputModule::new),
    TEST_BACKUP_POWER_SUPPLY(ItemBackupPowerSupplyModule::new, 5000 * CCServers.RF_MULTIPLIER, 20 * CCServers.RF_MULTIPLIER),
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
