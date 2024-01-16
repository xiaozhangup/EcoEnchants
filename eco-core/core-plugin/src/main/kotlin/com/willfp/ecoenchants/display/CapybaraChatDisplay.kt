package com.willfp.ecoenchants.display

import com.willfp.eco.core.display.Display
import com.willfp.eco.core.fast.fast
import com.willfp.ecoenchants.commands.CommandToggleDescriptions.Companion.seesEnchantmentDescriptions
import com.willfp.ecoenchants.display.EnchantSorter.sortForDisplay
import com.willfp.ecoenchants.enchant.EcoEnchant
import com.willfp.ecoenchants.enchant.wrap
import com.willfp.ecoenchants.plugin
import com.willfp.libreforge.ItemProvidedHolder
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent
import me.xiaozhangup.capybara.serves.chat.event.PlayerChatShowItemEvent
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCreativeEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class CapybaraChatDisplay : Listener {
    @EventHandler
    fun e(e: PlayerChatShowItemEvent) {
        if (e.itemStack != null) {
            e.itemStack = display(e.itemStack!!.clone())
        }
    }

    private fun display(itemStack: ItemStack): ItemStack {
        if (itemStack.enchantments.isEmpty() && itemStack.type != Material.ENCHANTED_BOOK) return itemStack // 不处理没有附魔的物品

        val fast = itemStack.fast()

        val lore = fast.lore
        val enchantLore = mutableListOf<String>()

        // Get enchants mapped to EcoEnchantLike
        val unsorted = fast.getEnchants(true)
        val enchants = unsorted.keys.sortForDisplay()
            .associateWith { unsorted[it]!! }

        val shouldCollapse = enchants.size > plugin.configYml.getInt("display.collapse.threshold")

        val formattedNames = mutableMapOf<DisplayableEnchant, String>()

        val notMetLines = mutableListOf<String>()

        for ((enchant, level) in enchants) {
            formattedNames[DisplayableEnchant(enchant.wrap(), level)] =
                enchant.wrap().getFormattedName(level, showNotMet = false)
        }

        if (shouldCollapse) {
            val perLine = plugin.configYml.getInt("display.collapse.per-line")
            for (names in formattedNames.values.chunked(perLine)) {
                enchantLore.add(
                    Display.PREFIX + names.joinToString(
                        plugin.configYml.getFormattedString("display.collapse.delimiter")
                    )
                )
            }
        } else {
            for ((_, formattedName) in formattedNames) {
                enchantLore.add(Display.PREFIX + formattedName)
            }
        }

        fast.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        if (itemStack.type == Material.ENCHANTED_BOOK) {
            fast.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS)
        }

        fast.lore = enchantLore + lore + notMetLines
        return fast.unwrap()
    }
}