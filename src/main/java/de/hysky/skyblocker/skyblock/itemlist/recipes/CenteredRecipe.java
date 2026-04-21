package de.hysky.skyblocker.skyblock.itemlist.recipes;

import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface CenteredRecipe extends SkyblockRecipe {
	int SLOT_SIZE = 18;
	int ARROW_LENGTH = 24;
	int ARROW_PADDING = 3;

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
	static List<SkyblockRecipe.RecipeSlot> arrangeInputs(int width, int height, @Nullable ItemStack centeredItem, List<ItemStack> inputs) {
		List<SkyblockRecipe.RecipeSlot> slots = new ArrayList<>();
		if (centeredItem != null)
			slots.add(new SkyblockRecipe.RecipeSlot((width - SLOT_SIZE) / 2, SLOT_SIZE / 2, centeredItem));
		else
			height = height - SLOT_SIZE;

		int centerX = getCenterX(width, inputs);
		int centerY = height / 2;

		boolean onSecondRow = false; // Max of 2 rows
		int rowSize = getRowSize(inputs);

		int x = centerX - (SLOT_SIZE * Math.min(rowSize, 3)) - ARROW_LENGTH / 2 - ARROW_PADDING;
		int y = shouldSplit(inputs) ? centerY - SLOT_SIZE / 2 + 3 : centerY;

		for (int i = 0; i < inputs.size(); i++) {
			slots.add(new SkyblockRecipe.RecipeSlot(x, y, inputs.get(i)));
			x += SLOT_SIZE;
			if (((i + 1) % rowSize == 0) && !onSecondRow) {
				onSecondRow = true;
				x -= rowSize * SLOT_SIZE;
				y += SLOT_SIZE;
			}
		}

		return slots;
	}

	static List<SkyblockRecipe.RecipeSlot> arrangeOutputs(int width, int height, List<ItemStack> inputs, ItemStack output) {
		int centerX = getCenterX(width, inputs);
		int centerY = height / 2;
		if (inputs.size() == 7 || inputs.size() == 8) centerX += SLOT_SIZE;
		return List.of(new SkyblockRecipe.RecipeSlot(centerX + ARROW_LENGTH / 2 + ARROW_PADDING, centerY, output));
	}

	static ScreenPosition getArrowLocation(int width, int height, List<ItemStack> inputs) {
		int centerX = getCenterX(width, inputs);
		int centerY = height / 2;
		if (inputs.size() == 7 || inputs.size() == 8) centerX += SLOT_SIZE;
		return new ScreenPosition(centerX - ARROW_LENGTH / 2 - 1, centerY);
	}

	ItemStack getIcon();

	@Nullable ItemStack getRepresentative();
}
