package com.bradyrussell.ccservers.items.modules.powered;

import com.bradyrussell.ccservers.CCServers;
import com.bradyrussell.ccservers.items.EServerModuleType;
import com.bradyrussell.ccservers.items.ItemServerPoweredLuaModuleBase;
import com.bradyrussell.ccservers.items.ModuleEnergy;
import com.bradyrussell.ccservers.serverfunctions.ServerCrypto;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemCryptoHashModule extends ItemServerPoweredLuaModuleBase {
    public ItemCryptoHashModule(EServerModuleType type) {
        super(type);
    }


    private final static int RF_PER_CHARACTER = 10 * CCServers.RF_MULTIPLIER;

    @Override
    public int getModuleServerEnergyCapacity(ItemStack moduleItem) {
        return 0;
    }

    @Override
    public String[] getMethodNames() {
        return new String[]{"hash"};
    }

    @Override
    public Object[] callMethod(ItemStack moduleStack, IComputerAccess computer, @Nonnull ILuaContext luaContext, int method, @Nonnull Object[] arguments) throws LuaException, InterruptedException {
        if (arguments.length != 1) throw new LuaException("Expected 1 parameter: hash(String input)");
        if (!(arguments[0] instanceof String)) throw new LuaException("Expected string for parameter 1: hash(String input)");

        String input = (String) arguments[0];

        final int extract = input.length() * RF_PER_CHARACTER;
        if(extract > ModuleEnergy.getEnergy(moduleStack)) throw new LuaException("Insufficient power to hash "+input.length()+" characters.");
        int consumed = extractEnergy(moduleStack, extract);

        if(consumed < extract) throw new LuaException("Failed to hash "+input.length()+" characters.");

        return new Object[]{ServerCrypto.sha256(input)};
    }

}
