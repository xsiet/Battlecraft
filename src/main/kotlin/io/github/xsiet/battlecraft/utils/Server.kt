package io.github.xsiet.battlecraft.utils

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.Server
import java.time.Duration

fun Server.showTitle(title: Component, subtitle: Component, durationSeconds: Long) {
    showTitle(
        Title.title(
        title, subtitle,
        Title.Times.times(Duration.ofSeconds(0), Duration.ofSeconds(durationSeconds), Duration.ofSeconds(0))
    ))
}
fun Server.showTitle(title: Component, subtitle: Component) {
    showTitle(title, subtitle, 5)
}
fun Server.playSound(key: String, volume: Float, pitch: Float) {
    playSound(Sound.sound(Key.key(key), Sound.Source.PLAYER, volume, pitch))
}
fun Server.playSound(key: String, volume: Float) {
    playSound(key, volume, 1F)
}
fun Server.playSound(key: String) {
    playSound(key, 1F)
}