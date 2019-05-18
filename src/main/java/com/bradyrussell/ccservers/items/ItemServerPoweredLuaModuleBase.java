package com.bradyrussell.ccservers.items;

import com.bradyrussell.ccservers.entities.TileEntityServerChassis;
import net.minecraft.item.ItemStack;

public abstract class ItemServerPoweredLuaModuleBase extends ItemServerLuaModuleBase {
    protected static final int energyDurabilityBarFactor = 1000;

    public ItemServerPoweredLuaModuleBase(EServerModuleType type) {
        super(type);
        if (type.energyBuffer > 0) setMaxDamage(energyDurabilityBarFactor);
    }

    public static int receiveEnergy(ItemStack moduleItem, int maxReceive) {
        final int receiveEnergy = ModuleEnergy.receiveEnergy(moduleItem, maxReceive);
        updatePowerBar(moduleItem);
        return receiveEnergy;
    }

    public static int extractEnergy(ItemStack moduleItem, int maxExtract) {
        final int extractEnergy = ModuleEnergy.extractEnergy(moduleItem, maxExtract);
        updatePowerBar(moduleItem);
        return extractEnergy;
    }

    public static void setModuleCurrentDraw(ItemStack itemStack, int currentDraw) {
        ModuleEnergy.getNBT(itemStack).setInteger("module_current_draw", Math.min(currentDraw, ((ItemServerModuleBase) itemStack.getItem()).type.maxDraw));
    }

    public static int getModuleCurrentDraw(ItemStack itemStack) {
        return ModuleEnergy.getNBT(itemStack).getInteger("module_current_draw");
    }

    public static void updatePowerBar(ItemStack itemStack) {
        itemStack.getItem().setDamage(itemStack, (int) (energyDurabilityBarFactor - ((double) ModuleEnergy.getEnergy(itemStack) / ModuleEnergy.getMaxEnergyStored(itemStack) * energyDurabilityBarFactor))+1);
    }

    @Override
    public void onTick(ItemStack moduleItem, TileEntityServerChassis serverChassis) {
        handleCharging(moduleItem, serverChassis);
        int storedEnergy = ModuleEnergy.getEnergy(moduleItem);

        if (storedEnergy < ModuleEnergy.getMaxEnergyStored(moduleItem)) {
            setModuleCurrentDraw(moduleItem, Math.min(ModuleEnergy.getMaxEnergyStored(moduleItem) - storedEnergy, type.maxDraw));
        } else {
            setModuleCurrentDraw(moduleItem, 0);
        }

    }

    public static void handleCharging(ItemStack moduleItem, TileEntityServerChassis serverChassis) { // can be changed to use receiveEnergy?
        if (getModuleCurrentDraw(moduleItem) > 0 && serverChassis.isCurrentlyPowered()) {
            receiveEnergy(moduleItem, getModuleCurrentDraw(moduleItem));
            //ModuleEnergy.setEnergy(moduleItem, Math.min(getModuleCurrentDraw(moduleItem) + ModuleEnergy.getEnergy(moduleItem), ModuleEnergy.getMaxEnergyStored(moduleItem)));
            //updatePowerBar(moduleItem);
        }
        setModuleCurrentDraw(moduleItem, 0);
    }

    @Override
    public int getCurrentEnergyConsumption(ItemStack moduleItem) {
        return getModuleCurrentDraw(moduleItem);
    }
}
