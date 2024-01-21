package com.willfp.ecoenchants.enchant.impl.hardcoded.legacy

import com.willfp.eco.core.EcoPlugin
import com.willfp.ecoenchants.EcoEnchantsPlugin
import com.willfp.ecoenchants.enchant.EcoEnchant
import com.willfp.ecoenchants.enchant.impl.HardcodedEcoEnchant
import com.willfp.ecoenchants.target.EnchantFinder.getItemsWithEnchantActive
import com.willfp.ecoenchants.target.EnchantFinder.hasEnchantActive
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

class EnchantmentJellylegs(
    plugin: EcoEnchantsPlugin
) : HardcodedEcoEnchant(
    "jelly_legs",
    plugin
) {
    private var handler = EnchantmentJellylegs(this, plugin)

    override fun onRegister() {
        plugin.eventManager.registerListener(handler)
    }

    override fun onRemove() {
        plugin.eventManager.unregisterListener(handler)
    }

    private class EnchantmentJellylegs(
        private val enchant: EcoEnchant,
        private val plugin: EcoPlugin
    ) : Listener {
        val high = hashMapOf(1 to 16, 2 to 48, 3 to 256)

        @EventHandler(
            ignoreCancelled = true
        )
        fun handle(event: EntityDamageEvent) {
            if (event.entity.type != EntityType.PLAYER) return
            if (event.cause != EntityDamageEvent.DamageCause.FALL) return
            val player = event.entity as Player

            if (!player.hasEnchantActive(enchant)) return

            val level = player.getItemsWithEnchantActive(enchant).maxOf { it.value }
            if (player.fallDistance < high.getOrDefault(level, -1)) {
                event.damage -= (level * 3)
                player.velocity = player.velocity.multiply(-0.5)
            }
        }

    }
}
