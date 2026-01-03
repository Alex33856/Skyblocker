package de.hysky.skyblocker.utils.render.primitive;

import org.joml.Matrix4f;
import com.mojang.blaze3d.vertex.BufferBuilder;
import de.hysky.skyblocker.utils.render.Renderer;
import de.hysky.skyblocker.utils.render.SkyblockerRenderPipelines;
import de.hysky.skyblocker.utils.render.state.OutlinedBoxRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.CameraRenderState;

//? if <1.21.11 {
/*import com.mojang.blaze3d.vertex.PoseStack;
import de.hysky.skyblocker.utils.render.MatrixHelper;
import net.minecraft.client.renderer.ShapeRenderer;
*///? }

public final class OutlinedBoxRenderer implements PrimitiveRenderer<OutlinedBoxRenderState> {
	protected static final OutlinedBoxRenderer INSTANCE = new OutlinedBoxRenderer();

	private OutlinedBoxRenderer() {}

	@Override
	public void submitPrimitives(OutlinedBoxRenderState state, CameraRenderState cameraState) {
		BufferBuilder buffer = Renderer.getBuffer(state.throughWalls ? SkyblockerRenderPipelines.LINES_THROUGH_WALLS : RenderPipelines.LINES/*? if <1.21.11 {*//*, state.lineWidth*//*?}*/);
		Matrix4f positionMatrix = new Matrix4f()
				.translate((float) -cameraState.pos.x, (float) -cameraState.pos.y, (float) -cameraState.pos.z);
		//? if <1.21.11 {
		/*PoseStack matrices = MatrixHelper.toStack(positionMatrix);

		ShapeRenderer.renderLineBox(matrices.last(), buffer, state.minX, state.minY, state.minZ, state.maxX, state.maxY, state.maxZ, state.colourComponents[0], state.colourComponents[1], state.colourComponents[2], state.alpha);
		*///? } else {
		float minX = (float) state.minX;
		float minY = (float) state.minY;
		float minZ = (float) state.minZ;
		float maxX = (float) state.maxX;
		float maxY = (float) state.maxY;
		float maxZ = (float) state.maxZ;
		float red = state.colourComponents[0];
		float green = state.colourComponents[1];
		float blue = state.colourComponents[2];
		float alpha = state.alpha;

		buffer.addVertex(positionMatrix, minX, minY, minZ).setColor(red, green, blue, alpha).setNormal(1.0f, 0.0f, 0.0f).setLineWidth(state.lineWidth);
		buffer.addVertex(positionMatrix, maxX, minY, minZ).setColor(red, green, blue, alpha).setNormal(1.0f, 0.0f, 0.0f).setLineWidth(state.lineWidth);
		buffer.addVertex(positionMatrix, minX, minY, minZ).setColor(red, green, blue, alpha).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(state.lineWidth);
		buffer.addVertex(positionMatrix, minX, maxY, minZ).setColor(red, green, blue, alpha).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(state.lineWidth);
		buffer.addVertex(positionMatrix, minX, minY, minZ).setColor(red, green, blue, alpha).setNormal(0.0f, 0.0f, 1.0f).setLineWidth(state.lineWidth);
		buffer.addVertex(positionMatrix, minX, minY, maxZ).setColor(red, green, blue, alpha).setNormal(0.0f, 0.0f, 1.0f).setLineWidth(state.lineWidth);
		buffer.addVertex(positionMatrix, maxX, minY, minZ).setColor(red, green, blue, alpha).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(state.lineWidth);
		buffer.addVertex(positionMatrix, maxX, maxY, minZ).setColor(red, green, blue, alpha).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(state.lineWidth);
		buffer.addVertex(positionMatrix, maxX, maxY, minZ).setColor(red, green, blue, alpha).setNormal(-1.0f, 0.0f, 0.0f).setLineWidth(state.lineWidth);
		buffer.addVertex(positionMatrix, minX, maxY, minZ).setColor(red, green, blue, alpha).setNormal(-1.0f, 0.0f, 0.0f).setLineWidth(state.lineWidth);
		buffer.addVertex(positionMatrix, minX, maxY, minZ).setColor(red, green, blue, alpha).setNormal(0.0f, 0.0f, 1.0f).setLineWidth(state.lineWidth);
		buffer.addVertex(positionMatrix, minX, maxY, maxZ).setColor(red, green, blue, alpha).setNormal(0.0f, 0.0f, 1.0f).setLineWidth(state.lineWidth);
		buffer.addVertex(positionMatrix, minX, maxY, maxZ).setColor(red, green, blue, alpha).setNormal(0.0f, -1.0f, 0.0f).setLineWidth(state.lineWidth);
		buffer.addVertex(positionMatrix, minX, minY, maxZ).setColor(red, green, blue, alpha).setNormal(0.0f, -1.0f, 0.0f).setLineWidth(state.lineWidth);
		buffer.addVertex(positionMatrix, minX, minY, maxZ).setColor(red, green, blue, alpha).setNormal(1.0f, 0.0f, 0.0f).setLineWidth(state.lineWidth);
		buffer.addVertex(positionMatrix, maxX, minY, maxZ).setColor(red, green, blue, alpha).setNormal(1.0f, 0.0f, 0.0f).setLineWidth(state.lineWidth);
		buffer.addVertex(positionMatrix, maxX, minY, maxZ).setColor(red, green, blue, alpha).setNormal(0.0f, 0.0f, -1.0f).setLineWidth(state.lineWidth);
		buffer.addVertex(positionMatrix, maxX, minY, minZ).setColor(red, green, blue, alpha).setNormal(0.0f, 0.0f, -1.0f).setLineWidth(state.lineWidth);
		buffer.addVertex(positionMatrix, minX, maxY, maxZ).setColor(red, green, blue, alpha).setNormal(1.0f, 0.0f, 0.0f).setLineWidth(state.lineWidth);
		buffer.addVertex(positionMatrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha).setNormal(1.0f, 0.0f, 0.0f).setLineWidth(state.lineWidth);
		buffer.addVertex(positionMatrix, maxX, minY, maxZ).setColor(red, green, blue, alpha).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(state.lineWidth);
		buffer.addVertex(positionMatrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha).setNormal(0.0f, 1.0f, 0.0f).setLineWidth(state.lineWidth);
		buffer.addVertex(positionMatrix, maxX, maxY, minZ).setColor(red, green, blue, alpha).setNormal(0.0f, 0.0f, 1.0f).setLineWidth(state.lineWidth);
		buffer.addVertex(positionMatrix, maxX, maxY, maxZ).setColor(red, green, blue, alpha).setNormal(0.0f, 0.0f, 1.0f).setLineWidth(state.lineWidth);
		//? }
	}
}
