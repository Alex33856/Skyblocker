package de.hysky.skyblocker.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.pipeline.RenderPipeline;
//? if <=1.21.11
import com.mojang.blaze3d.platform.DepthTestFunction;
import de.hysky.skyblocker.utils.render.GlowRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//? if > 1.21.11 {
/*import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.platform.CompareOp;
*///? }

@Mixin(RenderPipeline.class)
public class RenderPipelineMixin {
	//? if <=1.21.11 {
	@ModifyReturnValue(method = "getDepthTestFunction", at = @At("RETURN"))
	private DepthTestFunction skyblocker$modifyGlowDepthTest(DepthTestFunction original) {
		return ((Object) this == RenderPipelines.OUTLINE_CULL || (Object) this == RenderPipelines.OUTLINE_NO_CULL) && GlowRenderer.isRenderingGlow() ? DepthTestFunction.LEQUAL_DEPTH_TEST : original;
	}
	//? } else {
	/*@ModifyReturnValue(method = "getDepthStencilState", at = @At("RETURN"))
	private DepthStencilState skyblocker$modifyGlowDepthStencil(DepthStencilState original) {
		if (((Object) this == RenderPipelines.OUTLINE_CULL || (Object) this == RenderPipelines.OUTLINE_NO_CULL) && GlowRenderer.isRenderingGlow()) {
			if (original == null) {
				return new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, true);
			}
			return new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, original.writeDepth(), original.depthBiasScaleFactor(), original.depthBiasConstant());
		}
		return original;
	}
	*///? }
}
