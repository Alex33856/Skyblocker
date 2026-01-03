package de.hysky.skyblocker.skyblock.dungeon;

import de.hysky.skyblocker.SkyblockerMod;
import de.hysky.skyblocker.annotations.Init;
//? if >1.21.10 {
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
//?} else {

/*import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
*///?}

public class DungeonTextures {
	@Init
	public static void init() {
		//? if >1.21.10 {
		ResourceLoader.registerBuiltinPack(
				SkyblockerMod.id("recolored_dungeon_items"),
				SkyblockerMod.SKYBLOCKER_MOD,
				PackActivationType.NORMAL
		);
		//?} else {
		/*ResourceManagerHelper.registerBuiltinResourcePack(
				SkyblockerMod.id("recolored_dungeon_items"),
				SkyblockerMod.SKYBLOCKER_MOD,
				ResourcePackActivationType.NORMAL
		);
		*///?}
	}
}
