package com.bradyrussell.ccservers.items.modules;

import com.bradyrussell.ccservers.CCServers;
import com.bradyrussell.ccservers.entities.TileEntityServerChassis;
import com.bradyrussell.ccservers.items.EServerModuleType;
import com.bradyrussell.ccservers.items.ItemServerModuleBase;
import net.minecraft.item.ItemStack;

public class ItemTestCapacityModule extends ItemServerModuleBase {
    public ItemTestCapacityModule(EServerModuleType type) {
        super(type);
    }

    @Override
    public int getCurrentEnergyConsumption(ItemStack moduleItem) {
        return 0 * CCServers.RF_MULTIPLIER;
    }


    @Override
    public int getModuleServerEnergyCapacity(ItemStack moduleItem) {
        return 5000 * CCServers.RF_MULTIPLIER;
    }

    @Override
    public void onTick(ItemStack moduleItem, TileEntityServerChassis serverChassis) {

    }

}
