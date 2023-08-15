package io.github.xsiet.battlecraft.game

import io.github.xsiet.battlecraft.BattlecraftPlugin
import io.github.xsiet.battlecraft.utils.playSound
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor

class Process(
    val name: String,
    val delayedMinutes: Int,
    val delayedSeconds: Int,
    val notify: Boolean,
    val task: Runnable
) {
    var remainingMinutes = delayedMinutes
    var remainingSeconds = delayedSeconds
}
class Processor(
    private val plugin: BattlecraftPlugin
) {
    private val server = plugin.server
    private val processes = ArrayList<Process>()
    var recess = false
    fun addProcess(process: Process) = processes.add(process)
    fun run() {
        var process = processes[0]
        val bossBar = BossBar.bossBar(text(""), 0F, BossBar.Color.GREEN, BossBar.Overlay.PROGRESS)
        fun getRemainingTimeComponent(color: NamedTextColor) = text("${process.remainingMinutes}분 ${process.remainingSeconds}초", color)
        fun getProcessNameComponent() = text(process.name, NamedTextColor.GOLD)
        fun sendNotify(timeColor: NamedTextColor) {
            if (process.notify) {
                server.sendMessage(getProcessNameComponent()
                    .append(text("까지 ", NamedTextColor.WHITE))
                    .append(getRemainingTimeComponent(timeColor))
                    .append(text(" 남았습니다!", NamedTextColor.WHITE)))
                server.playSound("ui.button.click")
            }
        }
        server.scheduler.runTaskTimer(plugin, Runnable {
            if (!recess) {
                if (process.remainingSeconds == 0) {
                    process.remainingMinutes --
                    process.remainingSeconds = 59
                }
                else process.remainingSeconds --
                val seconds = (process.remainingMinutes * 60) + process.remainingSeconds
                var textColor = NamedTextColor.GREEN
                var bossBarColor = BossBar.Color.GREEN
                if (seconds in 2 * 60 + 1..5 * 60) {
                    textColor = NamedTextColor.YELLOW
                    bossBarColor = BossBar.Color.YELLOW
                }
                if (seconds < 2 * 60 + 1) {
                    textColor = NamedTextColor.RED
                    bossBarColor = BossBar.Color.RED
                }
                bossBar.name(getRemainingTimeComponent(textColor).append(text(" 후 ", NamedTextColor.WHITE))
                    .append(getProcessNameComponent()))
                bossBar.color(bossBarColor)
                bossBar.progress(seconds.toFloat() / ((process.delayedMinutes.toFloat() * 60) + process.delayedSeconds.toFloat()))
                server.onlinePlayers.forEach {
                    if (!bossBar.viewers().contains(it)) bossBar.addViewer(it)
                }
                when (process.remainingMinutes) {
                    5, 3 -> if (process.remainingSeconds == 0) sendNotify(NamedTextColor.YELLOW)
                    2, 1 -> if (process.remainingSeconds == 0) sendNotify(NamedTextColor.RED)
                    0 -> when (process.remainingSeconds) {
                        30, 15, 10, 5, 4, 3, 2, 1 -> sendNotify(NamedTextColor.RED)
                        0 -> {
                            process.task.run()
                            processes.remove(process)
                            if (processes.isEmpty()) {
                                server.onlinePlayers.forEach {
                                    if (bossBar.viewers().contains(it)) bossBar.removeViewer(it)
                                }
                                recess = true
                            }
                            else process = processes[0]
                        }
                    }
                }
            }
        }, 0, 20L)
    }
}