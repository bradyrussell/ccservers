package com.bradyrussell.ccservers.computercraft;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.item.ItemStack;

public class CCUtil {
    public static LuaObjectPeripheralWrap[] wrapPeripheral(IPeripheral peripheral, IComputerAccess computer){
        return new LuaObjectPeripheralWrap[]{new LuaObjectPeripheralWrap(peripheral,computer)};
    }

    public static LuaObjectModuleWrapper[] wrapModule(ItemStack module, IComputerAccess computer){
        return new LuaObjectModuleWrapper[]{new LuaObjectModuleWrapper(module, computer)};
    }
}
