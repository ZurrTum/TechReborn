/*
 * This file is part of TechReborn, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2023 TechReborn
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

package techreborn.init;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.sound.BlockSoundGroup;

public class TRBlockSettings {
	private static AbstractBlock.Settings metal() {
		return AbstractBlock.Settings.create()
			.sounds(BlockSoundGroup.METAL)
			.mapColor(MapColor.IRON_GRAY)
			.strength(2f, 2f);
	}

	public static AbstractBlock.Settings machine() {
		return metal();
	}

	public static AbstractBlock.Settings nuke() {
		return AbstractBlock.Settings.create()
			.strength(2F, 2F)
			.mapColor(MapColor.BRIGHT_RED);
	}

	public static AbstractBlock.Settings reinforcedGlass() {
		return AbstractBlock.Settings.copy(Blocks.GLASS)
			.strength(4f, 60f)
			.sounds(BlockSoundGroup.STONE);
	}

	private static AbstractBlock.Settings rubber(boolean noCollision, float hardness, float resistance) {
		var settings = AbstractBlock.Settings.create()
			.mapColor(MapColor.SPRUCE_BROWN)
			.strength(hardness, resistance)
			.sounds(BlockSoundGroup.WOOD);

		if (noCollision) {
			settings.noCollision();
		}

		return settings;
	}

	private static AbstractBlock.Settings rubber(float hardness, float resistance) {
		return rubber(false, hardness, resistance);
	}

	public static AbstractBlock.Settings rubberWood() {
		return rubber(2f, 2f)
			.burnable();
	}

	public static AbstractBlock.Settings rubberWoodStripped() {
		return rubberWood()
			.strength(2.0F, 15.0F);
	}

	public static AbstractBlock.Settings rubberLeaves() {
		return AbstractBlock.Settings.copy(Blocks.SPRUCE_LEAVES)
			.mapColor(MapColor.SPRUCE_BROWN);
	}

	public static AbstractBlock.Settings rubberSapling() {
		return AbstractBlock.Settings.copy(Blocks.SPRUCE_SAPLING)
			.mapColor(MapColor.SPRUCE_BROWN);
	}

	public static AbstractBlock.Settings rubberLog() {
		return AbstractBlock.Settings.copy(Blocks.SPRUCE_LOG)
			.ticksRandomly()
			.mapColor(MapColor.SPRUCE_BROWN);
	}

	public static AbstractBlock.Settings rubberLogStripped() {
		return rubberLog().strength(2.0F, 15.0F);
	}

	public static AbstractBlock.Settings rubberSlab() {
		return rubberLog();
	}

	public static AbstractBlock.Settings rubberFence() {
		return rubberLog();
	}

	public static AbstractBlock.Settings rubberFenceGate() {
		return rubberLog();
	}

	public static AbstractBlock.Settings pottedRubberSapling() {
		return AbstractBlock.Settings.copy(Blocks.POTTED_SPRUCE_SAPLING);
	}

	public static AbstractBlock.Settings copperWall() {
		return AbstractBlock.Settings.copy(Blocks.COPPER_BLOCK)
			.strength(2f, 2f);
	}

	public static AbstractBlock.Settings rubberTrapdoor() {
		return rubber(3.0F, 3.0F);
	}

	public static AbstractBlock.Settings rubberDoor() {
		return rubber(3.0F, 3.0F);
	}

	public static AbstractBlock.Settings rubberButton() {
		return rubber(true, 0.5F, 0.5F);
	}

	public static AbstractBlock.Settings rubberPressurePlate() {
		return rubber(true, 0.5F, 0.5F);
	}

	public static AbstractBlock.Settings refinedIronFence() {
		return metal()
			.strength(2.0F, 3.0F);
	}

	public static AbstractBlock.Settings storageBlock(boolean isHot, float hardness, float resistance) {
		AbstractBlock.Settings settings = AbstractBlock.Settings.create()
			.strength(hardness, resistance)
			.mapColor(MapColor.IRON_GRAY) // TODO 1.20 maybe set the color based off the block?
			.sounds(BlockSoundGroup.METAL);

		if (isHot) {
			settings = settings.luminance(state -> 15)
				.nonOpaque();
		}

		return settings;
	}

	public static AbstractBlock.Settings ore(boolean deepslate) {
		return AbstractBlock.Settings.create()
			.requiresTool()
			.sounds(deepslate ? BlockSoundGroup.DEEPSLATE : BlockSoundGroup.STONE)
			.hardness(deepslate ? 4.5f : 3f)
			.resistance(3f);
	}

	public static AbstractBlock.Settings machineFrame() {
		return metal()
			.strength(1f, 1f);
	}

	public static AbstractBlock.Settings machineCasing() {
		return metal()
			.strength(2f, 2f)
			.requiresTool();
	}

	public static AbstractBlock.Settings energyStorage() {
		return metal();
	}

	public static AbstractBlock.Settings lsuStorage() {
		return metal();
	}

	public static AbstractBlock.Settings storageUnit(boolean wooden) {
		if (!wooden) {
			return metal();
		}

		return AbstractBlock.Settings.create()
			.sounds(BlockSoundGroup.WOOD)
			.mapColor(MapColor.OAK_TAN)
			.strength(2f, 2f);
	}

	public static AbstractBlock.Settings fusionCoil() {
		return metal();
	}

	public static AbstractBlock.Settings transformer() {
		return metal();
	}

	public static AbstractBlock.Settings playerDetector() {
		return metal();
	}

	public static AbstractBlock.Settings fluid() {
		return AbstractBlock.Settings.copy(Blocks.WATER);
	}

	public static AbstractBlock.Settings computerCube() {
		return metal();
	}

	public static AbstractBlock.Settings alarm() {
		return metal();
	}

	public static AbstractBlock.Settings genericMachine() {
		return metal();
	}

	public static AbstractBlock.Settings tankUnit() {
		return metal();
	}

	public static AbstractBlock.Settings fusionControlComputer() {
		return metal();
	}

	public static AbstractBlock.Settings solarPanel() {
		return metal();
	}

	public static AbstractBlock.Settings cable() {
		return metal().strength(1f, 8f);
	}

	public static AbstractBlock.Settings resinBasin() {
		return AbstractBlock.Settings.create()
			.mapColor(MapColor.OAK_TAN)
			.sounds(BlockSoundGroup.WOOD)
			.strength(2F, 2F);
	}

	public static AbstractBlock.Settings lightBlock() {
		return AbstractBlock.Settings.copy(Blocks.REDSTONE_BLOCK)
			.strength(2f, 2f);
	}
}
