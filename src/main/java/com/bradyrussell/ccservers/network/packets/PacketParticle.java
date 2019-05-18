package com.bradyrussell.ccservers.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Objects;

public class PacketParticle implements IMessage {
    private double posX,posY,posZ;
    private int particleID, count;

    public PacketParticle() {
    }

    public PacketParticle(int particleID, double posX, double posY, double posZ, int count) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.particleID = particleID;
        this.count = count;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        particleID = buf.readInt();
        count = buf.readInt();
        posX = buf.readDouble();
        posY = buf.readDouble();
        posZ = buf.readDouble();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(particleID);
        buf.writeInt(count);
        buf.writeDouble(posX);
        buf.writeDouble(posY);
        buf.writeDouble(posZ);
    }

    public static class PacketParticleHandler implements IMessageHandler<PacketParticle, IMessage> {
        @Override
        public IMessage onMessage(PacketParticle message, MessageContext ctx) {
            //System.out.println("im a packet");
            Minecraft.getMinecraft().world.spawnParticle(Objects.requireNonNull(EnumParticleTypes.getParticleFromId(message.particleID)),message.posX,message.posY+2,message.posZ,0,1,0,0);
            return null;
        }
    }
}
