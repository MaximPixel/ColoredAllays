package net.maximpixel.coloredallays.mixin;

import net.maximpixel.coloredallays.ColoredAllay;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Allay.class)
public class MixinAllay extends PathfinderMob implements ColoredAllay {
	@SuppressWarnings("WrongEntityDataParameterClass")
	private static final EntityDataAccessor<Byte> COLOR = SynchedEntityData.defineId(Allay.class, EntityDataSerializers.BYTE);

	protected MixinAllay(EntityType<? extends PathfinderMob> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(at = @At("TAIL"), method = "defineSynchedData()V")
	protected void defineSynchedData(CallbackInfo ci) {
		entityData.define(COLOR, (byte)3);
	}

	@Inject(at = @At("TAIL"), method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
	protected void addAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
		nbt.putByte("AllayColor", (byte)getAllayColor().getId());
	}

	@Inject(at = @At("TAIL"), method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V")
	public void readAdditionalSaveData(CompoundTag nbt, CallbackInfo ci) {
		byte colorByte = nbt.contains("AllayColor", 99) ? nbt.getByte("AllayColor") : (byte) 3;
		setAllayColor(DyeColor.byId(colorByte));
	}

	@Override
	public DyeColor getAllayColor() {
		return DyeColor.byId(entityData.get(COLOR) & 15);
	}

	@Override
	public void setAllayColor(DyeColor color) {
		entityData.set(COLOR, (byte)(color.getId() & 15));
	}

	@Inject(at = @At("HEAD"), method = "mobInteract(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;", cancellable = true)
	protected void mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		if (!isAlive()) {
			return;
		}

		ItemStack stack = player.getItemInHand(hand);

		if (!stack.isEmpty()) {
			Item item = stack.getItem();

			if (item instanceof DyeItem) {
				DyeColor color = ((DyeItem) item).getDyeColor();

				if (getAllayColor() != color) {
					level.playSound(player, this, SoundEvents.DYE_USE, SoundSource.PLAYERS, 1F, 1F);

					if (!level.isClientSide) {
						setAllayColor(color);
						stack.shrink(1);
					}

					cir.setReturnValue(InteractionResult.SUCCESS);
				}
			}
		}
	}
}
