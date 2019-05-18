package com.bradyrussell.ccservers.items;

import com.bradyrussell.ccservers.CCServers;
import com.bradyrussell.ccservers.entities.TileEntityServerChassis;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class ItemServerModuleBase extends Item {
    public EServerModuleType type;

    public ItemServerModuleBase(EServerModuleType type) {
        this.type = type;
        this.setCreativeTab(CCServers.ccserversTab);
        this.setMaxStackSize(1);
    }

    public abstract int getCurrentEnergyConsumption(ItemStack moduleItem);

    public abstract int getModuleServerEnergyCapacity(ItemStack moduleItem);

    public abstract void onTick(ItemStack moduleItem, TileEntityServerChassis serverChassis);


}
