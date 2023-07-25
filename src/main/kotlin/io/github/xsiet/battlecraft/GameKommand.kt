package io.github.xsiet.battlecraft

import io.github.monun.kommand.kommand
import io.github.xsiet.battlecraft.game.core.start
import io.github.xsiet.battlecraft.utils.addPotionEffect
import org.bukkit.potion.PotionEffectType

fun BattlecraftPlugin.registerGameKommand() {
    kommand {
        register("exit") {
            requires { isPlayer }
            executes {
                //player.isInvulnerable = true
                //player.isInvisible = true
                //player.addPotionEffect(PotionEffectType.NIGHT_VISION, 200, 0, false)
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