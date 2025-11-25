package de.hysky.skyblocker.skyblock.dungeon.secrets;

import com.mojang.serialization.Dynamic;
import de.hysky.skyblocker.annotations.Init;
import de.hysky.skyblocker.events.DungeonEvents;
import de.hysky.skyblocker.utils.render.RenderHelper;
import de.hysky.skyblocker.utils.ws.Service;
import de.hysky.skyblocker.utils.ws.Type;
import de.hysky.skyblocker.utils.ws.WsStateManager;
import de.hysky.skyblocker.utils.ws.message.DungeonRoomMatchMessage;
import de.hysky.skyblocker.utils.ws.message.DungeonRoomSecretCountMessage;
import de.hysky.skyblocker.utils.ws.message.DungeonRunSecretCountMessage;

import java.util.Optional;

public class DungeonWebSocket {
	@Init
	public static void init() {
		DungeonEvents.DUNGEON_STARTED.register(DungeonWebSocket::connect);
	}

	public static void connect() {
		WsStateManager.subscribeServer(Service.DUNGEON_SECRETS, Optional.empty());
	}

	public static void handleMessage(Type type, Optional<Dynamic<?>> rawMsg) {
		if (rawMsg.isEmpty()) return;
		Dynamic<?> message = rawMsg.get();

		if (type == Type.RESPONSE) {
			if (message.get("type").asString().isError()) return;
			String messageType = message.get("type").asString().getOrThrow();
			switch (messageType) {
				case DungeonRunSecretCountMessage.TYPE -> RenderHelper.runOnRenderThread(() -> DungeonRunSecretCountMessage.handle(message));
				// Secret Sync messages
				case DungeonRoomMatchMessage.TYPE -> RenderHelper.runOnRenderThread(() -> SecretSync.handleRoomMatch(DungeonRoomMatchMessage.CODEC.parse(message).getOrThrow()));
				case DungeonRoomSecretCountMessage.TYPE -> RenderHelper.runOnRenderThread(() -> SecretSync.handleSecretCountUpdate(DungeonRoomSecretCountMessage.CODEC.parse(message).getOrThrow()));
			}
		}
	}
}
