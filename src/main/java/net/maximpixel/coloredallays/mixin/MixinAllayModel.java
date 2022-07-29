package net.maximpixel.coloredallays.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.maximpixel.coloredallays.ColoredAllay;
import net.minecraft.client.model.AllayModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(AllayModel.class)
public class MixinAllayModel {
	@Final
	@Shadow
	private ModelPart root;

	private Allay allayEntity;

	@Inject(at = @At("TAIL"), method = "setupAnim(Lnet/minecraft/world/entity/animal/allay/Allay;FFFFF)V")
	public void setupAnim(Allay allayEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
		this.allayEntity = allayEntity;
	}

	/**
	 * @author MaximPixel
	 * @reason to recolor default model
	 */
	@Overwrite
	public void renderToBuffer(PoseStack ps, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		if (allayEntity instanceof ColoredAllay colored) {
			DyeColor color = colored.getAllayColor();
			float[] rgb = color.getTextureDiffuseColors();
			root.render(ps, vertices, light, overlay, rgb[0], rgb[1], rgb[2], alpha);
		} else {
			root.render(ps, vertices, light, overlay);
		}
	}
}
