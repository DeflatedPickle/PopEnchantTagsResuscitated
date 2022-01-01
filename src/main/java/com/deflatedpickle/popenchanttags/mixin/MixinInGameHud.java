package com.deflatedpickle.popenchanttags.mixin;

import com.deflatedpickle.popenchanttags.PopEnchantTagsResuscitated;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@SuppressWarnings({"unused", "UnusedMixin", "MixinAnnotationTarget"})
@Mixin(InGameHud.class)
public class MixinInGameHud extends DrawableHelper {
    @Inject(
            id = "move",
            method = {
                    "renderHeldItemTooltip",
            },
            at = {
                    @At(value = "HEAD", id = "head"),
                    @At(value = "RETURN", id = "return")
            }
    )
    public void moveItemName(MatrixStack matrixStack, CallbackInfo info) {
        PopEnchantTagsResuscitated.INSTANCE.moveItemTitle(matrixStack, info);
    }

    @Inject(
            method = "renderHeldItemTooltip",
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.AFTER,
                    ordinal = 0,
                    target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I"
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void drawExtraText(MatrixStack matrices, CallbackInfo ci, MutableText mutableText, int i, int j, int k, int l) {
        PopEnchantTagsResuscitated.INSTANCE.drawExtraText(matrices, ci, mutableText, i, j, k, l);
    }
}
