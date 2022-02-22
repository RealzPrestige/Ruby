package dev.zprestige.ruby.mixins.gui;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.client.Enemies;
import dev.zprestige.ruby.module.client.Friends;
import dev.zprestige.ruby.module.misc.TabList;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(value = GuiPlayerTabOverlay.class)
public class MixinTabOverlay extends Gui {

    @Redirect(method = {"renderPlayerlist"}, at = @At(value = "INVOKE", target = "Ljava/util/List;subList(II)Ljava/util/List;", remap = false))
    protected List<NetworkPlayerInfo> subListHook(List<NetworkPlayerInfo> list, int fromIndex, int toIndex) {
        if (TabList.Instance.isEnabled()) {
            switch (TabList.Instance.order.getValue()) {
                case "Ping":
                    return list.stream().sorted(Comparator.comparing(NetworkPlayerInfo::getResponseTime)).limit(TabList.Instance.maxSize.getValue()).collect(Collectors.toList());
                case "Alphabet":
                    return list.stream().sorted(Comparator.comparing(playerInfo -> playerInfo.getGameProfile().getName())).limit(TabList.Instance.maxSize.getValue()).collect(Collectors.toList());
                case "Length":
                    return list.stream().sorted(Comparator.comparing(playerInfo -> playerInfo.getGameProfile().getName().length())).limit(TabList.Instance.maxSize.getValue()).collect(Collectors.toList());
                case "Normal":
                    return list.stream().limit(TabList.Instance.maxSize.getValue()).collect(Collectors.toList());
            }
        }
        return list.subList(fromIndex, toIndex);
    }


    @Inject(method = {"getPlayerName"}, at = {@At(value = "HEAD")}, cancellable = true)
    public void getPlayerNameHook(NetworkPlayerInfo networkPlayerInfoIn, CallbackInfoReturnable<String> callbackInfoReturnable) {
        String name = networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
        if (TabList.Instance.isEnabled() && TabList.Instance.showPing.getValue())
            callbackInfoReturnable.setReturnValue(name + " [" + networkPlayerInfoIn.getResponseTime() + "]");
        if (Friends.Instance.isEnabled() && Friends.Instance.tabHighlight.getValue() && Ruby.friendManager.isFriend(name)) {
            if (Friends.Instance.tabPrefix.getValue())
                callbackInfoReturnable.setReturnValue(ChatFormatting.AQUA + Friends.Instance.tabPrefixPrefix.getValue() + " " + name + (TabList.Instance.isEnabled() && TabList.Instance.showPing.getValue() ? " [" + networkPlayerInfoIn.getResponseTime() + "]" : ""));
            else
                callbackInfoReturnable.setReturnValue(ChatFormatting.AQUA + "" + name+ (TabList.Instance.isEnabled() && TabList.Instance.showPing.getValue() ? " [" + networkPlayerInfoIn.getResponseTime() + "]" : ""));
        }
        if (Enemies.Instance.isEnabled() && Enemies.Instance.tabHighlight.getValue() && Ruby.enemyManager.isEnemy(name)) {
            if (Enemies.Instance.tabPrefix.getValue())
                callbackInfoReturnable.setReturnValue(ChatFormatting.RED + Enemies.Instance.tabPrefixPrefix.getValue() + " " + name+ (TabList.Instance.isEnabled() && TabList.Instance.showPing.getValue() ? " [" + networkPlayerInfoIn.getResponseTime() + "]" : ""));
            else
                callbackInfoReturnable.setReturnValue(ChatFormatting.RED + "" + name+ (TabList.Instance.isEnabled() && TabList.Instance.showPing.getValue() ? " [" + networkPlayerInfoIn.getResponseTime() + "]" : ""));
        }
    }
}