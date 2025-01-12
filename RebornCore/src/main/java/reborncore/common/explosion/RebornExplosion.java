/*
 * This file is part of RebornCore, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2021 TeamReborn
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

package reborncore.common.explosion;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.explosion.ExplosionImpl;
import org.apache.commons.lang3.time.StopWatch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reborncore.RebornCore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by modmuss50 on 12/03/2016.
 */
public class RebornExplosion extends ExplosionImpl {
	final BlockPos center;
	final int radius;

	@Nullable
	LivingEntity livingBase;

	public RebornExplosion(@NotNull BlockPos center, @NotNull ServerWorld world, int radius) {
		super(world, null, null, null, center.toCenterPos(), radius, false, DestructionType.DESTROY);
		this.center = center;
		this.radius = radius;
	}

	public void setLivingBase(@Nullable LivingEntity livingBase) {
		this.livingBase = livingBase;
	}

	@Nullable
	public LivingEntity getLivingBase() {
		return livingBase;
	}

	@Override
	public void explode() {
		StopWatch watch = new StopWatch();
		watch.start();
		for (int tx = -radius; tx < radius + 1; tx++) {
			for (int ty = -radius; ty < radius + 1; ty++) {
				for (int tz = -radius; tz < radius + 1; tz++) {
					if (Math.sqrt(Math.pow(tx, 2) + Math.pow(ty, 2) + Math.pow(tz, 2)) <= radius - 2) {
						BlockPos pos = center.add(tx, ty, tz);
						BlockState state = getWorld().getBlockState(pos);
						Block block = state.getBlock();
						if (block != Blocks.BEDROCK && !state.isAir()) {
							block.onDestroyedByExplosion(getWorld(), pos, this);
							getWorld().setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
						}
					}
				}
			}
		}
		RebornCore.LOGGER.info("The explosion took" + watch + " to explode");
	}

	@Nullable
	@Override
	public LivingEntity getCausingEntity() {
		return livingBase;
	}

	@Override
	public List<BlockPos> getBlocksToDestroy() {
		List<BlockPos> poses = new ArrayList<>();
		for (int tx = -radius; tx < radius + 1; tx++) {
			for (int ty = -radius; ty < radius + 1; ty++) {
				for (int tz = -radius; tz < radius + 1; tz++) {
					if (Math.sqrt(Math.pow(tx, 2) + Math.pow(ty, 2) + Math.pow(tz, 2)) <= radius - 2) {
						BlockPos pos = center.add(tx, ty, tz);
						BlockState state = getWorld().getBlockState(pos);
						Block block = state.getBlock();
						if (block != Blocks.BEDROCK && !state.isAir()) {
							poses.add(pos);
						}
					}
				}
			}
		}
		return poses;
	}
}
