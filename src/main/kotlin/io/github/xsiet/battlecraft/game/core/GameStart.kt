package io.github.xsiet.battlecraft.game.core

import io.github.xsiet.battlecraft.Process
import io.github.xsiet.battlecraft.utils.*
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Difficulty
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
        it.gameMode = GameMode.SPECTATOR
        it.resetData()
        it.addPotionEffect(PotionEffectType.BLINDNESS, 180, 0, false)
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
        fun decreaseWorldBorder(newSize: Int, delayedMinutes: Int, delayedSeconds: Int) {
            server.showTitle(text(""), text("게임 구역 축소 중...", NamedTextColor.RED))
            server.playSound("entity.elder_guardian.curse")
            isDecreasingWorldBorder = true
            val seconds = ((delayedMinutes * 60) + delayedSeconds).toLong()
            worldBorder.setSize(newSize.toDouble(), seconds)
            server.scheduler.runTaskLater(plugin, Runnable {
                isDecreasingWorldBorder = false
            }, seconds * 20L)
        }
        processor.apply {
            addProcess(Process("파밍 시간 종료", 8 ,0, true) {
                survivors.forEach {
                    it.isInvulnerable = false
                }
                world.difficulty = Difficulty.HARD
            })
            addProcess(Process("게임 구역 축소 시작 (1/8)", 6, 0, true) {
                decreaseWorldBorder(800, 2, 0)
            })
            addProcess(Process("게임 구역 축소 완료 (1/8)", 2, 0, false) {})
            addProcess(Process("게임 구역 축소 시작 (2/8)", 6, 0, true) {
                decreaseWorldBorder(600, 2, 0)
            })
            addProcess(Process("게임 구역 축소 완료 (2/8)", 2, 0, false) {})
            addProcess(Process("게임 구역 축소 시작 (3/8)", 6, 0, true) {
                decreaseWorldBorder(400, 2, 0)
            })
            addProcess(Process("게임 구역 축소 완료 (3/8)", 2, 0, false) {})
            addProcess(Process("게임 구역 축소 시작 (4/8)", 6, 0, true) {
                decreaseWorldBorder(200, 2, 0)
            })
            addProcess(Process("게임 구역 축소 완료 (4/8)", 2, 0, false) {})
            addProcess(Process("게임 구역 축소 시작 (5/8)", 3, 0, true) {
                decreaseWorldBorder(100, 1, 0)
            })
            addProcess(Process("게임 구역 축소 완료 (5/8)", 1, 0, false) {})
            addProcess(Process("게임 구역 축소 시작 (6/8)", 1, 30, true) {
                decreaseWorldBorder(50, 0, 30)
            })
            addProcess(Process("게임 구역 축소 완료 (6/8)", 0, 30, false) {})
            addProcess(Process("게임 구역 축소 시작 (7/8)", 1, 30, true) {
                decreaseWorldBorder(10, 0, 30)
            })
            addProcess(Process("게임 구역 축소 완료 (7/8)", 0, 30, false) {})
            addProcess(Process("게임 구역 축소 시작 (8/8)", 1, 30, true) {
                decreaseWorldBorder(2, 0, 30)
            })
            addProcess(Process("게임 구역 축소 완료 (8/8)", 0, 30, false) {})
            run()
        }
    }, delay + 20L)
}