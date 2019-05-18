package com.bradyrussell.ccservers.items.modules;

import com.bradyrussell.ccservers.entities.TileEntityServerChassis;
import com.bradyrussell.ccservers.items.EServerModuleType;
import com.bradyrussell.ccservers.items.ItemServerModuleBase;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemEnergyOutputModule extends ItemServerModuleBase {
    public ItemEnergyOutputModule(EServerModuleType type) {
        super(type);
    }

    @Override
    public int getCurrentEnergyConsumption(ItemStack moduleItem) {
        return 0;
    }

    @Override
    public int getModuleServerEnergyCapacity(ItemStack moduleItem) {
        return 0;
    }

    @Override
    public void onTick(ItemStack moduleItem, TileEntityServerChassis serverChassis) {

    }

    public int getCurrentEnergyOutput(ItemStack moduleItem) {
        return 10;
    }


}
