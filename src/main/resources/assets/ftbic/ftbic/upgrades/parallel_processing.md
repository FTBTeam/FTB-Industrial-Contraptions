---
navigation:
  title: Parallel Processing
  icon: ftbic:parallel_processing_upgrade
  parent: upgrades/index.md
  position: 5
item_ids:
  - ftbic:parallel_processing_upgrade
---

# <Color id="gold">Parallel Processing Upgrade</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="parallel_processing_upgrade" scale="2" />

  Process more copies of a recipe in the same cycle. Each upgrade adds **one operation**, up to **four operations** with three upgrades.
</Column>

<RecipeFor id="parallel_processing_upgrade" />

## Compatible machines

Install this upgrade in the **Advanced Powered Furnace**, **Advanced Macerator**, **Advanced Compressor**, **Advanced Centrifuge**, **Alloy Smelter**, or **Reprocessor**. The limit is **three upgrades per machine**, shared across its upgrade slots. Basic processors and utility machines do not accept it.

## How batches work

* All operations run the **same recipe** and share one progress bar. A cycle takes the usual processing time.
* At the start of each cycle, the machine chooses as many operations as its ingredients, output space, and stored energy allow. It can still run smaller batches.
* Every operation needs a full set of ingredients. In a recipe with multiple item ingredients, keep each ingredient in its own input slot, with enough items for the batch.
* Centrifuge batches multiply both item and fluid quantities. Each operation rolls chance outputs independently. The machine requires room for every possible output before proceeding.
* Ingredients are consumed when the cycle finishes. Adding ingredients during a cycle increases the next batch, without increasing the batch already in progress.

The **running / maximum** counter beneath the progress arrow shows how many operations are running. Hover over it for an explanation. An idle or blocked machine shows zero running operations.

## Power and interruptions

Each operation uses the machine's full energy cost per tick. Four operations use **four times the energy per tick**, with the same energy cost per recipe as one operation. <ItemLink id="overclocker_upgrade" /> upgrades still affect both speed and the energy cost of each operation.

For example, a machine using **<Energy zaps="20" rate="true" />** uses **<Energy zaps="80" rate="true" />** while processing four operations. Make sure its cables, power supply, and energy storage can keep up.

Blocked outputs pause the cycle without using energy. Running out of power, losing required ingredients, changing recipes, or removing upgrades below the current batch size resets unfinished progress. Unfinished batches consume no ingredients and produce no output. Saving and loading preserves the batch size and progress.
