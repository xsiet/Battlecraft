package io.github.xsiet.battlecraft.utils

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.attribute.Attribute
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.time.Duration

fun Player.showTitle(title: Component, subtitle: Component, durationSeconds: Long) {
    showTitle(Title.title(
        title, subtitle,
        Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(durationSeconds), Duration.ofSeconds(0))
    ))
}
fun Player.showTitle(title: Component, subtitle: Component) {
    showTitle(title, subtitle, 5)
}
fun Player.playSound(key: String, volume: Float, pitch: Float) {
    playSound(Sound.sound(Key.key(key), Sound.Source.PLAYER, volume, pitch))
}
fun Player.playSound(key: String, volume: Float) {
    playSound(key, volume, 1F)
}
fun Player.playSound(key: String) {
    playSound(key, 1F)
}
fun Player.addPotionEffect(type: PotionEffectType, duration: Int, amplifier: Int, showStatus: Boolean) {
    addPotionEffect(PotionEffect(type, duration, amplifier, showStatus, showStatus, showStatus))
}
fun Player.teleport(world: World, x: Double, y: Double, z: Double) = teleport(Location(world, x, y, z))
fun Player.toCraftPlayer() = player as CraftPlayer
fun Player.sendPacket(packet: Packet<ClientGamePacketListener>) = toCraftPlayer().handle.connection.send(packet)
fun Player.resetData() {
    inventory.clear()
    exp = 0F
    level = 0
    clearActivePotionEffects()
    foodLevel = 20
    health = getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
    val advancementIterator = Bukkit.advancementIterator()
    while (advancementIterator.hasNext()) {
        getAdvancementProgress(advancementIterator.next()).apply {
            awardedCriteria.forEach {
                revokeCriteria(it)
            }
        }
    }
}