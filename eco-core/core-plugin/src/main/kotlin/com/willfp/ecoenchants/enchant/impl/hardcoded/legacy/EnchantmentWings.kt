package com.willfp.ecoenchants.enchant.impl.hardcoded.legacy

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.util.damage
import com.willfp.ecoenchants.EcoEnchantsPlugin
import com.willfp.ecoenchants.enchant.EcoEnchant
import com.willfp.ecoenchants.enchant.impl.HardcodedEcoEnchant
import com.willfp.ecoenchants.target.EnchantFinder.getItemsWithEnchantActive
import com.willfp.ecoenchants.target.EnchantFinder.hasEnchantActive
import me.xiaozhangup.ecoimpl.Baffle
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.meta.Damageable
import java.util.concurrent.TimeUnit

class EnchantmentWings(
    plugin: EcoEnchantsPlugin
) : HardcodedEcoEnchant(
    "wings",
    plugin
) {
    private var handler = EnchantmentWings(this, plugin)

    override fun onRegister() {
        plugin.eventManager.registerListener(handler)
    }

    override fun onRemove() {
        plugin.eventManager.unregisterListener(handler)
    }

    private class EnchantmentWings(
        private val enchant: EcoEnchant,
        private val plugin: EcoPlugin
    ) : Listener {
        val baffle = Baffle.of(1, TimeUnit.SECONDS)

        @EventHandler(
            ignoreCancelled = true
        )
        fun handle(event: PlayerMoveEvent) {
            val player = event.player

            if (
                !player.isFlying ||
                player.gameMode != GameMode.SURVIVAL ||
                !player.hasEnchantActive(enchant)
            ) {
                return
            }

            if (baffle.hasNext(player.name)) {
                for ((item, _) in player.getItemsWithEnchantActive(enchant)) {
                    val meta = item.itemMeta
                    if (item.hasItemMeta() && meta is Damageable && meta.damage <= 0) {
                        item.amount -= 1
                        player.isFlying = false
                        player.allowFlight = false
                        continue
                    }

                    item.damage(2)
                }
            }

        }

        @EventHandler
        fun handle(event: PlayerJumpEvent) {
            val player = event.player

            if (player.gameMode != GameMode.SURVIVAL || player.isOp) return

            if (!player.hasEnchantActive(enchant) && !player.isOp) {
                player.allowFlight = false
            } else {
                player.allowFlight = true
            }
        }

        @EventHandler
        fun handle(event: PlayerQuitEvent) {
            baffle.reset(event.player.name)
        }
    }
}
