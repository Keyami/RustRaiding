package io.github.Keyami.RustRaiding;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.github.Keyami.RustRaiding.Blueprints.getBlueprint;
import static io.github.Keyami.RustRaiding.CommandGiveExplosive.*;
import static io.github.Keyami.RustRaiding.RustRaiding.*;

public class ExplosionEvent implements Listener {

    File customYml = new File(pl.getDataFolder() + "/learned.yml");
    FileConfiguration learned = YamlConfiguration.loadConfiguration(customYml);

    public void saveCustomYml(FileConfiguration ymlConfig, File ymlFile) {
        try {
            ymlConfig.save(ymlFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * getDirection
     *
     * Get the direction that the player is facing.
     *
     * loc      [in]           Current location of player.
     *
     * Returns a string with the name of the cardinal direction the player is facing.
     */
    private String getDirection(Location loc) {
        int pitch = (Math.round(loc.getPitch()));
        int degrees = Math.round(loc.getYaw() - 90) % 360;
        if (degrees < 0) {
            degrees += 360.0;
        }

        if (pitch < -38) {
            return "Up";
        } else if (pitch > 48) {
            return "Down";
        } else if ((degrees >= 135 && degrees <= 225)) {
            return "East";
        } else if ((degrees >= 0 && degrees <= 45) || (degrees >= 300 && degrees <= 360)) {
            return "West";
        } else if (degrees > 225 && degrees < 300) {
            return "South";
        } else if (degrees > 45 && degrees < 135) {
            return "North";
        }
        return getDirection(loc);
    }

    /*
     * setExplosionLocation
     *
     * Uses the getDirection function to find the correct location where the explosion should occur
     * relative to the player. It also creates that explosion at the correct location.
     *
     * loc        [in]           The players current location.
     * skull      [in]           The skull that was placed.
     * skullID    [in]           The UUID of the skull.
     * strength   [in]           Float, explosion strength.
     * destroy    [in]           Bool to destroy blocks.
     * fire       [in]           Bool to set fire to nearby blocks.
     * b          [in]           Block at location.
     * timer      [in]           Timer before explosion is set off.
     *
     */
    public void setExplosionLocation(final Location loc, Skull skull, String skullID, final Float strength, final Boolean destroy, final Boolean fire, final Block b, final int timer) {
        if (skull.getOwningPlayer().getUniqueId().toString().equals(skullID)) {
            final Location explosion = new Location(b.getLocation().getWorld(), b.getLocation().getBlockX(), b.getLocation().getBlockY(), b.getLocation().getBlockZ());

            if (getDirection(loc).equals("North")) explosion.subtract(0, 0, 1);
            if (getDirection(loc).equals("South")) explosion.add(0, 0, 1);

            if (getDirection(loc).equals("East")) explosion.add(1, 0, 0);
            if (getDirection(loc).equals("West")) explosion.subtract(1, 0, 0);

            if (getDirection(loc).equals("Up")) explosion.add(0, 1, 0);
            if (getDirection(loc).equals("Down")) explosion.subtract(0, 1, 0);

            explicitYaw = getDirection(loc);

            Bukkit.getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
                public void run() {
                    b.setType(Material.AIR);
                    b.getWorld().createExplosion(explosion, strength, fire, destroy);
                }
            }, timer);
        }
    }

    /*
     * setRPGExplosionLocation
     *
     * Sister function to setExplosionLocation, same thing except
     * it's intended for the rocket launcher explosion.
     *
     * loc       [in]           The players current location.
     * strength  [in]           Float, explosion strength.
     * destroy   [in]           Bool to destroy blocks.
     * fire      [in]           Bool to set fire to nearby blocks.
     *
     */
    public void setRPGExplosionLocation(final Location loc, final Float strength, final Boolean destroy, final Boolean fire) {

        if (getDirection(loc).equals("North")) loc.subtract(0, 0, 1);
        if (getDirection(loc).equals("South")) loc.add(0, 0, 1);

        if (getDirection(loc).equals("East")) loc.add(1, 0, 0);
        if (getDirection(loc).equals("West")) loc.subtract(1, 0, 0);

        if (getDirection(loc).equals("Up")) loc.add(0, 1, 0);
        if (getDirection(loc).equals("Down")) loc.subtract(0, 1, 0);

        explicitYaw = getDirection(loc);

        loc.getWorld().createExplosion(loc, strength, fire, destroy);
    }

    /*
     * checkMetadata
     *
     * Check if the effected blocks have our custom metadata,
     * if they don't, then we set the metadata to the defaults
     * defined in the config. If it does have the metadata,
     * then we decrease it by the damage of the explosive.
     *
     * b        [in]           The block we're checking.
     * damage   [in]           Int, damage dealt by the explosive.
     * health   [in]           Int, health of the block.
     *
     */
    public void checkMetadata(Block b, int damage, int health) {
        if (!b.hasMetadata("hp")) {
            b.setMetadata("hp", new FixedMetadataValue(pl, health));
            b.setMetadata("hits", new FixedMetadataValue(pl, damage));
        } else if (b.hasMetadata("hp")) {
            b.setMetadata("hits", new FixedMetadataValue(pl, (b.getMetadata("hits").get(0).asInt() + damage)));
        }
        if (Integer.valueOf((Integer) b.getMetadata("hits").get(0).value()) >= Integer.valueOf((Integer) b.getMetadata("hp").get(0).value())) {
            b.setType(Material.AIR);
            b.setMetadata("hits", new FixedMetadataValue(pl, 0));
        }
    }

    /*
     * obsidianCheck
     *
     * Obsidian does not get included in the returned explosion
     * list because it is immune to explosions, so we have to have a manual
     * check at the explosions location. Since I can't check the exact spread,
     * I opted to to use a 1x3 for all explosives. Then we add those blocks to
     * list of blocked impacted.
     *
     * initial  [in]           The block we start the loop at.
     * range    [in]           How far to check nearby blocks.
     * l        [in]           The location we are checking blocks at.
     * blocks   [in]           The list we are adding these obsidian blocks to.
     *
     * Returns the modified list of blocks.
     */
    public void obsidianCheck(int initial, int range, Location l, List<Block> blocks) {
        if (explicitYaw.equals("North")) {
            for (int x = initial; x <= range; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= range; z++) {
                        Block block = l.getWorld().getBlockAt(l.getBlockX() + x, l.getBlockY() + y, l.getBlockZ() - z);
                        if (block.getBlockData().getMaterial().toString().equals("OBSIDIAN") ||
                                block.getBlockData().getMaterial().toString().equals("BEDROCK")) {
                            blocks.add(block);
                        }
                    }
                }
            }
        }
        if (explicitYaw.equals("South")) {
            for (int x = initial; x <= range; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= range; z++) {
                        Block block = l.getWorld().getBlockAt(l.getBlockX() + x, l.getBlockY() + y, l.getBlockZ() + z);
                        if (block.getBlockData().getMaterial().toString().equals("OBSIDIAN") ||
                                block.getBlockData().getMaterial().toString().equals("BEDROCK")) {
                            blocks.add(block);
                        }
                    }
                }
            }
        }
        if (explicitYaw.equals("East")) {
            for (int x = -1; x <= range; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = initial; z <= range; z++) {
                        Block block = l.getWorld().getBlockAt(l.getBlockX() + x, l.getBlockY() + y, l.getBlockZ() + z);
                        if (block.getBlockData().getMaterial().toString().equals("OBSIDIAN") ||
                                block.getBlockData().getMaterial().toString().equals("BEDROCK")) {
                            blocks.add(block);
                        }
                    }
                }
            }
        }
        if (explicitYaw.equals("West")) {
            for (int x = initial; x <= range; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = initial; z <= range; z++) {
                        Block block = l.getWorld().getBlockAt(l.getBlockX() + x, l.getBlockY() + y, l.getBlockZ() - z);
                        if (block.getBlockData().getMaterial().toString().equals("OBSIDIAN") ||
                                block.getBlockData().getMaterial().toString().equals("BEDROCK")) {
                            blocks.add(block);
                        }
                    }
                }
            }
        }
        if (explicitYaw.equals("Up")) {
            for (int x = initial; x <= range; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= range; z++) {
                        Block block = l.getWorld().getBlockAt(l.getBlockX() + x, l.getBlockY() + y, l.getBlockZ() + z);
                        if (block.getBlockData().getMaterial().toString().equals("OBSIDIAN") ||
                                block.getBlockData().getMaterial().toString().equals("BEDROCK")) {
                            blocks.add(block);
                        }
                    }
                }
            }
        }
        if (explicitYaw.equals("Down")) {
            for (int x = initial; x <= range; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = initial; z <= range; z++) {
                        Block block = l.getWorld().getBlockAt(l.getBlockX() + x, l.getBlockY() - y, l.getBlockZ() + z);
                        if (block.getBlockData().getMaterial().toString().equals("OBSIDIAN") ||
                                block.getBlockData().getMaterial().toString().equals("BEDROCK")) {
                            blocks.add(block);
                        }
                    }
                }
            }
        }
    }

    //Checks if the block placed was a RustRaiding specific explosive
    @EventHandler(priority = EventPriority.HIGH)
    public void checkExplosivePlaced(BlockPlaceEvent e) {
        if (pl.getConfig().getBoolean("explosives-enabled")) {
            //Set our variables.
            Block block = e.getBlock();
            Player p = e.getPlayer();
            Location loc = p.getLocation();

            float c4Strength = Float.parseFloat(Objects.requireNonNull(pl.getConfig().getString("c4-explosion-strength")));
            float satchelStrength = Float.parseFloat(Objects.requireNonNull(pl.getConfig().getString("satchel-explosion-strength")));

            //If the block is a skull that also has our specific UUID assigned
            if (block.getState() instanceof Skull) {
                Skull skull = ((Skull) block.getState());
                String skullID = Objects.requireNonNull(skull.getOwningPlayer()).getUniqueId().toString();
                if (skullID.equals(satchelID)) {
                    // Factions Hook
                    if (pl.getServer().getPluginManager().isPluginEnabled("Factions")) {
                        FLocation floc = new FLocation(e.getBlock().getLocation());
                        Faction faction = Board.getInstance().getFactionAt(floc);
                        if (!(faction.getTag().equals(ChatColor.DARK_GREEN + "Wilderness"))) {
                            e.setCancelled(false);
                        }
                    }
                    //Sets a random timer for the satchel charge
                    double x = (Math.random() * ((3.0 - 0) + 1)) + 0;
                    int timer = (int) (x * 20);
                    //Assign the correct name and enters the explosion function.
                    bombType = "satchel";
                    setExplosionLocation(loc, skull, skullID, satchelStrength, true, false, block, timer);
                }
                if (skullID.equals(c4ID)) {
                    // Factions Hook
                    if (pl.getServer().getPluginManager().isPluginEnabled("Factions")) {
                        FLocation floc = new FLocation(e.getBlock().getLocation());
                        Faction faction = Board.getInstance().getFactionAt(floc);
                        if (!(faction.getTag().equals(ChatColor.DARK_GREEN + "Wilderness"))) {
                            e.setCancelled(false);
                        }
                    }
                    //Sets the timer for C4
                    int timer = 120;
                    //Assigns the correct name and enters the explosion functions
                    bombType = "c4";
                    setExplosionLocation(loc, skull, skullID, c4Strength, true, false, block, timer);
                }
            }
            if (pl.getConfig().get("disable-tnt").toString().equals("true")) {
                if (e.getBlockPlaced().getBlockData().getMaterial().equals(Material.TNT)) {
                    e.setCancelled(true);
                }
            }
        } else {
            Block block = e.getBlock();
            Player p = e.getPlayer();
            if (block.getState() instanceof Skull) {
                Skull skull = ((Skull) block.getState());
                String skullID = Objects.requireNonNull(skull.getOwningPlayer()).getUniqueId().toString();
                if (skullID.equals(satchelID) || (skullID.equals(c4ID)) || (skullID.equals(rocketID))) {
                    p.sendMessage(prefix + " Explosives are disabled!");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void checkExplosion(BlockExplodeEvent e) {
        //Cancel the explosion
        e.setCancelled(true);

        //Create the look of an explosion.
        e.getBlock().getWorld().spawnParticle(Particle.EXPLOSION_HUGE, e.getBlock().getLocation(), 3);
        //After testing, other players can hear this sound up to 210 blocks away from source.
        e.getBlock().getWorld().playSound(e.getBlock().getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 15, -1);
        //Despite being cancelled, this even takes in a list of the blocks that should have been impacted.
        List<Block> blocks = e.blockList();
        Location l = e.getBlock().getLocation();

        obsidianCheck(0, 0, l, blocks);

        World world = l.getWorld();

        List<String> disabled = pl.getConfig().getStringList("disabled-blocks");
        for (Block b : blocks) {
            if (!disabled.contains(b.getBlockData().getMaterial().toString())) {
                if (pl.getConfig().getBoolean("custom-hp-enabled")) {
                    for (String string : pl.getConfig().getStringList("custom-hp")) {
                        String configBlockHealth = string.replaceAll("[^\\d.]", "");
                        String configBlock = string.replaceAll("[^A-Za-z_]", "");
                        int configBlockHealthInt = Integer.parseInt(configBlockHealth);

                        if (b.getBlockData().getMaterial().toString().equals(configBlock)) {
                            if (bombType.equals("satchel")) {
                                checkMetadata(b, pl.getConfig().getInt("satchel"), configBlockHealthInt);
                            }
                            if (bombType.equals("c4")) {
                                checkMetadata(b, pl.getConfig().getInt("c4"), configBlockHealthInt);
                            }
                            if (bombType.equals("rpg")) {
                                checkMetadata(b, pl.getConfig().getInt("rpg"), configBlockHealthInt);
                            }
                        }
                    }
                }
            }
            if (!disabled.contains(b.getBlockData().getMaterial().toString())) {
                if (bombType.equals("satchel")) {
                    checkMetadata(b, pl.getConfig().getInt("satchel"), 500);
                }
                if (bombType.equals("c4")) {
                    checkMetadata(b, pl.getConfig().getInt("c4"), 500);
                }
                if (bombType.equals("rpg")) {
                    checkMetadata(b, pl.getConfig().getInt("rpg"), 500);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void checkBreakExplosive(BlockBreakEvent e) {
        Block b = e.getBlock();
        if (b.getState() instanceof Skull) {
            Skull skull = ((Skull) b.getState());
            String skullID = (Objects.requireNonNull(skull.getOwningPlayer())).getUniqueId().toString();
            if (skullID.equals(satchelID)) {
                e.setDropItems(false);
                e.getPlayer().getInventory().addItem(satchelItem);
            } else if (skullID.equals(c4ID)) {
                e.setDropItems(false);
                e.getPlayer().getInventory().addItem(c4Item);
            } else if (skullID.equals(rocketID)) {
                e.setDropItems(false);
                e.getPlayer().getInventory().addItem(rpgAmmo);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void checkBlockHealth(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Block b = e.getClickedBlock();
        if (p.getInventory().getItemInMainHand().getType().toString().equalsIgnoreCase(Objects.requireNonNull(pl.getConfig().get("health-checker")).toString())) {
            if (!(b == null)) {
                if (b.hasMetadata("hp")) {
                    p.sendMessage(ChatColor.RED + "Health: " + ChatColor.GRAY + (b.getMetadata("hp").get(0).asInt() - b.getMetadata("hits").get(0).asInt()) + " / " + b.getMetadata("hp").get(0).value());
                } else {
                    if (pl.getConfig().getBoolean("custom-hp-enabled")) {
                        for (String string : pl.getConfig().getStringList("custom-hp")) {
                            String configBlockHealth = string.replaceAll("[^\\d.]", "");
                            String configBlock = string.replaceAll("[^A-Za-z_]", "");
                            int configBlockHealthInt = Integer.parseInt(configBlockHealth);
                            if (b.getBlockData().getMaterial().toString().equals(configBlock)) {
                                b.setMetadata("hp", new FixedMetadataValue(pl, configBlockHealthInt));
                                b.setMetadata("hits", new FixedMetadataValue(pl, 0));
                                p.sendMessage(ChatColor.RED + "Health: " + ChatColor.GRAY + (b.getMetadata("hp").get(0).asInt() - b.getMetadata("hits").get(0).asInt()) + " / " + b.getMetadata("hp").get(0).value());
                                break;
                            } else {
                                b.setMetadata("hp", new FixedMetadataValue(pl, 500));
                                b.setMetadata("hits", new FixedMetadataValue(pl, 0));
                            }
                        }
                    } else {
                        b.setMetadata("hp", new FixedMetadataValue(pl, 500));
                        b.setMetadata("hits", new FixedMetadataValue(pl, 0));
                        p.sendMessage(ChatColor.RED + "Health: " + ChatColor.GRAY + (b.getMetadata("hp").get(0).asInt() - b.getMetadata("hits").get(0).asInt()) + " / " + b.getMetadata("hp").get(0).value());
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void rpgExplode(ProjectileLaunchEvent e) {

        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.GRAY + "Ammunition for the RPG");

        if (e.getEntity().getShooter() instanceof Player) {
            Player p = (Player) e.getEntity().getShooter();
            if (e.getEntityType().equals(EntityType.TRIDENT) &&
                    (p.getInventory().getItemInMainHand().equals(rpgItem))) {
                if (pl.getConfig().getBoolean("rpg-enabled")) {
                    e.setCancelled(true);
                    for (ItemStack item : p.getInventory().getContents()) {
                        if (!(item == null) &&
                                item.getItemMeta() instanceof SkullMeta &&
                                item.getItemMeta().hasLore() &&
                                item.getItemMeta().getLore().equals(lore)) {

                            item.setAmount(item.getAmount() - 1);
                            Location start = e.getLocation();
                            Vector dir = e.getEntity().getVelocity();
                            dir.multiply(10);
                            dir.normalize();

                            List<String> blockRange = new ArrayList<String>();
                            Location loc = start.add(dir);
                            while (loc.getBlock().getBlockData().getMaterial().equals(Material.AIR) &&
                                    blockRange.size() < Integer.parseInt(pl.getConfig().get("rpg-max-range").toString())) {
                                blockRange.add(loc.getBlock().getBlockData().getMaterial().toString());
                                loc.getWorld().spawnParticle(Particle.SPELL, loc, 10);
                                loc = start.add(dir);
                            }
                            bombType = "rpg";
                            setRPGExplosionLocation(loc, 4.0F, true, true);
                        }
                    }
                } else {
                    p.sendMessage(prefix + " Rocket Launcher is disabled!");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void learnBlueprint(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                && e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.PAPER)) {
            if (e.getPlayer().getInventory().getItemInMainHand().hasItemMeta()) {
                List<String> bl = e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getLore();

                if (bl.equals(getBlueprint("Rocket Blueprint").getItemMeta().getLore())) {
                    if (!(learned.isSet("rocket-learned." + e.getPlayer().getUniqueId()))) {
                        e.getPlayer().getInventory().remove(getBlueprint("Rocket Blueprint"));
                        e.getPlayer().sendMessage(prefix + " Learned Rocket Blueprint.");
                        learned.set("rocket-learned." + e.getPlayer().getUniqueId(), true);
                        saveCustomYml(learned, customYml);
                    } else {
                        e.getPlayer().sendMessage(prefix + " You already know this blueprint!");
                    }
                }
                if (bl.equals(getBlueprint("C4 Blueprint").getItemMeta().getLore())) {
                    if (!(learned.isSet("c4-learned." + e.getPlayer().getUniqueId()))) {
                        e.getPlayer().getInventory().remove(getBlueprint("C4 Blueprint"));
                        e.getPlayer().sendMessage(prefix + " Learned C4 Blueprint.");
                        learned.set("c4-learned." + e.getPlayer().getUniqueId(), true);
                        saveCustomYml(learned, customYml);
                    } else {
                        e.getPlayer().sendMessage(prefix + " You already know this blueprint!");
                    }
                }
                if (bl.equals(getBlueprint("Rocket Launcher Blueprint").getItemMeta().getLore())) {
                    if (!(learned.isSet("rpg-learned." + e.getPlayer().getUniqueId()))) {
                        e.getPlayer().getInventory().remove(getBlueprint("Rocket Launcher Blueprint"));
                        e.getPlayer().sendMessage(prefix + " Learned Rocket Launcher Blueprint.");
                        learned.set("rpg-learned." + e.getPlayer().getUniqueId(), true);
                        saveCustomYml(learned, customYml);
                    } else {
                        e.getPlayer().sendMessage(prefix + " You already know this blueprint!");
                    }
                }
                if (bl.equals(getBlueprint("Satchel Blueprint").getItemMeta().getLore())) {
                    if (!(learned.isSet("satchel-learned." + e.getPlayer().getUniqueId()))) {
                        e.getPlayer().getInventory().remove(getBlueprint("Satchel Blueprint"));
                        e.getPlayer().sendMessage(prefix + " Learned Satchel Blueprint.");
                        learned.set("satchel-learned." + e.getPlayer().getUniqueId(), true);
                        saveCustomYml(learned, customYml);
                    } else {
                        e.getPlayer().sendMessage(prefix + " You already know this blueprint!");
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void craftExplosive(CraftItemEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (pl.getConfig().getBoolean("blueprints-required")) {
            if (e.getRecipe().getResult().getType().equals(Material.PLAYER_HEAD)) {
                if (Objects.requireNonNull(e.getRecipe().getResult().getItemMeta()).getLore().equals(rpgAmmo.getItemMeta().getLore())) {
                    if (!(learned.isSet("rocket-learned." + p.getUniqueId()))) {
                        e.setCancelled(true);
                        p.sendMessage(prefix + " You haven't learned this recipe yet!");
                    }
                } else if (e.getRecipe().getResult().getItemMeta().getLore().equals(c4Item.getItemMeta().getLore())) {
                    if (!(learned.isSet("c4-learned." + p.getUniqueId()))) {
                        e.setCancelled(true);
                        p.sendMessage(prefix + " You haven't learned this recipe yet!");
                    }
                } else if (e.getRecipe().getResult().getItemMeta().getLore().equals(satchelItem.getItemMeta().getLore())) {
                    if (!(learned.isSet("satchel-learned." + p.getUniqueId()))) {
                        e.setCancelled(true);
                        p.sendMessage(prefix + " You haven't learned this recipe yet!");
                    }
                } else if (e.getRecipe().getResult().getItemMeta().getLore().equals(rpgItem.getItemMeta().getLore())) {
                    if (!(learned.isSet("rpg-learned." + p.getUniqueId()))) {
                        e.setCancelled(true);
                        p.sendMessage(prefix + " You haven't learned this recipe yet!");
                    }
                }
            }
        }
    }
}