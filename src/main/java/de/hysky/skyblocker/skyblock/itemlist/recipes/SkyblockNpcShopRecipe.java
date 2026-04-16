package de.hysky.skyblocker.skyblock.itemlist.recipes;

import de.hysky.skyblocker.SkyblockerMod;
import de.hysky.skyblocker.skyblock.itemlist.ItemRepository;
import io.github.moulberry.repo.data.NEUNpcShopRecipe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class SkyblockNpcShopRecipe implements SkyblockRecipe {
	public static final Identifier ID = SkyblockerMod.id("skyblock_npc_shop");
	private static final int SLOT_SIZE = 18;
	private static final int ARROW_LENGTH = 24;
	private static final int ARROW_PADDING = 3;

	private final ItemStack npcShop;
	private final List<ItemStack> inputs;
	private final ItemStack output;

	public SkyblockNpcShopRecipe(NEUNpcShopRecipe shopRecipe) {
		npcShop = ItemRepository.getItemStack(shopRecipe.getIsSoldBy().getSkyblockItemId());
		inputs = shopRecipe.getCost().stream().map(SkyblockRecipe::getItemStack).toList();
		output = SkyblockRecipe.getItemStack(shopRecipe.getResult());
	}

	private static boolean shouldSplit(List<ItemStack> inputs) {
		return inputs.size() > 3;
	}

	private static int getRowSize(List<ItemStack> inputs) {
		return shouldSplit(inputs) ? Math.floorDiv(inputs.size(), 2) : inputs.size();
	}

	/**
	 * For larger recipes, we shift the center slightly so all the items fit on the screen.
	 * <p>
	 * Recipes greater than 3 items are split into 2 rows evenly.
	 * If the input size is odd, it is offset further so those items do not overlap with the arrow.
	 * There are currently no recipes with > 7 items.
	 */
	private static int getCenterX(int width, List<ItemStack> inputs) {
		int centerX = width / 2;
		int size = inputs.size();
		centerX += Math.min(getRowSize(inputs), 3) * SLOT_SIZE / 2 - SLOT_SIZE / 2;
		if (size > 1 && size % 2 == 1) centerX -= SLOT_SIZE / 2;
		return centerX;
	}

	/**
	 * Input items are displayed in 1 or 2 rows depending on the recipe size.
	 */
	public static List<RecipeSlot> arrangeInputs(int width, int height, @Nullable ItemStack centeredItem, List<ItemStack> inputs) {
		List<RecipeSlot> slots = new ArrayList<>();
		if (centeredItem != null)
			slots.add(new RecipeSlot((width - SLOT_SIZE) / 2, SLOT_SIZE / 2, centeredItem));
		else
			height = height - SLOT_SIZE;

		int centerX = getCenterX(width, inputs);
		int centerY = height / 2;

		boolean onSecondRow = false; // Max of 2 rows
		int rowSize = getRowSize(inputs);

		int x = centerX - (SLOT_SIZE * Math.min(rowSize, 3)) - ARROW_LENGTH / 2 - ARROW_PADDING;
		int y = shouldSplit(inputs) ? centerY - SLOT_SIZE / 2 + 3 : centerY;

		for (int i = 0; i < inputs.size(); i++) {
			slots.add(new RecipeSlot(x, y, inputs.get(i)));
			x += SLOT_SIZE;
			if (((i + 1) % rowSize == 0) && !onSecondRow) {
				onSecondRow = true;
				x -= rowSize * SLOT_SIZE;
				y += SLOT_SIZE;
			}
		}

		return slots;
	}

	public static List<RecipeSlot> arrangeOutputs(int width, int height, List<ItemStack> inputs, ItemStack output) {
		int centerX = getCenterX(width, inputs);
		int centerY = height / 2;
		if (inputs.size() == 7 || inputs.size() == 8) centerX += SLOT_SIZE;
		return List.of(new RecipeSlot(centerX + ARROW_LENGTH / 2 + ARROW_PADDING, centerY, output));
	}

	@Override
	public List<RecipeSlot> getInputSlots(int width, int height) {
		return arrangeInputs(width, height, npcShop, inputs);
	}

	@Override
	public List<RecipeSlot> getOutputSlots(int width, int height) {
		return arrangeOutputs(width, height, inputs, output);
	}

	public static @Nullable ScreenPosition getArrowLocation(int width, int height, List<ItemStack> inputs) {
		int centerX = getCenterX(width, inputs);
		int centerY = height / 2;
		if (inputs.size() == 7 || inputs.size() == 8) centerX += SLOT_SIZE;
		return new ScreenPosition(centerX - ARROW_LENGTH / 2 - 1, centerY);
	}

	@Override
	public @Nullable ScreenPosition getArrowLocation(int width, int height) {
		return getArrowLocation(width, height, inputs);
	}

	public ItemStack getNpcItem() {
		return npcShop;
	}

	@Override
	public List<ItemStack> getInputs() {
		return inputs;
	}

	@Override
	public List<ItemStack> getOutputs() {
		return Collections.singletonList(output);
	}

	@Override
	public Component getExtraText() {
		return Component.empty();
	}

	@Override
	public Identifier getCategoryIdentifier() {
		return ID;
	}

	@Override
	public Identifier getRecipeIdentifier() {
		return Identifier.fromNamespaceAndPath("skyblock", output.getSkyblockId().toLowerCase(Locale.ENGLISH).replace(';', '_') + "_" + output.getCount());
	}
}
