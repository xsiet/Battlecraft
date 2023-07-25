package io.github.xsiet.battlecraft

import io.github.xsiet.battlecraft.game.core.Game
import io.github.xsiet.battlecraft.game.world.GameWorld
import org.bukkit.plugin.java.JavaPlugin
import org.popcraft.chunky.api.ChunkyAPI

class BattlecraftPlugin: JavaPlugin() {
    val chunky get() = server.servicesManager.load(ChunkyAPI::class.java)!!
    lateinit var gameWorld: GameWorld
    lateinit var game: Game
    override fun onEnable() {
        gameWorld = GameWorld(this)
        game = Game(this)
        registerGameKommand()
    }
    override fun onDisable() {
        gameWorld.delete()
    }
}