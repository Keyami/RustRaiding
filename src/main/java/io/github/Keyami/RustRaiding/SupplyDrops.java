package io.github.Keyami.RustRaiding;

import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Random;

import static io.github.Keyami.RustRaiding.RustRaiding.pl;

public class SupplyDrops {

    public static void supplyDropSpawn() {
        int maxX = pl.getConfig().getInt("supply-crate-max-x");
        int minX = pl.getConfig().getInt("supply-crate-min-x");

        int maxZ = pl.getConfig().getInt("supply-crate-max-z");
        int minZ = pl.getConfig().getInt("supply-crate-min-x");

        //Randomize the numbers that you'll TP to.
        Random rand = new Random();
        int rangeX = maxX - minX + 1;
        int newX = rand.nextInt(rangeX) + minX;

        //Randomize the Z value here.
        int rangeZ = maxZ - minZ + 1;
        int newZ = rand.nextInt(rangeZ) + minZ;

        Location sampleInfo = new Location(pl.getServer().getWorld(Objects.requireNonNull(pl.getConfig().getString("supply-drop-world"))), newX, 100, newZ);
        final Location dropFinal = new Location(pl.getServer().getWorld(Objects.requireNonNull(pl.getConfig().getString("supply-drop-world"))), newX, sampleInfo.getWorld().getHighestBlockYAt(sampleInfo) + 1, newZ);

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', pl.getConfig().getString("plugin-prefix")) + " A supply crate has spawned at X: " + newX + " Z: " + newZ);

        dropFinal.getBlock().setType(Material.CHEST);
        dropFinal.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, dropFinal, 100);

        final Chest supplyDrop = (Chest) dropFinal.getBlock().getState();

        int j = 0;
        for (ItemStack item : supplyDrop.getBlockInventory().getContents()) {
            if (item == null) {
                supplyDrop.getBlockInventory().setItem(j, LootTable.getRandom());
                j++;
            }
        }

        Bukkit.getServer().getScheduler().runTaskLater(pl, new Runnable() {
            @Override
            public void run() {
                dropFinal.getBlock().setType(Material.AIR);
            }
        }, 1200 * pl.getConfig().getInt("supply-crate-despawn-timer"));

    }
}

