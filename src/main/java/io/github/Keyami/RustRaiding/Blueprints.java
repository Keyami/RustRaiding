package io.github.Keyami.RustRaiding;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Blueprints {

    public static ItemStack getBlueprint(String name) {
        List<String> lore = new ArrayList<String>();
        ItemStack bl = new ItemStack(Material.PAPER, 1);

        lore.add(ChatColor.GRAY + "Right-click to learn this blueprint!");
        lore.add(ChatColor.GRAY + "Blueprint for: " + name);
        ItemMeta blueprintMeta = bl.getItemMeta();

        blueprintMeta.setDisplayName(ChatColor.BLUE + name);

        blueprintMeta.setLore(lore);
        bl.setItemMeta(blueprintMeta);

        return bl;

    }

}