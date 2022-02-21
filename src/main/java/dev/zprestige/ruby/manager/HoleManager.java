package dev.zprestige.ruby.manager;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.util.BlockUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class HoleManager {
    public ArrayList<BlockPos> obsidianHoles = new ArrayList<>();
    public ArrayList<BlockPos> bedrockHoles = new ArrayList<>();
    public Thread thread1 = new Thread(() -> {
        while (true) {
            try {
                obsidianHoles = setupObsidian();
            } catch (Exception ignored) {
            }

        }
    });
    public Thread thread2 = new Thread(() -> {
        while (true) {
            try {
                bedrockHoles = setupBedrock();
            } catch (Exception ignored) {
            }
        }
    });

    @SuppressWarnings("deprecation")
    public void onThreadReset() {
        thread1.stop();
        thread1 = new Thread(() -> {
            while (true) {
                try {
                    obsidianHoles = setupObsidian();
                } catch (Exception ignored) {
                }
            }
        });
        thread2.stop();
        thread2 = new Thread(() -> {
            while (true) {
                try {
                    bedrockHoles = setupBedrock();
                } catch (Exception ignored) {
                }
            }
        });
    }

    public void onRenderWorldLastEvent() {
        if (thread1 != null && (!thread1.isAlive() || thread1.isInterrupted()))
            thread1.start();
        if (thread2 != null && (!thread2.isAlive() || thread2.isInterrupted()))
            thread2.start();
    }

    public ArrayList<BlockPos> setupBedrock() {
        ArrayList<BlockPos> posses = new ArrayList<>();
        for (BlockPos pos : BlockUtil.getSphere(50.0, BlockUtil.AirType.OnlyAir, Ruby.mc.player)) {
            if (Ruby.mc.world.getBlockState(pos.up().up()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK) {
                posses.add(pos);
            } else if (Ruby.mc.world.getBlockState(pos.up().up()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos.north().up()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.north().down()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.north()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos.north().north()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.north().east()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.north().west()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK) {
                posses.add(pos);
                posses.add(pos.north());
            } else {
                if (Ruby.mc.world.getBlockState(pos.up().up()).getBlock() != Blocks.AIR || Ruby.mc.world.getBlockState(pos.up()).getBlock() != Blocks.AIR || Ruby.mc.world.getBlockState(pos.west().up()).getBlock() != Blocks.AIR || Ruby.mc.world.getBlockState(pos).getBlock() != Blocks.AIR || Ruby.mc.world.getBlockState(pos.down()).getBlock() != Blocks.BEDROCK || Ruby.mc.world.getBlockState(pos.west().down()).getBlock() != Blocks.BEDROCK || Ruby.mc.world.getBlockState(pos.north()).getBlock() != Blocks.BEDROCK || Ruby.mc.world.getBlockState(pos.south()).getBlock() != Blocks.BEDROCK || Ruby.mc.world.getBlockState(pos.west()).getBlock() != Blocks.AIR || Ruby.mc.world.getBlockState(pos.east()).getBlock() != Blocks.BEDROCK || Ruby.mc.world.getBlockState(pos.west().south()).getBlock() != Blocks.BEDROCK || Ruby.mc.world.getBlockState(pos.west().north()).getBlock() != Blocks.BEDROCK || Ruby.mc.world.getBlockState(pos.west().west()).getBlock() != Blocks.BEDROCK) {
                    continue;
                }
                posses.add(pos);
                posses.add(pos.west());
            }
        }
        if (!posses.isEmpty()) {
            return posses;
        }
        return null;
    }

    public ArrayList<BlockPos> setupObsidian() {
        ArrayList<BlockPos> posses = new ArrayList<>();
        for (BlockPos pos : BlockUtil.getSphere(50.0, BlockUtil.AirType.OnlyAir, Ruby.mc.player)) {
            if (Ruby.mc.world.getBlockState(pos.up().up()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK) {
                ignoreStatement();
            } else if (Ruby.mc.world.getBlockState(pos.up().up()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos).getBlock() == Blocks.AIR && (Ruby.mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK || Ruby.mc.world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN) && (Ruby.mc.world.getBlockState(pos.north()).getBlock() == Blocks.OBSIDIAN || Ruby.mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK) && (Ruby.mc.world.getBlockState(pos.south()).getBlock() == Blocks.OBSIDIAN || Ruby.mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK) && (Ruby.mc.world.getBlockState(pos.west()).getBlock() == Blocks.OBSIDIAN || Ruby.mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK) && (Ruby.mc.world.getBlockState(pos.east()).getBlock() == Blocks.OBSIDIAN || Ruby.mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK)) {
                posses.add(pos);
            } else if (Ruby.mc.world.getBlockState(pos.up().up()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos.north().up()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.north().down()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.north()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos.north().north()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.north().east()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.north().west()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK) {
                ignoreStatement();
            } else if (Ruby.mc.world.getBlockState(pos.up().up()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos.north().up()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos).getBlock() == Blocks.AIR && (Ruby.mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK || Ruby.mc.world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN) && Ruby.mc.world.getBlockState(pos.north()).getBlock() == Blocks.AIR && (Ruby.mc.world.getBlockState(pos.south()).getBlock() == Blocks.OBSIDIAN || Ruby.mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK) && (Ruby.mc.world.getBlockState(pos.west()).getBlock() == Blocks.OBSIDIAN || Ruby.mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK) && (Ruby.mc.world.getBlockState(pos.east()).getBlock() == Blocks.OBSIDIAN || Ruby.mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK) && (Ruby.mc.world.getBlockState(pos.north().north()).getBlock() == Blocks.BEDROCK || Ruby.mc.world.getBlockState(pos.north().north()).getBlock() == Blocks.OBSIDIAN) && (Ruby.mc.world.getBlockState(pos.north().east()).getBlock() == Blocks.BEDROCK || Ruby.mc.world.getBlockState(pos.north().east()).getBlock() == Blocks.OBSIDIAN) && (Ruby.mc.world.getBlockState(pos.north().west()).getBlock() == Blocks.BEDROCK || Ruby.mc.world.getBlockState(pos.north().west()).getBlock() == Blocks.OBSIDIAN) && (Ruby.mc.world.getBlockState(pos.north().down()).getBlock() == Blocks.OBSIDIAN || Ruby.mc.world.getBlockState(pos.north().down()).getBlock() == Blocks.BEDROCK)) {
                posses.add(pos);
                posses.add(pos.north());
            } else if (Ruby.mc.world.getBlockState(pos.up().up()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos.west().up()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.west().down()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.west()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.west().south()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.west().north()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.west().west()).getBlock() == Blocks.BEDROCK) {
                ignoreStatement();
            } else {
                if (Ruby.mc.world.getBlockState(pos.up().up()).getBlock() != Blocks.AIR || Ruby.mc.world.getBlockState(pos.up()).getBlock() != Blocks.AIR || Ruby.mc.world.getBlockState(pos.west().up()).getBlock() != Blocks.AIR || Ruby.mc.world.getBlockState(pos).getBlock() != Blocks.AIR || (Ruby.mc.world.getBlockState(pos.down()).getBlock() != Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.down()).getBlock() != Blocks.OBSIDIAN) || (Ruby.mc.world.getBlockState(pos.west().down()).getBlock() != Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.west().down()).getBlock() != Blocks.OBSIDIAN) || (Ruby.mc.world.getBlockState(pos.north()).getBlock() != Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.north()).getBlock() != Blocks.OBSIDIAN) || (Ruby.mc.world.getBlockState(pos.south()).getBlock() != Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.south()).getBlock() != Blocks.OBSIDIAN) || Ruby.mc.world.getBlockState(pos.west()).getBlock() != Blocks.AIR || (Ruby.mc.world.getBlockState(pos.east()).getBlock() != Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.east()).getBlock() != Blocks.OBSIDIAN) || (Ruby.mc.world.getBlockState(pos.west().south()).getBlock() != Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.west().south()).getBlock() != Blocks.OBSIDIAN) || (Ruby.mc.world.getBlockState(pos.west().north()).getBlock() != Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.west().north()).getBlock() != Blocks.OBSIDIAN) || (Ruby.mc.world.getBlockState(pos.west().west()).getBlock() != Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.west().west()).getBlock() != Blocks.OBSIDIAN)) {
                    continue;
                }
                posses.add(pos);
                posses.add(pos.west());
            }
        }
        if (!posses.isEmpty()) {
            return posses;
        }
        return null;
    }

    public void ignoreStatement() {
    }

    public ArrayList<BlockPos> getObsidianHoles(double radius) {
        if (obsidianHoles != null && !obsidianHoles.isEmpty())
            return obsidianHoles.stream().filter(pos -> Ruby.mc.player.getDistanceSq(pos) < (radius * radius)).collect(Collectors.toCollection(ArrayList::new));
        return null;

    }

    public ArrayList<BlockPos> getBedrockHoles(double radius) {
        if (bedrockHoles != null && !bedrockHoles.isEmpty())
            return bedrockHoles.stream().filter(pos -> Ruby.mc.player.getDistanceSq(pos) < (radius * radius)).collect(Collectors.toCollection(ArrayList::new));
        return null;
    }
}
