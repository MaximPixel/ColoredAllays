package net.maximpixel.coloredallays.mixin;

import net.maximpixel.coloredallays.ColoredAllay;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AllayEntity.class)
public class MixinAllayEntity extends PathAwareEntity implements ColoredAllay {
	private static final TrackedData<Byte> COLOR = DataTracker.registerData(AllayEntity.class, TrackedDataHandlerRegistry.BYTE);

	protected MixinAllayEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		dataTracker.startTracking(COLOR, (byte)0);
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
}
