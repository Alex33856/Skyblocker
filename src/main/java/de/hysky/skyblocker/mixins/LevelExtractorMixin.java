package de.hysky.skyblocker.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import de.hysky.skyblocker.config.SkyblockerConfigManager;
import de.hysky.skyblocker.skyblock.dwarven.BlockBreakPrediction;
import de.hysky.skyblocker.skyblock.entity.MobGlow;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.client.renderer.state.level.BlockBreakingRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelExtractor.class)
public class LevelExtractorMixin {
	@ModifyExpressionValue(method = "extractVisibleEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/state/EntityRenderState;appearsGlowing()Z"))
	private boolean skyblocker$markCustomGlowUsedThisFrame(boolean hasVanillaGlow, @Local(name = "state") EntityRenderState entityRenderState, @Local(name = "output", argsOnly = true) LevelRenderState levelRenderState) {
		boolean hasCustomGlow = entityRenderState.getDataOrDefault(MobGlow.ENTITY_CUSTOM_GLOW_COLOUR, MobGlow.NO_GLOW) != MobGlow.NO_GLOW;

		if (hasCustomGlow) {
			levelRenderState.setData(MobGlow.FRAME_USES_CUSTOM_GLOW, true);
		}

		return hasVanillaGlow || hasCustomGlow;
	}

	@WrapOperation(method = "extractBlockDestroyAnimation", at = @At(value = "NEW", target = "(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Lnet/minecraft/client/renderer/state/level/BlockBreakingRenderState;"))
	private BlockBreakingRenderState skyblocker$addBlockBreakingProgressRenderState(BlockPos pos, BlockState state, int progress, Operation<BlockBreakingRenderState> original) {
		if (SkyblockerConfigManager.get().mining.blockBreakPrediction.enabled) {
			int pingModifiedProgress = BlockBreakPrediction.getBlockBreakPrediction(pos, progress);
			return new BlockBreakingRenderState(pos, state, pingModifiedProgress);

		}
		//if the setting is not enabled do not modify anything
		else {
			return original.call(pos, state, progress);
		}
	}
}
