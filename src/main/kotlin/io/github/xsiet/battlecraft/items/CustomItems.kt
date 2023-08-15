package io.github.xsiet.battlecraft.items

import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

object CustomItems {
    fun getPlayerHead(player: Player): ItemStack = ItemStack(Material.PLAYER_HEAD).apply {
        itemMeta = (itemMeta as SkullMeta).apply {
            owningPlayer = player
            displayName(text("${player.name}님의 머리", NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false))
            fun getEffectInfoLore(effectName: String, textColor: NamedTextColor) = text(" - ", NamedTextColor.WHITE)
                .append(text(effectName, textColor)).decoration(TextDecoration.ITALIC, false)
            fun getEffectInfoLore(effectName: String, effectDuration: String) = getEffectInfoLore(effectName, NamedTextColor.AQUA)
                    .append(text(" (", NamedTextColor.WHITE)).append(text(effectDuration, NamedTextColor.YELLOW))
                    .append(text(")", NamedTextColor.WHITE))
            lore(listOf(
                text("사용 시 적용되는 효과:", NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false),
                getEffectInfoLore("배고픔 2칸 즉시 회복", NamedTextColor.GREEN),
                getEffectInfoLore("체력 2칸 즉시 회복", NamedTextColor.GREEN),
                getEffectInfoLore("재생 III", "5초"),
                getEffectInfoLore("속도 증가 I ", "10초"),
                getEffectInfoLore("흡수 I", "2분")
            ))
        }
    }
    val lockedSlot = ItemStack(Material.GRAY_STAINED_GLASS_PANE).apply {
        itemMeta = itemMeta.apply {
            displayName(text(""))
        }
    }
}