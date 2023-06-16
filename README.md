# Custom Entity Models (but for Fabric)

An implementation of custom entity models heavily based off of Optifine's format that aims to achieve feature parity with Optifine's custom entity models.

## Current State of this Mod

This mod is currently in **_ALPHA_**, meaning that things _MAY_ or _MAY NOT_ work properly! There is absolutely no
guarantee that things will work as intended or at all!

## Installation

Check out [Modrinth](https://modrinth.com/mod/cem) page,
the [CurseForge page](https://www.curseforge.com/minecraft/mc-mods/custom-entity-models-cem "CurseForge Page"), or
build it yourself. This mod requires the installation of the [Fabric Loader](https://fabricmc.net/use/ "Fabric Loader").
If you want to configure this mod, the installation
of [YACL](https://github.com/isXander/YetAnotherConfigLib "yacl") is also required.

## How to use

This mod loads .jem and .jpm files from "assets/dorianpb/cem" folder in resource packs. For resource packs intended to
be used with Optifine, renaming "
assets/minecraft/optifine/cem" to "assets/dorianpb/cem" should work fine. If you install the optional dependencies, this
isn't necessary as there is an option within the
config to just load from optifine's folder structure.

## Discord Server Link

Join my discord server here: https://discord.gg/3qxFZm7Ycw

## For resource pack devs

The file format for the .jem and .jpm is identical to the Optfine's (check out [here](https://github.com/sp614x/optifine/blob/master/OptiFineDoc/doc/cem_model.txt ".jem")
, [here](https://github.com/sp614x/optifine/blob/master/OptiFineDoc/doc/cem_part.txt ".jpm"),
and [here](https://github.com/sp614x/optifine/blob/master/OptiFineDoc/doc/cem_animation.txt "animations")). The folder structures inside "assets/dorianpb/cem" does not matter.
If you want to assign a custom model for a creeper, you could put the file as "assets/dorianpb/cem/creeper.jem", "assets/dorianpb/cem/creeper/creeper.jem", "
assets/dorianpb/cem/za/waurdo/creeper.jem", etc.

## Differences

- Most living entites should have support, and some non-living entites might not have anmimations.
- No support for armor right now.

- Individual part textures are not supported (you can only specify texture and texture size in the .jem file).
- Sprites are not supported.
- The `attach` option is not supported.
- Currently, the animations do behave slightly differently than optifine's, just enough to break some packs.

## Licensing

This project is licensed with LGPL v3.0. Please don't repost this project anywhere without my written permission first.

## Goals

- Implement the rest of the entities
- Make animations perfect
- `attach` support
- A better icon! This one succs

## For mod devs

If your fabric mod creates new entities, you can add support for CEM without too much work! Look at
net.dorianpb.external for an example implementation. Make sure to use the "
cem" entrypoint.

## Credits

- dorianpb, for actually creating the mod
- sp614x, for Optifine's CEM, which this mod is based on
- Mojang, for actually creating Minecraft
- The Zombie Pigman, whose restoration is the entirety of my motivation for this project
- Anyone else who decides to contribute ([Look here](https://github.com/dorianpb/cem/graphs/contributors))
