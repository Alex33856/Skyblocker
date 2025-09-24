package de.hysky.skyblocker.utils.ws.message;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.hysky.skyblocker.skyblock.dungeon.secrets.SecretsTracker;
import de.hysky.skyblocker.utils.ws.Type;
import net.minecraft.util.Uuids;

import java.util.Optional;
import java.util.UUID;

public record DungeonSecretCountMessage(UUID uuid, int secretsFound) implements Message<DungeonSecretCountMessage> {
	private static final Codec<DungeonSecretCountMessage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
					Uuids.STRING_CODEC.fieldOf("uuid").forGetter(DungeonSecretCountMessage::uuid),
					Codec.INT.fieldOf("secretsFound").forGetter(DungeonSecretCountMessage::secretsFound))
			.apply(instance, DungeonSecretCountMessage::new));

	@Override
	public Codec<DungeonSecretCountMessage> getCodec() {
		return CODEC;
	}

	public static void handle(Type type, Optional<Dynamic<?>> rawMsg) {
		if (type != Type.PUBLISH || rawMsg.isEmpty()) return;

		DungeonSecretCountMessage data = CODEC.parse(rawMsg.get()).getOrThrow();
		SecretsTracker.onSecretCountReceived(data);
	}
}
