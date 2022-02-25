package dev.zprestige.ruby.command.impl;

import dev.zprestige.ruby.command.Command;
import dev.zprestige.ruby.module.player.FakeHacker;

public class FakeHackerCommand extends Command {

    public FakeHackerCommand() {
        super("fakehacker", "FakeHacker target <name>");
    }

    @Override
    public void listener(String string) {
        try {
            String[] split = string.split(" ");
            mc.world.playerEntities.stream().filter(entityPlayer -> entityPlayer.getName().equals(split[2])).forEach(entityPlayer -> FakeHacker.target = entityPlayer.getName());
        } catch (Exception ignored) {
            throwException(format);
        }
    }
}