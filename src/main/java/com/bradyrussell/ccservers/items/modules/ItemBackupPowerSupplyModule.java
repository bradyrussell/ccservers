package com.bradyrussell.ccservers.items.modules;

import com.bradyrussell.ccservers.entities.TileEntityServerChassis;
import com.bradyrussell.ccservers.items.EServerModuleType;
import com.bradyrussell.ccservers.items.ItemServerModuleBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemBackupPowerSupplyModule extends ItemServerModuleBase {
    //private int currentDraw = 0;
    private static final int maxCurrentDraw = 20;
    private static final int energyDurabilityBarFactor = 1000;

    private static final double BACKUP_PSU_ACTIVATION_THRESHOLD = .05;

    public ItemBackupPowerSupplyModule(EServerModuleType type) {
        super(type);
        setMaxDamage(energyDurabilityBarFactor);
    }

    @Override
    public int getCurrentEnergyConsumption(ItemStack moduleItem) {
        return getItemCurrentDraw(moduleItem);
    }

    @Override
    public int getModuleEnergyCapacity(ItemStack moduleItem) {
        return 0;
    }

    @Override
    public void onTick(ItemStack moduleItem, TileEntityServerChassis serverChassis) {
        int storedEnergy = getStoredEnergy(moduleItem);

        if (getItemCurrentDraw(moduleItem) > 0 && serverChassis.isCurrentlyPowered) {
            setStoredEnergy(moduleItem, Math.min(getItemCurrentDraw(moduleItem) + storedEnergy, getEnergyCapacity()));
        }

        setItemCurrentDraw(moduleItem,0);

        if(serverChassis.energyStorage.getEnergyStored() < serverChassis.energyStorage.getMaxEnergyStored()*BACKUP_PSU_ACTIVATION_THRESHOLD){
            // release power back to the server
            final int energy = getStoredEnergy(moduleItem);

            if(energy > 0) setStoredEnergy(moduleItem, energy - serverChassis.energyStorage.receiveEnergy(Math.min(maxCurrentDraw*5, energy), false));

        } else {
            if (storedEnergy < getEnergyCapacity()) {
                setItemCurrentDraw(moduleItem,Math.min(getEnergyCapacity() - storedEnergy, maxCurrentDraw));
            } else {
                setItemCurrentDraw(moduleItem,0);
            }
        }
    }

    public int getEnergyCapacity(){
        switch (type){
            case TEST_BACKUP_POWER_SUPPLY:
                return 5000;
            default:
                return 0;
        }
    }

    public static void setStoredEnergy(ItemStack itemStack, int energy){
        itemStack.getItem().setDamage(itemStack, (int) (energyDurabilityBarFactor-((double)energy/((ItemBackupPowerSupplyModule)itemStack.getItem()).getEnergyCapacity()*energyDurabilityBarFactor)));
        itemStack.setStackDisplayName("Backup PSU: "+energy+" / "+((ItemBackupPowerSupplyModule)itemStack.getItem()).getEnergyCapacity()+" RF");
        getNBT(itemStack).setInteger("stored_energy", energy);
    }

    public static int getStoredEnergy(ItemStack itemStack){
        return getNBT(itemStack).getInteger("stored_energy");
    }

    public static void setItemCurrentDraw(ItemStack itemStack, int currentDraw){
        getNBT(itemStack).setInteger("current_draw", currentDraw);
    }

    public static int getItemCurrentDraw(ItemStack itemStack){
        return getNBT(itemStack).getInteger("current_draw");
    }

    public static NBTTagCompound getNBT(ItemStack itemStack){
        if(itemStack.getTagCompound() != null) return itemStack.getTagCompound();
        NBTTagCompound nbt = new NBTTagCompound();
        itemStack.setTagCompound(nbt);
        itemStack.getItem().setDamage(itemStack, energyDurabilityBarFactor);
        return nbt;
    }
}
