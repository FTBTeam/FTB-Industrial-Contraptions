---
navigation:
  title: Hydroponic Accelerator
  icon: ftbic:hydroponic_accelerator
  parent: machines/index.md
  position: 5
item_ids:
  - ftbic:hydroponic_accelerator
  - ftbic:advanced_hydroponic_accelerator
---

# Hydroponic Accelerator

<ItemImage id="hydroponic_accelerator" scale="2" float="right"/>

<RecipeFor id="hydroponic_accelerator" />

Place a seed in the **seed** slot and an allowed block in the **soil** slot. The soil stays in the machine and is never consumed. Supply water and energy to grow crops. The output slots keep produce, returned seeds, and the optional byproduct separate. The starter wheat recipe accepts dirt at normal speed or moss at **1.25× speed**. JEI lists the soil choices and speed for every recipe.

The basic machine has one growing pair and three output slots. The water tank holds 16,000 mB. A default growth cycle uses 250 mB. Use **I/O** to configure item, fluid, and energy faces. The **L** button locks seed and soil slots to exact items; you can also drag items from JEI onto those slots to set ghost locks. Ghost locks are filters and do not supply ingredients.

## Mutation mode

The Advanced Hydroponic Accelerator alone can switch between **Growth** and **Mutation**. Put one parent seed in each lane of a cross. Both parents need a valid growth recipe for their respective soils. JEI shows the available crosses and their success chance. A successful cross consumes one of each parent and produces the listed mutant item. A failed cross returns both parent seeds. Either way, the operation uses time, water, and energy. The machine waits if either possible output cannot fit.

## Advanced Hydroponic Accelerator

<ItemImage id="advanced_hydroponic_accelerator" scale="2" float="right"/>

<RecipeFor id="advanced_hydroponic_accelerator" />

The advanced machine has **four independent seed and soil pairs** and twelve output slots, grouped as produce, seeds, and byproduct for each lane. Every input pair can have its own ghost locks and crop. Its water tank holds 32,000 mB. Each active lane draws power separately, up to four times the single-lane use.

In Mutation mode, lanes **1 + 2** and **3 + 4** form two independent crosses. Each parent uses its own soil. The slower soil determines the cross speed. Failed parents return to their respective lane's seed output. The mode, locks, and progress persist when the world is saved.
