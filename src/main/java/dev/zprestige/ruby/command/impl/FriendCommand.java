package dev.zprestige.ruby.command.impl;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.command.Command;

public class FriendCommand extends Command {

    public FriendCommand() {
        super("friend", "Friend <Add/Del> <Name>");
    }

    @Override
    public void listener(String string) {
        try {
            String[] split = string.split(" ");
            if (split[1].equals("add")) {
                Ruby.friendManager.addFriend(split[2]);
                completeMessage("added " + split[2] + " to your friends list");
            } else if (split[1].equals("del")) {
                Ruby.friendManager.removeFriend(split[2]);
                completeMessage("deleted " + split[2] + " from your friends list");
            }
        } catch (Exception ignored) {
            throwException(format);
        }
    }
}