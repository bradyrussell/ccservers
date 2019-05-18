package com.bradyrussell.ccservers.entities;

import com.bradyrussell.ccservers.EServerChassisType;
import com.bradyrussell.ccservers.EnergyDisplayAmount;
import com.bradyrussell.ccservers.blocks.BlockServerChassis;
import com.bradyrussell.ccservers.items.ItemServerModuleBase;
import com.bradyrussell.ccservers.items.modules.ItemEnergyOutputModule;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;

/**
 * User: brandon3055
 * Date: 06/01/2015
 * <p>
 * TileInventorySmelting is an advanced sided inventory that works like a vanilla furnace except that it has 5 input and output slots,
 * 4 fuel slots and cooks at up to four times the speed.
 * The input slots are used sequentially rather than in parallel, i.e. the first slot cooks, then the second, then the third, etc
 * The fuel slots are used in parallel.  The more slots burning in parallel, the faster the cook time.
 * The code is heavily based on TileEntityFurnace.
 */
public class TileEntityServerChassis extends TileEntity implements IInventory, ITickable {
    public final int BATTERY_SLOTS_COUNT = 1;
    public int SERVER_SLOTS_COUNT;

    public int TOTAL_SLOTS_COUNT;

    public final int POS_BATTERY_SLOT = 0;
    public final int POS_FIRST_SERVER_SLOT = POS_BATTERY_SLOT + BATTERY_SLOTS_COUNT;

    public EServerChassisType chassisType;
    public ServerEnergyStorage energyStorage;
    public EnergyDisplayAmount energyDisplay;

    public EnergyDisplayAmount energyConsumedDisplay = EnergyDisplayAmount.fromEnergyAmount(0);
    public EnergyDisplayAmount energyCapacityDisplay = EnergyDisplayAmount.fromEnergyAmount(0);

    public boolean isCurrentlyPowered = false;

    public EServerRedstoneBehavior redstoneBehavior = EServerRedstoneBehavior.DO_NOTHING;

    private int cachedNumberOfFilledServerSlots = -1;
    private ItemStack[] serverSlots;

    public double displayEnergyPct = .2;

    public TileEntityServerChassis() {
        chassisType = null;
    }

    public TileEntityServerChassis(EServerChassisType eServerChassisType) {
        setChassisType(eServerChassisType);
    }

    public void setChassisType(EServerChassisType eServerChassisType){
        chassisType = eServerChassisType;
        if(eServerChassisType != null) {
            energyStorage = new ServerEnergyStorage(chassisType.baseCapacity);
            energyDisplay = EnergyDisplayAmount.fromEnergyAmount(energyStorage.getEnergyStored());

            SERVER_SLOTS_COUNT = chassisType.availableSlots;
            TOTAL_SLOTS_COUNT = BATTERY_SLOTS_COUNT + SERVER_SLOTS_COUNT;

            serverSlots = new ItemStack[TOTAL_SLOTS_COUNT];

            clear();
        }
    }

    public double fractionOfEnergyStored() {
        double fraction = energyStorage.getEnergyStored() / (double) energyStorage.getMaxEnergyStored();
        return MathHelper.clamp(fraction, 0.0, 1.0);
    }

