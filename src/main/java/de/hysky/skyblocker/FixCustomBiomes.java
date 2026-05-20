package de.hysky.skyblocker;

import de.hysky.skyblocker.utils.Constants;
import de.hysky.skyblocker.utils.scheduler.Scheduler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.configuration.ClientboundRegistryDataPacket;

import java.util.Optional;

/**
 * Hypixel hasn't updated their custom biomes to support 1.21.11+ yet, so we have to do it...
 */
public final class FixCustomBiomes {
	static boolean willAlert = false;

	private static void alert() {
		if (willAlert) return;
		willAlert = true;
		Scheduler.INSTANCE.schedule(() -> {
			if (Minecraft.getInstance().player != null) {
				Minecraft.getInstance().player.sendSystemMessage(Constants.PREFIX.get().append("custom biomes were fixed!!"));
			}
		}, 5);
	}

	private static void processColors(CompoundTag effects, CompoundTag attributes) {
		effects.getInt("fog_color").ifPresent(integer -> attributes.putInt("visual/fog_color", integer));
		effects.getInt("sky_color").ifPresent(integer -> attributes.putInt("visual/sky_color", integer));
		effects.getInt("water_fog_color").ifPresent(integer -> attributes.putInt("visual/water_fog_color", integer));
	}

	private static void processParticles(CompoundTag effects, CompoundTag attributes) {
		effects.getCompound("particle").ifPresent(tag -> {
			ListTag list = new ListTag();
			list.add(tag);
			tag.put("particle", tag.getCompoundOrEmpty("options"));
			tag.remove("options");
			attributes.put("visual/ambient_particles", list);
		});
	}

	private static void processSounds(CompoundTag effects, CompoundTag attributes) {
		if (effects.keySet().stream().noneMatch(key -> key.contains("_sound"))) return;
		CompoundTag sounds = new CompoundTag();
		attributes.put("audio/ambient_sounds", sounds);
		effects.getString("ambient_sound").ifPresent(str -> sounds.putString("loop", str));
		effects.getCompound("ambient_sound").ifPresent(tag -> sounds.put("loop", tag));
		effects.getCompound("mood_sound").ifPresent(tag -> sounds.put("mood", tag));
		effects.getCompound("additions_sounds").ifPresent(tag -> sounds.put("additions", tag));
	}

	private static void processMusic(CompoundTag effects, CompoundTag attributes) {
		effects.getFloat("music_volume").ifPresent(vol -> attributes.putFloat("audio/music_volume", vol));
		effects.getCompound("music").ifPresent(tag -> {
			CompoundTag audioTag = new CompoundTag();
			audioTag.put("default", tag);
			attributes.put("audio/background_music", audioTag);
		});
	}

	private static void processCompoundTag(CompoundTag tag) {
		CompoundTag effects = tag.getCompoundOrEmpty("effects");
		if (effects.isEmpty()) return;
		CompoundTag attributes = tag.getCompoundOrEmpty("attributes");
		if (tag.keySet().contains("attributes")) {
			alert();
			return;
		}
		tag.put("attributes", attributes);
		processColors(effects, attributes);
		processParticles(effects, attributes);
		processSounds(effects, attributes);
		processMusic(effects, attributes);
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
