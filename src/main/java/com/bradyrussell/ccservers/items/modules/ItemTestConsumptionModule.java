package com.bradyrussell.ccservers.items.modules;

import com.bradyrussell.ccservers.entities.TileEntityServerChassis;
import com.bradyrussell.ccservers.items.EServerModuleType;
import com.bradyrussell.ccservers.items.ItemServerModuleBase;
import net.minecraft.item.ItemStack;

public class ItemTestConsumptionModule extends ItemServerModuleBase {
    public ItemTestConsumptionModule(EServerModuleType type) {
        super(type);
    }

    @Override
    public int getCurrentEnergyConsumption(ItemStack moduleItem) {
        return 10;
    }


    @Override
    public int getModuleEnergyCapacity(ItemStack moduleItem) {
        return 0;
    }

    @Override
    public void onTick(ItemStack moduleItem, TileEntityServerChassis serverChassis) {

    }

}
