package de.hysky.skyblocker.utils.ws.message;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.hysky.skyblocker.skyblock.dungeon.secrets.SecretsTracker;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record DungeonSecretCountMessage(String type, UUID uuid, int secretsFound) implements Message<DungeonSecretCountMessage> {
	public static final String TYPE = "run_secret_count";

	public DungeonSecretCountMessage(UUID uuid, int secretsFound) {
		this(TYPE, uuid, secretsFound);
	}

	private static final Codec<DungeonSecretCountMessage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
					Codec.STRING.fieldOf("type").forGetter(DungeonSecretCountMessage::type),
					Uuids.STRING_CODEC.fieldOf("uuid").forGetter(DungeonSecretCountMessage::uuid),
					Codec.INT.fieldOf("secretsFound").forGetter(DungeonSecretCountMessage::secretsFound))
			.apply(instance, DungeonSecretCountMessage::new));

	@Override
	public Codec<DungeonSecretCountMessage> getCodec() {
		return CODEC;
	}

	public static void handle(Dynamic<?> rawMsg) {
		DungeonSecretCountMessage data = CODEC.parse(rawMsg).getOrThrow();
		SecretsTracker.onSecretCountReceived(data);
	}
}
