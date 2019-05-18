package com.bradyrussell.ccservers.items.modules;

import com.bradyrussell.ccservers.entities.TileEntityServerChassis;
import com.bradyrussell.ccservers.items.EServerModuleType;
import com.bradyrussell.ccservers.items.ItemServerModuleBase;
import net.minecraft.item.ItemStack;

public class ItemEnergyOutputModule extends ItemServerModuleBase {
    public ItemEnergyOutputModule(EServerModuleType type) {
        super(type);
    }

    @Override
    public int getCurrentEnergyConsumption(ItemStack moduleItem) {
        return 0;
    }

    @Override
    public int getModuleEnergyCapacity(ItemStack moduleItem) {
        return 0;
    }

    @Override
    public void onTick(ItemStack moduleItem, TileEntityServerChassis serverChassis) {

    }

    public int getCurrentEnergyOutput(ItemStack moduleItem) {
        return 10;
    }
}
