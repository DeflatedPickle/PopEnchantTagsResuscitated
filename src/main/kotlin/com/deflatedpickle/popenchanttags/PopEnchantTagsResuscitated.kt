/* Copyright (c) 2021-2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.popenchanttags

import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.effect.StatusEffectUtil
import net.minecraft.inventory.Inventories
import net.minecraft.item.BlockItem
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.PotionItem
import net.minecraft.item.TippedArrowItem
import net.minecraft.potion.PotionUtil
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.text.Text.literal
import net.minecraft.text.Text.translatable
import net.minecraft.util.Formatting
import net.minecraft.util.collection.DefaultedList
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Suppress("UNUSED")
object PopEnchantTagsResuscitated : ClientModInitializer {
    private const val MOD_ID = "$[id]"
    private const val NAME = "$[name]"
    private const val GROUP = "$[group]"
    private const val AUTHOR = "$[author]"
    private const val VERSION = "$[version]"

    const val DISTANCE = 10

    override fun onInitializeClient(mod: ModContainer) {
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
        matrices: MatrixStack,
        width: Int,
        x: Int,
        y: Int,
        opacity: Int
    ) {
        val mc = MinecraftClient.getInstance()
        val textRenderer = mc.textRenderer
        val currentStack = mc.player!!.mainHandStack

        val textList = mutableListOf<Text>()

        if (currentStack.item == Items.SHULKER_BOX) {
            BlockItem.getBlockEntityNbtFromStack(currentStack)?.let { nbtCompound ->
                if (nbtCompound.contains("Items")) {
                    val defaultedList = DefaultedList.ofSize(27, ItemStack.EMPTY)
                    Inventories.readNbt(nbtCompound, defaultedList)

                    for (i in defaultedList) {
                        if (i.isEmpty) continue
                        val text = i.name.copyContentOnly()
                        text.append(" x").append(i.count.toString())
                        textList.add(text)
                    }
                }
            }
        } else if (currentStack.item is PotionItem || currentStack.item is TippedArrowItem) {
            val effects = PotionUtil.getPotionEffects(currentStack)

            if (effects.isEmpty()) {
                textList.add(translatable("effect.none"))
            } else {
                for (i in effects) {
                    var text = translatable(i.translationKey)
                    if (i.duration > 20) {
                        text = translatable(
                            "potion.withDuration",
                            text,
                            StatusEffectUtil.durationToString(
                                i,
                                when (currentStack.item) {
                                    is PotionItem -> 1.0f
                                    is TippedArrowItem -> 0.125f
                                    else -> 0.0f
                                }
                            )
                        )
                    }
                    textList.add(text)
                }
            }
        } else if (currentStack.item == Items.ENCHANTED_BOOK || currentStack.hasEnchantments()) {
            ItemStack.appendEnchantments(
                textList,
                when (currentStack.item) {
                    is EnchantedBookItem -> EnchantedBookItem.getEnchantmentNbt(currentStack)
                    else -> currentStack.enchantments
                }
            )
        }

        val text = literal("").apply {
            for ((i, s) in textList.take(4).withIndex()) {
                append(s)
                if (i < textList.size - 1) {
                    append(", ")
                }
            }
            if (textList.size > 4) {
                append("...")
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
