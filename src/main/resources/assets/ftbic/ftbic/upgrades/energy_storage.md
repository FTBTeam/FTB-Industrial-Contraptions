---
navigation:
  title: Energy Storage Upgrade
  icon: ftbic:energy_storage_upgrade
  parent: upgrades/index.md
  position: 3
item_ids:
  - ftbic:energy_storage_upgrade
---

# <Color id="gold">Energy Storage Upgrade</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="energy_storage_upgrade" scale="2" />

  Adds **<Energy config="machines.storage_upgrade" />** to a machine's internal buffer per upgrade. Useful for smoothing out bursty workloads or for machines that need to run briefly off-grid.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="energy_storage_upgrade" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

Install up to **4** per machine for an extra <Energy config="machines.storage_upgrade" times="4" /> of local buffer.
