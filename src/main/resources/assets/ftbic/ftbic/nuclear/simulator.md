---
navigation:
  title: Reactor Simulator
  icon: ftbic:reactor_simulator
  parent: nuclear/index.md
  position: 10
item_ids:
  - ftbic:reactor_simulator
---

# <Color id="gold">Reactor Simulator</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="reactor_simulator" scale="2" />

  A design bench for nuclear reactors. Drop components into the grid, run them at up to 1000x speed, and see exactly how hot the reactor will get before you build the real thing in world.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">How to use it</Color>
</Column>

1. Place the simulator and open its GUI.
2. Use the chamber stepper to pick how many reactor chambers your real design will have (0 to 6, which unlocks 3 to 9 active columns).
3. Use the water stepper to set how much of the reactor hull will be touching water. Matches the real reactor's environmental cooling multiplier.
4. Fill the grid with components. Drag from JEI, or shift-click from your own inventory. Right-click a slot to clear it. You are not consuming real items. The simulator just needs to know the layout.
5. Click **Analyze** for a quick verdict. The simulator runs the full fuel-rod lifetime on a copy of your design and reports either **STABLE** or **OVERHEATS @ cycle N**.
6. Click **Start** to run the live simulation. Use the speed row to pick 20x / 100x / 500x / 1000x. Numbers update live: current power per tick, cumulative power, and elapsed cycles.

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Controls</Color>
</Column>

* **Start**: begin or resume the simulation.
* **Pause**: freeze mid-run. Slots become editable again. Run counters stay where they were.
* **Reset**: clear every slot and reset all counters. The whole board goes back to blank.
* **Restart**: re-run the current design from cycle 0. Fuel rods and coolant cells are refilled to full durability.
* **Analyze**: run the full fuel-rod lifetime as a background check without touching live counters.

While the simulation is active (running and not paused) the slots lock and the chamber/water steppers are disabled. Pause first to edit.

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Sharing designs</Color>
</Column>

* **Export** copies a JSON description of the current design to your clipboard. Paste it into chat or save it in a notes file.
* **Import** reads JSON from your clipboard and loads it into the simulator.

The JSON format is human-readable and stable across versions. A web-based simulator can read and write the exact same format, so designs you build in-game can be edited online and vice-versa.

<ItemImage id="minecraft:air" scale="0.25"/>
***

The simulator requires no power. Make a few. Share them around. When you finally build the real thing in your base, you already know how it's going to behave.
