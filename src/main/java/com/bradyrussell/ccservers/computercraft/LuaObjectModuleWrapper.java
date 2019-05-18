package com.bradyrussell.ccservers.computercraft;

import com.bradyrussell.ccservers.CCServers;
import com.bradyrussell.ccservers.items.ItemServerLuaModuleBase;
import com.bradyrussell.ccservers.items.ItemServerModuleBase;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.ILuaObject;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LuaObjectModuleWrapper implements ILuaObject {
    private IComputerAccess computer;
    private ItemStack moduleStack;
    private ItemServerLuaModuleBase moduleItem;

    public LuaObjectModuleWrapper(ItemStack module, IComputerAccess computer) {
        if(!(module.getItem() instanceof ItemServerLuaModuleBase)) {
            throw new IllegalArgumentException("LuaObjectModuleWrapper: Tried to wrap non Lua-module item "+module.getDisplayName());
        }
        this.computer = computer;
        this.moduleStack = module;
        this.moduleItem = (ItemServerLuaModuleBase) module.getItem();
    }

    @Nonnull
    @Override
    public String[] getMethodNames() {
        return moduleItem.getMethodNames();
    }

    @Nullable
    @Override
    public Object[] callMethod(@Nonnull ILuaContext luaContext, int method, @Nonnull Object[] arguments) throws LuaException, InterruptedException {
        if(moduleStack.isEmpty()) throw new LuaException("Lost connection to "+ CCServers.prependModID(moduleItem.type.registryName)+". It may have been moved.");
        return moduleItem.callMethod(moduleStack, computer, luaContext, method, arguments);
    }

}
