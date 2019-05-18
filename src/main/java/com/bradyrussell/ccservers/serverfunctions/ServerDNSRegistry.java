package com.bradyrussell.ccservers.serverfunctions;

import com.bradyrussell.ccservers.entities.TileEntityServerChassis;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.DimensionManager;

import java.util.HashMap;

public class ServerDNSRegistry { // this might be a flawed concept
    private static HashMap<String, ServerLocation> dnsEntries = new HashMap<>();

    private static TileEntityServerChassis getServerFromLocation(ServerLocation location){
        final TileEntity entity = DimensionManager.getWorld(location.dimension).getTileEntity(location.pos);
        if(!(entity instanceof TileEntityServerChassis)) return null;
        return (TileEntityServerChassis) entity; // todo warning this will load unloaded chunks
    }

    public static boolean doesHostExist(String host){
        return dnsEntries.containsKey(host);
    }

    public static TileEntityServerChassis getHost(String host){
        return getServerFromLocation(dnsEntries.get(host));
    }

    public static boolean register(String host,TileEntityServerChassis serverChassis){
        if (doesHostExist(host)) return false;
        dnsEntries.put(host, new ServerLocation(serverChassis.getWorld().provider.getDimension(),serverChassis.getPos()));
        return true;
    }

    private static class ServerLocation{
        int dimension;
        BlockPos pos;

        public ServerLocation(int dimension, BlockPos pos) {
            this.dimension = dimension;
            this.pos = pos;
        }
    }

}
