package de.hysky.skyblocker.utils.ws.message;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.hysky.skyblocker.skyblock.dungeon.secrets.SecretsTracker;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record DungeonRunSecretCountMessage(String type, UUID uuid, int secretsFound) implements Message<DungeonRunSecretCountMessage> {
	public static final String TYPE = "run_secret_count";
	private static final Codec<DungeonRunSecretCountMessage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
					Codec.STRING.fieldOf("type").forGetter(DungeonRunSecretCountMessage::type),
					Uuids.STRING_CODEC.fieldOf("uuid").forGetter(DungeonRunSecretCountMessage::uuid),
					Codec.INT.fieldOf("secretsFound").forGetter(DungeonRunSecretCountMessage::secretsFound))
			.apply(instance, DungeonRunSecretCountMessage::new));

	public DungeonRunSecretCountMessage(UUID uuid, int secretsFound) {
		this(TYPE, uuid, secretsFound);
	}

	@Override
	public Codec<DungeonRunSecretCountMessage> getCodec() {
		return CODEC;
	}

	public static void handle(Dynamic<?> rawMsg) {
		DungeonRunSecretCountMessage data = CODEC.parse(rawMsg).getOrThrow();
		SecretsTracker.onSecretCountReceived(data);
	}
}
