---
navigation:
  title: Ejector Upgrade
  icon: ftbic:ejector_upgrade
  parent: upgrades/index.md
  position: 4
item_ids:
  - ftbic:ejector_upgrade
---

# <Color id="gold">Ejector Upgrade</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="ejector_upgrade" scale="2" />

  Automatically pushes finished items out of the machine's output slot into an adjacent inventory through a face that permits item output.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="ejector_upgrade" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

* Choose output faces using [Side Configuration](../machines/side_configuration.md). Input-only and disabled item faces do not eject.
* Pairs well with the <ItemLink id="powered_crafting_table" /> to build automation chains without hopper chains.
* One ejector upgrade is enough. Additional copies do not speed up output.
