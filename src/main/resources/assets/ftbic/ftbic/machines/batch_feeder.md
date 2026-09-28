---
navigation:
  title: Batch Feeder
  icon: ftbic:batch_feeder
  parent: machines/index.md
  position: 2
item_ids:
  - ftbic:batch_feeder
---

# <Color id="gold">Batch Feeder</Color>

<ItemImage id="ftbic:batch_feeder" scale="2" />

The Batch Feeder sends complete ingredient sets to the inventory directly in front of its outlet. It requires no power and has nine item buffer slots, a **16,000 mB fluid buffer**, three ghost item settings, and one ghost fluid setting. Batches can contain items, fluid, or both.

<RecipeFor id="ftbic:batch_feeder" />

## Configure the items

1. Place the feeder with its three-channel outlet facing the destination machine or inventory.
2. Pick up an ingredient stack from your inventory, then click a ghost slot in the **Batch** column. It copies the item, its data, and its count without consuming the stack. Return the real stack to your inventory or the feeder's **Buffer**.
3. With an empty cursor, left-click a ghost slot to increase its count or right-click to decrease it. Shift-click clears that entry. Each entry is limited to one normal stack of that item, up to 64.
4. Fill the buffer manually, or insert ingredients through any face except the front using pipes or hoppers. Input sides can be disabled using **I/O**.

You can also drag items from **JEI** onto the three ghost slots. This copies the displayed item and count without creating real items.

For bronze, configure **3 copper ingots** and **1 tin ingot**, and leave the third entry empty. The feeder waits until both are available and the destination accepts all four items.

## Configure the fluid

The tall tank on the **left** is the real fluid buffer. Supply fluid through any non-front face using pipes, or click this tank with a bucket or fluid cell on your cursor. Clicking with an empty container drains it. You can also use a fluid container directly on the block. Manual tank access remains available when automatic sides are disabled.

The tall tank on the **right**, beside the three item settings, is the ghost fluid ingredient:

1. Pick up a filled bucket or fluid cell, then click the ghost tank to copy its fluid and amount. This does not consume the container or put fluid into the buffer.
2. Alternatively, click it with an empty cursor to copy the buffer's fluid, initially up to **1,000 mB**.
3. Type the exact amount in **Fluid mB**, then press **Set** or **Enter**. Any amount from **1 to 16,000 mB** is supported. Typing alone does not change the batch.
4. Right-click or Shift-click the ghost tank to clear the fluid ingredient.

You can also drag a **JEI fluid ingredient**, filled bucket, or fluid cell onto the ghost tank. Use **Fluid mB** to adjust the amount afterward.

For a fluid-only batch, leave all three item settings empty. For example, configure **1,000 mB lava** to feed a centrifuge. For a mixed batch, the destination must accept both the items and fluid through the connected face.

## Transfer rules

The feeder attempts one batch every **8 ticks**. If any ingredient is missing, or the destination cannot accept the complete batch, **no items move**. Matching includes item and fluid data components. Duplicate entries count toward the same total, so an ingredient is never counted twice from the buffer.

A redstone signal pauses feeding. Disabling **Front → Items** or **Front → Fluids** stops batches that contain that resource. Configure the receiving machine's connected face to accept each required resource.

Pipes cannot extract loose items or fluid from the buffers; automatic delivery always goes through the complete-batch operation. You can remove items manually and drain the fluid buffer with a container. If you use an intermediate chest, any later transfers out of that chest are controlled by your pipes or hoppers.

The feeder has no upgrade slots. Its batching settings persist after reloading the world. A <ItemLink id="ftbic:configuration_card" /> copies the item and fluid assignments, quantities, and side settings to another Batch Feeder, without copying buffer contents.

Use [input slot locks](side_configuration.md) on a receiving recipe machine when specific slots must remain assigned to specific ingredients.
