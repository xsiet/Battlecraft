package io.github.xsiet.battlecraft.game.core

import io.github.xsiet.battlecraft.utils.*
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffectType
import kotlin.random.Random

fun Game.start() {
    state = GameState.STARTING
    var delay = 10L
    server.onlinePlayers.forEach {
        survivors.add(it)
        it.gameMode = GameMode.ADVENTURE
        it.resetData()
        it.addPotionEffect(PotionEffectType.BLINDNESS, delay.toInt() + 20 + (server.onlinePlayers.size * 5), 0, false)
        it.showTitle(text(""), text("게임 시작 중...", NamedTextColor.GOLD), 180)
        it.inventory.apply {
            setItem(EquipmentSlot.OFF_HAND, ItemStack(Material.COMPASS))
            addItem(ItemStack(Material.STONE_AXE))
            addItem(ItemStack(Material.STONE_SHOVEL))
            addItem(ItemStack(Material.STONE_PICKAXE))
            addItem(ItemStack(Material.COOKED_BEEF, 16))
        }
        server.scheduler.runTaskLater(plugin, Runnable {
            val from = -480
            val until = 480
            val randomX = Random.nextInt(from, until)
            val randomZ = Random.nextInt(from, until)
            it.teleport(world, randomX.toDouble(), world.getHighestBlockYAt(randomX, randomZ).toDouble() + 1, randomZ.toDouble())
        }, delay)
        delay += 5L
    }
    server.scheduler.runTaskLater(plugin, Runnable {
        gameWorld.setCageFloor(Material.AIR)
        world.apply {
            worldBorder.size = 1000.0
        }
        state = GameState.PLAYING
        survivors.forEach {
            it.gameMode = GameMode.SURVIVAL
        }
        server.showTitle(text("GAME START!", NamedTextColor.GREEN), text("마지막까지 살아남으세요!"))
        server.playSound("entity.player.levelup")
        runTask()
    }, delay + 20L)
}