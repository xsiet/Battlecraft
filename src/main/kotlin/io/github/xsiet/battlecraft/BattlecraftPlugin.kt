package io.github.xsiet.battlecraft

import io.github.xsiet.battlecraft.game.GameWorld
import io.github.xsiet.battlecraft.game.core.Game
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.popcraft.chunky.api.ChunkyAPI

class BattlecraftPlugin: JavaPlugin() {
    val chunkyAPI get() = server.servicesManager.load(ChunkyAPI::class.java)!!
    fun registerEvents(listener: Listener) {
        server.pluginManager.registerEvents(listener, this)
    }
    override fun onEnable() {
        registerKommand(Game(this, GameWorld(this)))
    }
}