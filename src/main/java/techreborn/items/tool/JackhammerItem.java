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
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import reborncore.common.powerSystem.RcEnergyItem;
import reborncore.common.powerSystem.RcEnergyTier;
import reborncore.common.util.ItemUtils;
import techreborn.init.TRContent;
import techreborn.init.TRItemSettings;


public class JackhammerItem extends PickaxeItem implements RcEnergyItem {
	public final int maxCharge;
	public final RcEnergyTier tier;
	public final int cost;
	protected final float unpoweredSpeed = 0.5F;

	public JackhammerItem(ToolMaterial material, int energyCapacity, RcEnergyTier tier, int cost, String name) {
		super(material, -2f, -2.8f, TRItemSettings.item(name).maxDamage(0));
		this.maxCharge = energyCapacity;
		this.tier = tier;
		this.cost = cost;
	}

	/**
	 * Checks if block in additional BlockPos should be broken. Used for AOE mining.
	 *
	 * @param worldIn     World reference
	 * @param originalPos Original mined block
	 * @param pos         Additional block to check
	 * @return Returns true if block should be broken by AOE mining
	 */
	protected boolean shouldBreak(World worldIn, BlockPos originalPos, BlockPos pos) {
		if (originalPos.equals(pos)) {
			return false;
		}
		return worldIn.getBlockState(pos).isIn(TRContent.BlockTags.JACKHAMMER_MINEABLE);
	}

	// MiningToolItem
	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
			return true;
		}

	// Item
	@Override
	public float getMiningSpeed(ItemStack stack, BlockState state) {
		if (getStoredEnergy(stack) >= cost && state.isIn(TRContent.BlockTags.JACKHAMMER_MINEABLE)) {
			return super.getMiningSpeed(stack, state);
		}
		return unpoweredSpeed;
	}

	@Override
	public boolean postMine(ItemStack stack, World worldIn, BlockState blockIn, BlockPos pos, LivingEntity entityLiving) {
		tryUseEnergy(stack, cost);
		return true;
	}

	@Override
	public int getItemBarStep(ItemStack stack) {
		return ItemUtils.getPowerForDurabilityBar(stack);
	}

	@Override
	public boolean isItemBarVisible(ItemStack stack) {
		return true;
	}

	@Override
	public int getItemBarColor(ItemStack stack) {
		return ItemUtils.getColorForDurabilityBar(stack);
	}

	// RcEnergyItem
	@Override
	public long getEnergyCapacity(ItemStack stack) {
		return maxCharge;
	}

	@Override
	public RcEnergyTier getTier() {
		return tier;
	}

	@Override
	public long getEnergyMaxOutput(ItemStack stack) {
		return 0;
	}

}
