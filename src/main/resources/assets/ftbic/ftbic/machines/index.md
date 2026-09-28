---
navigation:
  title: Machines
  icon: ftbic:macerator
  position: 30
---

# <Color id="gold">Machines</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="macerator" scale="2" />

  FTBIC's recipe-processing machines share an inventory, a recipe tick loop, an energy buffer, and up to four **upgrade** slots. Advanced variants have larger buffers and higher throughput. Utility machines such as the Batch Feeder have their own controls.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Machine Framework</Color>
</Column>

Recipe-processing machines share:

* **Energy buffer**: shown on the right of the GUI
* **Progress bar**: fills as the recipe ticks; default 200 ticks / recipe
* **Upgrade slots**: install <ItemLink id="overclocker_upgrade" />, <ItemLink id="energy_storage_upgrade" />, <ItemLink id="transformer_upgrade" />, or <ItemLink id="ejector_upgrade" />
* **[Side Configuration](side_configuration.md)**: independent item, fluid, and energy faces from the I/O button
* **[Input Slot Locks](side_configuration.md)**: persistent ghost assignments from the L button

All LV machines are built around the <ItemLink id="machine_block" /> shell plus a recipe-specific core (for example, <ItemLink id="electronic_circuit" /> plus flint and stone for the Macerator).

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Subsections</Color>
</Column>

<SubPages icons={true} />
