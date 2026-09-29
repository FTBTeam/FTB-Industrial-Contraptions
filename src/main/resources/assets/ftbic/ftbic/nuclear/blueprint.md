---
navigation:
  title: Reactor Blueprint
  icon: ftbic:reactor_blueprint
  parent: nuclear/index.md
  position: 11
item_ids:
  - ftbic:reactor_blueprint
---

# <Color id="gold">Reactor Blueprint</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="reactor_blueprint" scale="2" />

  Carry a tested layout from the <ItemLink id="reactor_simulator" /> to a <ItemLink id="nuclear_reactor" />, or give a copy to another player. A blueprint saves component positions, the chamber count, and the planned water cooling.
</Column>

<RecipeFor id="reactor_blueprint" />

## <Color id="gold">Record a Design</Color>

1. Arrange and test your layout in the [Reactor Simulator](simulator.md).
2. Keep a **blank Reactor Blueprint** in your inventory and press **Write Blueprint** in the planner.
3. The written blueprint glows and its tooltip shows the saved chamber and component counts.

You can also hold a blank blueprint and **use it on the planner block** to copy its current layout. To record another copy, use another blank blueprint. The button only writes to blank blueprints.

## <Color id="gold">Build the Reactor</Color>

1. Hold the written blueprint and **use it on the reactor block**. Its GUI opens with your saved layout shown as ghost items.
2. Attach the required <ItemLink id="nuclear_reactor_chamber" />s. **Chambers** and **Water** show the actual conditions first and the planned conditions second.
3. **Pause the reactor and disable redstone control.** Put the required components in your inventory.
4. Press **Build from Inventory**. Each matching empty slot receives one real component from your inventory.

The materials list shows how many parts are available and how many are still needed after counting correctly installed components. Use the arrows below the list to see more parts.

Missing parts stay visible as ghosts. Bring more components and press **Build from Inventory** again to finish. **Red borders** mark conflicting parts or planned slots that need more chambers. Remove conflicting parts yourself; autofill only fills empty slots.

Used components keep their existing **heat and wear**. A blueprint saves the layout, not the condition of the simulated items. Match the planned cooling and check the installed parts before starting the reactor yourself.

**Clear** removes the preview and leaves the real components in place.

## <Color id="gold">Reuse and Share</Color>

* **Load into a planner:** use a written blueprint on a planner block. Pause an active simulation first.
* **Overwrite a blueprint:** hold it and **Sneak+Use** on the planner to replace its saved design with the current layout.
* **Copy a loaded preview:** keep a blank blueprint in your inventory and press **Write Blueprint** in the reactor GUI.
* **Share a design:** hand the written blueprint to another player. Using a blueprint does not consume it.

## <Color id="gold">Presets and Clipboard Designs</Color>

The reactor can also load layouts directly. Choose a saved preset with the arrows and press **Load Preset**, or press **Export** in the planner and **Paste Design** in the reactor.

Both routes use the same preview, materials list, and **Build from Inventory** button. You can then write that loaded design onto a blank blueprint.
