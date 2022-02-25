package dev.zprestige.ruby.ui.altening;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.mojang.realmsclient.gui.ChatFormatting;
import dev.zprestige.ruby.mixins.minecraft.IMixinMinecraft;
import dev.zprestige.ruby.ui.altening.switcher.AlteningServiceType;
import dev.zprestige.ruby.ui.altening.switcher.ServiceSwitcher;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.Session;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.io.IOException;
import java.net.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.stream.IntStream;

public class AlteningGuiScreen extends GuiScreen {
    public static ArrayList<String> altInformation = new ArrayList<>();
    public static ServiceSwitcher serviceSwitcher = new ServiceSwitcher();
    public GuiScreen guiScreen;
    public String responseMessage;
    public GuiTextField freeTokenField;
    public GuiButton useButton;
    public GuiButton generator;

    public AlteningGuiScreen(GuiScreen guiScreen) {
        this.guiScreen = guiScreen;
    }

    public void initGui() {
        int widthOfComponents = 200;
        freeTokenField = new GuiTextField(1, fontRenderer, width / 2 - widthOfComponents / 2, height / 2 + height / 6 - 40, widthOfComponents, 20);
        generator = new GuiButton(2, width / 2 - widthOfComponents / 2, height / 2 + height / 6, "Generator");
        useButton = new GuiButton(2, width / 2 - widthOfComponents / 2, height / 2 + height / 6 + 22, "Use free token");
        buttonList.add(new GuiButton(3, width / 2 - widthOfComponents / 2, height / 2 + height / 6 + 44, "Back"));
        super.initGui();
    }

    public void updateScreen() {
        freeTokenField.updateCursorCounter();
        super.updateScreen();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        fontRenderer.drawStringWithShadow("Altening Manager", (width / 2f - fontRenderer.getStringWidth("Altening Manager") / 2f), height / 2f + height / 6f - 70, -1);
        freeTokenField.drawTextBox();
        useButton.drawButton(mc, mouseX, mouseY, partialTicks);
        generator.drawButton(mc, mouseX, mouseY, partialTicks);
        if (responseMessage != null)
            fontRenderer.drawStringWithShadow(responseMessage, (float) (width / 2 - fontRenderer.getStringWidth(responseMessage) / 2), height / 2f + height / 6f - 60, -1);
        if (!AlteningGuiScreen.altInformation.isEmpty()) {
            IntStream.range(0, AlteningGuiScreen.altInformation.size()).forEach(i -> {
                String string = AlteningGuiScreen.altInformation.get(i);
                fontRenderer.drawStringWithShadow(ChatFormatting.GREEN + string, (width / 2f - fontRenderer.getStringWidth(string) / 2f), height / 2f + height / 6f - 60, -1);
            });
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            mc.displayGuiScreen(guiScreen);
            return;
        }
        freeTokenField.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 3) {
            mc.displayGuiScreen(guiScreen);
            return;
        }
        super.actionPerformed(button);
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        freeTokenField.mouseClicked(mouseX, mouseY, mouseButton);
        if (useButton.isMouseOver()) {
            useButton.playPressSound(mc.getSoundHandler());
            if ((freeTokenField.getText().isEmpty() || freeTokenField.getText().length() == 0)) {
                responseMessage = TextFormatting.RED + "Field is empty.";
            } else {
                responseMessage = TextFormatting.YELLOW + "Attempting login.";
                new Thread(AlteningGuiScreen.this::checkFreeToken).start();
            }
        }
        if (generator.isMouseOver()) {
            useButton.playPressSound(mc.getSoundHandler());
            try {
                Desktop.getDesktop().browse(URI.create("https://thealtening.com/free/free-minecraft-alt"));
            } catch (Exception ignored) {
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void checkFreeToken() {
        AlteningGuiScreen.serviceSwitcher.switchToService(AlteningServiceType.THEALTENING);
        YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        YggdrasilUserAuthentication yggdrasilUserAuthentication = (YggdrasilUserAuthentication) yggdrasilAuthenticationService.createUserAuthentication(Agent.MINECRAFT);
        yggdrasilUserAuthentication.setUsername(freeTokenField.getText());
        yggdrasilUserAuthentication.setPassword("zprestigeontop");
        try {
            yggdrasilUserAuthentication.logIn();
        } catch (AuthenticationException e) {
            e.printStackTrace();
            responseMessage = TextFormatting.RED + "Login attempt failed.";
            return;
        }
        Session session = new Session(yggdrasilUserAuthentication.getSelectedProfile().getName(), yggdrasilUserAuthentication.getSelectedProfile().getId().toString(), yggdrasilUserAuthentication.getAuthenticatedToken(), "LEGACY");
        ((IMixinMinecraft) mc).setSession(session);
        AlteningGuiScreen.altInformation.clear();
        AlteningGuiScreen.altInformation.add("Logged in as " + session.getUsername() + ".");
        responseMessage = null;
    }
}
