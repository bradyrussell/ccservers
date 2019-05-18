package com.bradyrussell.ccservers;

import com.bradyrussell.ccservers.blocks.BlockServerChassis;
import com.bradyrussell.ccservers.entities.TileEntityServerChassis;
import com.bradyrussell.ccservers.items.EServerModuleType;
import com.bradyrussell.ccservers.network.GuiHandlerRegistry;
import com.bradyrussell.ccservers.network.packets.PacketParticle;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Objects;

/**
 * User: brandon3055
 * Date: 06/01/2015
 * <p>
 * The Startup class for this example is called during startup, in the following order:
 * preInitCommon
 * preInitClientOnly
 * initCommon
 * initClientOnly
 * postInitCommon
 * postInitClientOnly
 * See CCServers class for more information
 */
public class StartupCommon {
    public static Block blockInventoryAdvanced[] = new Block[EServerChassisType.values().length];  // this holds the unique instance of your block
    public static ItemBlock itemBlockInventoryAdvanced[] = new ItemBlock[EServerChassisType.values().length];; // this holds the unique instance of the ItemBlock corresponding to your block

    static int i = 0;

    public static void preInitCommon() {
        i = 0;
        for (EServerChassisType type:EServerChassisType.values()){
            blockInventoryAdvanced[i] = new BlockServerChassis(type).setUnlocalizedName(type.registryName+"_unlocalised_name");
            blockInventoryAdvanced[i].setRegistryName(type.registryName);
            ForgeRegistries.BLOCKS.register(blockInventoryAdvanced[i]);

            // We also need to create and register an ItemBlock for this block otherwise it won't appear in the inventory
            itemBlockInventoryAdvanced[i] = new ItemBlock(blockInventoryAdvanced[i]);
            itemBlockInventoryAdvanced[i].setRegistryName(Objects.requireNonNull(blockInventoryAdvanced[i].getRegistryName()));
            ForgeRegistries.ITEMS.register(itemBlockInventoryAdvanced[i]);
            i++;
        }

        // Each of your tile entities needs to be registered with a name that is unique to your mod.
        GameRegistry.registerTileEntity(TileEntityServerChassis.class, new ResourceLocation("ccserver_tile_entity"));

        // You need to register a GUIHandler for the container.  However there can be only one handler per mod, so for the purposes
        //   of this project, we create a single GuiHandlerRegistry for all examples.
        // We register this GuiHandlerRegistry with the NetworkRegistry, and then tell the GuiHandlerRegistry about
        //   each example's GuiHandler, in this case GuiHandlerMBE31, so that when it gets a request from NetworkRegistry,
        //   it passes the request on to the correct example's GuiHandler.
        NetworkRegistry.INSTANCE.registerGuiHandler(CCServers.instance, GuiHandlerRegistry.getInstance());
        GuiHandlerRegistry.getInstance().registerGuiHandler(new GuiHandlerMBE31(), GuiHandlerMBE31.getGuiID());


        EServerModuleType.InstantiateModules();
        for(EServerModuleType type:EServerModuleType.values()){
            ForgeRegistries.ITEMS.register(type.moduleInstance);
        }
    }

    private static int messageID = 0;
    public static void initCommon() {
        CCServers.NetworkWrapper.registerMessage(PacketParticle.PacketParticleHandler.class,PacketParticle.class, messageID++, Side.CLIENT);
    }

    public static void postInitCommon() {
    }
}
