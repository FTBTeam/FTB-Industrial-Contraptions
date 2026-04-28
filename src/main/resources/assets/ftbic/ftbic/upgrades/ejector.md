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

  Automatically pushes finished items out of the machine's output slot into any adjacent inventory — hopper, chest, pipe, or another machine.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="ejector_upgrade" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

* Transfer rate is defined by the machine's configured **item transfer efficiency** (default 20 items/second-ish).
* Pairs well with the <ItemLink id="powered_crafting_table" /> to build automation chains without hopper chains.
* One ejector upgrade is enough — additional copies do not speed up output.
