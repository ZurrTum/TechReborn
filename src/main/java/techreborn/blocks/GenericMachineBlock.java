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

package techreborn.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import reborncore.api.blockentity.IMachineGuiHandler;
import reborncore.common.blocks.BlockMachineBase;
import techreborn.init.TRBlockSettings;

import java.util.function.BiFunction;

/**
 * @author drcrazy
 */
public class GenericMachineBlock extends BlockMachineBase {

	private final IMachineGuiHandler gui;
	final BiFunction<BlockPos, BlockState, BlockEntity> blockEntityClass;

	public GenericMachineBlock(IMachineGuiHandler gui, BiFunction<BlockPos, BlockState, BlockEntity> blockEntityClass, String name) {
		super(TRBlockSettings.genericMachine(name));
		this.blockEntityClass = blockEntityClass;
		this.gui = gui;
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		if (blockEntityClass == null) {
			return null;
		}
		return blockEntityClass.apply(pos, state);
	}

	@Override
	public IMachineGuiHandler getGui() {
		return gui;
	}
}
