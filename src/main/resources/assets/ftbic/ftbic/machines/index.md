---
navigation:
  title: Machines
  icon: ftbic:macerator
  position: 30
---

# <Color id="gold">Machines</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="macerator" scale="2" />

  Every FTBIC machine runs on the same framework: an inventory, a recipe tick loop, an energy buffer, and up to four **upgrade** slots. Advanced variants are the same machine with larger buffers and higher throughput.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Machine Framework</Color>
</Column>

All machines share:

* **Energy buffer** — shown on the right of the GUI
* **Progress bar** — fills as the recipe ticks; default 200 ticks / recipe
* **Upgrade slots** — install <ItemLink id="overclocker_upgrade" />, <ItemLink id="energy_storage_upgrade" />, <ItemLink id="transformer_upgrade" />, or <ItemLink id="ejector_upgrade" />
* **Face I/O** — configurable item and energy sides from the GUI

All LV machines are built around the <ItemLink id="machine_block" /> shell plus a recipe-specific core (for example, <ItemLink id="electronic_circuit" /> plus flint and stone for the Macerator).

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Subsections</Color>
</Column>

<SubPages icons={true} />
