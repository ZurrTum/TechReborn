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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import techreborn.init.TRItemSettings;

public class GpsItem extends Item {

	public GpsItem(String name) {
		super(TRItemSettings.item(name));
	}

	@Override
	public ActionResult use(World world, PlayerEntity player, Hand hand) {
		if (player instanceof ServerPlayerEntity serverPlayerEntity) {
			BlockPos pos = player.getBlockPos();
			serverPlayerEntity.sendMessage(Text.literal(" X:").formatted(Formatting.GRAY)
											.append(Text.literal(String.valueOf(pos.getX())).formatted(Formatting.GOLD))
											.append(Text.literal(" Y:").formatted(Formatting.GRAY))
											.append(Text.literal(String.valueOf(pos.getY())).formatted(Formatting.GOLD))
											.append(Text.literal(" Z:").formatted(Formatting.GRAY))
											.append(Text.literal(String.valueOf(pos.getZ())).formatted(Formatting.GOLD)), true);
			return ActionResult.SUCCESS;
		}
		return ActionResult.PASS;
	}
}
