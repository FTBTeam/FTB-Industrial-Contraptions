---
navigation:
  title: Batteries & Battery Boxes
  icon: ftbic:hv_battery
  parent: energy/index.md
  position: 2
item_ids:
  - ftbic:single_use_battery
  - ftbic:lv_battery
  - ftbic:mv_battery
  - ftbic:hv_battery
  - ftbic:ev_battery
  - ftbic:creative_battery
  - ftbic:lv_battery_box
  - ftbic:mv_battery_box
  - ftbic:hv_battery_box
  - ftbic:ev_battery_box
  - ftbic:industrial_bank_cell
  - ftbic:industrial_bank_port
  - ftbic:energy_crystal
---

# <Color id="gold">Batteries & Battery Boxes</Color>

## <Color id="gold">Industrial Battery Bank</Color>

Place <ItemLink id="industrial_bank_cell" /> and <ItemLink id="industrial_bank_port" /> face to face to form one bank. Each cell adds storage; each port accepts and supplies energy through its exposed faces. Add more ports for more connections. Right-click any cell or port to see the bank's charge, capacity, and member counts. A port's screen also has four **charge** slots that charge batteries and other energy items from the bank. The side configuration on a port can restrict input and output faces.

Each cell holds <Energy config="energy.bank_cell_capacity" />. Each port automatically outputs up to <Energy config="energy.bank_port_transfer" rate="true" /> and accepts up to that amount in one input transaction. Incoming energy moves into connected cells so the port can keep accepting power. Server config controls both values and the maximum number of blocks searched per bank. Energy remains in each cell if you split the bank, and reconnecting cells restores the shared readout. Bank casings join visually along touching faces.

Sneak and right-click a port to cycle its appearance: **Port** (the default connection symbol), **Gauge**, then **Basic** (plain steel like a cell). The choice belongs to that port and survives a world reload. Changing the appearance does not change energy input or output. The selected style appears briefly above the hotbar.

Gauge shows the whole bank's fill level with a glowing bar. Set neighboring ports to Gauge on the same flat face to form a wider or taller display. A rectangle of Gauge ports fills from the bottom of the entire panel. Basic and Port styles interrupt the display, as do gaps, covered faces, and corners. Irregular Gauge shapes form smaller rectangular panels.

<RecipeFor id="industrial_bank_cell" />
<RecipeFor id="industrial_bank_port" />

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="hv_battery_box" scale="2" />

  Batteries are portable energy. Battery boxes are network buffers: drop batteries in and they charge, or pull them out charged to power a handheld device.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Handheld Batteries</Color>
</Column>

<Row>
  <ItemImage id="single_use_battery" />
  ### <Color id="aqua">Single Use Battery</Color>
</Row>

A cheap throwaway battery. <Energy config="energy.single_use_battery_capacity" />. Good for emergencies, useless for infrastructure.

<RecipeFor id="single_use_battery" />

<Row>
  <ItemImage id="lv_battery" />
  ### <Color id="aqua">LV Battery</Color>
</Row>

Holds <Energy config="energy.lv_battery_capacity" />, LV tier. The first rechargeable battery.

<RecipeFor id="lv_battery" />

<Row>
  <ItemImage id="mv_battery" />
  ### <Color id="aqua">MV Battery</Color>
</Row>

<Energy config="energy.mv_battery_capacity" />. Upgrade from the LV battery using <ItemLink id="energy_crystal" />.

<RecipeFor id="mv_battery" />

<Row>
  <ItemImage id="hv_battery" />
  ### <Color id="aqua">HV Battery</Color>
</Row>

<Energy config="energy.hv_battery_capacity" />. Feeds mid- to late-game handheld gear.

<RecipeFor id="hv_battery" />

<Row>
  <ItemImage id="ev_battery" />
  ### <Color id="aqua">EV Battery</Color>
</Row>

<Energy config="energy.ev_battery_capacity" />. The largest portable battery in the mod.

<RecipeFor id="ev_battery" />

<Row>
  <ItemImage id="creative_battery" />
  ### <Color id="aqua">Creative Battery</Color>
</Row>

Infinite energy. Creative-only.

<ItemImage id="minecraft:air" scale="0.25"/>
***

<Column alignItems="center" fullWidth={true}>
  ## <Color id="gold">Battery Boxes</Color>
</Column>

A battery box has a **discharge** slot on the left and a **charge** slot on the right. Place a charged battery in the discharge slot to feed the box; put an empty battery or other energy item in the charge slot to refill it. Each box also has an internal buffer matched to its tier.

Open **I/O**, choose **Energy**, then set any face to **Output** to send power there. You can set several faces to Output. Set a face to **Input** to receive power, **Both** to allow both directions, or **Disabled** to disconnect it. **Default** keeps the original arrangement: the front outputs, and the other faces input. Each face changes between the input and output texture when its output role changes. The same controls work on LV, MV, HV, and EV Battery Boxes.

<Row>
  <ItemImage id="lv_battery_box" />
  ### <Color id="aqua">LV Battery Box (<Energy config="energy.lv_battery_box_capacity" />)</Color>
</Row>

<RecipeFor id="lv_battery_box" />

<Row>
  <ItemImage id="mv_battery_box" />
  ### <Color id="aqua">MV Battery Box (<Energy config="energy.mv_battery_box_capacity" />)</Color>
</Row>

<RecipeFor id="mv_battery_box" />

<Row>
  <ItemImage id="hv_battery_box" />
  ### <Color id="aqua">HV Battery Box (<Energy config="energy.hv_battery_box_capacity" />)</Color>
</Row>

<RecipeFor id="hv_battery_box" />

<Row>
  <ItemImage id="ev_battery_box" />
  ### <Color id="aqua">EV Battery Box (<Energy config="energy.ev_battery_box_capacity" />)</Color>
</Row>

<RecipeFor id="ev_battery_box" />

<ItemImage id="minecraft:air" scale="0.25"/>

<Row>
  <ItemImage id="energy_crystal" />
  ### <Color id="aqua">Energy Crystal</Color>
</Row>

A compressed block of diamonds and redstone. Required to craft MV+ batteries and high-capacity devices.

<RecipeFor id="energy_crystal" />
