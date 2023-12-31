package io.github.xsiet.battlecraft.game.core

import io.github.xsiet.battlecraft.game.Corpse
import io.github.xsiet.battlecraft.game.getKillCount
import io.github.xsiet.battlecraft.game.setKillCount
import io.github.xsiet.battlecraft.game.setRanking
import io.github.xsiet.battlecraft.utils.saveLastLocation
import io.github.xsiet.battlecraft.utils.showTitle
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Player

fun Game.removeSurvivor(player: Player) {
    player.gameMode = GameMode.SPECTATOR
    player.isInvulnerable = false
    val location = player.location.toBlockLocation().apply {
        while (listOf(Material.AIR, Material.GRASS, Material.TALL_GRASS, Material.FERN, Material.LARGE_FERN).contains(block.type)) {
            y --
        }
        y += 1
    }
    world.apply {
        strikeLightningEffect(location)
        spawnParticle(Particle.EXPLOSION_LARGE, location, 10)
    }
    corpses[player] = Corpse(plugin, player, location)
    val ranking = survivors.size
    player.setRanking(ranking)
    survivors.remove(player)
    var deathMessage = text(player.name, NamedTextColor.RED).append(text("님이 ", NamedTextColor.WHITE))
    if (player.killer != null) {
        val killer = player.killer!!
        killer.setKillCount(killer.getKillCount() + 1)
        deathMessage = deathMessage.append(text(player.killer!!.name, NamedTextColor.GREEN))
            .append(text("님에 의해 ", NamedTextColor.WHITE))
    }
    player.apply {
        saveLastLocation()
        showTitle(text("#${ranking}", NamedTextColor.YELLOW), text("그럴 수 있어... 이런 날도 있는 거지 뭐..."), 10)
    }
    server.sendMessage(deathMessage.append(text("사망하셨습니다! (", NamedTextColor.WHITE))
        .append(text("남은 생존자", NamedTextColor.YELLOW)).append(text(": ", NamedTextColor.WHITE))
        .append(text("${survivors.size}명", NamedTextColor.YELLOW)).append(text(")", NamedTextColor.WHITE)))
    if (survivors.size == 1) end()
}