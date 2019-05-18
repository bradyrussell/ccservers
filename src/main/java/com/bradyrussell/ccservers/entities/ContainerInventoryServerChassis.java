package com.bradyrussell.ccservers.entities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * User: brandon3055
 * Date: 06/01/2015
 * <p>
 * ContainerSmelting is used to link the client side gui to the server side inventory and it is where
 * you add the slots holding items. It is also used to send server side data such as progress bars to the client
 * for use in guis
 */
public class ContainerInventoryServerChassis extends Container {

    // Stores the tile entity instance for later use
    private TileEntityServerChassis tileEntityServerChassis;

    // These store cache values, used by the server to only update the client side tile entity when values have changed
    private int[] cachedFields;

    // must assign a slot index to each of the slots used by the GUI.
    // For this container, we can see the furnace fuel, input, and output slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container using addSlotToContainer(), it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 39 = fuel slots (tileEntity 0 - 3)
    //  40 - 44 = input slots (tileEntity 4 - 8)
    //  45 - 49 = output slots (tileEntity 9 - 13)

    private final int HOTBAR_SLOT_COUNT = 9;
    private final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;

    public final int SERVER_MODULE_SLOTS_COUNT;
    //public final int INPUT_SLOTS_COUNT = 0;
   // public final int OUTPUT_SLOTS_COUNT = 0;
    public final int TOTAL_SERVER_SLOTS_COUNT;

    // slot index is the unique index for all slots in this container i.e. 0 - 35 for invPlayer then 36 - 49 for tileEntityServerChassis
    private final int VANILLA_FIRST_SLOT_INDEX = 0;
    private final int FIRST_SERVER_MODULE_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
//    private final int FIRST_INPUT_SLOT_INDEX = FIRST_SERVER_MODULE_SLOT_INDEX + SERVER_MODULE_SLOTS_COUNT;
//    private final int FIRST_OUTPUT_SLOT_INDEX = FIRST_INPUT_SLOT_INDEX + INPUT_SLOTS_COUNT;

    // slot number is the slot number within each component; i.e. invPlayer slots 0 - 35, and tileEntityServerChassis slots 0 - 14
    private final int FIRST_SERVER_MODULE_SLOT_NUMBER = 0;

    public ContainerInventoryServerChassis(InventoryPlayer invPlayer, TileEntityServerChassis tileEntityServerChassis) {
        this.tileEntityServerChassis = tileEntityServerChassis;
        SERVER_MODULE_SLOTS_COUNT = tileEntityServerChassis.chassisType.availableSlots;
        TOTAL_SERVER_SLOTS_COUNT = SERVER_MODULE_SLOTS_COUNT; /*+ INPUT_SLOTS_COUNT + OUTPUT_SLOTS_COUNT*///;

        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;
        final int HOTBAR_XPOS = 8;
        final int HOTBAR_YPOS = 183;
        // Add the players hotbar to the gui - the [xpos, ypos] location of each item
        for (int x = 0; x < HOTBAR_SLOT_COUNT; x++) {
            addSlotToContainer(new Slot(invPlayer, x, HOTBAR_XPOS + SLOT_X_SPACING * x, HOTBAR_YPOS));
        }

        final int PLAYER_INVENTORY_XPOS = 8;
        final int PLAYER_INVENTORY_YPOS = 125;
        // Add the rest of the players inventory to the gui
        for (int y = 0; y < PLAYER_INVENTORY_ROW_COUNT; y++) {
            for (int x = 0; x < PLAYER_INVENTORY_COLUMN_COUNT; x++) {
                int slotNumber = HOTBAR_SLOT_COUNT + y * PLAYER_INVENTORY_COLUMN_COUNT + x;
                int xpos = PLAYER_INVENTORY_XPOS + x * SLOT_X_SPACING;
                int ypos = PLAYER_INVENTORY_YPOS + y * SLOT_Y_SPACING;
                addSlotToContainer(new Slot(invPlayer, slotNumber, xpos, ypos));
            }
        }

        final int SERVER_MODULE_SLOTS_XPOS = 53;
        final int SERVER_MODULE_SLOTS_YPOS = 24;
        for (int i = 0; i < SERVER_MODULE_SLOTS_COUNT; i++) {
            int slotNumber = i + FIRST_SERVER_MODULE_SLOT_NUMBER;
            int x = i % 4;
            int y = 4-(i / 4);

            addSlotToContainer(new SlotServerModule(tileEntityServerChassis, slotNumber, SERVER_MODULE_SLOTS_XPOS + SLOT_X_SPACING * x, SERVER_MODULE_SLOTS_YPOS + SLOT_Y_SPACING * y));
        }
    }

