package dev.zprestige.ruby.manager;

import java.util.ArrayList;

public class EnemyManager {
    public ArrayList<EnemyPlayer> enemyList = new ArrayList<>();

    public void addEnemy(String name) {
        if (!isEnemy(name))
            enemyList.add(new EnemyPlayer(name));
    }

    public void removeEnemy(String name) {
        enemyList.removeIf(player -> player.getName().equals(name));
    }

    public ArrayList<EnemyPlayer> getEnemyList() {
        return enemyList;
    }

    public boolean isEnemy(String name) {
        return enemyList.stream().anyMatch(player -> player.getName().equals(name));
    }

    public static class EnemyPlayer {
        String name;

        public EnemyPlayer(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
