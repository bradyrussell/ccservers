package com.bradyrussell.ccservers.items.modules;

import com.bradyrussell.ccservers.entities.TileEntityServerChassis;
import com.bradyrussell.ccservers.items.EServerModuleType;
import com.bradyrussell.ccservers.items.ItemServerModuleBase;
import com.bradyrussell.ccservers.items.ItemServerPoweredLuaModuleBase;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemTestConsumptionModule extends ItemServerPoweredLuaModuleBase {
    public ItemTestConsumptionModule(EServerModuleType type) {
        super(type);
    }

    @Override
    public int getModuleServerEnergyCapacity(ItemStack moduleItem) {
        return 0;
    }

    @Override
    public String[] getMethodNames() {
        return new String[]{"consumeEnergy"};
    }

    @Override
    public Object[] callMethod(ItemStack moduleStack, IComputerAccess computer, @Nonnull ILuaContext luaContext, int method, @Nonnull Object[] arguments) throws LuaException, InterruptedException {
        if (arguments.length < 1) throw new LuaException("Expected 1 parameter");
        if (!(arguments[0] instanceof Double)) throw new LuaException("Expected integer for parameter 1");
        int consumed = extractEnergy(moduleStack, (int) Math.floor((Double) arguments[0]));

        return new Object[]{"Consumed "+consumed};
    }

}