    // Checks each tick to make sure the player is still able to access the inventory and if not closes the gui
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tileEntityServerChassis.isUsableByPlayer(player);
    }

    // This is where you specify what happens when a player shift clicks a slot in the gui
    //  (when you shift click a slot in the TileEntity Inventory, it moves it to the first available position in the hotbar and/or
    //    player inventory.  When you you shift-click a hotbar or player inventory item, it moves it to the first available
    //    position in the TileEntity inventory - either input or fuel as appropriate for the item you clicked)
    // At the very least you must override this and return EMPTY_ITEM or the game will crash when the player shift clicks a slot
    // returns EMPTY_ITEM if the source slot is empty, or if none of the source slot items could be moved.
    //   otherwise, returns a copy of the source stack
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int sourceSlotIndex) {
        Slot sourceSlot = inventorySlots.get(sourceSlotIndex);
        if (sourceSlot == null || !sourceSlot.getHasStack()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getStack();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (sourceSlotIndex >= VANILLA_FIRST_SLOT_INDEX && sourceSlotIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into one of the furnace slots
            // If the stack is smeltable try to merge merge the stack into the input slots
            if (TileEntityServerChassis.isItemValidForServerModuleSlot(sourceStack)) {  //isEmptyItem
                if (!mergeItemStack(sourceStack, FIRST_SERVER_MODULE_SLOT_INDEX, FIRST_SERVER_MODULE_SLOT_INDEX + SERVER_MODULE_SLOTS_COUNT, false)) {
                    return ItemStack.EMPTY;  //EMPTY_ITEM;
                }
            } else {
                return ItemStack.EMPTY;  //EMPTY_ITEM;
            }
        } else if (sourceSlotIndex >= FIRST_SERVER_MODULE_SLOT_INDEX && sourceSlotIndex < FIRST_SERVER_MODULE_SLOT_INDEX + TOTAL_SERVER_SLOTS_COUNT) {
            // This is a furnace slot so merge the stack into the players inventory: try the hotbar first and then the main inventory
            //   because the main inventory slots are immediately after the hotbar slots, we can just merge with a single call
            if (!mergeItemStack(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  //EMPTY_ITEM;
            }
        } else {
            System.err.print("Invalid slotIndex:" + sourceSlotIndex);
            return ItemStack.EMPTY;  //EMPTY_ITEM;
        }

        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {  //getStackSize()
            sourceSlot.putStack(ItemStack.EMPTY);  // Empty Item
        } else {
            sourceSlot.onSlotChanged();
        }

        sourceSlot.onTake(player, sourceStack);  // onPickupFromSlot()
        return copyOfSourceStack;
    }

    /* Client Synchronization */

    // This is where you check if any values have changed and if so send an update to any clients accessing this container
    // The container itemstacks are tested in Container.detectAndSendChanges, so we don't need to do that
    // We iterate through all of the TileEntity Fields to find any which have changed, and send them.
    // You don't have to use fields if you don't wish to; just manually match the ID in sendWindowProperty with the value in
    //   updateProgressBar()
    // The progress bar values are restricted to shorts.  If you have a larger value (eg int), it's not a good idea to try and split it
    //   up into two shorts because the progress bar values are sent independently, and unless you add synchronisation logic at the
    //   receiving side, your int value will be wrong until the second short arrives.  Use a custom packet instead.
    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        boolean allFieldsHaveChanged = false;
        boolean[] fieldHasChanged = new boolean[tileEntityServerChassis.getFieldCount()];
        if (cachedFields == null) {
            cachedFields = new int[tileEntityServerChassis.getFieldCount()];
            allFieldsHaveChanged = true;
        }
        for (int i = 0; i < cachedFields.length; ++i) {
            if (allFieldsHaveChanged || cachedFields[i] != tileEntityServerChassis.getField(i)) {
                cachedFields[i] = tileEntityServerChassis.getField(i);
                fieldHasChanged[i] = true;
            }
        }

        // go through the list of listeners (players using this container) and update them if necessary
        for (IContainerListener listener : this.listeners) {
            for (int fieldID = 0; fieldID < tileEntityServerChassis.getFieldCount(); ++fieldID) {
                if (fieldHasChanged[fieldID]) {
                    // Note that although sendWindowProperty takes 2 ints on a server these are truncated to shorts
                    listener.sendWindowProperty(this, fieldID, cachedFields[fieldID]);
                }
            }
        }
    }

    // Called when a progress bar update is received from the server. The two values (id and data) are the same two
    // values given to sendWindowProperty.  In this case we are using fields so we just pass them to the tileEntity.
    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int id, int data) {
        tileEntityServerChassis.setField(id, data);
    }

    public class SlotServerModule extends Slot {
        public SlotServerModule(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        // if this function returns false, the player won't be able to insert the given item into this slot
        @Override
        public boolean isItemValid(ItemStack stack) {
            return TileEntityServerChassis.isItemValidForServerModuleSlot(stack);
        }
    }
}
