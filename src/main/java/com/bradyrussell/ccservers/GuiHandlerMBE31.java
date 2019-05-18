package com.bradyrussell.ccservers;


import com.bradyrussell.ccservers.entities.ContainerInventoryServerChassis;
import com.bradyrussell.ccservers.entities.GuiInventoryServerChassis;
import com.bradyrussell.ccservers.entities.TileEntityServerChassis;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

/**
 * User: brandon3055
 * Date: 06/01/2015
 *
 * This class is used to get the client and server gui elements when a player opens a gui. There can only be one registered
 *   IGuiHandler instance handler per mod.
 */
public class GuiHandlerMBE31 implements IGuiHandler {
	private static final int GUIID_MBE_31 = 31;
	public static int getGuiID() {return GUIID_MBE_31;}

	// Gets the server side element for the given gui id this should return a container
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID != getGuiID()) {
			System.err.println("Invalid ID: expected " + getGuiID() + ", received " + ID);
		}

		BlockPos xyz = new BlockPos(x, y, z);
		TileEntity tileEntity = world.getTileEntity(xyz);
		if (tileEntity instanceof TileEntityServerChassis) {
			TileEntityServerChassis tileEntityServerChassis = (TileEntityServerChassis) tileEntity;
			return new ContainerInventoryServerChassis(player.inventory, tileEntityServerChassis);
		}
		return null;
	}

	// Gets the client side element for the given gui id this should return a gui
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID != getGuiID()) {
			System.err.println("Invalid ID: expected " + getGuiID() + ", received " + ID);
		}

		BlockPos xyz = new BlockPos(x, y, z);
		TileEntity tileEntity = world.getTileEntity(xyz);
		if (tileEntity instanceof TileEntityServerChassis) {
			TileEntityServerChassis tileEntityServerChassis = (TileEntityServerChassis) tileEntity;
			return new GuiInventoryServerChassis(player.inventory, tileEntityServerChassis);
		}
		return null;
	}

}
