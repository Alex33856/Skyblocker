package de.hysky.skyblocker.skyblock.dungeon.secrets;

import de.hysky.skyblocker.annotations.Init;
import de.hysky.skyblocker.events.DungeonEvents;
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
		CLIENT.player.sendMessage(Text.literal("You found %d secrets - awesome!".formatted(secretsFound)), false);
		WsMessageHandler.sendMessage(Service.DUNGEON_SECRETS, new DungeonSecretCountMessage(CLIENT.player.getUuid(), secretsFound));
	}

	public static void onSecretCountReceived(DungeonSecretCountMessage message) {
		if (!Utils.isInDungeons() || CLIENT.player == null || CLIENT.world == null) return;
		LOGGER.info("{} found {} secrets!", message.uuid(), message.secretsFound());
		if (message.uuid() == CLIENT.player.getUuid()) {
			LOGGER.info("ignoring our own secret message");
			return;
		}

		Entity player = CLIENT.world.getEntity(message.uuid());
		if (player == null) {
			LOGGER.info("received a message for an entity that doesn't exist?");
			return;
		}
		String playerName = player.getName().getString();
		CLIENT.player.sendMessage(Text.translatable("skyblocker.dungeons.secretsTracker.feedback", playerName, message.secretsFound()), false);
	}

	protected static void onSecretFound() {
		secretsFound += 1;
	}

	/*
	private static void calculate(RunPhase phase) {
		switch (phase) {
			case START -> CompletableFuture.runAsync(() -> {
				TrackedRun newlyStartedRun = new TrackedRun();

				//Initialize players in new run
				for (int i = 0; i < 5; i++) {
					String playerName = getPlayerNameAt(i + 1);

					//The player name will be blank if there isn't a player at that index
					if (playerName.isEmpty()) continue;

					//If the player was a part of the last run, had non-empty secret data and that run ended less than 5 mins ago then copy the secret data over
					if (lastRun != null && System.currentTimeMillis() <= lastRunEnded + 300_000 && lastRun.playersSecretData().getOrDefault(playerName, SecretData.EMPTY) != SecretData.EMPTY) {
						newlyStartedRun.playersSecretData().put(playerName, lastRun.playersSecretData().get(playerName));
					} else {
						newlyStartedRun.playersSecretData().put(playerName, getPlayerSecrets(playerName));
					}
				}

				currentRun = newlyStartedRun;
			});

			case END -> CompletableFuture.runAsync(() -> {
				//In case the game crashes from something
				if (currentRun != null) {
					Object2ObjectOpenHashMap<String, SecretData> secretsFound = new Object2ObjectOpenHashMap<>();

					//Update secret counts
					for (Entry<String, SecretData> entry : currentRun.playersSecretData().entrySet()) {
						String playerName = entry.getKey();
						SecretData startingSecrets = entry.getValue();
						SecretData secretsNow = getPlayerSecrets(playerName);
						int secretsPlayerFound = secretsNow.secrets() - startingSecrets.secrets();

						//Add an entry to the secretsFound map with the data - if the secret data from now or the start was cached a warning will be shown
						secretsFound.put(playerName, secretsNow.updated(secretsPlayerFound, startingSecrets.cached() || secretsNow.cached()));
						entry.setValue(secretsNow);
					}

					//Print the results all in one go, so its clean and less of a chance of it being broken up
					for (Map.Entry<String, SecretData> entry : secretsFound.entrySet()) {
						sendResultMessage(entry.getKey(), entry.getValue(), true);
					}

					//Swap the current and last run as well as mark the run end time
					lastRunEnded = System.currentTimeMillis();
					lastRun = currentRun;
					currentRun = null;
				} else {
					sendResultMessage(null, null, false);
				}
			});
		}
	}

	private static void sendResultMessage(String player, SecretData secretData, boolean success) {
		PlayerEntity playerEntity = MinecraftClient.getInstance().player;
		if (playerEntity != null) {
			if (success) {
				playerEntity.sendMessage(Constants.PREFIX.get().append(Text.translatable("skyblocker.dungeons.secretsTracker.feedback", Text.literal(player).append(" (" + DungeonPlayerManager.getClassFromPlayer(player).displayName() + ")").withColor(0xf57542), "§7" + secretData.secrets(), getCacheText(secretData.cached(), secretData.cacheAge()))), false);
			} else {
				playerEntity.sendMessage(Constants.PREFIX.get().append(Text.translatable("skyblocker.dungeons.secretsTracker.failFeedback")), false);
			}
		}
	}

	private static Text getCacheText(boolean cached, int cacheAge) {
		return Text.literal("\u2139").styled(style -> style.withColor(cached ? 0xeac864 : 0x218bff).withHoverEvent(
				new HoverEvent.ShowText(cached ? Text.translatable("skyblocker.api.cache.HIT", cacheAge) : Text.translatable("skyblocker.api.cache.MISS"))));
	}

	private static boolean onMessage(Text text, boolean overlay) {
		if (Utils.isInDungeons() && SkyblockerConfigManager.get().dungeons.playerSecretsTracker && !overlay) {
			String message = Formatting.strip(text.getString());

			try {
				if (TEAM_SCORE_PATTERN.matcher(message).matches()) calculate(RunPhase.END);
			} catch (Exception e) {
				LOGGER.error("[Skyblocker] Encountered an unknown error while trying to track player secrets!", e);
			}
		}

		return true;
	}

	private static String getPlayerNameAt(int index) {
		Matcher matcher = DungeonPlayerManager.getPlayerFromTab(index);

		return matcher != null ? matcher.group("name") : "";
	}

	private static SecretData getPlayerSecrets(String name) {
		String uuid = ApiUtils.name2Uuid(name);

		if (!uuid.isEmpty()) {
			try (ApiResponse response = Http.sendHypixelRequest("player", "?uuid=" + uuid)) {
				return new SecretData(getSecretCountFromAchievements(JsonParser.parseString(response.content()).getAsJsonObject()), response.cached(), response.age());
			} catch (Exception e) {
				LOGGER.error("[Skyblocker] Encountered an error while trying to fetch {} secret count!", name + "'s", e);
			}
		}

		return SecretData.EMPTY;
	}

	// Gets a player's secret count from their hypixel achievements
	private static int getSecretCountFromAchievements(JsonObject playerJson) {
		JsonObject player = playerJson.getAsJsonObject("player");
		JsonObject achievements = player.has("achievements") ? player.getAsJsonObject("achievements") : null;
		return (achievements != null && achievements.has("skyblock_treasure_hunter")) ? achievements.get("skyblock_treasure_hunter").getAsInt() : 0;
	}

	// This will either reflect the value at the start or the end depending on when this is called
	private record TrackedRun(Object2ObjectOpenHashMap<String, SecretData> playersSecretData) {
		private TrackedRun() {
			this(new Object2ObjectOpenHashMap<>());
		}
	}

	private record SecretData(int secrets, boolean cached, int cacheAge) {
		private static final SecretData EMPTY = new SecretData(0, false, 0);

		//If only we had Derived Record Creation :( - https://bugs.openjdk.org/browse/JDK-8321133
		private SecretData updated(int secrets, boolean cached) {
			return new SecretData(secrets, cached, this.cacheAge);
		}
	}

	private enum RunPhase {
        START, END
	}*/
}
