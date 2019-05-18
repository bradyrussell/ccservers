package com.bradyrussell.ccservers.network.packets;

import com.bradyrussell.ccservers.entities.ContainerInventoryServerChassis;
import com.bradyrussell.ccservers.entities.EServerRedstoneBehavior;
import com.bradyrussell.ccservers.entities.TileEntityServerChassis;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketGUI_ToggleServer implements IMessage {
    private int button;

    public PacketGUI_ToggleServer() {
    }

    public PacketGUI_ToggleServer(int button) {
        this.button = button;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        button = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(button);
    }


    public static class PacketGUI_ToggleServerHandler implements IMessageHandler<PacketGUI_ToggleServer, IMessage> {
        @Override
        public IMessage onMessage(PacketGUI_ToggleServer message, MessageContext ctx) {
            final EntityPlayerMP playerMP = ctx.getServerHandler().player;
            playerMP.getServerWorld().addScheduledTask(() -> {

                if (playerMP.openContainer instanceof ContainerInventoryServerChassis) {
                    TileEntityServerChassis serverChassis = ((ContainerInventoryServerChassis) playerMP.openContainer).getTileEntityServerChassis();

                    switch (message.button) {
                        case 1: {
                            serverChassis.setServerEnabled(!serverChassis.isServerEnabled());
                            return;
                        }
                        case 2: {
                            serverChassis.setRedstoneBehavior(EServerRedstoneBehavior.getNext(serverChassis.getRedstoneBehavior()));
                            return;
                        }
                    }
                } else {
                    playerMP.attackEntityFrom(DamageSource.ANVIL, 5);
                }
            });
            return null;
        }
    }
}
