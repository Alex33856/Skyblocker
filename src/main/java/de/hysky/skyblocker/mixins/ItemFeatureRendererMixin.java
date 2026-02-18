package de.hysky.skyblocker.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import de.hysky.skyblocker.skyblock.entity.MobGlow;
import de.hysky.skyblocker.utils.render.GlowRenderer;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemFeatureRenderer.class)
public class ItemFeatureRendererMixin {
	@WrapOperation(
			//? if >1.21.11 {
			/*method = {"renderSolid", "renderTranslucent"},
			*///? } else {
			method = "render",
			//? }
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeStorage$ItemSubmit;outlineColor()I"), require = 2)
	private int skyblocker$useCustomGlowColour(SubmitNodeStorage.ItemSubmit command, Operation<Integer> operation) {
		return command.skyblocker$getCustomGlowColour() != MobGlow.NO_GLOW ? command.skyblocker$getCustomGlowColour() : operation.call(command);
	}

	@ModifyVariable(
			//? if >1.21.11 {
			/*method = {"renderSolid", "renderTranslucent"},
			*///? } else {
			method = "render",
			//? }
			at = @At("LOAD"), argsOnly = true, require = 2)
	private OutlineBufferSource skyblocker$useCustomGlowConsumers(OutlineBufferSource original, @Local SubmitNodeStorage.ItemSubmit command) {
		return command.skyblocker$getCustomGlowColour() != MobGlow.NO_GLOW ? GlowRenderer.getInstance().getGlowVertexConsumers() : original;
	}
}
