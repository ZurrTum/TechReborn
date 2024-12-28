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

package techreborn.items.tool;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import techreborn.blockentity.cable.CableBlockEntity;
import techreborn.blocks.cable.CableBlock;
import techreborn.component.TRDataComponentTypes;
import techreborn.init.TRContent;
import techreborn.init.TRItemSettings;

import java.util.List;

public class PaintingToolItem extends Item {

	public PaintingToolItem(String name) {
		super(TRItemSettings.item(name).maxDamage(64));
	}

	public ActionResult useOnBlock(ItemUsageContext context) {
		PlayerEntity player = context.getPlayer();
		if (player == null) {
			return ActionResult.FAIL;
		}

		BlockState blockState = context.getWorld().getBlockState(context.getBlockPos());
		if (player.isSneaking()) {
			boolean opaqueFullCube = blockState.isOpaqueFullCube()
				&& blockState.getBlock().getDefaultState().isOpaqueFullCube();

			if (opaqueFullCube || blockState.isIn(TRContent.BlockTags.NONE_SOLID_COVERS)) {
				context.getStack().set(TRDataComponentTypes.PAINTING_COVER, blockState);
				context.getStack().set(TRDataComponentTypes.PAINTING_COVER, blockState);
				return ActionResult.SUCCESS;
			}
			return ActionResult.FAIL;
		} else {
			BlockState cover = getCover(context.getStack());
			if (cover != null && blockState.getBlock() instanceof CableBlock && blockState.get(CableBlock.COVERED)) {
				BlockEntity blockEntity = context.getWorld().getBlockEntity(context.getBlockPos());
				if (blockEntity == null) {
					return ActionResult.FAIL;
				}
				((CableBlockEntity) blockEntity).setCover(cover);

				context.getWorld().playSound(player, context.getBlockPos(), SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.BLOCKS, 0.6F, 1.0F);
				if (!context.getWorld().isClient) {
					context.getStack().damage(1, player, context.getHand() == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
				}

				return ActionResult.SUCCESS;
			}
		}

		return ActionResult.FAIL;
	}

	public static BlockState getCover(ItemStack stack) {
		return stack.getOrDefault(TRDataComponentTypes.PAINTING_COVER, null);
	}

	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		BlockState blockState = getCover(stack);
		if (blockState != null) {
			tooltip.add((Text.translatable(blockState.getBlock().getTranslationKey())).formatted(Formatting.GRAY));
			tooltip.add((Text.translatable("techreborn.tooltip.painting_tool.apply")).formatted(Formatting.GOLD));
		} else {
			tooltip.add((Text.translatable("techreborn.tooltip.painting_tool.select")).formatted(Formatting.GOLD));
		}
	}

}
