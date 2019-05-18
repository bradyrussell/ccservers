package com.bradyrussell.ccservers;

import com.bradyrussell.ccservers.StartupCommon;
import com.bradyrussell.ccservers.items.EServerModuleType;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

/**
 * User: brandon3055
 * Date: 06/01/2015
 * <p>
 * The Startup classes for this example are called during startup, in the following order:
 * preInitCommon
 * preInitClientOnly
 * initCommon
 * initClientOnly
 * postInitCommon
 * postInitClientOnly
 * See CCServers class for more information
 */
public class StartupClientOnly {
    public static void preInitClientOnly() {
        // This step is necessary in order to make your block render properly when it is an item (i.e. in the inventory
        //   or in your hand or thrown on the ground).
        // It must be done on client only, and must be done after the block has been created in Common.preinit().

        int i = 0;

        for(EServerChassisType type:EServerChassisType.values()){
            ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation("ccservers:"+type.registryName, "inventory");
            final int DEFAULT_ITEM_SUBTYPE = 0;
            ModelLoader.setCustomModelResourceLocation(StartupCommon.itemBlockInventoryAdvanced[i++], DEFAULT_ITEM_SUBTYPE, itemModelResourceLocation);
        }

        for(EServerModuleType type:EServerModuleType.values()){
            ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation("ccservers:"+type.registryName, "inventory");
            final int DEFAULT_ITEM_SUBTYPE = 0;
            ModelLoader.setCustomModelResourceLocation(type.moduleInstance, DEFAULT_ITEM_SUBTYPE, itemModelResourceLocation);
        }
    }

    public static void initClientOnly() {

    }

    public static void postInitClientOnly() {
    }
}
