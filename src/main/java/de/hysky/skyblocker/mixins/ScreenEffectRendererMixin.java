package de.hysky.skyblocker.mixins;

import de.hysky.skyblocker.config.SkyblockerConfigManager;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {

	@ModifyArg(method = "buildFireQuad", index = 6, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ScreenEffectRenderer;buildSpriteQuad(Lcom/mojang/blaze3d/vertex/VertexConsumer;Lorg/joml/Matrix4f;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;FFFFFI)V"))
	private static float configureFlameHeight(float y) {
		return y - (0.5f - ((float) SkyblockerConfigManager.get().uiAndVisuals.flameOverlay.flameHeight / 200.0f));
	}

	@ModifyArg(method = "buildFireQuad", index = 8, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ScreenEffectRenderer;buildSpriteQuad(Lcom/mojang/blaze3d/vertex/VertexConsumer;Lorg/joml/Matrix4f;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;FFFFFI)V"))
	private static int configureFlameOpacity(int color) {
		return ARGB.multiplyAlpha(color, (float) SkyblockerConfigManager.get().uiAndVisuals.flameOverlay.flameOpacity / 100f);
	}

}