    private static int timer = 0;

/**     This method is called every tick to update the tile entity, i.e.
     - see if the fuel has run out, and if so turn the furnace "off" and slowly uncook the current item (if any)
     - see if any of the items have finished smelting
     It runs both on the server and the client.*/
    @Override
    public void update() {
        // when the number of burning slots changes, we need to force the block to re-render, otherwise the change in
        //   state will not be visible.  Likewise, we need to force a lighting recalculation.
        // The block update (for renderer) is only required on client side, but the lighting is required on both, since
        //    the client needs it for rendering and the server needs it for crop growth etc
        if (cachedNumberOfFilledServerSlots != numberOfModulesInstalled()) {
            cachedNumberOfFilledServerSlots = numberOfModulesInstalled();
            if (world.isRemote) {
                IBlockState iblockstate = this.world.getBlockState(pos);
                final int FLAGS = 3;  // I'm not sure what these flags do, exactly.
                world.notifyBlockUpdate(pos, iblockstate, iblockstate, FLAGS);
            } else {
                RecalculateEnergyCapacity();
            }
            //RecalculateEnergyCapacity();
            world.checkLightFor(EnumSkyBlock.BLOCK, pos);
        }


        if (!world.isRemote) { // running on server
            if (numberOfModulesInstalled() > 0) {
                int currentModuleEnergyConsumption = getCurrentModuleEnergyConsumption();
                int extractEnergy = energyStorage.extractEnergy(currentModuleEnergyConsumption, false);

                isCurrentlyPowered = extractEnergy >= currentModuleEnergyConsumption;

                int pushedEnergy = 0;
                int currentModuleEnergyOutput = getCurrentModuleEnergyOutput();

                if(currentModuleEnergyOutput > 0) {
                    pushedEnergy = pushEnergyToOutput(currentModuleEnergyOutput);
                }

                energyConsumedDisplay = EnergyDisplayAmount.fromEnergyAmount(pushedEnergy + extractEnergy);

                if(extractEnergy > 0 && isCurrentlyPowered && timer++%10==0) ((WorldServer)world).spawnParticle(EnumParticleTypes.REDSTONE,pos.getX()+.5,pos.getY()+1.5,pos.getZ()+.5, 1,.2,.2,.2,2.0,0,255,0);

                for(ItemStack module:serverSlots){
                    if(!module.isEmpty() && module.getItem() instanceof ItemServerModuleBase) {
                        ((ItemServerModuleBase) module.getItem()).onTick(module, this);
                    }
                }

            } else {
                isCurrentlyPowered = false;
                energyConsumedDisplay = EnergyDisplayAmount.fromEnergyAmount(0);
            }
        }
    }
    //private static int timer = 0;

    // Gets the number of slots in the inventory
    @Override
    public int getSizeInventory() {
        return serverSlots.length;
    }

