package com.bradyrussell.ccservers.blocks;

import com.bradyrussell.ccservers.CCServers;
import com.bradyrussell.ccservers.EServerChassisType;
import com.bradyrussell.ccservers.GuiHandlerMBE31;
import com.bradyrussell.ccservers.entities.TileEntityServerChassis;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;


/**
 * User: brandon3055
 * Date: 06/01/2015
 * <p>
 * BlockInventoryAdvanced is an advanced furnace with 5 input, 4 output and 4 fuel slots that smelts at twice the speed
 * of a regular furnace. The block itself doesn't do much more then any regular block except create a tile entity when
 * placed, open a gui when right clicked and drop tne inventory's contents when harvested. Everything else is handled
 * by the tile entity.
 * <p>
 * The block model will change appearance depending on how many fuel slots are burning.
 * The amount of "block light" produced by the furnace will also depending on how many fuel slots are burning.
 * <p>
 * //Note that in 1.10.*, extending BlockContainer can cause rendering problems if you don't extend getRenderType()
 * // If you don't want to extend BlockContainer, make sure to add the tile entity manually,
 * //   using hasTileEntity() and createTileEntity().  See BlockContainer for a couple of other important methods you may
 * //  need to implement.
 */
public class BlockServerChassis extends BlockContainer {
    private EServerChassisType chassisType;
    private TileEntityServerChassis entityServerChassis;

    public BlockServerChassis(EServerChassisType eServerChassisType) {
        super(Material.IRON);
        chassisType = eServerChassisType;
        this.setCreativeTab(CreativeTabs.REDSTONE);
        setDefaultState(blockState.getBaseState().withProperty(PROPERTYFACING, EnumFacing.SOUTH));
    }

    // Called when the block is placed or loaded client side to get the tile entity for the block
    // Should return a new instance of the tile entity for the block
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        entityServerChassis = new TileEntityServerChassis(chassisType);
        return entityServerChassis;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        entityServerChassis = new TileEntityServerChassis(chassisType);
        return entityServerChassis;
    }

    // Called when the block is right clicked
    // In this block it is used to open the blocks gui when right clicked by a player
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand,
                                    EnumFacing side, float hitX, float hitY, float hitZ) {
        // Uses the gui handler registered to your mod to open the gui for the given gui id
        // open on the server side only  (not sure why you shouldn't open client side too... vanilla doesn't, so we better not either)
        if (worldIn.isRemote) return true;

        playerIn.openGui(CCServers.instance, GuiHandlerMBE31.getGuiID(), worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    // This is where you can do something when the block is broken. In this case drop the inventory's contents
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof IInventory) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory) tileEntity);
        }

        // Super MUST be called last because it removes the tile entity
        super.breakBlock(worldIn, pos, state);
    }

    //------------------------------------------------------------
    //  The code below isn't necessary for illustrating the Inventory Furnace concepts, it's just used for rendering.
    //  For more background information see MBE03

    // we will give our Block a property which tracks the number of burning sides, 0 - 4.
    // This will affect the appearance of the block model, but does not need to be stored in metadata (it's stored in
    //  the tileEntity) so we only need to implement getActualState.  getStateFromMeta, getMetaFromState aren't required
    //   but we give defaults anyway because the base class getMetaFromState gives an error if we don't

    // update the block state depending on the number of slots which contain burning fuel
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileEntityServerChassis) {
            TileEntityServerChassis tileEntityServerChassis = (TileEntityServerChassis) tileEntity;
            int filledSlots = tileEntityServerChassis.numberOfModulesInstalled();
            filledSlots = MathHelper.clamp(filledSlots, 0, 16);
            return state.withProperty(SLOTS_FILLED_COUNT, filledSlots);
        }
        return state;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(PROPERTYFACING, EnumFacing.getHorizontal(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PROPERTYFACING).getHorizontalIndex();
    }

    // necessary to define which properties your blocks use
    // will also affect the variants listed in the blockstates model file.  See MBE03 for more info.
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, SLOTS_FILLED_COUNT/*, FAN_STATUS*/,PROPERTYFACING);
    }

    public static final PropertyDirection PROPERTYFACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyInteger SLOTS_FILLED_COUNT = PropertyInteger.create("slots_filled_count", 0, 16);
    //public static final PropertyInteger FAN_STATUS = PropertyInteger.create("fan", 0, 1);

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return 0;
    }

    // the block will render in the SOLID layer.  See http://greyminecraftcoder.blogspot.co.at/2014/12/block-rendering-18.html for more information.
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.SOLID;
    }

    // render using a BakedModel
    // required because the default (super method) is INVISIBLE for BlockContainers.
    @Override
    public EnumBlockRenderType getRenderType(IBlockState iBlockState) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        worldIn.setBlockState(pos, state.withProperty(PROPERTYFACING, placer.getHorizontalFacing().getOpposite()));
    }

    public EServerChassisType getChassisType() {
        return chassisType;
    }

/*    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(world, pos, neighbor);
        if(entityServerChassis != null)
            entityServerChassis.handleRedstoneBehavior();
    }*/

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EnumFacing side) {
        return true;
    }
}
