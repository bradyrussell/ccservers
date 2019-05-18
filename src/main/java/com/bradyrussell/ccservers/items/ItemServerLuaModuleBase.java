package com.bradyrussell.ccservers.items;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.*;

public abstract class ItemServerLuaModuleBase extends ItemServerModuleBase {
    public ItemServerLuaModuleBase(EServerModuleType type) {
        super(type);
    }

    public abstract String[] getMethodNames();

    public abstract Object[] callMethod(ItemStack moduleStack, IComputerAccess computer, @Nonnull ILuaContext luaContext, int method, @Nonnull Object[] arguments) throws LuaException, InterruptedException;

}
