package de.hysky.skyblocker.mixins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(targets = "net/minecraft/core/Holder$Reference")
public abstract class HolderReferenceMixin<T> implements Holder<T> {
	@Shadow
	private @Nullable DataComponentMap components;

	@Inject(at = @At("HEAD"), method = "components()Lnet/minecraft/core/component/DataComponentMap;", cancellable = true)
	void components(CallbackInfoReturnable<DataComponentMap> cir) {
		if (components == null) {
			cir.setReturnValue(DataComponentMap.EMPTY);
		}
	}
}
