package de.hysky.skyblocker.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.multiplayer.ClientConfigurationPacketListenerImpl;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.configuration.ClientboundRegistryDataPacket;

import de.hysky.skyblocker.FixCustomBiomes;

@Mixin(ClientConfigurationPacketListenerImpl.class)
public class ClientConfigurationPacketListenerImplMixin {
	@Inject(method = "handleRegistryData", at = @At("TAIL"))
	public void skyblocker$handleRegistryData(ClientboundRegistryDataPacket packet, CallbackInfo ci) {
		if (!packet.registry().identifier().equals(Registries.BIOME.identifier())) return;
		FixCustomBiomes.fixBiomes(packet);
	}
}
