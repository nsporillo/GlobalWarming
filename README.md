# GlobalWarming-![](https://travis-ci.org/nsporillo/GlobalWarming.svg?branch=master) - Supports Minecraft 1.14+
Minecraft Server Java Edition (Spigot) plugin which adds game-changing climate change mechanics.

<a href="https://discord.gg/VR96VvC"><img src="https://discordapp.com/assets/fc0b01fe10a0b8c602fb0106d8189d9b.png" height="50"></a>

Table of Contents
=================

   * [GlobalWarming - <a target="_blank" rel="noopener noreferrer" href="https://camo.githubusercontent.com/259d74d6a1c5d317c8f7b10d09183313da539383/68747470733a2f2f7472617669732d63692e6f72672f6e73706f72696c6c6f2f476c6f62616c5761726d696e672e7376673f6272616e63683d6d6173746572"><img src="https://camo.githubusercontent.com/259d74d6a1c5d317c8f7b10d09183313da539383/68747470733a2f2f7472617669732d63692e6f72672f6e73706f72696c6c6f2f476c6f62616c5761726d696e672e7376673f6272616e63683d6d6173746572" alt="" data-canonical-src="https://travis-ci.org/nsporillo/GlobalWarming.svg?branch=master" style="max-width:100\x;"></a> - Supports Minecraft 1.14 ](#globalwarming------supports-minecraft-114)
      * [Contributing](#contributing)
      * [Install](#install)
      * [Overview](#overview)
      * [Purpose](#purpose)
      * [Mechanics](#mechanics)
      * [Challenges](#challenges)
      * [(Planned) Features](#planned-features)
      * [Negative Climate Damages (Sample Model)](#negative-climate-damages-sample-model)
      * [Roadmap](#roadmap)
      * [Related Science of Climate Change](#related-science-of-climate-change)
         * [Atmosphere warms with more CO2 because of Greenhouse Effect](#atmosphere-warms-with-more-co2-because-of-greenhouse-effect)
         * [Atmospheric Energy Budget](#atmospheric-energy-budget)
         * [Impact on Oceans](#impact-on-oceans)
         * [Ecological Niche](#ecological-niche)
      * [Suggestions](#suggestions)


## Contributing
- [Developer Setup](https://github.com/nsporillo/GlobalWarming/wiki/Developer-Setup-and-Installation)
- There is a Kanban board on Github where I'll be prioritizing work, feel free to help work on any of the tasks.
- Pull requests welcome! This is a very new project and I appreciate contributions.

## Install
+ Download and install mysql: https://dev.mysql.com/downloads/mysql/
  + Setup a user that can create tables. 
  + Add a schema called GlobalWarming (if your mysql is configured to only allow lowercase schemas, globalwarming with lowercase works, you just have to change that config in config.yaml below)
+ Setup and install BuildTools with it's Prerequisites (java and git): https://www.spigotmc.org/wiki/buildtools/
+ Setup a Spigot server using these instructions: https://www.spigotmc.org/wiki/spigot-installation/
  + Copy the spigot.jar for the build tools install to a new directory with the batch script from the Spigot server install, making sure to edit the batch script with the amount of Ram for the server and the correct file name
+ Run the server batch script to get all of the folders created
+ Copy GlobalWarming.jar into the plugins folder in your server directory
+ Run the server again now that you have the plugin.  This will create a GlobalWarming folder in the plugins folder.
+ Edit config.yaml in the GlobalWarming folder to specify your mysql db username and password.
+ You should be able to run the server with the GlobalWarming plugin successfully now and connect to your server from the Minecraft Game.

## Overview
+ Adds the concept of greenhouse gases (CO2) in the worlds atmosphere 
+ Furnaces emit CO2 when players smelt items
+ Farmed Animals emit CH4 when they are killed
+ Trees (instantly) absorb CO2 when they grow from a sapling
+ As CO2 levels rise, global temperature rises because of the [Greenhouse Effect](http://hyperphysics.phy-astr.gsu.edu/hbase/thermo/grnhse.html)

## Purpose
+ Ever had the hankering to turn a game meant for fun into an emulator of one of the modern world's toughest challenges?
+ Observe the [Prisoner's Dilemma](https://en.wikipedia.org/wiki/Prisoner%27s_dilemma#In_environmental_studies) first hand! 
+ Players are best off when they co-operate and agree to reduce their emissions
+ However, each player typically believes they are better off for themselves to emit as much as they wish. 
+ Avoiding a [Tragedy of the Commons](https://en.wikipedia.org/wiki/Tragedy_of_the_commons) might be fun! If the players don't play nice and end up dealing with the consequences, they might decide to agree to fix the planet. 

## Mechanics 
+ Every furnace burn causes a "Contribution" to emissions with an associated numerical value (based on the model)
+ Every tree growth causes a "Reduction" from emissions with an associated numerical value (based on the model)
+ The global temperature is a function of the net global carbon score. 
+ As the global temperature rises, the frequency and severity of negative climate damages increases.
+ Players can purchase "carbon offsets" which creates a tree-planting bounty for other players to fulfill. 

## Challenges
+ Designing a default model that doesn't quickly destroy worlds
+ Efficiently applying in-game mechanics changes

## Features
+ Scoreboard Integration - Players can compete for carbon neutrality! The worst polluters can be shamed.
+ Economy Integration - Set up tree-planting bounties 
+ Carbon Scorecard - Each player can see their latest carbon footprint trends via the command line.
+ Multi-world - Associate emissions in the end or nether worlds to the primary overworld with ease.
+ Custom Models - The inner numerical workings are as configurable as possible. Set thresholds, probabilities, and distributions.
+ Database storage - Load data on startup, queue DB changes to be done async and at intervals (instead of as they happen, that'd kill performance), and empty queue on shutdown. 
+ Highly configurable - Almost everything will have some degree of configuration to suit your server's needs.
+ Efficient - Despite major mechanics changes and an extensive event listening setup, GlobalWarming is fast.

## Negative Climate Damages (Sample Model)
Higher temps inherit the damages from the lower temps

| Global Temp | Effect 1 | Effect 2 | Effect 3 | Effect 4 |
| ------ | ------------ | ------- | --------|-----------|
| 14.0 C | None | | | |
| 15.0 C | Some mobs spawn less | some mobs spawn more | | |
| 16.0 C | +1 Sea level rise | Some fish die | Ice/Snow stops forming | |
| 17.0 C | +1 Sea level rise | Flora species growth stunted | Aquatic life stops spawning | Ocean flora/fauna die |
| 18.0 C | +1 Sea level rise | Area Potion Effect Clouds | Farm yields lower | Snow/Ice melts |
| 19.0 C | +1 Sea level rise | Forest Fires | Slower Health Regen | Frequent T-Storms | |
| 20.0 C | +1 Sea level rise | Severe Forest Fires | Violent T-Storms | Permanent Slowness Effect |

These are just a sample of the possible effects, these will be configurable and implement randomness and probabilities.


## Related Science of Climate Change
### Atmosphere warms with more CO2 because of Greenhouse Effect
![Greenhouse Effect](https://i.imgur.com/XsWJGz9.png)
### Atmospheric Energy Budget
+ ![Earth's Global Energy Budget](https://i.imgur.com/aHdJxXc.png)

Source: RIT "Climate Change: Science, Technology, and Policy" lecture slides
### Impact on Oceans
![Oceans](https://i.imgur.com/dJPkYAo.png)

Source: RIT "Climate Change: Science, Technology, and Policy" lecture slides
### Ecological Niche
![Niche](https://i.imgur.com/e6pwXlI.png)
- Species have a temperature range in which they can survive
- Plants also have niches (Sunlight, Temperature, Water, Nutrients, CO2, Soil, etc)

Source: RIT "Climate Change: Science, Technology, and Policy" lecture slides

## Suggestions 
Feel free to create issues on this GitHub project, or join the discord
