package com.bradyrussell.ccservers.entities;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: brandon3055
 * Date: 06/01/2015
 * <p>
 * GuiInventoryAdvanced is a gui similar to that of a furnace. It has a progress bar and a burn time indicator.
 * Both indicators have mouse over text
 */
@SideOnly(Side.CLIENT)
public class GuiInventoryServerChassis extends GuiContainer {

    //private EServerChassisType chassisType;

    // This is the resource location for the background image
    private ResourceLocation texture;
    private TileEntityServerChassis tileEntity;

    private boolean isRedstoneButtonHovered = false;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    public GuiInventoryServerChassis(InventoryPlayer invPlayer, TileEntityServerChassis tileEntityServerChassis) {
        super(new ContainerInventoryServerChassis(invPlayer, tileEntityServerChassis));

        // Set the width and height of the gui
        xSize = 176;
        ySize = 207;

        //chassisType = tileEntityServerChassis.chassisType;

        this.tileEntity = tileEntityServerChassis;
        texture = new ResourceLocation("ccservers", "textures/gui/"+ tileEntityServerChassis.getChassisType().registryName+".png");
    }

    // some [x,y] coordinates of graphical elements
    private final int CHARGE_BAR_XPOS = 9;
    private final int CHARGE_BAR_YPOS = 9;
    private final int CHARGE_BAR_ICON_U = 176;   // texture position of white arrow icon
    private final int CHARGE_BAR_ICON_V = 0;
    private final int CHARGE_BAR_WIDTH = 14;
    private final int CHARGE_BAR_HEIGHT = 102;

    private final int RS_BTN_XPOS = 151;
    private final int RS_BTN_YPOS = 7;
    private final int RS_BTN_ICON_U = 190;   // texture position of flame icon
    private final int RS_BTN_ICON_V = 0;
    private final int RS_BTN_WIDTH = 18;
    private final int RS_BTN_HEIGHT = 18;
    private final int RS_BTN_X_SPACING = 18;

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int x, int y) {
        // Bind the image texture
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        // Draw the image
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        // get cook progress as a double between 0 and 1
        double chargePct = tileEntity.displayEnergyPct;
        // draw the cook progress bar
        drawTexturedModalRect(guiLeft + CHARGE_BAR_XPOS, guiTop + CHARGE_BAR_YPOS+(int)Math.round ((1-chargePct) * CHARGE_BAR_HEIGHT), CHARGE_BAR_ICON_U, CHARGE_BAR_ICON_V,
                 CHARGE_BAR_WIDTH, (int)Math.round (chargePct * CHARGE_BAR_HEIGHT));

        if(isRedstoneButtonHovered){
            int yOffset = 0;
            drawTexturedModalRect(guiLeft + RS_BTN_XPOS, guiTop + RS_BTN_YPOS + yOffset, RS_BTN_ICON_U, RS_BTN_ICON_V + yOffset, RS_BTN_WIDTH, RS_BTN_HEIGHT - yOffset);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        final int LABEL_XPOS = 30;
        final int LABEL_YPOS = 5;
        fontRenderer.drawString(tileEntity.getChassisType().title, LABEL_XPOS, LABEL_YPOS, Color.darkGray.getRGB());
        fontRenderer.drawString(tileEntity.getEnergyCapacityDisplay().getDisplayString()+" RF Capacity", LABEL_XPOS, LABEL_YPOS+10, Color.darkGray.getRGB());
        fontRenderer.drawString("Consuming "+ tileEntity.getEnergyConsumedDisplay().getDisplayString()+" RF/t", LABEL_XPOS, LABEL_YPOS+20, Color.darkGray.getRGB());

        List<String> hoveringText = new ArrayList<>();

        // If the mouse is over the progress bar add the progress bar hovering text
        if (isInRect(guiLeft + CHARGE_BAR_XPOS, guiTop + CHARGE_BAR_YPOS, CHARGE_BAR_WIDTH, CHARGE_BAR_HEIGHT, mouseX, mouseY)) {
            hoveringText.add("Energy:");

            if(tileEntity.getEnergyDisplay().getAmount() < 0) System.out.println("<<<<<DEBUG ENERGY DISPLAY UNDERFLOW>>>>>");
            hoveringText.add(Math.round(100*tileEntity.displayEnergyPct) + "% "+ tileEntity.getEnergyDisplay().getAmount()+" "+ tileEntity.getEnergyDisplay().getSuffix());
        }

        // If the mouse is over one of the burn time indicator add the burn time indicator hovering text
        //for (int i = 0; i < TileEntityServerChassis.BATTERY_SLOTS_COUNT; ++i) {
            if (isInRect(guiLeft + RS_BTN_XPOS, guiTop + RS_BTN_YPOS, RS_BTN_WIDTH, RS_BTN_HEIGHT, mouseX, mouseY)) {
                isRedstoneButtonHovered = true;
                hoveringText.add("Redstone Behavior: "+ tileEntity.getRedstoneBehavior().name());
            } else {
                isRedstoneButtonHovered = false;
            }
        //}
        // If hoveringText is not empty draw the hovering text
        if (!hoveringText.isEmpty()) {
            drawHoveringText(hoveringText, mouseX - guiLeft, mouseY - guiTop, fontRenderer);
        }
//		// You must re bind the texture and reset the colour if you still need to use it after drawing a string
//		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
//		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

    }

    // Returns true if the given x,y coordinates are within the given rectangle
    public static boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY) {
        return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isInRect(guiLeft + RS_BTN_XPOS, guiTop + RS_BTN_YPOS, RS_BTN_WIDTH, RS_BTN_HEIGHT, mouseX, mouseY)) {
            tileEntity.setRedstoneBehavior(EServerRedstoneBehavior.getNext(tileEntity.getRedstoneBehavior()));
        }
    }
}
