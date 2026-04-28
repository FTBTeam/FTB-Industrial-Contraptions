---
navigation:
  title: Transformers
  icon: ftbic:mv_transformer
  parent: energy/index.md
  position: 3
item_ids:
  - ftbic:lv_transformer
  - ftbic:mv_transformer
  - ftbic:hv_transformer
  - ftbic:ev_transformer
---

# <Color id="gold">Transformers</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="mv_transformer" scale="2" />

  Transformers step voltage **down** from a higher tier to a lower one, or **up** by placing them backwards. Every transformer has a single higher-tier face and five lower-tier faces.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

Without a transformer, feeding HV into an LV machine burns it out instantly. Transformers split a tier-N signal into the next tier down at the matching lower transfer rate.

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Row>
  <ItemImage id="lv_transformer" />
  ### <Color id="aqua">LV Transformer</Color>
</Row>

Steps MV (128 zap/t) down to LV (32 zap/t).

<RecipeFor id="lv_transformer" />

<Row>
  <ItemImage id="mv_transformer" />
  ### <Color id="aqua">MV Transformer</Color>
</Row>

Steps HV (512 zap/t) down to MV (128 zap/t).

<RecipeFor id="mv_transformer" />

<Row>
  <ItemImage id="hv_transformer" />
  ### <Color id="aqua">HV Transformer</Color>
</Row>

Steps EV (2,048 zap/t) down to HV (512 zap/t).

<RecipeFor id="hv_transformer" />

<Row>
  <ItemImage id="ev_transformer" />
  ### <Color id="aqua">EV Transformer</Color>
</Row>

Steps IV (8,192 zap/t) down to EV (2,048 zap/t).

<RecipeFor id="ev_transformer" />
