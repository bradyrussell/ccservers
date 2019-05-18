package com.bradyrussell.ccservers.items.modules.powered;

import com.bradyrussell.ccservers.CCServers;
import com.bradyrussell.ccservers.items.EServerModuleType;
import com.bradyrussell.ccservers.items.ItemServerPoweredLuaModuleBase;
import com.bradyrussell.ccservers.serverfunctions.ServerCrypto;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import static com.bradyrussell.ccservers.items.ModuleEnergy.getNBT;

public class ItemCryptoAuthenticationDatabaseModule extends ItemServerPoweredLuaModuleBase {
    private static final int CREATE_ACCOUNT_RF = 1000 * CCServers.RF_MULTIPLIER;
    private static final int VERIFY_ACCOUNT_RF = 500 * CCServers.RF_MULTIPLIER;
    private static final int DUMP_RF = 4096 * CCServers.RF_MULTIPLIER;

    public ItemCryptoAuthenticationDatabaseModule(EServerModuleType type) {
        super(type);
    }

    @Override
    public int getModuleServerEnergyCapacity(ItemStack moduleItem) {
        return 0;
    }

    @Override
    public String[] getMethodNames() {
        return new String[]{"registerAccount","verifyAccount","dump"};
    }

    @Override
    public Object[] callMethod(ItemStack moduleStack, IComputerAccess computer, @Nonnull ILuaContext luaContext, int method, @Nonnull Object[] arguments) throws LuaException, InterruptedException {
        switch (method){
            case 0:{
                verifyParams(arguments);
                return new Object[]{ createAccount(moduleStack, (String) arguments[0], (String) arguments[1])};
            }
            case 1:{
                verifyParams(arguments);
                return new Object[]{ verifyAccount(moduleStack, (String) arguments[0], (String) arguments[1])};
            }
            case 2:{
                System.out.println("Computer ID#"+computer.getID()+" is dumping database contained within "+moduleStack.getDisplayName()+".");
                return new Object[]{ dump(moduleStack)};
            }
        }

        throw new LuaException("unsupported operation");
    }

    public void verifyParams(@Nonnull Object[] arguments) throws LuaException {
        if (arguments.length < 2) throw new LuaException("Expected 2 parameters");
        if (!(arguments[0] instanceof String) || !(arguments[1] instanceof String)) throw new LuaException("Expected String for parameter 1 and 2");
    }

    public static boolean createAccount(ItemStack moduleItem, String username, String password) throws LuaException {
        final NBTTagCompound database = getDatabaseNBT(moduleItem);
        final String saltLocation = ServerCrypto.sha256(username);
        if(database.hasKey(username) || database.hasKey(saltLocation)) return false;

        if(extractEnergy(moduleItem, CREATE_ACCOUNT_RF) < CREATE_ACCOUNT_RF) throw new LuaException("Insufficient energy to create account.");

        byte[] salt = new byte[32];
        ThreadLocalRandom.current().nextBytes(salt);
        final String sha256_salt = ServerCrypto.sha256(new String(salt));

        database.setString(username, ServerCrypto.sha256(sha256_salt+password));
        database.setString(saltLocation, sha256_salt); // salt is stored in hash of username. not smart just lazy
        setDatabaseNBT(moduleItem,database);
        return true;
    }

    public static boolean verifyAccount(ItemStack moduleItem, String username, String password) throws LuaException {
        final NBTTagCompound database = getDatabaseNBT(moduleItem);
        final String salt = ServerCrypto.sha256(username);
        if(!database.hasKey(username) || !database.hasKey(salt)) return false;

        if(extractEnergy(moduleItem, VERIFY_ACCOUNT_RF) < VERIFY_ACCOUNT_RF) throw new LuaException("Insufficient energy to verify account.");

        String checkPassword = ServerCrypto.sha256(database.getString(salt)+password);
        return checkPassword.equals(database.getString(username));
    }

    public static HashMap<String, String> dump(ItemStack moduleItem) throws LuaException {
        if(extractEnergy(moduleItem, DUMP_RF) < DUMP_RF) throw new LuaException("Insufficient energy to dump database.");
        HashMap<String, String> dumped = new HashMap<>();

        final NBTTagCompound databaseNBT = getDatabaseNBT(moduleItem);
        for(String key: databaseNBT.getKeySet()){
            dumped.put(key, databaseNBT.getString(key));
        }
        return dumped;
    }


    private static NBTTagCompound getDatabaseNBT(ItemStack itemStack){
        return getNBT(itemStack).getCompoundTag("authentication_database");
    }

    private static void setDatabaseNBT(ItemStack itemStack, NBTTagCompound nbt){
        getNBT(itemStack).setTag("authentication_database",nbt);
    }

}
