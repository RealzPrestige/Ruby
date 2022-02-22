package dev.zprestige.ruby.ui.buttons;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.misc.MiddleClick;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class MiddleClickInterface extends GuiScreen {
    ScaledResolution resolution;
    ArrayList<Quadrant> quadrants = new ArrayList<>();
    Entity entity;

    public MiddleClickInterface(ScaledResolution resolution, Entity entity) {
        this.resolution = resolution;
        this.entity = entity;
        quadrants.add(new Quadrant((resolution.getScaledWidth() / 2) - 64, (resolution.getScaledHeight() / 2) - 64, 64, 64, new ResourceLocation("textures/icons/target.png"), QuadrantType.AddEnemy, entity));
        quadrants.add(new Quadrant((resolution.getScaledWidth() / 2) - 10, (resolution.getScaledHeight() / 2) - 64, 64, 64, new ResourceLocation("textures/icons/addfriend.png"), QuadrantType.AddFriend, entity));
        quadrants.add(new Quadrant((resolution.getScaledWidth() / 2) - 64, (resolution.getScaledHeight() / 2) - 10, 64, 64, new ResourceLocation("textures/icons/block.png"), QuadrantType.Block, entity));
        quadrants.add(new Quadrant((resolution.getScaledWidth() / 2) - 10, (resolution.getScaledHeight() / 2) - 10, 64, 64, new ResourceLocation("textures/icons/talk.png"), QuadrantType.Whisper, entity));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.enableAlpha();
        Ruby.mc.getTextureManager().bindTexture(new ResourceLocation("textures/icons/circle.png"));
        GL11.glPushMatrix();
        GuiScreen.drawScaledCustomSizeModalRect((resolution.getScaledWidth() / 2) - 64, (resolution.getScaledHeight() / 2) - 64, 0, 128, 128, 128, 128, 128, 128, 128);
        GL11.glScaled(2.0f, 2.0f, 0.0f);
        Ruby.rubyFont.drawStringWithShadow(entity.getName(), ((resolution.getScaledWidth() / 2f) - Ruby.rubyFont.getStringWidth(entity.getName())) / 2.0f, (((resolution.getScaledHeight() / 2f) - 64 - Ruby.rubyFont.getHeight(entity.getName()))  / 2.0f) - 10, -1);
        GL11.glPopMatrix();
        GlStateManager.disableAlpha();
        quadrants.forEach(quadrant -> quadrant.drawScreen(mouseX, mouseY, partialTicks));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int releasedButton) {
        if (releasedButton == 2)
            quadrants.forEach(quadrant -> quadrant.mouseReleased(mouseX, mouseY));

    }

    public static class Quadrant {
        int x, y, width, height;
        ResourceLocation resourceLocation;
        QuadrantType quadrantType;
        Entity entity;

        public Quadrant(int x, int y, int width, int height, ResourceLocation resourceLocation, QuadrantType quadrantType, Entity entity) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.resourceLocation = resourceLocation;
            this.quadrantType = quadrantType;
            this.entity = entity;
        }

        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            Ruby.mc.getTextureManager().bindTexture(resourceLocation);
            GlStateManager.color(1f, 1f, 1f);
            GL11.glPushMatrix();
            if (isHovering(mouseX, mouseY)) {
                GuiScreen.drawScaledCustomSizeModalRect(x + 20, y + 20, 0, 0, 34, 34, 34, 34, 34, 34);
                Ruby.rubyFont.drawStringWithShadow(quadrantType.getName(), x + 37 - (Ruby.rubyFont.getStringWidth(quadrantType.getName()) / 2f), y + 54, -1);
            } else
                GuiScreen.drawScaledCustomSizeModalRect(x + 22, y + 22, 0, 0, 32, 32, 32, 32, 32, 32);
            GL11.glPopMatrix();
            GlStateManager.disableAlpha();
        }

        public void mouseReleased(int mouseX, int mouseY) {
            if (isHovering(mouseX, mouseY)) {
                String prefix = ChatFormatting.RED + "[Ruby] ";
                switch (quadrantType) {
                    case AddFriend:
                        if (Ruby.friendManager.isFriend(entity.getName())) {
                            Ruby.friendManager.removeFriend(entity.getName());
                            Ruby.mc.player.sendMessage(new TextComponentString(prefix + ChatFormatting.RESET + "Successfully removed " + entity.getName() + " as friend."));
                        } else {
                            Ruby.friendManager.addFriend(entity.getName());
                            Ruby.mc.player.sendMessage(new TextComponentString(prefix + ChatFormatting.RESET + "Successfully added " + entity.getName() + " as friend."));
                        }
                        break;
                    case AddEnemy:
                        if (Ruby.enemyManager.isEnemy(entity.getName())) {
                            Ruby.enemyManager.removeEnemy(entity.getName());
                            Ruby.mc.player.sendMessage(new TextComponentString(prefix + ChatFormatting.RESET + "Successfully removed " + entity.getName() + " as enemy."));
                        } else {
                            Ruby.enemyManager.addEnemy(entity.getName());
                            Ruby.mc.player.sendMessage(new TextComponentString(prefix + ChatFormatting.RESET + "Successfully added " + entity.getName() + " as enemy."));
                        }
                        break;
                    case Block:
                        if (MiddleClick.Instance.blockedList.contains((EntityPlayer)entity)) {
                            MiddleClick.Instance.blockedList.remove((EntityPlayer)entity);
                            Ruby.mc.player.sendMessage(new TextComponentString(prefix + ChatFormatting.RESET + "Successfully unblocked " + entity.getName() + "."));
                        }
                        else {
                            MiddleClick.Instance.blockedList.add((EntityPlayer) entity);
                            Ruby.mc.player.sendMessage(new TextComponentString(prefix + ChatFormatting.RESET + "Successfully blocked " + entity.getName() + "."));
                        }

                        break;
                    case Whisper:
                        Ruby.mc.player.sendChatMessage("/msg " + entity.getName() + " yo");
                        break;
                }
            }
        }

        public boolean isHovering(int mouseX, int mouseY) {
            return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
        }
    }

    public enum QuadrantType {
        AddFriend("Add Friend"),
        Whisper("Whisper"),
        Block("Block"),
        AddEnemy("Add Enemy");

        String name;

        QuadrantType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
