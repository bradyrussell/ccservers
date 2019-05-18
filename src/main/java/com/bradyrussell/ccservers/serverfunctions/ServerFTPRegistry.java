package com.bradyrussell.ccservers.serverfunctions;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.filesystem.IWritableMount;
import net.minecraft.world.World;

import java.util.HashMap;

public class ServerFTPRegistry {
    public static HashMap<String, IWritableMount> ftpDrives = new HashMap<>();

    public static

    void a(World world){
        ftpDrives.get("a");
        ComputerCraftAPI.createSaveDirMount(world,"ftp/"+ComputerCraftAPI.createUniqueNumberedSaveDir(world,"ftp"), 8 * 1024 * 1024);
    }

    private class FTPServer{
        String host;
        String password;
    }
}
