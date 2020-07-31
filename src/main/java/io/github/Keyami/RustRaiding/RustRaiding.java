package io.github.Keyami.RustRaiding;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import static io.github.Keyami.RustRaiding.Blueprints.getBlueprint;
import static io.github.Keyami.RustRaiding.CommandGiveExplosive.*;
import static io.github.Keyami.RustRaiding.SupplyDrops.supplyDropSpawn;

public class RustRaiding extends JavaPlugin {
    public static RustRaiding pl;

    public static String satchelID = "d12a76ab-a565-4ffe-b162-986fa39f8025";
    public static String c4ID = "dd0c8f9e-faaa-4367-9ba3-4b8ebc6c7c48";
    public static String rocketID = "f733b369-d43e-4371-a360-29135ca55690";
    public static String bombType = null;
    public static String explicitYaw = null;

    public static String satchel_base64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVkMDk2ZjhjNjhlZWMyYTcwMWQxZDVkMmQzMDdjMjdmOGRjYmU4Mzc5ZDAwNTI4YmZiMjg2NGM2NjRjMSJ9fX0=";
    public static String c4_base64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2U0ZWNjYjE3YThlOTI2MDFhNTE5MTdjYjVhNmZjMTdlYWYyYWRjMjdlODM3MzIzNmIyMzIzZjQ3NGVmNDhmMSJ9fX0=";
    public static String rocket_base64 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODNkNDlkZGU3NWUxMmI2MGViZTZlODk4MWVhNGZiMjY2YjIwNzUyYzJmNTVlOTZhZjExM2MyODdlZWQ2M2U4MSJ9fX0=";
    public static LootTable supplyLoot;
    NamespacedKey satch = new NamespacedKey(this, "satchel");
    NamespacedKey c4 = new NamespacedKey(this, "c4");
    NamespacedKey rpg = new NamespacedKey(this, "rpg");
    NamespacedKey rocket = new NamespacedKey(this, "rocket");

    @Override
    public void onEnable() {
        getLogger().info("RustRaiding by Keyami has been enabled!");
        pl = this;

        if (pl.getConfig().getBoolean("supply-drops-enabled")) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(pl, new Runnable() {
                @Override
                public void run() {
                    supplyDropSpawn();
                }
            }, 0L, 1200 * (pl.getConfig().getInt("supply-crate-interval")));
        }

        supplyLoot = new LootTable.LootTableBuilder()
                .add(getBlueprint("Rocket Blueprint"), pl.getConfig().getInt("rocket-blueprint-supply-crate-chance"))
                .add(getBlueprint("Rocket Launcher Blueprint"), pl.getConfig().getInt("rpg-blueprint-supply-crate-chance"))
                .add(getBlueprint("C4 Blueprint"), pl.getConfig().getInt("c4-blueprint-supply-crate-chance"))
                .add(getBlueprint("Satchel Blueprint"), pl.getConfig().getInt("satchel-blueprint-supply-crate-chance"))

                .add(c4Item, pl.getConfig().getInt("c4-supply-crate-chance"))
                .add(satchelItem, pl.getConfig().getInt("satchel-supply-crate-chance"))
                .add(rpgItem, pl.getConfig().getInt("rpg-supply-crate-chance"))
                .add(rpgAmmo, pl.getConfig().getInt("rocket-supply-crate-chance"))

                .add(new ItemStack(Material.AIR, 1), 900)
                .build();

        //Satchel Recipe
        ShapedRecipe satchelRecipe = new ShapedRecipe(satch, satchelItem);
        satchelRecipe.shape("TTT", "TFT", "TTT");
        satchelRecipe.setIngredient('T', Material.TNT);
        satchelRecipe.setIngredient('F', Material.FIRE_CHARGE);

        // C4 Recipe
        ShapedRecipe c4Recipe = new ShapedRecipe(c4, c4Item);
        c4Recipe.shape("SSS", "FFF", "SSS");
        c4Recipe.setIngredient('F', Material.FIRE_CHARGE);
        c4Recipe.setIngredient('S', new RecipeChoice.ExactChoice(satchelItem));

        // Rocket Recipe
        ShapedRecipe rocketRecipe = new ShapedRecipe(rocket, rpgAmmo);
        rocketRecipe.shape("ASA", "FCF", "AFA");
        rocketRecipe.setIngredient('F', Material.FIRE_CHARGE);
        rocketRecipe.setIngredient('A', Material.AIR);
        rocketRecipe.setIngredient('S', new RecipeChoice.ExactChoice(satchelItem));
        rocketRecipe.setIngredient('C', new RecipeChoice.ExactChoice(c4Item));

        // RPG Recipe
        ShapedRecipe rpgRecipe = new ShapedRecipe(rpg, rpgItem);
        rpgRecipe.shape("AOC", "OTO", "COA");
        rpgRecipe.setIngredient('T', Material.TRIDENT);
        rpgRecipe.setIngredient('O', Material.OBSIDIAN);
        rpgRecipe.setIngredient('A', Material.AIR);
        rpgRecipe.setIngredient('C', new RecipeChoice.ExactChoice(c4Item));

        if (pl.getConfig().getBoolean("craftable")) {
            pl.getLogger().info("Crafting recipes are enabled!");
            Bukkit.addRecipe(satchelRecipe);
            Bukkit.addRecipe(rpgRecipe);
            Bukkit.addRecipe(c4Recipe);
            Bukkit.addRecipe(rocketRecipe);
        } else {
            pl.getLogger().info("Crafting recipes are disabled!");
        }

        if (pl.getConfig().getBoolean("disable-tnt")) {
            pl.getLogger().info("TNT is disabled!");
        } else {
            pl.getLogger().info("TNT is not disabled!");
        }

        if (pl.getServer().getPluginManager().isPluginEnabled("Factions")) {
            pl.getLogger().info("Factions found! Hooking!");
        }

        // /rr command
        this.getCommand("rr").setExecutor(new CommandGiveExplosive());
        this.getCommand("rr").setTabCompleter(new TabCompletion());

        getServer().getPluginManager().registerEvents(new ExplosionEvent(), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("RustRaiding by Keyami has been disabled!");

        Bukkit.getScheduler().cancelTasks(pl);

        Bukkit.removeRecipe(satch);
        Bukkit.removeRecipe(rocket);
        Bukkit.removeRecipe(rpg);
        Bukkit.removeRecipe(c4);
    }
}