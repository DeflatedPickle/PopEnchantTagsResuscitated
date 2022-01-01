/* Copyright (c) 2021-2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.popenchanttags

import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtList
import net.minecraft.text.LiteralText
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import java.awt.SystemColor.text

@Suppress("UNUSED")
object PopEnchantTagsResuscitated : ClientModInitializer {
    private const val MOD_ID = "$[id]"
    private const val NAME = "$[name]"
    private const val GROUP = "$[group]"
    private const val AUTHOR = "$[author]"
    private const val VERSION = "$[version]"

    val DISTANCE = 10

    override fun onInitializeClient() {
        println(listOf(MOD_ID, NAME, GROUP, AUTHOR, VERSION))
    }

    fun moveItemTitle(matrixStack: MatrixStack, info: CallbackInfo) {
        val mc = MinecraftClient.getInstance()
        val currentStack = mc.player!!.mainHandStack

        if (!currentStack.hasNbt()) return
        if (MinecraftClient.getInstance().interactionManager?.hasStatusBars() == false) return

        if (info.id == "move:head") {
            matrixStack.push()
            matrixStack.translate(0.0, -DISTANCE.toDouble(), 0.0)
        } else matrixStack.pop()
    }

    fun drawExtraText(
        matrices: MatrixStack?,
        ci: CallbackInfo?,
        mutableText: MutableText?,
        width: Int,
        x: Int,
        y: Int,
        opacity: Int
    ) {
        val mc = MinecraftClient.getInstance()
        val textRenderer = mc.textRenderer
        val currentStack = mc.player!!.mainHandStack

        val tagList: NbtList = when (currentStack.item) {
            is EnchantedBookItem -> EnchantedBookItem.getEnchantmentNbt(currentStack)
            else -> currentStack.enchantments
        }

        val enchantmentList = mutableListOf<Text>()
        ItemStack.appendEnchantments(enchantmentList, tagList)

        val text = LiteralText("").apply {
            for ((i, s) in enchantmentList.withIndex()) {
                append(s)
                if (i < enchantmentList.size - 1) {
                    append(", ")
                }
            }
        }.formatted(Formatting.ITALIC, Formatting.GRAY)

        val textWidth = textRenderer.getWidth(text)

        textRenderer.drawWithShadow(
            matrices,
            text,
            (x + (width - textWidth) / 2).toFloat(),
            y.toFloat() + DISTANCE,
            0xFFFFFF + (opacity shl 24)
        )
    }
}
