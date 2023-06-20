# BetterFurnaceCart
*A minecraft spigot plugin for faster furnace carts*

Are you tired of your regular old minecarts? Well, let me introduce you to the world of **Steam Powered Locomotion!** If you're not already aware in Minecraft Furnace carts can be powered with coal/charcoal to move along tracks carrying up to 3 minecarts behind it. This is achieved using the "shunting" method (where minecarts are pressed together). The main problem however is the speed limitation of this form of transport.

A Furnace cart will move slower than a player can run. So this plugin works to fix that by making steam-powered high-speed rail a reality! If you want long trains that run several times faster than normal sticky-powered rail trains, then check this plugin out!


## How it works
Furnace carts will "grab" any minecarts that are behind it and move it in sync with the furnace cart. This happens only when the furnace cart is powered AND on rails. If a turn or incline approaches, it will stop grabbing them until its past the turn/incline. It will try to re-grab the carts trailing behind, but may only manage to grab 1-2 carts.

The range of this "grab" goes up to 3 blocks wide (1 block on each side of the track) and ~5-10 blocks along the track so make sure you do not put parallel rails within a block reach of high-speed track.

## Installation
Download the Jar from [Releases](https://github.com/firez2469/BetterCoalCart/releases) and add it to your plugins folder.