    // returns true if all of the slots in the inventory are empty
    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : serverSlots) {
            if (!itemstack.isEmpty()) {  // isEmpty()
                return false;
            }
        }

        return true;
    }

    // Gets the stack in the given slot
    @Override
    public ItemStack getStackInSlot(int i) {
        return serverSlots[i];
    }

    /**
     * Removes some of the units from itemstack in the given slot, and returns as a separate itemstack
     *
     * @param slotIndex the slot number to remove the items from
     * @param count     the number of units to remove
     * @return a new itemstack containing the units removed from the slot
     */
    @Override
    public ItemStack decrStackSize(int slotIndex, int count) {
        ItemStack itemStackInSlot = getStackInSlot(slotIndex);
        if (itemStackInSlot.isEmpty()) return ItemStack.EMPTY;  //isEmpty(), EMPTY_ITEM

        ItemStack itemStackRemoved;
        if (itemStackInSlot.getCount() <= count) { //getStackSize
            itemStackRemoved = itemStackInSlot;
            setInventorySlotContents(slotIndex, ItemStack.EMPTY); // EMPTY_ITEM
        } else {
            itemStackRemoved = itemStackInSlot.splitStack(count);
            if (itemStackInSlot.getCount() == 0) { //getStackSize
                setInventorySlotContents(slotIndex, ItemStack.EMPTY); //EMPTY_ITEM
            }
        }
        markDirty();
        return itemStackRemoved;
    }

    // overwrites the stack in the given slotIndex with the given stack
    @Override
    public void setInventorySlotContents(int slotIndex, ItemStack itemstack) {
        serverSlots[slotIndex] = itemstack;
        if (!itemstack.isEmpty() && itemstack.getCount() > getInventoryStackLimit()) {  // isEmpty();  getStackSize()
            itemstack.setCount(getInventoryStackLimit());  //setStackSize()
        }
        markDirty();
    }

    // This is the maximum number if items allowed in each slot
    // This only affects things such as hoppers trying to insert items you need to use the container to enforce this for players
    // inserting items via the gui
    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    // Return true if the given player is able to use this block. In this case it checks that
    // 1) the world tileentity hasn't been replaced in the meantime, and
    // 2) the player isn't too far away from the centre of the block
    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        if (this.world.getTileEntity(this.pos) != this) return false;
        final double X_CENTRE_OFFSET = 0.5;
        final double Y_CENTRE_OFFSET = 0.5;
        final double Z_CENTRE_OFFSET = 0.5;
        final double MAXIMUM_DISTANCE_SQ = 8.0 * 8.0;
        return player.getDistanceSq(pos.getX() + X_CENTRE_OFFSET, pos.getY() + Y_CENTRE_OFFSET, pos.getZ() + Z_CENTRE_OFFSET) < MAXIMUM_DISTANCE_SQ;
    }


    // Return true if the given stack is allowed to be inserted in the given slot
    // Unlike the vanilla furnace, we allow anything to be placed in the fuel slots
    static public boolean isItemValidForServerModuleSlot(ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemServerModuleBase;
    }


    //------------------------------

    // This is where you save any data that you don't want to lose when the tile entity unloads
    // In this case, it saves the state of the furnace (burn time etc) and the itemstacks stored in the fuel, input, and output slots
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound parentNBTTagCompound) {
        super.writeToNBT(parentNBTTagCompound); // The super call is required to save and load the tiles location

        NBTTagList dataForAllSlots = new NBTTagList();
        for (int i = 0; i < this.serverSlots.length; ++i) {
            if (!this.serverSlots[i].isEmpty()) {  //isEmpty()
                NBTTagCompound dataForThisSlot = new NBTTagCompound();
                dataForThisSlot.setByte("Slot", (byte) i);
                this.serverSlots[i].writeToNBT(dataForThisSlot);
                dataForAllSlots.appendTag(dataForThisSlot);
            }
        }

        parentNBTTagCompound.setTag("Items", dataForAllSlots);
        parentNBTTagCompound.setTag("server_energy", energyStorage.serializeNBT());
        parentNBTTagCompound.setTag("chassis_type", new NBTTagByte((byte) chassisType.ordinal()));
        parentNBTTagCompound.setTag("chassis_rs_behavior", new NBTTagByte((byte) redstoneBehavior.ordinal()));

        return parentNBTTagCompound;
    }

    // This is where you load the data that you saved in writeToNBT
    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound); // The super call is required to save and load the tiles location

        if(chassisType == null){
            setChassisType(EServerChassisType.values()[(int)nbtTagCompound.getByte("chassis_type")]);
        }

        redstoneBehavior = EServerRedstoneBehavior.values()[(int)nbtTagCompound.getByte("chassis_rs_behavior")];

        final byte NBT_TYPE_COMPOUND = 10;       // See NBTBase.createNewByType() for a listing
        NBTTagList dataForAllSlots = nbtTagCompound.getTagList("Items", NBT_TYPE_COMPOUND);

        Arrays.fill(serverSlots, ItemStack.EMPTY);           // set all slots to empty EMPTY_ITEM
        for (int i = 0; i < dataForAllSlots.tagCount(); ++i) {
            NBTTagCompound dataForOneSlot = dataForAllSlots.getCompoundTagAt(i);
            byte slotNumber = dataForOneSlot.getByte("Slot");
            if (slotNumber >= 0 && slotNumber < this.serverSlots.length) {
                this.serverSlots[slotNumber] = new ItemStack(dataForOneSlot);
            }
        }

        energyStorage.deserializeNBT(nbtTagCompound.getCompoundTag("server_energy"));
        cachedNumberOfFilledServerSlots = -1;
    }

    //	// When the world loads from disk, the server needs to send the TileEntity information to the client
