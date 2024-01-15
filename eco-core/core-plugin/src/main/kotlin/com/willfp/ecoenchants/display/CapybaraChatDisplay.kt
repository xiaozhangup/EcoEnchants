package com.willfp.ecoenchants.display

import com.willfp.eco.core.display.Display
import me.xiaozhangup.capybara.serves.chat.event.PlayerChatShowItemEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class CapybaraChatDisplay : Listener {
    @EventHandler
    fun e(e: PlayerChatShowItemEvent) {
        if (e.itemStack != null) {
            e.itemStack = Display.display(e.itemStack!!)
        }
    }
}