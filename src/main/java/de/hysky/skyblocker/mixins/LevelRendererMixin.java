package de.hysky.skyblocker.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import de.hysky.skyblocker.config.SkyblockerConfigManager;
import de.hysky.skyblocker.injected.EntityRenderMarker;
import de.hysky.skyblocker.skyblock.dwarven.BlockBreakPrediction;
import de.hysky.skyblocker.skyblock.entity.MobGlow;
import de.hysky.skyblocker.utils.render.GlowRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.BlockBreakingRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin implements EntityRenderMarker {
	@Shadow
	@Final
	private LevelRenderState levelRenderState;
	@Unique
	private @Nullable EntityRenderState currentEntityStateBeingRendered;

	@Override
	public @Nullable EntityRenderState skyblocker$getEntityStateBeingRendered() {
		return this.currentEntityStateBeingRendered;
	}

	@Inject(method = "lambda$addMainPass$0",
			slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/state/level/LevelRenderState;shouldShowEntityOutlines:Z")),
			at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/CommandEncoder;clearColorAndDepthTextures(Lcom/mojang/blaze3d/textures/GpuTexture;ILcom/mojang/blaze3d/textures/GpuTexture;D)V", ordinal = 0, shift = At.Shift.AFTER)
	)
	private void skyblocker$updateGlowDepthTexDepth(CallbackInfo ci) {
		if (this.levelRenderState.getDataOrDefault(MobGlow.FRAME_USES_CUSTOM_GLOW, false)) {
			GlowRenderer.getInstance().updateGlowDepthTexDepth();
		}
	}

	@Inject(method = "submitEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/client/renderer/state/level/CameraRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V"))
	private void skyblocker$markEntityStateBeingRendered(CallbackInfo ci, @Local(name = "state") EntityRenderState state) {
		this.currentEntityStateBeingRendered = state;
	}

	@Inject(method = "submitEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/client/renderer/state/level/CameraRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V", shift = At.Shift.AFTER))
	private void skyblocker$clearEntityStateBeingRendered(CallbackInfo ci) {
		this.currentEntityStateBeingRendered = null;
	}

	@Inject(method = "lambda$addMainPass$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/OutlineBufferSource;endOutlineBatch()V"))
	private void skyblocker$drawGlowVertexConsumers(CallbackInfo ci) {
		GlowRenderer.getInstance().getGlowBufferSource().endOutlineBatch();
	}
}
