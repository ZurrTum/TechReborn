/*
 * This file is part of TechReborn, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2020 TechReborn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package techreborn.items;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantItemStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.FluidFillable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;
import reborncore.common.fluid.FluidUtils;
import reborncore.common.fluid.container.ItemFluidInfo;
import techreborn.component.TRDataComponentTypes;
import techreborn.init.TRContent;
import techreborn.init.TRItemSettings;

import java.util.Optional;

/**
 * Created by modmuss50 on 17/05/2016.
 */
public class DynamicCellItem extends Item implements ItemFluidInfo {

	public DynamicCellItem(String name) {
		super(TRItemSettings.item(name).maxCount(16).component(TRDataComponentTypes.FLUID, Fluids.EMPTY.getRegistryEntry()));
	}

	// Thanks vanilla :)
	@SuppressWarnings("deprecation")
	private void playEmptyingSound(@Nullable PlayerEntity playerEntity, WorldAccess world, BlockPos blockPos, Fluid fluid) {
		SoundEvent soundEvent = fluid.isIn(FluidTags.LAVA) ? SoundEvents.ITEM_BUCKET_EMPTY_LAVA : SoundEvents.ITEM_BUCKET_EMPTY;
		world.playSound(playerEntity, blockPos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
	}

	public static ItemStack getCellWithFluid(Fluid fluid, int stackSize) {
		Validate.notNull(fluid, "Can't get cell with NULL fluid");
		ItemStack stack = new ItemStack(TRContent.CELL, stackSize);
		stack.set(TRDataComponentTypes.FLUID, fluid.getRegistryEntry());
		stack.setCount(stackSize);
		return stack;
	}

	public static ItemStack getCellWithFluid(Fluid fluid) {
		return getCellWithFluid(fluid, 1);
	}

	public static ItemStack getEmptyCell(int amount) {
		return new ItemStack(TRContent.CELL, amount);
	}

	private void insertOrDropStack(PlayerEntity playerEntity, ServerWorld world, ItemStack stack) {
		if (!playerEntity.getInventory().insertStack(stack)) {
			playerEntity.dropStack(world, stack);
		}
	}

	public boolean placeFluid(@Nullable PlayerEntity player, World world, BlockPos pos, @Nullable BlockHitResult hitResult, ItemStack filledCell) {
		Fluid fluid = getFluid(filledCell);
		if (fluid == Fluids.EMPTY) {
			return false;
		}

		BlockState blockState = world.getBlockState(pos);
		boolean canPlace = blockState.canBucketPlace(fluid);

		if (!blockState.isAir() && !canPlace && (!(blockState.getBlock() instanceof FluidFillable) || !((FluidFillable) blockState.getBlock()).canFillWithFluid(player, world, pos, blockState, fluid))) {
			return hitResult != null && this.placeFluid(player, world, hitResult.getBlockPos().offset(hitResult.getSide()), null, filledCell);
		} else {
			//noinspection deprecation
			if (world.getDimension().ultrawarm() && fluid.isIn(FluidTags.WATER)) {
				int i = pos.getX();
				int j = pos.getY();
				int k = pos.getZ();
				world.playSound(player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

				for (int l = 0; l < 8; ++l) {
					world.addParticle(ParticleTypes.LARGE_SMOKE, (double) i + Math.random(), (double) j + Math.random(), (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
				}
			} else if (blockState.getBlock() instanceof FluidFillable && fluid == Fluids.WATER) {
				if (((FluidFillable) blockState.getBlock()).tryFillWithFluid(world, pos, blockState, ((FlowableFluid) fluid).getStill(false))) {
					this.playEmptyingSound(player, world, pos, fluid);
				}
			} else {
				//noinspection deprecation
				if (!world.isClient && canPlace && !blockState.isLiquid()) {
					world.breakBlock(pos, true);
				}

				this.playEmptyingSound(player, world, pos, fluid);
				world.setBlockState(pos, fluid.getDefaultState().getBlockState(), 11);
			}
			return true;
		}
	}

	@Override
	public Text getName(ItemStack itemStack) {
		Fluid fluid = getFluid(itemStack);
		if (fluid != Fluids.EMPTY) {
			// TODO use translation keys for fluid and the cell https://fabric.asie.pl/wiki/tutorial:lang?s[]=translation might be useful
			return Text.literal(Text.translatable("item.techreborn.cell.fluid").getString().replace("$fluid$", FluidUtils.getFluidName(fluid)));
		}
		return super.getName(itemStack);
	}

	@Override
	public ActionResult use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getStackInHand(hand);
		Fluid containedFluid = getFluid(stack);

		BlockHitResult hitResult = raycast(world, player, containedFluid == Fluids.EMPTY ? RaycastContext.FluidHandling.SOURCE_ONLY : RaycastContext.FluidHandling.NONE);
			if (hitResult.getType() == HitResult.Type.MISS || !(containedFluid instanceof FlowableFluid || Fluids.EMPTY == containedFluid)) {
			return ActionResult.PASS;
		}
		if (hitResult.getType() != HitResult.Type.BLOCK) {
			return ActionResult.PASS;
		}

		BlockPos hitPos = hitResult.getBlockPos();
		if (!world.canPlayerModifyAt(player, hitPos)) {
			return ActionResult.FAIL;
		}

		Direction side = hitResult.getSide();
		BlockPos placePos = hitPos.offset(side);
		if (!player.canPlaceOn(placePos, side, stack)) {
			return ActionResult.FAIL;
		}

		BlockState hitState = world.getBlockState(hitPos);

		if (containedFluid == Fluids.EMPTY) {
			if (!(hitState.getBlock() instanceof FluidDrainable fluidDrainable)) {
				return ActionResult.FAIL;
			}
			// This will give us bucket, not a cell
			ItemStack itemStack = fluidDrainable.tryDrainFluid(player, world, hitPos, hitState);
			if (!itemStack.isEmpty() && itemStack.getItem() instanceof ItemFluidInfo) {
				Fluid drainFluid = ((ItemFluidInfo) itemStack.getItem()).getFluid(itemStack);
				fluidDrainable.getBucketFillSound().ifPresent((sound) -> player.playSound(sound, 1.0F, 1.0F));
				world.emitGameEvent(player, GameEvent.FLUID_PICKUP, hitPos);
				// Replace bucket item with cell item
				itemStack = getCellWithFluid(drainFluid, 1);
				ItemStack resultStack = ItemUsage.exchangeStack(stack, player, itemStack, false);
				if (resultStack == stack) {
					return ActionResult.SUCCESS;
				} else {
					return ActionResult.SUCCESS.withNewHandStack(resultStack);
				}
			}
		} else {
			placePos = hitState.getBlock() instanceof FluidFillable ? hitPos : placePos;
			if (this.placeFluid(player, world, placePos, hitResult, stack)) {

				if (player.getAbilities().creativeMode) {
					return ActionResult.SUCCESS;
				}

				if (stack.getCount() == 1) {
					return ActionResult.SUCCESS.withNewHandStack(getEmpty());
				} else {
					stack.decrement(1);
					if (!world.isClient) {
						insertOrDropStack(player, (ServerWorld) world, getEmpty());
					}

					return ActionResult.SUCCESS;
				}
			}
		}

		return ActionResult.FAIL;
	}

	// ItemFluidInfo
	@Override
	public ItemStack getEmpty() {
		return new ItemStack(this);
	}

	@Override
	public ItemStack getFull(Fluid fluid) {
		return getCellWithFluid(fluid);
	}

	@Override
	public Fluid getFluid(ItemStack itemStack) {
		RegistryEntry<Fluid> fluidEntry = itemStack.getOrDefault(TRDataComponentTypes.FLUID, Fluids.EMPTY.getRegistryEntry());
		return fluidEntry.value();
	}

	public void registerFluidApi() {
		FluidStorage.ITEM.registerForItems((stack, ctx) -> new CellStorage(ctx), this);
	}

	public class CellStorage extends SingleVariantItemStorage<FluidVariant> {
		public CellStorage(ContainerItemContext context) {
			super(context);
		}

		@Override
		protected FluidVariant getBlankResource() {
			return FluidVariant.blank();
		}

		@Override
		protected FluidVariant getResource(ItemVariant currentVariant) {
			Optional<? extends RegistryEntry<Fluid>> registryEntry = currentVariant.getComponents().get(TRDataComponentTypes.FLUID);

			if (registryEntry != null && registryEntry.isPresent()) {
				return FluidVariant.of(registryEntry.get().value());
			}

			return FluidVariant.of(Fluids.EMPTY);
		}

		@Override
		protected long getAmount(ItemVariant currentVariant) {
			return getResource(currentVariant).isBlank() ? 0 : FluidConstants.BUCKET;
		}

		@Override
		protected long getCapacity(FluidVariant variant) {
			return FluidConstants.BUCKET;
		}

		@Override
		protected ItemVariant getUpdatedVariant(ItemVariant currentVariant, FluidVariant newResource, long newAmount) {
			if (newAmount != 0 && newAmount != FluidConstants.BUCKET) {
				throw new IllegalArgumentException("Only amounts of 0 and 1 bucket are supported! This is a bug!");
			}
			// TODO: this is not ideal since we delete any extra NBT, but it probably doesn't matter in practice?
			if (newResource.isBlank() || newAmount == 0) {
				return ItemVariant.of(DynamicCellItem.this);
			} else {
				return ItemVariant.of(getCellWithFluid(newResource.getFluid()));
			}
		}

		// A few "hacks" to ensure that transfer is always exactly 0 or 1 bucket.
		@Override
		public long insert(FluidVariant insertedResource, long maxAmount, TransactionContext transaction) {
			if (isResourceBlank() && maxAmount >= FluidConstants.BUCKET) {
				return super.insert(insertedResource, FluidConstants.BUCKET, transaction);
			} else {
				return 0;
			}
		}

		@Override
		public long extract(FluidVariant extractedResource, long maxAmount, TransactionContext transaction) {
			if (!isResourceBlank() && maxAmount >= FluidConstants.BUCKET) {
				return super.extract(extractedResource, FluidConstants.BUCKET, transaction);
			} else {
				return 0;
			}
		}
	}
}
