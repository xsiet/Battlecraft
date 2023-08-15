package io.github.xsiet.battlecraft.game

import io.github.xsiet.battlecraft.game.core.Game
import io.github.xsiet.battlecraft.game.core.GameState
import io.github.xsiet.battlecraft.game.core.removeSurvivor
import io.github.xsiet.battlecraft.utils.resetData
import io.github.xsiet.battlecraft.utils.saveLastLocation
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Server
import org.bukkit.World
import org.bukkit.entity.Animals
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.PortalCreateEvent

class GameEvents(
    private val game: Game,
    private val server: Server,
    private val world: World
): Listener {
    private fun getPlayerNameComponent(player: Player) = text(player.name, NamedTextColor.GOLD)
    private fun getPlayersCountComponent(onlinePlayersCount: Int) =
        text("(", NamedTextColor.YELLOW).append(text(onlinePlayersCount, NamedTextColor.AQUA))
            .append(text("/", NamedTextColor.YELLOW)).append(text(server.maxPlayers, NamedTextColor.AQUA))
            .append(text(")", NamedTextColor.YELLOW))
    @EventHandler
    private fun PlayerJoinEvent.on() {
        if (game.state == GameState.WAITING) {
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
            if (player.world != world) {
                if (player.bedSpawnLocation != null) player.teleport(player.bedSpawnLocation!!)
                else player.teleport(world.spawnLocation)
            }
            joinMessage(null)
        }
    }
    @EventHandler
    private fun PlayerQuitEvent.on() {
        if (game.state == GameState.WAITING) {
            player.isInvulnerable = false
            quitMessage(getPlayerNameComponent(player)
                .append(text("님이 게임을 나가셨습니다! ", NamedTextColor.YELLOW))
                .append(getPlayersCountComponent(server.onlinePlayers.count() - 1)))
        }
        else if (game.state != GameState.ENDED) {
            if (game.survivors.contains(player)) game.removeSurvivor(player)
            else player.saveLastLocation()
            quitMessage(null)
        }
    }
    @EventHandler
    private fun PlayerMoveEvent.on() {
        if (game.state == GameState.STARTING && game.survivors.contains(player)) isCancelled = true
    }
    @EventHandler
    private fun PlayerInteractEvent.on() {
        if (game.state == GameState.STARTING && game.survivors.contains(player)) isCancelled = true
    }
    @EventHandler
    private fun PlayerDeathEvent.on() {
        if (game.survivors.contains(player)) {
            game.removeSurvivor(player)
            deathMessage(null)
        }
    }
    @EventHandler
    private fun EntitySpawnEvent.on() {
        if (entity is Mob && entity !is Animals) isCancelled = true
    }
    @EventHandler
    private fun PortalCreateEvent.on() {
        isCancelled = true
    }
    @EventHandler
    private fun BlockDropItemEvent.on() {
        if (items.size != 0) {
            fun change(material: Material, experience: Int) {
                items[0].itemStack.type = material
                block.world.spawn(block.location, ExperienceOrb::class.java).experience = experience
            }
            when (items[0].itemStack.type) {
                Material.RAW_IRON -> change(Material.IRON_INGOT, 1)
                Material.RAW_COPPER -> change(Material.COPPER_INGOT, 1)
                Material.RAW_GOLD -> change(Material.GOLD_INGOT, 1)
                Material.ANCIENT_DEBRIS -> change(Material.NETHERITE_SCRAP, 2)
                else -> return
            }
        }
    }
}