package net.maximpixel.coloredallays.mixin;

import net.maximpixel.coloredallays.ColoredAllay;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AllayEntity.class)
public class MixinAllayEntity extends PathAwareEntity implements ColoredAllay {
	@SuppressWarnings("WrongEntityDataParameterClass")
	private static final TrackedData<Byte> COLOR = DataTracker.registerData(AllayEntity.class, TrackedDataHandlerRegistry.BYTE);

	protected MixinAllayEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		dataTracker.startTracking(COLOR, (byte)3);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putByte("AllayColor", (byte)getAllayColor().getId());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		setAllayColor(DyeColor.byId(nbt.getByte("AllayColor")));
	}

	@Override
	public DyeColor getAllayColor() {
		return DyeColor.byId(dataTracker.get(COLOR) & 15);
	}

	@Override
	public void setAllayColor(DyeColor color) {
		dataTracker.set(COLOR, (byte)(color.getId() & 15));
	}

	@Inject(at = @At("HEAD"), method = "interactMob(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;", cancellable = true)
	protected void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
		if (!isAlive()) {
			return;
		}

		ItemStack stack = player.getStackInHand(hand);

		if (!stack.isEmpty()) {
			Item item = stack.getItem();

			if (item instanceof DyeItem) {
				DyeColor color = ((DyeItem) item).getColor();

				if (getAllayColor() != color) {
					world.playSoundFromEntity(player, this, SoundEvents.ITEM_DYE_USE, SoundCategory.PLAYERS, 1F, 1F);

					if (!world.isClient) {
						setAllayColor(color);
						stack.decrement(1);
					}

					cir.setReturnValue(ActionResult.SUCCESS);
				}
			}
		}
	}
}
