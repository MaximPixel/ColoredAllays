package net.maximpixel.coloredallays.mixin;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.AllayEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.AllayEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AllayEntityModel.class)
public class MixinAllayEntityModel {
	@Final
	@Shadow
	private ModelPart root;

	private AllayEntity allayEntity;

	@Inject(at = @At("TAIL"), method = "setAngles(Lnet/minecraft/entity/passive/AllayEntity;FFFFF)V")
	public void setAngles(AllayEntity allayEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
		this.allayEntity = allayEntity;
	}

	/**
	 * @author MaximPixel
	 */
	@Overwrite
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		if (allayEntity != null) {
			root.render(matrices, vertices, light, overlay, 1F, 0F, 0F, 1F);
		} else {
			root.render(matrices, vertices, light, overlay);
		}
	}
}