//	//  it uses getUpdatePacket(), getUpdateTag(), onDataPacket(), and handleUpdateTag() to do this
    @Override
    @Nullable
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound updateTagDescribingTileEntityState = getUpdateTag();
        final int METADATA = 0;
        return new SPacketUpdateTileEntity(this.pos, METADATA, updateTagDescribingTileEntityState);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound updateTagDescribingTileEntityState = pkt.getNbtCompound();
        handleUpdateTag(updateTagDescribingTileEntityState);
    }

    /* Creates a tag containing the TileEntity information, used by vanilla to transmit from server to client
       Warning - although our getUpdatePacket() uses this method, vanilla also calls it directly, so don't remove it.
     */
    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        writeToNBT(nbtTagCompound);
        return nbtTagCompound;
    }

    /* Populates this TileEntity with information from the tag, used by vanilla to transmit from server to client
     Warning - although our onDataPacket() uses this method, vanilla also calls it directly, so don't remove it.
   */
    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        this.readFromNBT(tag);
    }
    //------------------------

    // set all slots to empty
    @Override
    public void clear() {
        Arrays.fill(serverSlots, ItemStack.EMPTY);  //EMPTY_ITEM
    }

    // will add a key for this container to the lang file so we can name it in the GUI
    @Override
    public String getName() {
        return "container."+chassisType.registryName+".name";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    // standard code to look up what the human-readable name is
    @Nullable
    @Override
    public ITextComponent getDisplayName() {
        return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName());
    }

    // Fields are used to send non-inventory information from the server to interested clients
    // The container code caches the fields and sends the client any fields which have changed.
    // The field ID is limited to byte, and the field value is limited to short. (if you use more than this, they get cast down
    //   in the network packets)
    // If you need more than this, or shorts are too small, use a custom packet in your container instead.

    private static final int FIELD_COUNT = 7;
    private static final byte ENERGY_FIELD_ID = 0;
    private static final byte ENERGY_SUFFIX_FIELD_ID = 1;
    private static final byte ENERGY_PCT_FIELD_ID = 2;
    private static final byte ENERGY_CONSUME_FIELD_ID = 3;
    private static final byte ENERGY_CONSUME_SUFFIX_FIELD_ID = 4;
    private static final byte ENERGY_MAX_FIELD_ID = 5;
    private static final byte ENERGY_MAX_SUFFIX_FIELD_ID = 6;

    @Override
    public int getField(int id) {
        if (id == ENERGY_FIELD_ID) return EnergyDisplayAmount.fromEnergyAmount(energyStorage.getEnergyStored()).getAmountShort();

        if (id == ENERGY_SUFFIX_FIELD_ID) return EnergyDisplayAmount.fromEnergyAmount(energyStorage.getEnergyStored()).getSuffixShort();

        if (id == ENERGY_PCT_FIELD_ID) return (int)(fractionOfEnergyStored()*100);

        if (id == ENERGY_CONSUME_FIELD_ID) return energyConsumedDisplay.getAmountShort();
        if (id == ENERGY_CONSUME_SUFFIX_FIELD_ID) return energyConsumedDisplay.getSuffixShort();

        if (id == ENERGY_MAX_FIELD_ID) return energyCapacityDisplay.getAmountShort();
        if (id == ENERGY_MAX_SUFFIX_FIELD_ID) return energyCapacityDisplay.getSuffixShort();

        System.err.println("Invalid field ID in TileInventorySmelting.getField:" + id);
        return 0;
    }

    @Override
    public void setField(int id, int value) {
        if (id == ENERGY_FIELD_ID) {
            energyDisplay.setAmountFromShort((short) value);
        }
        else if (id == ENERGY_SUFFIX_FIELD_ID) {
            energyDisplay.setSuffixFromShort((short) value);
        }
        else if (id == ENERGY_PCT_FIELD_ID) {
            displayEnergyPct = value/100.0;
        } else if (id == ENERGY_CONSUME_FIELD_ID) {
            energyConsumedDisplay.setAmountFromShort((short) value);
        }
        else if (id == ENERGY_CONSUME_SUFFIX_FIELD_ID) {
            energyConsumedDisplay.setSuffixFromShort((short) value);
        }
        else if (id == ENERGY_MAX_FIELD_ID) {
            energyCapacityDisplay.setAmountFromShort((short) value);
        }
        else if (id == ENERGY_MAX_SUFFIX_FIELD_ID) {
            energyCapacityDisplay.setSuffixFromShort((short) value);
        }
    }

    @Override
    public int getFieldCount() {
        return FIELD_COUNT;
    }

    // -----------------------------------------------------------------------------------------------------------
    // The following methods are not needed for this example but are part of IInventory so they must be implemented

    // Unused unless your container specifically uses it.
    // Return true if the given stack is allowed to go in the given slot
    @Override
    public boolean isItemValidForSlot(int slotIndex, ItemStack itemstack) {
        return false;
    }

    /**
     * This method removes the entire contents of the given slot and returns it.
     * Used by containers such as crafting tables which return any items in their slots when you close the GUI
     *
     * @param slotIndex
     * @return
     */
    @Override
    public ItemStack removeStackFromSlot(int slotIndex) {
        ItemStack itemStack = getStackInSlot(slotIndex);
        if (!itemStack.isEmpty()) setInventorySlotContents(slotIndex, ItemStack.EMPTY);  //isEmpty();  EMPTY_ITEM
        return itemStack;
    }

    @Override
    public void openInventory(EntityPlayer player) {
        energyStorage.receiveEnergy(100,false);
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if(Objects.equals(facing, this.world.getBlockState(pos).getValue(BlockServerChassis.PROPERTYFACING)))
            return super.hasCapability(capability, facing);
        if(capability == CapabilityEnergy.ENERGY) return true;
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) { // todo: direction facing to be power output IF power output module is installed
        if(Objects.equals(facing, this.world.getBlockState(pos).getValue(BlockServerChassis.PROPERTYFACING)))
            return super.getCapability(capability, facing);

        if(capability == CapabilityEnergy.ENERGY){
            return (T)energyStorage;
        }
        return super.getCapability(capability, facing);
    }

    public int numberOfModulesInstalled() {
        int slotsFilled = 0;
        for(ItemStack i:serverSlots){
            if(!i.isEmpty()) slotsFilled++;
        }
        return slotsFilled;
    }

    public int getCurrentModuleEnergyConsumption() {
        int energyConsumption = 0;
        for(ItemStack i:serverSlots){
            if(!i.isEmpty()) energyConsumption += ((ItemServerModuleBase)i.getItem()).getCurrentEnergyConsumption(i);
        }
        return energyConsumption;
    }

    public int getCurrentModuleEnergyOutput() {
        int energyOutput = 0;
        for(ItemStack i:serverSlots){
            if(!i.isEmpty() && i.getItem() instanceof ItemEnergyOutputModule) energyOutput += ((ItemEnergyOutputModule)i.getItem()).getCurrentEnergyOutput(i);
        }
        return energyOutput;
    }

    public void RecalculateEnergyCapacity() {
        int energyCapacity = 0;
        for (ItemStack i : serverSlots) {
            if (!i.isEmpty()) energyCapacity += ((ItemServerModuleBase) i.getItem()).getModuleEnergyCapacity(i);
        }
        energyStorage.setCapacity(chassisType.baseCapacity + energyCapacity);
        energyStorage.setEnergyStored(Math.min(chassisType.baseCapacity + energyCapacity, energyStorage.getEnergyStored()));
        energyCapacityDisplay = EnergyDisplayAmount.fromEnergyAmount(chassisType.baseCapacity + energyCapacity);
        /*System.out.println("Current Energy: " + energyStorage.getEnergyStored() + "Max Energy: " + energyStorage.getMaxEnergyStored() + " for " + chassisType);*/
    }

    public int pushEnergyToOutput(int energy){
        // push to other face IF EXISTS
        EnumFacing outputDirection = this.world.getBlockState(pos).getValue(BlockServerChassis.PROPERTYFACING);
        TileEntity outputTileEntity = world.getTileEntity(pos.add(outputDirection.getDirectionVec()));

        if (outputTileEntity != null && outputTileEntity.hasCapability(CapabilityEnergy.ENERGY, outputDirection.getOpposite())) {
            IEnergyStorage outputEnergyStorage = outputTileEntity.getCapability(CapabilityEnergy.ENERGY, outputDirection.getOpposite());
            if (outputEnergyStorage != null && outputEnergyStorage.canReceive()) {
                return energyStorage.extractEnergy(outputEnergyStorage.receiveEnergy(Math.min(energyStorage.getEnergyStored(), energy), false), false); // perform action
            }
        }

        return 0;
    }
}
