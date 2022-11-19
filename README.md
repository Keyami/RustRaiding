# RustRaiding 1.16.4

The primary purpose of this plugin is to incorporate the explosives from the game Rust:
- Rocket Launcher & Rockets
- Time Explosive (aka C4)
- Satchel Charges
- Blueprints for every explosive can be found in supply crates.

This plugin also operates as a general purpose Factions plugin, some minor accompanying features include:
- Disable normal TNT.
- Allow mining spawners in claimed land.
- Allow placing explosives and using the Rocket Launcher in claimed land.

Supply crates by default will drop every hour and despawn after 45 minutes. Default item spawn chances:
- Rocket Launcher Blueprint: 3%
- Rocket Blueprint: 8%
- Satchel Blueprint: 21%
- Timed Explosive Blueprint: 10%
- Rocket Launcher: 6%
- Rocket: 12%
- Satchel Charge: 25%
- Timed Explosive: 15%


/rr <bp/give> <explosive name>

BP (aka Blueprint) provides the blueprint for an explosive.

Give grants you the respective item.[/code]
Permission: rr.give



In order to craft any explosives, you have to unlock the recipe. Blueprints can be found in supply crates, or spawned in using the /rr command.
For admins, every player has the blueprints they've learned attached to their UUID. If you want to remove a blueprint, you can go to learned.yml and set that blueprint to FALSE or you can delete the players information entirely.


#######################
#####Configuration#####
#######################

# Command prefix
plugin-prefix: "&0[&c&lRust Raidables&r&0]&r&7"

# Enable/Disable RPG and Explosives
explosives-enabled: true
rpg-enabled: true

# Should you be able to craft the explosives?
craftable: true

# If you are using this for hardcore factions then you should enable this option.
disable-tnt: false

# Makes spawners mineable in faction territory
mine-spawners: true

# By default, all blocks have 500 HP.
default-hp: 500

# Should player have to find blueprints to craft explosives?
blueprints-required: true

# Supply crates enabled?
supply-drops-enabled: true

# World where supply crates should drop.
supply-drop-world: "world"

# Supply crate spawning range.
supply-crate-max-x: 5000
supply-crate-min-x: -5000

supply-crate-max-z: 5000
supply-crate-min-z: -5000

# How long (in minutes) between each supply crate drop.
supply-crate-interval: 60

# How long (in minutes) before crates despawn?
supply-crate-despawn-timer: 35

# Supply crate item spawn chance weighted at 100:
c4-supply-crate-chance: 15
satchel-supply-crate-chance: 25
rocket-supply-crate-chance: 12
rpg-supply-crate-chance: 6

c4-blueprint-supply-crate-chance: 10
satchel-blueprint-supply-crate-chance: 21
rocket-blueprint-supply-crate-chance: 8
rpg-blueprint-supply-crate-chance: 3

# Add blocks that shouldn't be effected by the explosions.
disabled-blocks:
  - AIR

# Set the damage for the explosives.
rpg: 350
c4: 475
satchel: 75

# Set the range (in blocks) that a rocket can fly for.
rpg-max-range: 150

# Set the explosions range. For reference, TNT is 4.0
# If this isn't a float value, the plugin will disable itself.
# IF you go lower than 1.5, some blocks will not be detected by the explosion.
rpg-explosion-strength: 5.0
c4-explosion-strength: 3.0
satchel-explosion-strength: 3.0

#Sets the item to check block health.
health-checker: BAMBOO

# Custom HP for blocks.
# The format for adding custom HP to blocks is simply <block name> <health>
# By default, all unspecified blocks have 500 HP.
custom-hp:
  - WOODEN_DOOR 200
  - ACACIA_LOG 250
  - ACACIA_PLANKS 250
  - ACACIA_SLAB 250
  - ACACIA_STAIRS 250
  - ACACIA_WOOD 250
  - BIRCH_LOG 250
  - BIRCH_PLANKS 250
  - BIRCH_SLAB 250
  - BIRCH_STAIRS 250
  - BIRCH_WOOD 250
  - DARK_OAK_LOG 250
  - DARK_OAK_PLANKS 250
  - DARK_OAK_SLAB 250
  - DARK_OAK_STAIRS 250
  - DARK_OAK_WOOD 250
  - JUNGLE_LOG 250
  - JUNGLE_PLANKS 250
  - JUNGLE_SLAB 250
  - JUNGLE_STAIRS 250
  - JUNGLE_WOOD 250
  - OAK_LOG 250
  - OAK_PLANKS 250
  - OAK_PRESSURE_PLATE 250
  - OAK_SLAB 250
  - OAK_STAIRS 250
  - OAK_WOOD 250
  - SPRUCE_LOG 250
  - SPRUCE_PLANKS 250
  - SPRUCE_SLAB 250
  - SPRUCE_STAIRS 250
  - SPRUCE_WOOD 250
  - STRIPPED_ACACIA_WOOD 250
  - STRIPPED_BIRCH_WOOD 250
  - STRIPPED_DARK_OAK_WOOD 250
  - STRIPPED_JUNGLE_WOOD 250
  - STRIPPED_OAK_WOOD 250
  - STRIPPED_SPRUCE_WOOD 250
  - GOLD_BLOCK 350
  - IRON_BLOCK 1000
  - IRON_BARS 300
  - IRON_DOOR 800
  - OBSIDIAN 2000
  - ACACIA_LEAVES 50
  - BIRCH_LEAVES 50
  - DARK_OAK_LEAVES 50
  - JUNGLE_LEAVES 50
  - OAK_LEAVES 50
  - SPRUCE_LEAVES 50
  - GRASS_BLOCK 150
  - DIRT 100
  - BEDROCK 2000[/code]
