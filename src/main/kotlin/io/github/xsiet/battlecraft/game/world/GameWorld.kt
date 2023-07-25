package io.github.xsiet.battlecraft.game.world

import io.github.xsiet.battlecraft.BattlecraftPlugin
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.*
import org.bukkit.event.Listener
import java.io.File

class GameWorld(
    plugin: BattlecraftPlugin
): Listener {
    private val server = plugin.server
    private val chunky = plugin.chunky
    private val worldName = "world_game"
    val world get() = server.getWorld(worldName)!!
    private val chunkyProgressBar = BossBar.bossBar(text(""), 0F, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS)
    private var chunkyProgress = 0
    private val cageSize = 18
    private val cageY = 300
    init {
        chunky.onGenerationProgress { generation ->
            val progress = generation.progress.toDouble()
            chunkyProgress = progress.toInt()
            chunkyProgressBar.apply {
                name(text("청크 생성 중... (")
                    .append(text("${chunkyProgress}% 완료", NamedTextColor.AQUA)).append(text(")")))
                progress((progress / 100).toFloat())
                server.onlinePlayers.forEach {
                    if (generation.complete && viewers().contains(it)) removeViewer(it)
                    if (!generation.complete && !viewers().contains(it)) addViewer(it)
                }
            }
        }
        create()
        server.pluginManager.registerEvents(GameWorldEvents(world), plugin)
    }
    fun setCageFloor(material: Material) {
        for (x in -cageSize..cageSize) {
            for (z in -cageSize..cageSize) {
                Location(world, x.toDouble(), cageY.toDouble(), z.toDouble()).block.type = material
            }
        }
    }
    private fun create() {
        WorldCreator(worldName).createWorld()!!.apply {
            setSpawnLocation(0, getHighestBlockYAt(0, 0), 0)
            setGameRule(GameRule.DO_WEATHER_CYCLE, false)
            setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
            setGameRule(GameRule.KEEP_INVENTORY, true)
            time = 1000
            difficulty = Difficulty.PEACEFUL
            setCageFloor(Material.GLASS)
            worldBorder.apply {
                setCenter(0.5 , 0.5)
                size = (cageSize * 2 + 1).toDouble()
            }
            chunky.startTask(name, "square", 0.0, 0.0, 500.0, 500.0, "concentric")
        }
    }
    fun delete() {
        if (chunky.isRunning(worldName)) {
            chunky.cancelTask(worldName)
            server.onlinePlayers.forEach {
                if (chunkyProgressBar.viewers().contains(it)) chunkyProgressBar.removeViewer(it)
            }
        }
        world.players.forEach {
            it.teleport(server.getWorld("world")!!.spawnLocation)
        }
        server.unloadWorld(world, false)
        File(worldName).deleteRecursively()
    }
}