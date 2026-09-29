---
navigation:
  title: Ore Refining
  icon: ftbic:ore_washer
  parent: machines/index.md
  position: 4
item_ids:
  - ftbic:ore_washer
  - ftbic:crushed_ore
  - ftbic:washed_ore
  - ftbic:refined_concentrate
---

# Ore Refining

The full line produces **5 ingots per raw ore**, or **15 ingots per ore block** for supported metals. Fortune affects the raw ore collected before processing. Datapacks can change these defaults.

## Processing line

1. **Macerator:** 1 ore block becomes **3 raw ore**.
2. **Macerator:** 1 raw ore becomes **2 crushed ore**.
3. **Ore Washer:** **2 crushed ore + 1,000 mB water** become **3 washed ore**.
4. **Centrifuge:** **3 washed ore** become **5 refined concentrate**.
5. **Any furnace:** 1 concentrate becomes **1 ingot**.

Blocks of raw ore go straight into the Macerator too. One block becomes **18 crushed ore**, the same as crushing its nine raw ore one at a time.

Starting with one ore block gives **3 raw, 6 crushed, 9 washed, 15 concentrate, and 15 ingots**. Washing that complete batch uses three buckets of water.

Crushed ore and washed ore can also be smelted directly, one item per ingot. These shorter routes yield **2 ingots per raw** after crushing, or **3 per raw** after washing. Ingots and ordinary dust cannot reenter the refining chain.

## Ore Washer

<RecipeFor id="ftbic:ore_washer" />

The washer accepts LV power and holds **16,000 mB** in each tank. Its default recipes use the left input tank; the right tank supports fluid outputs in custom recipes. Insert water with a bucket, fluid cell, or fluid automation. Set fluid and item faces independently with **I/O**.

By default it uses **<Energy config="machines.ore_washer_use" rate="true" />** and takes **20 seconds** per wash, before upgrades. The basic macerator takes 10 seconds per crushing operation, or 90 seconds for a block of raw ore; the basic centrifuge takes 30 seconds per refining batch. Machine configuration and material definitions can change these values.

Processing waits for a complete item batch, enough fluid and power, and space for every output. Use a [Batch Feeder](batch_feeder.md) to deliver two crushed ore with one bucket of water together.

## Materials and automation

FTBIC discovers metals from common raw-material and ingot tags and validates their smelting relationship. An ore tag enables the ore-to-raw entry recipe, and a raw storage block tag enables block crushing. Incomplete or conflicting mappings need a pack author's material definition. Gems and unusual processing systems are not automatically converted into metals.

Each material has separate named, colored intermediates. Matching materials stack normally; different materials stay separate. JEI shows the detected variants and their recipes. Drag the exact variant into machine input locks or Batch Feeder ghost slots.

Colors use a datapack override first, a known material palette second, and the selected ingot texture for unfamiliar metals. Resource-pack reloads refresh sampled colors. The material ID in the tooltip distinguishes similarly colored materials.

Material definitions may add up to two centrifuge byproducts. Recipes with three distinct item outputs require the **Advanced Centrifuge**. Automatic discovery supplies no guessed secondary metals.
