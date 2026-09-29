---
navigation:
  title: Quarry Filter
  icon: ftbic:quarry_filter_upgrade
  parent: upgrades/index.md
  position: 6
item_ids:
  - ftbic:quarry_filter_upgrade
---

# <Color id="gold">Quarry Filter Upgrade</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="quarry_filter_upgrade" scale="2" />

  Makes the <ItemLink id="quarry" /> mine selected blocks while leaving unwanted blocks in the world.
</Column>

<RecipeFor id="quarry_filter_upgrade" />

Install **one** in a Quarry upgrade slot. Open the **F** button beside the Quarry screen to select block samples or block tags, choose whitelist or blacklist, and optionally enable ore-only mode. Drag block items from JEI into the sample slots, or click with a block item on your cursor. Press Enter after typing a block tag.

Ore-only mode uses `#c:ores`. Skipped blocks use no mining energy. A Configuration Card can copy the filter settings to another Quarry. See the <ItemLink id="quarry" /> page for details.
