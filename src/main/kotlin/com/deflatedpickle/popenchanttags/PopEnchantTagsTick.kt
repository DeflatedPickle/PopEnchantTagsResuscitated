/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.popenchanttags

import com.deflatedpickle.popenchanttags.PopEnchantTagsResuscitated.highlightingItemStack
import com.deflatedpickle.popenchanttags.PopEnchantTagsResuscitated.remainingHighlightTicks
import com.deflatedpickle.popenchanttags.PopEnchantTagsResuscitated.ticks
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack

object PopEnchantTagsTick : ClientTickEvents.EndTick {
    override fun onEndTick(client: MinecraftClient) {
        client.player?.let { player ->
            val item = player.inventory.mainHandStack
            if (item == null) {
                remainingHighlightTicks = 0.0f
            } else if (highlightingItemStack != null && item.item === highlightingItemStack!!.item && ItemStack.areEqual(
                    item,
                    highlightingItemStack
                )
            ) {
                if (remainingHighlightTicks > 0.0f) {
                    remainingHighlightTicks = (40L - (System.currentTimeMillis() - ticks) / 50L).toFloat()
                }
            } else {
                remainingHighlightTicks = 40.0f
                ticks = System.currentTimeMillis()
            }
            highlightingItemStack = item
        }
    }
}
