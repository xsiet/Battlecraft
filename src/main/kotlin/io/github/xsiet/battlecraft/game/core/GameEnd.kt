package io.github.xsiet.battlecraft.game.core

import io.github.xsiet.battlecraft.game.setRanking
import io.github.xsiet.battlecraft.utils.playSound
import io.github.xsiet.battlecraft.utils.showTitle
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor

fun Game.end() {
    state = GameState.ENDED
    processor.recess = true
    val winner = survivors[0]
    winner.setRanking(1)
    server.apply {
        showTitle(text("GAME OVER!", NamedTextColor.RED), text("마지막 생존자: ${winner.name}"))
        playSound("ui.toast.challenge_complete", 0.8F)
    }
}