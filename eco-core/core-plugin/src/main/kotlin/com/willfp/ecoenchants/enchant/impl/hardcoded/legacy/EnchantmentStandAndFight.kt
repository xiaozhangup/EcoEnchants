package com.willfp.ecoenchants.enchant.impl.hardcoded.legacy

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.util.damage
import com.willfp.eco.util.randDouble
import com.willfp.ecoenchants.EcoEnchantsPlugin
import com.willfp.ecoenchants.enchant.EcoEnchant
import com.willfp.ecoenchants.enchant.impl.HardcodedEcoEnchant
import com.willfp.ecoenchants.target.EnchantFinder.getItemsWithEnchantActive
import com.willfp.ecoenchants.target.EnchantFinder.hasEnchantActive
import me.xiaozhangup.ecoimpl.Baffle
import org.bukkit.GameMode
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.concurrent.TimeUnit

class EnchantmentStandAndFight(
    plugin: EcoEnchantsPlugin
) : HardcodedEcoEnchant(
    "stand_and_fight",
    plugin
) {
    private var handler = EnchantmentStandAndFight(this, plugin)

    override fun onRegister() {
        plugin.eventManager.registerListener(handler)
    }

    override fun onRemove() {
        plugin.eventManager.unregisterListener(handler)
    }

    private class EnchantmentStandAndFight(
        private val enchant: EcoEnchant,
        private val plugin: EcoPlugin
    ) : Listener {
        @EventHandler(
            ignoreCancelled = true
        )
        fun handle(event: EntityDamageByEntityEvent) {
            if (event.damager.type != EntityType.PLAYER) return
            val player = event.damager as Player

            if (!player.hasEnchantActive(enchant)) return

            val level = player.getItemsWithEnchantActive(enchant).maxOf { it.value }
            if (player.health < 4.0) {
                event.damage *= (1.0 + (level.toDouble() / 10.0))
            }
        }
    }
}
