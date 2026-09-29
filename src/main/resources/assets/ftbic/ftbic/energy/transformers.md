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

  Transformers step voltage **down** from a higher tier to a lower one. They cannot step voltage up. Every transformer has a single higher-tier face and five lower-tier faces.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

Without a transformer, feeding HV into an LV machine burns it out instantly. Transformers split a tier-N signal into the next tier down at the matching lower transfer rate.

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Row>
  <ItemImage id="lv_transformer" />
  ### <Color id="aqua">LV Transformer</Color>
</Row>

Steps MV (<Energy config="energy.mv_transfer_rate" rate="true" />) down to LV (<Energy config="energy.lv_transfer_rate" rate="true" />).

<RecipeFor id="lv_transformer" />

<Row>
  <ItemImage id="mv_transformer" />
  ### <Color id="aqua">MV Transformer</Color>
</Row>

Steps HV (<Energy config="energy.hv_transfer_rate" rate="true" />) down to MV (<Energy config="energy.mv_transfer_rate" rate="true" />).

<RecipeFor id="mv_transformer" />

<Row>
  <ItemImage id="hv_transformer" />
  ### <Color id="aqua">HV Transformer</Color>
</Row>

Steps EV (<Energy config="energy.ev_transfer_rate" rate="true" />) down to HV (<Energy config="energy.hv_transfer_rate" rate="true" />).

<RecipeFor id="hv_transformer" />

<Row>
  <ItemImage id="ev_transformer" />
  ### <Color id="aqua">EV Transformer</Color>
</Row>

Steps IV (<Energy config="energy.iv_transfer_rate" rate="true" />) down to EV (<Energy config="energy.ev_transfer_rate" rate="true" />).

<RecipeFor id="ev_transformer" />
