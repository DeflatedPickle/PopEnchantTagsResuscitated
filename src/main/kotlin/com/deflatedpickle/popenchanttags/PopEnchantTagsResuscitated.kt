/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.popenchanttags

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.item.ItemStack

@Suppress("UNUSED")
object PopEnchantTagsResuscitated : ModInitializer {
    private const val MOD_ID = "$[id]"
    private const val NAME = "$[name]"
    private const val GROUP = "$[group]"
    private const val AUTHOR = "$[author]"
    private const val VERSION = "$[version]"

    var remainingHighlightTicks = -1f
    var highlightingItemStack: ItemStack? = null
    var ticks = 0L

    override fun onInitialize() {
        println(listOf(MOD_ID, NAME, GROUP, AUTHOR, VERSION))

        HudRenderCallback.EVENT.register(PopEnchantTagsRenderer)
        ClientTickEvents.END_CLIENT_TICK.register(PopEnchantTagsTick)
    }
}
