package io.github.Keyami.RustRaiding;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static io.github.Keyami.RustRaiding.RustRaiding.pl;

public class RocketLauncher {

    public static ItemStack getRPG() {
        List<String> lore = new ArrayList<String>();
        ItemStack rl = new ItemStack(Material.TRIDENT, 1);

        lore.add(ChatColor.GRAY + "Uses ranged rockets to");
        lore.add(ChatColor.GRAY + "explode in a 2x2x2 radius.");
        lore.add(ChatColor.GRAY + "Damage: " + pl.getConfig().get("rpg"));
        ItemMeta rpgItemMeta = rl.getItemMeta();

        rpgItemMeta.setDisplayName(ChatColor.RED + "Rocket Launcher");

        rpgItemMeta.setLore(lore);
        rl.setItemMeta(rpgItemMeta);

        return rl;

    }

}