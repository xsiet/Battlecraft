package io.github.xsiet.battlecraft

import io.github.monun.kommand.kommand
import io.github.xsiet.battlecraft.game.core.Game
import io.github.xsiet.battlecraft.game.core.start
import io.github.xsiet.battlecraft.items.CustomItems

fun BattlecraftPlugin.registerKommand(game: Game) {
    kommand {
        register("exit") {
            requires { isPlayer }
            executes {
                //player.isInvulnerable = true
                //player.isInvisible = true
                //player.addPotionEffect(PotionEffectType.NIGHT_VISION, 200, 0, false)
                //player.inventory.addItem(CustomItems.getPlayerHead(player))
            }
        }
        register("game") {
            requires { isConsole || isOp }
            then("start") {
                executes {
                    game.start()
                }
            }
        }
    }
}