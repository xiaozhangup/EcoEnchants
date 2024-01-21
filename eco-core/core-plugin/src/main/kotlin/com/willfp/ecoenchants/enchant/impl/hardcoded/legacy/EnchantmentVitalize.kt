package com.willfp.ecoenchants.enchant.impl.hardcoded.legacy

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.util.randDouble
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

class EnchantmentVitalize(
    plugin: EcoEnchantsPlugin
) : HardcodedEcoEnchant(
    "vitalize",
    plugin
) {
    private var handler = EnchantmentVitalize(this, plugin)

    override fun onRegister() {
        plugin.eventManager.registerListener(handler)
    }

    override fun onRemove() {
        plugin.eventManager.unregisterListener(handler)
    }

    private class EnchantmentVitalize(
        private val enchant: EcoEnchant,
        private val plugin: EcoPlugin
    ) : Listener {
        @EventHandler(
            ignoreCancelled = true
        )
        fun handle(event: EntityDamageEvent) {
            if (event.entity.type != EntityType.PLAYER) return
            val player = event.entity as Player

            if (!player.hasEnchantActive(enchant)) return

            val level = player.getItemsWithEnchantActive(enchant).maxOf { it.value }
            if (player.health < 4 * level && randDouble(0.0, 16.0) < level) {
                player.health += 6 * level
            }
        }
    }
}
