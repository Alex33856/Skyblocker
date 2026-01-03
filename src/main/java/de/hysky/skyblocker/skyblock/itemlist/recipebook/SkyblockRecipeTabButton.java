package de.hysky.skyblocker.skyblock.itemlist.recipebook;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.item.ItemStack;

/**
 * Based off {@link net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton}
 */
//? if >1.21.10 {
public class SkyblockRecipeTabButton extends ImageButton {
//? } else {
/* public class SkyblockRecipeTabButton extends net.minecraft.client.gui.components.StateSwitchingButton {
*///? }
	protected final ItemStack icon;
	private boolean selected;

	protected SkyblockRecipeTabButton(ItemStack icon) {
		super(0, 0, 35, 27, /*? if >1.21.10 {*/RecipeBookTabButton.SPRITES, _ignored -> {}/*? } else {*//*false *//*? }*/);
		this.icon = icon;
		//? if <1.21.11 {
		/*this.initTextureValues(RecipeBookTabButton.SPRITES);
		*///? }
	}

	@Override
	//? if >1.21.10 {
	public void renderContents(GuiGraphics context, int mouseX, int mouseY, float delta) {
	//? } else {
	/*public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
	*///? }
		if (this.sprites != null) {
			int x = this.getX();

			//Offset x
			if (this.selected) x -= 2;

			//Render main texture
			context.blitSprite(RenderPipelines.GUI_TEXTURED, this.sprites.get(true, this.selected), x, this.getY(), this.width, this.height);

			//Render item icon
			int offset = this.selected ? -2 : 0;
			context.renderFakeItem(this.icon, this.getX() + 9 + offset, this.getY() + 5);
		}

		//? if <1.21.11 {
		/* if (this.isHovered()) {
			context.requestCursor(com.mojang.blaze3d.platform.cursor.CursorTypes.POINTING_HAND);
		}
		*///? }
	}

	//? if >1.21.10 {
	@Override
	protected void handleCursor(GuiGraphics context) {
		if (!this.selected) {
			super.handleCursor(context);
		}
	}
	//? }

	public void select() {
		this.selected = true;
	}

	public void unselect() {
		this.selected = false;
	}
}
