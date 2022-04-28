package net.maximpixel.coloredallays.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.maximpixel.coloredallays.ColoredAllay;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.AllayEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
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
	 * @reason to recolor default model
	 */
	@Overwrite
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		if (allayEntity instanceof ColoredAllay colored) {
			DyeColor color = colored.getAllayColor();
			float[] rgb = color.getColorComponents();
			root.render(matrices, vertices, light, overlay, rgb[0], rgb[1], rgb[2], alpha);
		} else {
			root.render(matrices, vertices, light, overlay);
		}
	}
}
