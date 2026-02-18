package de.hysky.skyblocker.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.isxander.yacl3.gui.controllers.dropdown.ItemControllerElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// TODO (26.1): Get rid of this Mixin
@Environment(EnvType.CLIENT)
@Mixin(ItemControllerElement.class)
public class ItemControllerElementMixin {
	@WrapOperation(method = "getValueText", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;getName()Lnet/minecraft/network/chat/Component;"))
	Component getValueText(Item instance, Operation<Component> original) {
		try {
			return instance.getName(instance.getDefaultInstance());
		} catch (Exception ex) {
			return Component.literal("!! Broken !!").withStyle(ChatFormatting.DARK_RED);
		}
	}
}
