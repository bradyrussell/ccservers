package com.bradyrussell.ccservers.entities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;

public class ServerEnergyStorage extends EnergyStorage implements INBTSerializable<NBTTagCompound> {

    public ServerEnergyStorage(int capacity) {
        super(capacity);
        this.maxExtract = (int) (capacity * .05);
        this.maxReceive = capacity;

    }

    public ServerEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public ServerEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public ServerEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return super.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return super.extractEnergy(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return super.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return super.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return super.canExtract();
    }

    @Override
    public boolean canReceive() {
        return super.canReceive();
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();

        //nbtTagCompound.setInteger("ServerEnergy_capacity",capacity != 0?capacity:-200);

        nbtTagCompound.setInteger("ServerEnergy_maxReceive",maxReceive);
        nbtTagCompound.setInteger("ServerEnergy_maxExtract",maxExtract);
        nbtTagCompound.setInteger("ServerEnergy_energy",energy);

        return nbtTagCompound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        maxReceive = nbt.getInteger("ServerEnergy_maxReceive");
        maxExtract = nbt.getInteger("ServerEnergy_maxExtract");
        if( nbt.hasKey("ServerEnergy_energy"))
            setEnergyStored(nbt.getInteger("ServerEnergy_energy"));
    }

    public void setEnergyStored(int i) {
        energy = Math.max(0,Math.min(i, getMaxEnergyStored()));
    }

    public void setCapacity(int capacity){
        this.capacity = capacity;
    }
}
