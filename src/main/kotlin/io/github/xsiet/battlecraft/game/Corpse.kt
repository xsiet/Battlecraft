package io.github.xsiet.battlecraft.game

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.mojang.datafixers.util.Pair
import io.github.xsiet.battlecraft.BattlecraftPlugin
import io.github.xsiet.battlecraft.items.CustomItems
import io.github.xsiet.battlecraft.utils.playSound
import io.github.xsiet.battlecraft.utils.sendPacket
import io.github.xsiet.battlecraft.utils.toCraftPlayer
import net.kyori.adventure.text.Component.text
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.*
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Pose
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack
import org.bukkit.entity.Interaction
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.ArrayList

class Corpse(
    private val plugin: BattlecraftPlugin,
    private val player: Player,
    location: Location
): Listener {
    private val server = plugin.server
    private val handle = player.toCraftPlayer().handle
    private val corpse = ServerPlayer(handle.server, handle.serverLevel(), GameProfile(UUID.randomUUID(), player.name))
    private val corpseInventory = server.createInventory(null, 54, text(player.name))
    private val interactions = ArrayList<Interaction>()
    private val packets = ArrayList<Packet<ClientGamePacketListener>>()
    private var updateNPCEquipmentPacket = true
    private fun Player.sendAddCorpsePacket() {
        if (packets.isNotEmpty()) {
            packets.forEach {
                sendPacket(it)
            }
            server.scheduler.runTaskLater(plugin, Runnable {
                sendPacket(ClientboundPlayerInfoRemovePacket(listOf(corpse.uuid)))
            }, 40L)
        }
    }
    init {
        corpse.setPos(location.x, location.y, location.z)
        corpse.pose = Pose.SLEEPING
        fun spawnInteraction(x: Double, z: Double, width: Float) {
            interactions.add(player.world.spawn(corpse.bukkitEntity.location.subtract(x, 0.3, z), Interaction::class.java) {
                it.interactionWidth = width
                it.interactionHeight = 0.6F
            })
        }
        listOf(0.25, 0.95, 1.65).forEach { x ->
            spawnInteraction(x, 0.0, 0.7F)
        }
        listOf(0.7, 0.95, 1.2, 1.45).forEach { x ->
            listOf(-0.475, 0.475).forEach { z ->
                spawnInteraction(x, z, 0.25F)
            }
        }
        val property = handle.gameProfile.properties.get("textures").toTypedArray()[0] as Property
        corpse.gameProfile.properties.put("textures", Property("textures", property.value, property.signature))
        packets.apply {
            add(ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, corpse))
            add(ClientboundAddPlayerPacket(corpse))
            add(ClientboundSetEntityDataPacket(corpse.id, corpse.entityData.apply {
                set(EntityDataAccessor(17, EntityDataSerializers.BYTE), 127)
                set(EntityDataAccessor(0, EntityDataSerializers.BYTE), 0x40)
            }.packDirty()!!))
            add(ClientboundRotateHeadPacket(corpse, (corpse.yRot * 256 / 360).toInt().toByte()))
        }
        server.onlinePlayers.forEach {
            it.sendAddCorpsePacket()
        }
        val inventory = player.inventory
        corpseInventory.apply {
            for (int: Int in 0..17) {
                fun setItem(itemStack: ItemStack) = setItem(int, itemStack)
                when (int) {
                    4 -> setItem(CustomItems.getPlayerHead(player))
                    11 -> setItem(inventory.getItem(EquipmentSlot.HEAD))
                    12 -> setItem(inventory.getItem(EquipmentSlot.CHEST))
                    13 -> setItem(inventory.getItem(EquipmentSlot.LEGS))
                    14 -> setItem(inventory.getItem(EquipmentSlot.FEET))
                    15 -> setItem(inventory.getItem(EquipmentSlot.OFF_HAND))
                    else -> setItem(CustomItems.lockedSlot)
                }
            }
            var slot = 45
            for (int: Int in 0..35) {
                setItem(slot, inventory.getItem(int))
                if (int == 8) slot = 18
                else slot += 1
            }
        }
        server.scheduler.runTaskTimer(plugin, Runnable {
            if (updateNPCEquipmentPacket) {
                server.onlinePlayers.forEach {
                    it.sendPacket(ClientboundSetEquipmentPacket(corpse.bukkitEntity.entityId, listOf(
                        Pair(net.minecraft.world.entity.EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(corpseInventory.getItem(11))),
                        Pair(net.minecraft.world.entity.EquipmentSlot.CHEST, CraftItemStack.asNMSCopy(corpseInventory.getItem(12))),
                        Pair(net.minecraft.world.entity.EquipmentSlot.LEGS, CraftItemStack.asNMSCopy(corpseInventory.getItem(13))),
                        Pair(net.minecraft.world.entity.EquipmentSlot.FEET, CraftItemStack.asNMSCopy(corpseInventory.getItem(14))),
                        Pair(net.minecraft.world.entity.EquipmentSlot.OFFHAND, CraftItemStack.asNMSCopy(corpseInventory.getItem(15)))
                    )))
                }
            }
        }, 0, 1)
        server.pluginManager.registerEvents(this, plugin)
    }
    fun delete() {
        updateNPCEquipmentPacket = false
        interactions.forEach {
            it.remove()
        }
        interactions.clear()
        corpseInventory.viewers.forEach {
            it.closeInventory()
        }
        corpseInventory.clear()
        packets.clear()
        server.onlinePlayers.forEach {
            it.sendPacket(ClientboundRemoveEntitiesPacket(corpse.bukkitEntity.entityId))
        }
    }
    @EventHandler
    private fun PlayerJoinEvent.on() {
        player.sendAddCorpsePacket()
    }
    @EventHandler
    private fun PlayerInteractAtEntityEvent.on() {
        if (interactions.contains(rightClicked)) {
            isCancelled = true
            player.playSound("block.chest.open", 0.5F)
            player.openInventory(corpseInventory)
        }
    }
    @EventHandler
    private fun InventoryCloseEvent.on() {
        if (inventory == corpseInventory) (player as Player).playSound("block.chest.close", 0.5F)
    }
}