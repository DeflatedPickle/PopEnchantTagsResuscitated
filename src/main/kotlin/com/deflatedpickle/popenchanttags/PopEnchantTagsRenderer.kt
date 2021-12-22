/* Copyright (c) 2021 DeflatedPickle under the MIT license */

package com.deflatedpickle.popenchanttags

import com.deflatedpickle.popenchanttags.PopEnchantTagsResuscitated.remainingHighlightTicks
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtList
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import kotlin.math.min
import kotlin.math.roundToInt

object PopEnchantTagsRenderer : HudRenderCallback {
    override fun onHudRender(matrixStack: MatrixStack, tickDelta: Float) {
        val mc = MinecraftClient.getInstance()
        val textRenderer = mc.textRenderer

        val screenWidth = mc.window.scaledWidth
        val screenHeight = mc.window.scaledHeight

        mc.player?.let { player ->
            player.inventory.mainHandStack?.let { item ->
                if (item.hasNbt()) {
                    val transparency = min(
                        (remainingHighlightTicks * 256f / 10f).roundToInt(),
                        255,
                    )

                    if (transparency > 0) {
                        val tagList: NbtList
                        val colour: Formatting

                        when (item.item) {
                            is EnchantedBookItem -> {
                                colour = Formatting.YELLOW
                                tagList = EnchantedBookItem.getEnchantmentNbt(item)
                            }
                            else -> {
                                colour = Formatting.AQUA
                                tagList = item.enchantments
                            }
                        }

                        var enchantmentList = mutableListOf<Text>()
                        ItemStack.appendEnchantments(enchantmentList, tagList)

                        enchantmentList = enchantmentList.mapIndexed { i, s ->
                            (s as TranslatableText)
                                .formatted(colour)
                                .append(if (i < enchantmentList.size - 1) "," else "")
                        }.toMutableList()

                        val collectedWidth = enchantmentList.sumOf { textRenderer.getWidth(it) + 5 }
                        var currentWidth = 0

                        val creative = if (player.isCreative) 0 else 1

                        for (i in enchantmentList) {
                            textRenderer.drawWithShadow(
                                matrixStack,
                                i,
                                currentWidth + (screenWidth - collectedWidth) / 2f,
                                (screenHeight - 59 - (14 * creative)).toFloat(),
                                16777215 + (transparency shl 24)
                            )
                            currentWidth += textRenderer.getWidth(i) + 5
                        }
                    }
                }
            }
        }
    }
}
