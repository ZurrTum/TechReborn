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

package techreborn.items.tool.advanced;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import reborncore.common.misc.MultiBlockBreakingTool;
import reborncore.common.powerSystem.RcEnergyTier;
import techreborn.config.TechRebornConfig;
import techreborn.init.TRToolMaterials;
import techreborn.items.tool.JackhammerItem;
import techreborn.utils.TRItemUtils;
import techreborn.utils.ToolsUtil;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AdvancedJackhammerItem extends JackhammerItem implements MultiBlockBreakingTool {

	public AdvancedJackhammerItem(String name) {
		super(TRToolMaterials.ADVANCED_JACKHAMMER, TechRebornConfig.advancedJackhammerCharge, RcEnergyTier.EXTREME, TechRebornConfig.advancedJackhammerCost, name);
	}

	// JackhammerItem
	@Override
	public boolean postMine(ItemStack stack, World worldIn, BlockState stateIn, BlockPos pos, LivingEntity entityLiving) {
		// No AOE mining turned on OR we've broken a wrong block
		if (!TRItemUtils.isActive(stack) || !isCorrectForDrops(stack, stateIn)) {
			return super.postMine(stack, worldIn, stateIn, pos, entityLiving);
		}

		// Do AoE mining except original block
		for (BlockPos additionalPos : ToolsUtil.getAOEMiningBlocks(worldIn, pos, entityLiving, 1)) {
			if (shouldBreak(worldIn, pos, additionalPos)) {
				ToolsUtil.breakBlock(stack, worldIn, additionalPos, entityLiving, cost);
			}
		}

		// Do not forget to use energy for original block
		return super.postMine(stack, worldIn, stateIn, pos, entityLiving);
	}

	// Item
	@Override
	public ActionResult use(final World world, final PlayerEntity player, final Hand hand) {
		final ItemStack stack = player.getStackInHand(hand);
		if (player.isSneaking()) {
			TRItemUtils.switchActive(stack, cost, player);
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
	}

	// MultiBlockBreakingTool
	@Override
	public Set<BlockPos> getBlocksToBreak(ItemStack stack, World worldIn, BlockPos pos, @Nullable LivingEntity entityLiving) {
		if (!isCorrectForDrops(stack, worldIn.getBlockState(pos))) {
			return Collections.emptySet();
		}
		return ToolsUtil.getAOEMiningBlocks(worldIn, pos, entityLiving, 1, false)
			.stream()
			.filter((blockPos -> shouldBreak(worldIn, pos, blockPos)))
			.collect(Collectors.toSet());
	}
}
