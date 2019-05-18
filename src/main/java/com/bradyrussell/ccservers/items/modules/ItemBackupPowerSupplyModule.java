package com.bradyrussell.ccservers.items.modules;

import com.bradyrussell.ccservers.entities.TileEntityServerChassis;
import com.bradyrussell.ccservers.items.EServerModuleType;
import com.bradyrussell.ccservers.items.ItemServerPoweredLuaModuleBase;
import com.bradyrussell.ccservers.items.ModuleEnergy;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemBackupPowerSupplyModule extends ItemServerPoweredLuaModuleBase {

    private static final double BACKUP_PSU_ACTIVATION_THRESHOLD = .05;

    public ItemBackupPowerSupplyModule(EServerModuleType type) {
        super(type);
        setMaxDamage(energyDurabilityBarFactor);
    }

    @Override
    public int getModuleServerEnergyCapacity(ItemStack moduleItem) {
        return 0;
    }

    @Override
    public void onTick(ItemStack moduleItem, TileEntityServerChassis serverChassis) {  // this module has custom behavior because it doesnt just charge, it also releases
        ItemServerPoweredLuaModuleBase.handleCharging(moduleItem, serverChassis);
        int storedEnergy = ModuleEnergy.getEnergy(moduleItem);

        if (serverChassis.getEnergyStorage().getEnergyStored() < serverChassis.getEnergyStorage().getMaxEnergyStored() * BACKUP_PSU_ACTIVATION_THRESHOLD) {
            // release power back to the server

            final int energy = ModuleEnergy.getEnergy(moduleItem);

            if (energy > 0)
                serverChassis.getEnergyStorage().receiveEnergy(extractEnergy(moduleItem, type.maxDraw * 5),false);

        } else {
            if (storedEnergy < ModuleEnergy.getMaxEnergyStored(moduleItem)) {
                setModuleCurrentDraw(moduleItem, Math.min(ModuleEnergy.getMaxEnergyStored(moduleItem) - storedEnergy, type.maxDraw));
            } else {
                setModuleCurrentDraw(moduleItem, 0);
            }
        }
    }

    /* Computercraft Module Lua */

    @Override
    public String[] getMethodNames() {
        return new String[]{"getStoredEnergy", "setStoredEnergy"};
    }

    @Override
    public Object[] callMethod(ItemStack moduleStack, IComputerAccess computer, @Nonnull ILuaContext luaContext, int method, @Nonnull Object[] arguments) throws LuaException, InterruptedException {
        switch (method) {
            case 0: {
                return new Object[]{ModuleEnergy.getEnergy(moduleStack), moduleStack.getDisplayName()};
            }

            case 6: {

            }
            default: {
                throw new LuaException("not yet implemented");
            }
        }

    }

}
