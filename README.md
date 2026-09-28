# FTB Industrial Contraptions

## Admin commands

Use `setupgrades` to set the total number of a specific upgrade type in a machine for admin testing. Requires gamemaster permissions (operator level 2).

```mcfunction
/ftbic setupgrades <x> <y> <z> <upgrade> <count>
```

For example, set the machine directly below you to four overclocker upgrades:

```mcfunction
/ftbic setupgrades ~ ~-1 ~ ftbic:overclocker_upgrade 4
```

Upgrade item IDs support tab completion. The command creates or removes upgrades without consuming items and preserves other upgrade types. Set the count to `0` to remove the selected type. The target must be in a loaded chunk in the command's dimension, and normal machine compatibility, upgrade limits, and available slots still apply. Rejected requests leave the machine unchanged.

## Credits

> This mod was inspired by Industrial Craft, an original concept by Alblaka. All content within the 'FTB Industrial Contraptions' was created by the FTB Team 

**Textures by** [@Ridanisaurus](https://github.com/Ridanisaurus/)
