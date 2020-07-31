package io.github.Keyami.RustRaiding;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.github.Keyami.RustRaiding.Blueprints.getBlueprint;
import static io.github.Keyami.RustRaiding.RocketLauncher.getRPG;
import static io.github.Keyami.RustRaiding.RustRaiding.*;

public class CommandGiveExplosive implements CommandExecutor {

    public static ItemStack rpgItem = getRPG();
    static String explosiveID;
    static String prefix = ChatColor.translateAlternateColorCodes('&', pl.getConfig().get("plugin-prefix").toString());
    public static ItemStack satchelItem = getExplosive(satchel_base64, "Satchel Charge", satchelID);
    public static ItemStack c4Item = getExplosive(c4_base64, "Timed Explosive", c4ID);
    public static ItemStack rpgAmmo = getExplosive(rocket_base64, "Rocket", rocketID);

    public static ItemStack getExplosive(String textureString, String name, String id) {
        // Create a new satchel item stack of a player head
        List<String> lore = new ArrayList<String>();

        explosiveID = id;
        if (id.equals(satchelID)) {
            lore.add(ChatColor.GRAY + "Blows up in a 1x1x1 Radius!");
            lore.add(ChatColor.GRAY + "Damage: " + pl.getConfig().get("satchel"));
        } else if (id.equals(c4ID)) {
            lore.add(ChatColor.GRAY + "Blows up in a 2x2x2 Radius!");
            lore.add(ChatColor.GRAY + "Damage: " + pl.getConfig().get("c4"));
        } else if (id.equals(rocketID)) {
            lore.add(ChatColor.GRAY + "Ammunition for the RPG");
        }

        ItemStack explosive = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta data = explosive.getItemMeta();
        data.setDisplayName(ChatColor.RED + name);
        data.setLore(lore);
        explosive.setItemMeta(data);

        // Cast its item meta to a satchel meta
        SkullMeta skullMeta = (SkullMeta) explosive.getItemMeta();

        // Create a new fake game profile
        GameProfile profile = new GameProfile(UUID.fromString(id), null);

        // Set the textures property to the texture desired
        profile.getProperties().put("textures", new Property("textures", textureString));

        // Reflection
        Field profileField = null;

        // Get the declared field
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
        } catch (Exception e1) {
            pl.getLogger().info(prefix + " No such field / Security exception");
        }

        // Set this field to be accessible
        profileField.setAccessible(true);

        // Set the satchel meta data
        try {
            profileField.set(skullMeta, profile);
        } catch (Exception e1) {
            pl.getLogger().info(prefix + " No such field / Security exception");
        }

        // reset the satchel meta to the item stack
        explosive.setItemMeta(skullMeta);
        return explosive;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        command.setUsage(ChatColor.RED + "/rr <type> " + ChatColor.GRAY + "- gives explosive.");
        String id;
        if (commandSender instanceof Player && commandSender.hasPermission("rr.give")) {
            if (!(strings.length == 1) || strings.length > 1) {
                Player p = (Player) commandSender;
                p.sendMessage(command.getUsage());
                return true;
            }
            if (strings[0].equalsIgnoreCase("satchel")) {
                Player p = (Player) commandSender;
                p.getInventory().addItem(satchelItem);
                p.sendMessage(prefix + " Obtained Satchel Charge!");
                return true;
            }
            if (strings[0].equalsIgnoreCase("c4")) {
                Player p = (Player) commandSender;
                p.getInventory().addItem(c4Item);
                p.sendMessage(prefix + " Obtained C4!");
                return true;
            }
            if (strings[0].equalsIgnoreCase("rpg")) {
                Player p = (Player) commandSender;
                p.getInventory().addItem(rpgItem);
                p.sendMessage(prefix + " Obtained Rocket Launcher!");
                return true;
            }
            if (strings[0].equalsIgnoreCase("rocket")) {
                Player p = (Player) commandSender;
                p.getInventory().addItem(rpgAmmo);
                p.sendMessage(prefix + " Obtained Ammo!");
                return true;
            }
            if (strings[0].equalsIgnoreCase("c4bl")) {
                Player p = (Player) commandSender;
                p.getInventory().addItem(getBlueprint("C4 Blueprint"));
                p.sendMessage(prefix + " Obtained C4 Blueprint!");
                return true;
            }
            if (strings[0].equalsIgnoreCase("rocketbl")) {
                Player p = (Player) commandSender;
                p.getInventory().addItem(getBlueprint("Rocket Blueprint"));
                p.sendMessage(prefix + " Obtained Rocket Blueprint!");
                return true;
            }
            if (strings[0].equalsIgnoreCase("rpgbl")) {
                Player p = (Player) commandSender;
                p.getInventory().addItem(getBlueprint("Rocket Launcher Blueprint"));
                p.sendMessage(prefix + " Obtained Rocket Launcher Blueprint!");
                return true;
            }
            if (strings[0].equalsIgnoreCase("satchelbl")) {
                Player p = (Player) commandSender;
                p.getInventory().addItem(getBlueprint("Satchel Blueprint"));
                p.sendMessage(prefix + " Obtained Satchel Blueprint!");
                return true;
            }
            Player p = (Player) commandSender;
            p.sendMessage(command.getUsage());
            return true;
        } else {
            commandSender.sendMessage(prefix + " Only players can use this command!");
            return false;
        }
    }
}