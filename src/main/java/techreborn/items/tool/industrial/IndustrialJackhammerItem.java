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

package techreborn.items.tool.industrial;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import reborncore.common.misc.MultiBlockBreakingTool;
import reborncore.common.powerSystem.RcEnergyTier;
import techreborn.component.TRDataComponentTypes;
import techreborn.config.TechRebornConfig;
import techreborn.init.TRToolMaterials;
import techreborn.items.tool.JackhammerItem;
import techreborn.utils.TRItemUtils;
import techreborn.utils.ToolsUtil;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class IndustrialJackhammerItem extends JackhammerItem implements MultiBlockBreakingTool {

	public IndustrialJackhammerItem(String name) {
		super(name, TRToolMaterials.INDUSTRIAL_JACKHAMMER, TechRebornConfig.industrialJackhammerCharge, RcEnergyTier.INSANE, TechRebornConfig.industrialJackhammerCost);
	}

	// Cycle Inactive, Active 3*3 and Active 5*5
	private void switchAOE(ItemStack stack, int cost, Entity entity) {
		TRItemUtils.checkActive(stack, cost, entity);
		if (!TRItemUtils.isActive(stack)) {
			TRItemUtils.switchActive(stack, cost, entity);
			stack.set(TRDataComponentTypes.AOE5, false);
			if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
				serverPlayerEntity.sendMessage(Text.translatable("techreborn.message.setTo").formatted(Formatting.GRAY).append(" ").append(Text.literal("3*3").formatted(Formatting.GOLD)), true);
			}
		} else {
			if (isAOE5(stack)) {
				TRItemUtils.switchActive(stack, cost, entity);
				stack.set(TRDataComponentTypes.AOE5, false);
			} else {
				stack.set(TRDataComponentTypes.AOE5, true);
				if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
					serverPlayerEntity.sendMessage(Text.translatable("techreborn.message.setTo").formatted(Formatting.GRAY).append(" ").append(Text.literal("5*5").formatted(Formatting.GOLD)), true);
				}
			}
		}
	}

	private boolean isAOE5(ItemStack stack) {
		return !stack.isEmpty() && Boolean.TRUE.equals(stack.get(TRDataComponentTypes.AOE5));
	}

	// JackhammerItem
	@Override
	public boolean postMine(ItemStack stack, World worldIn, BlockState stateIn, BlockPos pos, LivingEntity entityLiving) {
		// No AOE mining turned on OR we've broken a wrong block
		if (!TRItemUtils.isActive(stack) || !isCorrectForDrops(stack, stateIn)) {
			return super.postMine(stack, worldIn, stateIn, pos, entityLiving);
		}

		// Do AoE mining except original block
		int radius = isAOE5(stack) ? 2 : 1;
		for (BlockPos additionalPos : ToolsUtil.getAOEMiningBlocks(worldIn, pos, entityLiving, radius)) {
			if (shouldBreak(worldIn, pos, additionalPos)) {
				ToolsUtil.breakBlock(stack, worldIn, additionalPos, entityLiving, cost);
			}
		}

		// Do not forget to use energy for original block
		return super.postMine(stack, worldIn, stateIn, pos, entityLiving);
	}

	@Override
	public float getMiningSpeed(ItemStack stack, BlockState state) {
		float speed = super.getMiningSpeed(stack, state);

		if (speed > unpoweredSpeed) {
			return speed * 4;
		}

		return speed;
	}

	// Item
	@Override
	public ActionResult use(final World world, final PlayerEntity player, final Hand hand) {
		final ItemStack stack = player.getStackInHand(hand);
		if (player.isSneaking()) {
			switchAOE(stack, cost, player);
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		TRItemUtils.checkActive(stack, cost, entity);
	}

	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
		TRItemUtils.buildActiveTooltip(stack, tooltip);
		if (TRItemUtils.isActive(stack)) {
			if (isAOE5(stack)) {
				tooltip.add(Text.literal("5*5").formatted(Formatting.RED));
			} else {
				tooltip.add(Text.literal("3*3").formatted(Formatting.RED));
			}
		}
	}

	// MultiBlockBreakingTool
	@Override
	public Set<BlockPos> getBlocksToBreak(ItemStack stack, World worldIn, BlockPos pos, @Nullable LivingEntity entityLiving) {
		if (!isCorrectForDrops(stack, worldIn.getBlockState(pos))) {
			return Collections.emptySet();
		}
		int radius = isAOE5(stack) ? 2 : 1;
		return ToolsUtil.getAOEMiningBlocks(worldIn, pos, entityLiving, radius, false)
			.stream()
			.filter((blockPos -> shouldBreak(worldIn, pos, blockPos)))
			.collect(Collectors.toSet());
	}
}
