package io.github.xsiet.battlecraft.items

import io.github.xsiet.battlecraft.utils.addPotionEffect
import io.github.xsiet.battlecraft.utils.genericMaxHealth
import io.github.xsiet.battlecraft.utils.playSound
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.potion.PotionEffectType

class CustomItemsEvents: Listener {
    @EventHandler
    private fun PlayerInteractEvent.on() {
        if (action.isRightClick) {
            val itemStack = player.inventory.itemInMainHand
            val playerHead = Material.PLAYER_HEAD
            if (itemStack.type == playerHead && !player.hasCooldown(playerHead)) {
                isCancelled = true
                itemStack.amount --
                player.apply {
                    swingMainHand()
                    setCooldown(playerHead, 5 * 20)
                    playSound("entity.generic.eat")
                    if (foodLevel + 2 > 20) foodLevel = 20
                    else foodLevel += 4
                    if (health + 4 > genericMaxHealth) health = genericMaxHealth
                    else health += 4
                    addPotionEffect(PotionEffectType.REGENERATION, 5 * 20, 2)
                    addPotionEffect(PotionEffectType.SPEED, 10 * 20, 0)
                    addPotionEffect(PotionEffectType.ABSORPTION, 2 * 60 * 20, 0)
                }
            }
        }
    }
    @EventHandler
    private fun InventoryClickEvent.on() {
        if (currentItem == CustomItems.lockedSlot) isCancelled = true
    }
}