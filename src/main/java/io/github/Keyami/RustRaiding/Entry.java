package io.github.Keyami.RustRaiding;

import org.bukkit.inventory.ItemStack;

public class Entry {

    private int weight;
    private ItemStack item;
    private double chance;

    public Entry(ItemStack item, int weight) {
        this.item = item;
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public ItemStack getItem() {
        return item.clone();
    }

}