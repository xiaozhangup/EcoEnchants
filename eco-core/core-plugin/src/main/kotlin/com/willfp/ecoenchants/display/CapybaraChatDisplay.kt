package com.willfp.ecoenchants.display

import com.willfp.eco.core.display.Display
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent
import me.xiaozhangup.capybara.serves.chat.event.PlayerChatShowItemEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCreativeEvent

class CapybaraChatDisplay : Listener {
    @EventHandler
    fun e(e: PlayerChatShowItemEvent) {
        if (e.itemStack != null) {
            e.itemStack = Display.display(e.itemStack!!.clone())
        }
    }
}