package de.hysky.skyblocker.skyblock.dungeon.secrets;

import de.hysky.skyblocker.annotations.Init;
import de.hysky.skyblocker.events.DungeonEvents;
import de.hysky.skyblocker.utils.ws.Service;
import de.hysky.skyblocker.utils.ws.WsStateManager;

import java.util.Optional;

public class DungeonWebSocket {
	@Init
	public static void init() {
		DungeonEvents.DUNGEON_STARTED.register(DungeonWebSocket::connect);
	}

	public static void connect() {
		WsStateManager.subscribe(Service.DUNGEON_SECRETS, Optional.empty());
	}
}
