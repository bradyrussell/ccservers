package com.bradyrussell.ccservers.items;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ModuleEnergy {

    public static NBTTagCompound getNBT(ItemStack itemStack){
        if(itemStack.getTagCompound() != null) return itemStack.getTagCompound();
        NBTTagCompound nbt = new NBTTagCompound();
        itemStack.setTagCompound(nbt);
        return nbt;
    }

    public static int getEnergy(ItemStack moduleItem){
        return getNBT(moduleItem).getInteger("module_stored_energy");
    }

    public static void setEnergy(ItemStack moduleItem, int energy){
        getNBT(moduleItem).setInteger("module_stored_energy", energy);
    }

    public static int receiveEnergy(ItemStack moduleItem, int maxReceive) {
        int oldEnergy = getEnergy(moduleItem);
        setEnergy(moduleItem, Math.min(oldEnergy + maxReceive, getMaxEnergyStored(moduleItem)));
        return getEnergy(moduleItem) - oldEnergy;
    }


    public static int extractEnergy(ItemStack moduleItem, int maxExtract) {
        int oldEnergy = getEnergy(moduleItem);
        setEnergy(moduleItem, Math.max(oldEnergy - maxExtract, 0));
        return oldEnergy - getEnergy(moduleItem);
    }


    public static int getMaxEnergyStored(ItemStack moduleItem) {
        return ((ItemServerModuleBase)moduleItem.getItem()).type.energyBuffer;
    }


    public static boolean canExtract(ItemStack moduleItem) {
        return getEnergy(moduleItem) > 0;
    }


    public static boolean canReceive(ItemStack moduleItem) {
        return getEnergy(moduleItem) < getMaxEnergyStored(moduleItem);
    }
}
