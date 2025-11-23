package de.hysky.skyblocker.skyblock.dungeon.secrets;

import com.mojang.logging.LogUtils;
import de.hysky.skyblocker.annotations.Init;
import de.hysky.skyblocker.events.DungeonEvents;
import de.hysky.skyblocker.utils.ws.Service;
import de.hysky.skyblocker.utils.ws.WsMessageHandler;
import de.hysky.skyblocker.utils.ws.message.DungeonRoomMatchMessage;
import net.minecraft.client.MinecraftClient;
import org.joml.Vector2ic;
import org.slf4j.Logger;

import java.util.List;

public class SecretSync {
	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	private static final Logger LOGGER = LogUtils.getLogger();

	@Init
	public static void init() {
		DungeonEvents.ROOM_MATCHED.register(SecretSync::syncRoomMatch);
	}

	public static void syncRoomMatch(Room room) {
		if (CLIENT.player == null || room.fromWebsocket) return;
		List<Vector2ic> segments = room.getSegments().stream().toList();
		WsMessageHandler.sendServerMessage(Service.DUNGEON_SECRETS, new DungeonRoomMatchMessage(CLIENT.player.getUuid(), room.getType(), room.getShape(), room.getDirection(), room.getName(), segments));
	}

	public static void handleRoomMatch(DungeonRoomMatchMessage msg) {
		if (DungeonManager.getRoomMetadata(msg.room()) == null) {
			LOGGER.error("[Skyblocker Dungeons Secret Sync] Received an invalid room over the websocket, msg: {}", msg);
			return;
		}

		MinecraftClient client = MinecraftClient.getInstance();
		if (client.world == null) return;
		if (client.world.getPlayerByUuid(msg.uuid()) == null) {
			LOGGER.error("[Skyblocker Dungeon Secret Sync] Received a message from a player not in the Dungeons run, msg: {}", msg);
			return;
		}

		// Check if we already have this room
		if (!DungeonManager.validateRoomSegmentsFromWs(msg.pos())) return;

		// Make the room and add it
		Room newRoom = new Room(msg.roomType(), msg.shape(), msg.direction(), msg.room(), msg.pos().toArray(Vector2ic[]::new));
		DungeonManager.addRoomFromWs(newRoom);
	}
}
