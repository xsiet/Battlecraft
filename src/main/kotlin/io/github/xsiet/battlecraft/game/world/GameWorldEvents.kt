package io.github.xsiet.battlecraft.game.world

import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Animals
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.world.PortalCreateEvent

class GameWorldEvents(
    private val gameWorld: World
): Listener {
    @EventHandler
    private fun EntitySpawnEvent.on() {
        if (entity.world == gameWorld && (entity is Mob && entity !is Animals)) isCancelled = true
    }
    @EventHandler
    private fun PortalCreateEvent.on() {
        if (world == gameWorld) isCancelled = true
    }
    @EventHandler
    private fun BlockDropItemEvent.on() {
        if (block.world == gameWorld && items.size != 0) {
            fun change(material: Material, experience: Int) {
                items[0].itemStack.type = material
                block.world.spawn(block.location, ExperienceOrb::class.java).experience = experience
            }
            when (items[0].itemStack.type) {
                Material.RAW_IRON -> change(Material.IRON_INGOT, 1)
                Material.RAW_COPPER -> change(Material.COPPER_INGOT, 1)
                Material.RAW_GOLD -> change(Material.GOLD_INGOT, 1)
                Material.ANCIENT_DEBRIS -> change(Material.NETHERITE_SCRAP, 2)
                else -> return
            }
        }
    }
}