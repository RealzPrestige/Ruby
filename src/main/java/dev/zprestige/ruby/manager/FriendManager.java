package dev.zprestige.ruby.manager;

import java.util.ArrayList;

public class FriendManager {
    public ArrayList<FriendPlayer> friendList = new ArrayList<>();

    public void addFriend(String name) {
        if (!isFriend(name))
            friendList.add(new FriendPlayer(name));
    }

    public void removeFriend(String name) {
        friendList.removeIf(player -> player.getName().equals(name));
    }

    public ArrayList<FriendPlayer> getFriendList() {
        return friendList;
    }

    public boolean isFriend(String name) {
        return friendList.stream().anyMatch(player -> player.getName().equals(name));
    }

    public static class FriendPlayer {
        String name;

        public FriendPlayer(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
