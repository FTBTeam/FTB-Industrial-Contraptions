---
navigation:
  title: Location Card
  icon: ftbic:location_card
  parent: utilities/index.md
  position: 7
item_ids:
  - ftbic:location_card
---

# <Color id="gold">Location Card</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="location_card" scale="2" />

  A portable shortcut for binding <ItemLink id="teleporter" />s. Save a teleporter's coordinates to the card, then use the card to program another teleporter's destination — no need to scroll the peer-discovery list.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="location_card" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Usage</Color>
</Column>

1. **Save a destination.** Hold an empty Location Card and right-click a <ItemLink id="teleporter" />. The card stores that teleporter's dimension, position, and name.
2. **Program a second teleporter.** Hold the bound card and right-click another teleporter you own. The clicked teleporter's destination is set to the card's saved position.
3. **Clear the card.** Sneak-right-click in the air to wipe the binding.

<ItemImage id="minecraft:air" scale="0.25"/>
***

A bound card is **foiled** (glows like an enchanted item) and shows its saved dimension, coordinates, and teleporter name in the tooltip. Handy for keeping a labelled card per base or per network hub.
