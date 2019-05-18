package com.bradyrussell.ccservers;

public class EnergyDisplayAmount{
    public final double CONVERSION_FACTOR = 25.0;

    public EnergyDisplayAmount(short amount, short suffix) {
        this.suffix = EEnergyUnit.values()[suffix];
        this.amount = (double)amount / CONVERSION_FACTOR;
    }

    public EnergyDisplayAmount(double amount, EEnergyUnit suffix) {
        this.suffix = suffix;
        this.amount = amount;
    }

    public static EnergyDisplayAmount fromEnergyAmount(int energy){
        if(energy > EEnergyUnit.BILLION.unit) return new EnergyDisplayAmount((double) energy / (double) EEnergyUnit.BILLION.unit,EEnergyUnit.BILLION);
        if(energy > EEnergyUnit.MILLION.unit) return new EnergyDisplayAmount((double) energy / (double) EEnergyUnit.MILLION.unit,EEnergyUnit.MILLION);
        if(energy > EEnergyUnit.THOUSAND.unit) return new EnergyDisplayAmount((double) energy / (double) EEnergyUnit.THOUSAND.unit,EEnergyUnit.THOUSAND);
        if(energy > EEnergyUnit.NONE.unit) return new EnergyDisplayAmount((double) energy / (double) EEnergyUnit.NONE.unit,EEnergyUnit.NONE);
        //if(energy > 1) return new EnergyDisplayAmount((double) energy,EEnergyUnit.NONE);
        return new EnergyDisplayAmount(0,EEnergyUnit.NONE);
    }

    public short getSuffixShort() {
        return (short) suffix.ordinal();
    }

    public short getAmountShort() {
        return (short) (amount * CONVERSION_FACTOR);
    }

    public EEnergyUnit getSuffix() {
        return suffix;
    }

    public double getAmount() {
        return amount;
    }

    public String getDisplayString(){
        return getAmount()+" "+getSuffix().suffix;
    }

    public void setSuffixFromShort(short suffix) {
        this.suffix = EEnergyUnit.values()[suffix];
    }

    public void setAmountFromShort(short amount) {
        this.amount = (double)amount / CONVERSION_FACTOR;
    }

    EEnergyUnit suffix;
    double amount;
}
