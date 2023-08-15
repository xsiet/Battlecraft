package io.github.xsiet.battlecraft.game.core

import io.github.xsiet.battlecraft.BattlecraftPlugin
import io.github.xsiet.battlecraft.game.*
import io.github.xsiet.battlecraft.items.CustomItemsEvents
import org.bukkit.entity.Player
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

enum class GameState {
    WAITING,
    STARTING,
    PLAYING,
    ENDED
}
class Game(
    val plugin: BattlecraftPlugin,
    val gameWorld: GameWorld
) {
    val server = plugin.server
    val world = gameWorld.world
    val worldBorder = world.worldBorder
    val processor = Processor(plugin)
    val survivors = ArrayList<Player>()
    val corpses = LinkedHashMap<Player, Corpse>()
    var state = GameState.WAITING
    var isDecreasingWorldBorder = false
    init {
        plugin.registerEvents(GameEvents(this, server, world))
        plugin.registerEvents(CustomItemsEvents())
    }
}