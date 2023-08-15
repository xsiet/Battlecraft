package io.github.xsiet.battlecraft.game.core

import io.github.xsiet.battlecraft.game.Process
import io.github.xsiet.battlecraft.utils.playSound
import io.github.xsiet.battlecraft.utils.showTitle
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Difficulty

fun Game.runTask() {
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
        addProcess(Process("파밍 시간 종료", 30, 0, true) {
            survivors.forEach {
                it.isInvulnerable = false
            }
            world.difficulty = Difficulty.HARD
        })
        addProcess(Process("게임 구역 축소 시작 (1/5)", 1, 0, false) {
            decreaseWorldBorder(800, 1, 30)
        })
        addProcess(Process("게임 구역 축소 완료 (1/5)", 1, 30, false) {})
        addProcess(Process("게임 구역 축소 시작 (2/5)", 1, 0, false) {
            decreaseWorldBorder(600, 1, 30)
        })
        addProcess(Process("게임 구역 축소 완료 (2/5)", 1, 30, false) {})
        addProcess(Process("게임 구역 축소 시작 (3/5)", 1, 0, false) {
            decreaseWorldBorder(400, 1, 30)
        })
        addProcess(Process("게임 구역 축소 완료 (3/5)", 1, 30, false) {})
        addProcess(Process("게임 구역 축소 시작 (4/5)", 1, 0, false) {
            decreaseWorldBorder(200, 1, 30)
        })
        addProcess(Process("게임 구역 축소 완료 (4/5)", 1, 30, false) {})
        addProcess(Process("게임 구역 축소 시작 (5/5)", 0, 45, false) {
            decreaseWorldBorder(50, 1, 15)
        })
        addProcess(Process("게임 구역 축소 완료 (5/5)", 1, 15, false) {})
        run()
    }
}