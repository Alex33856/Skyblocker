package de.hysky.skyblocker.skyblock.dungeon.secrets;

import de.hysky.skyblocker.annotations.Init;
import de.hysky.skyblocker.events.DungeonEvents;
import de.hysky.skyblocker.utils.Constants;
import de.hysky.skyblocker.utils.Utils;
import de.hysky.skyblocker.utils.ws.Service;
import de.hysky.skyblocker.utils.ws.WsMessageHandler;
import de.hysky.skyblocker.utils.ws.message.DungeonSecretCountMessage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tracks the amount of secrets players get every run
 */
public class SecretsTracker {
	private static final Logger LOGGER = LoggerFactory.getLogger(SecretsTracker.class);
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

	private static int secretsFound = 0;
	private static boolean hasSent = true;

	@Init
	public static void init() {
		DungeonEvents.DUNGEON_STARTED.register(SecretsTracker::reset);
		DungeonEvents.DUNGEON_ENDED.register(SecretsTracker::sendSecretCount);
	}

	private static void reset() {
		secretsFound = 0;
		hasSent = false;
	}

	private static void sendSecretCount() {
		if (hasSent || CLIENT.player == null) return;
		sendMessageForPlayer(CLIENT.getSession().getUsername(), secretsFound);
		WsMessageHandler.sendServerMessage(Service.DUNGEON_SECRETS, new DungeonSecretCountMessage(CLIENT.player.getUuid(), secretsFound));
	}

	public static void onSecretCountReceived(DungeonSecretCountMessage message) {
		if (!Utils.isInDungeons() || CLIENT.player == null || CLIENT.world == null) return;

		Entity player = CLIENT.world.getEntity(message.uuid());
		if (player == null) {
			LOGGER.info("[Skyblocker Secrets Tracker] Received a secret count message for a player that doesn't exist? - Message: {}", message);
			return;
		}

		String playerName = player.getName().getString();
		sendMessageForPlayer(playerName, secretsFound);
	}

	private static void sendMessageForPlayer(String player, int secretCount) {
		if (CLIENT.player == null) return;
		CLIENT.player.sendMessage(Constants.PREFIX.get().append(Text.translatable("skyblocker.dungeons.secretsTracker.feedback", Text.literal(player).append(" (" + DungeonPlayerManager.getClassFromPlayer(player).displayName() + ")").withColor(0xf57542), "§7" + secretCount)), false);
	}

	protected static void onSecretFound() {
		secretsFound += 1;
	}

	protected static void onChestLocked() {
		secretsFound -= 1;
	}
}
