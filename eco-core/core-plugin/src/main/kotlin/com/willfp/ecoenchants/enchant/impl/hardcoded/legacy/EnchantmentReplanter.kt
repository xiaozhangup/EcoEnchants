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
import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min

class EnchantmentReplanter(
    plugin: EcoEnchantsPlugin
) : HardcodedEcoEnchant(
    "replanter",
    plugin
) {
    private var handler = EnchantmentReplanter(this, plugin)

    override fun onRegister() {
        plugin.eventManager.registerListener(handler)
    }

    override fun onRemove() {
        plugin.eventManager.unregisterListener(handler)
    }

    private class EnchantmentReplanter(
        private val enchant: EcoEnchant,
        private val plugin: EcoPlugin
    ) : Listener {
        @EventHandler(
            ignoreCancelled = true
        )
        fun handle(event: PlayerInteractEvent) {
            val player = event.player

            if (!player.hasEnchantActive(enchant)) return
            if (event.action != Action.PHYSICAL) return
            if (event.clickedBlock?.type != Material.FARMLAND) return

            val age = 3 - player.getItemsWithEnchantActive(enchant).maxOf { it.value }
            event.isCancelled = true

            event.clickedBlock!!.getRelative(0, 1, 0).apply {
                val ageable = this.blockData
                if (ageable is Ageable) {
                    ageable.age = max(0, ageable.age - age)
                    this.blockData = ageable
                }
            }
        }

    }
}
