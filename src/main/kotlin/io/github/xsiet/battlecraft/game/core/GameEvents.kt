package io.github.xsiet.battlecraft.game.core

import io.github.xsiet.battlecraft.utils.resetData
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Server
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent

class GameEvents(
    private val game: Game,
): Listener {
    private val server = game.server
    private val state = game.state
    private val world = game.world
    private val survivors = game.survivors
    private fun getPlayerNameComponent(player: Player) = text(player.name, NamedTextColor.GOLD)
    private fun getPlayersCountComponent(onlinePlayersCount: Int) =
        text("(", NamedTextColor.YELLOW).append(text(onlinePlayersCount, NamedTextColor.AQUA))
            .append(text("/", NamedTextColor.YELLOW)).append(text(server.maxPlayers, NamedTextColor.AQUA))
            .append(text(")", NamedTextColor.YELLOW))
    @EventHandler
    private fun PlayerJoinEvent.on() {
        if (state == GameState.WAITING) {
            player.isInvulnerable = true
            player.gameMode = GameMode.ADVENTURE
            player.resetData()
            player.teleport(Location(world, 0.0, world.getHighestBlockYAt(0, 0).toDouble() + 2, 0.0))
            joinMessage(getPlayerNameComponent(player)
                .append(text("님이 게임에 참여하셨습니다! ", NamedTextColor.YELLOW))
                .append(getPlayersCountComponent(server.onlinePlayers.count())))
        }
        else {
            player.gameMode = GameMode.SPECTATOR
            if (player.world != world) player.teleport(world.spawnLocation)
            joinMessage(null)
        }
    }
    @EventHandler
    private fun PlayerQuitEvent.on() {
        if (state == GameState.WAITING) {
            player.isInvulnerable = false
            quitMessage(getPlayerNameComponent(player)
                .append(text("님이 게임을 나가셨습니다! ", NamedTextColor.YELLOW))
                .append(getPlayersCountComponent(server.onlinePlayers.count() - 1)))
        }
        else if (game.state != GameState.ENDED) {
            game.removeSurvivor(player)
            quitMessage(null)
        }
    }
    @EventHandler
    private fun PlayerMoveEvent.on() {
        if (state == GameState.STARTING && survivors.contains(player)) isCancelled = true
    }
    @EventHandler
    private fun PlayerInteractEvent.on() {
        if (state == GameState.STARTING && survivors.contains(player)) isCancelled = true
    }
    @EventHandler
    private fun PlayerDeathEvent.on() {
        if (survivors.contains(player)) {
            game.removeSurvivor(player)
            deathMessage(null)
        }
    }
}