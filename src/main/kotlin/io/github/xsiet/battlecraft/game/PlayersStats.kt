package io.github.xsiet.battlecraft.game

import org.bukkit.entity.Player

object PlayersStats {
    val killCountMap = LinkedHashMap<Player, Int>()
    val rankingMap = LinkedHashMap<Player, Int>()
}
fun Player.setKillCount(value: Int) {
    PlayersStats.killCountMap[player!!] = value
}
fun Player.getKillCount(): Int {
    if (PlayersStats.killCountMap.contains(player)) return PlayersStats.killCountMap[player]!!
    setKillCount(0)
    return 0
}
fun Player.setRanking(value: Int) {
    PlayersStats.rankingMap[player!!] = value
}
fun Player.getRanking(): Int? {
    return PlayersStats.rankingMap[player]
}