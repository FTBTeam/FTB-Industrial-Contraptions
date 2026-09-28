---
navigation:
  title: Side Configuration
  icon: ftbic:configuration_card
  parent: machines/index.md
  position: 1
item_ids:
  - ftbic:configuration_card
---

# <Color id="gold">Side Configuration</Color>

Use the **I/O** button beside a machine's screen to choose which faces can transfer resources. Select **Items**, **Fluids**, or **Energy**. Only resource types supported by that machine appear, and the panel remembers your last selected tab during the current game session.

The colored squares form a view from **in front of the machine**: **Front** is in the center, **Top** above it, **Bottom** below it, and **Left** and **Right** on either side. **Back** is the separate square at the lower left. Each square shows its face name and mode symbol; the legend explains the colors.

* **Left-click** a face to cycle forward through its available modes.
* **Right-click** to cycle backward.
* **Shift-click** to restore just that face to **Default** for the selected resource.
* Hover a face to see its full name, world direction, and current mode.

You can also use Tab to focus a face and Enter or Space to cycle forward; Shift+Enter or Shift+Space resets it. Rotating a machine rotates its settings. Machines without a facing use north as their front.

## Modes

* **Default (*)** keeps the machine's original transfer rules. New machines start here.
* **Input (+)** allows insertion into valid input slots or tanks.
* **Output (-)** allows extraction from valid output slots or tanks.
* **Both (+/-)** allows input and output where the machine supports both.
* **Disabled (X)** blocks transfers of the selected resource through that face.

Item, fluid, and energy settings are independent. Disabling items on a face still allows energy there unless you disable that too. Manual access to the machine's inventory stays available.

**Reset to Defaults** restores all faces for all resource types. Machines in existing worlds keep their original behavior until configured.

## Example: Macerator

Set the **Top** item face to **Input**, the **Back** item face to **Output**, and the other item faces to **Disabled**. Feed ore from above and collect dust behind the machine. Energy can enter through a separately configured face.

Pipes and hoppers can extract from an output face directly. Install an <ItemLink id="ftbic:ejector_upgrade" /> if you want the machine to push finished items into an adjacent inventory itself. The ejector uses only faces that permit item output. Side configuration does not add automatic input pulling.

## Machine Rules

Side settings preserve the machine's slot restrictions, energy limits, and conversion rules. Battery boxes default to front output and input on the other faces; you can assign input, output, or both to any face. Their face texture follows the selected output role. Transformers retain their front input<ZapMode>, and rectifiers retain their front FE input</ZapMode>. The remaining electrical faces keep their existing roles; they can be disabled individually.

Energy settings apply to FTBIC cables and to FE connections from other mods wherever those interfaces are available. **Default** retains the original behavior of your energy compatibility mode.

A pump offers fluid output; a geothermal generator offers fluid input. On teleporters, item/fluid input fills the send inventory/tank and output drains the receive inventory/tank. Side settings control local access, while the link between teleporters continues operating normally.

Reactor chambers share their reactor's settings, using the outward face being accessed. A top-face setting applies to the top of the reactor and the top of each attached chamber. Reactor component slots still hold one item each. The Reactor Planner has no physical resource I/O to configure.

## Input Slot Locks

Electric machines with ordinary item input slots have an **L** button below **I/O**. Use it to assign ghost items to their input slots:

* Pick up an item on your cursor before opening **L**, then click an input row to assign it. The item is not consumed.
* With an empty cursor, clicking copies that input slot's existing item.
* Right-click or Shift-click a row to unlock it.

You can also **drag an item from JEI directly onto a machine input slot**, or onto a row in the **L** panel. This creates a ghost filter even if you do not own the item; it never adds items to the inventory. Output and auxiliary battery, pickaxe, and upgrade slots are not targets. This also works on the Powered Crafting Table's nine inputs, generators, pumps, charging slots, and Antimatter Constructor.

The **Iron Furnace** accepts JEI ghosts on its input and fuel slots. With an empty cursor, right-click an empty ghost to clear it, or Shift-right-click a filled locked slot. These filters also restrict hopper insertion.

A locked slot only accepts its assigned item with matching data components. It remembers that assignment when processing empties the slot; a faded ghost item and cyan marker show the lock. Existing contents are not removed when you change a lock, and you can still take them out manually. The locks persist after reloading the world. Reactor layouts use their separate design controls.

This is useful for an Alloy Smelter: reserve one input for copper and another for tin. Use a [Batch Feeder](batch_feeder.md) when you also need exact ingredient quantities.

## Configuration Card

<ItemImage id="ftbic:configuration_card" scale="2" />

<RecipeFor id="ftbic:configuration_card" />

* **Sneak+Use** on an electric machine or Batch Feeder copies its side settings, input locks, and Batch Feeder pattern (where supported) onto the card, overwriting any previous settings.
* **Use** on another machine of the **same type** applies those settings.
* The tooltip identifies the saved machine type. Applying settings keeps the card reusable.

Applying a card replaces the target's supported settings, including clearing locks or batch entries that were empty on the source. It does not move inventory contents, energy, upgrades, or reactor layouts. Use a [Reactor Blueprint](../nuclear/blueprint.md) for reactor designs.
