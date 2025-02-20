package com.willfp.ecoenchants.enchant.impl.hardcoded.legacy

import com.willfp.eco.core.EcoPlugin
import com.willfp.ecoenchants.EcoEnchantsPlugin
import com.willfp.ecoenchants.enchant.EcoEnchant
import com.willfp.ecoenchants.enchant.impl.HardcodedEcoEnchant
import com.willfp.ecoenchants.target.EnchantFinder.hasEnchantActive
import com.willfp.ecoenchants.utils.Baffle
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import java.util.concurrent.TimeUnit

class EnchantmentPropulsion(
    plugin: EcoEnchantsPlugin
) : HardcodedEcoEnchant(
    "propulsion",
    plugin
) {
    private var handler = EnchantmentPropulsion(this, plugin)

    override fun onRegister() {
        plugin.eventManager.registerListener(handler)
    }

    override fun onRemove() {
        plugin.eventManager.unregisterListener(handler)
    }

    private class EnchantmentPropulsion(
        private val enchant: EcoEnchant,
        private val plugin: EcoPlugin
    ) : Listener {
        val baffle = Baffle.of(6, TimeUnit.SECONDS)

        @EventHandler(
            ignoreCancelled = true
        )
        fun handle(event: PlayerToggleSneakEvent) {
            val player = event.player
            if (!player.hasEnchantActive(enchant)) return

            if (!player.isOnGround && baffle.hasNext()) {
                player.velocity = player.velocity.add(player.location.direction.setY(0.5))
            }
        }

        @EventHandler
        fun handle(event: PlayerQuitEvent) {
            baffle.reset(event.player.name)
        }
    }
}
