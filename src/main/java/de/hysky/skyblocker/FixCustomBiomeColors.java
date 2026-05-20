package de.hysky.skyblocker;

import de.hysky.skyblocker.utils.Constants;
import de.hysky.skyblocker.utils.scheduler.Scheduler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.configuration.ClientboundRegistryDataPacket;

import java.util.Optional;

public final class FixCustomBiomeColors {
	static boolean willAlert = false;

	public static void alert() {
		if (willAlert) return;
		willAlert = true;
		Scheduler.INSTANCE.schedule(() -> {
			if (Minecraft.getInstance().player != null) {
				Minecraft.getInstance().player.sendSystemMessage(Constants.PREFIX.get().append("biome colors were fixed!"));
			}
		}, 5);
	}

	public static void processCompoundTag(CompoundTag tag) {
		CompoundTag effects = tag.getCompoundOrEmpty("effects");
		if (effects.isEmpty()) return;
		CompoundTag attributes = tag.getCompoundOrEmpty("attributes");
		if (tag.keySet().contains("attributes")) {
			alert();
			return;
		}
		tag.put("attributes", attributes);
		effects.getInt("fog_color").ifPresent(integer -> attributes.putInt("visual/fog_color", integer));
		effects.getInt("sky_color").ifPresent(integer -> attributes.putInt("visual/sky_color", integer));
		effects.getInt("water_fog_color").ifPresent(integer -> attributes.putInt("visual/water_fog_color", integer));
	}

	public static void fixBiomes(ClientboundRegistryDataPacket packet) {
		willAlert = false;
		var entries = packet.entries();
		for (RegistrySynchronization.PackedRegistryEntry entry : entries) {
			if (!entry.id().getNamespace().equals("hypixel") || entry.data().isEmpty()) continue;
			Optional<CompoundTag> maybeTag = entry.data().get().asCompound();
			if (maybeTag.isEmpty()) continue;
			processCompoundTag(maybeTag.get());
		}
	}
}
