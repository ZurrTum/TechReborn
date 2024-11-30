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
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import techreborn.TechReborn;

public class TRBlockSettings {
	public static RegistryKey<Block> getRegistryKey(String name) {
		return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(TechReborn.MOD_ID, name));
	}

	private static AbstractBlock.Settings metal(String name) {
		return AbstractBlock.Settings.create()
			.registryKey(getRegistryKey(name))
			.sounds(BlockSoundGroup.METAL)
			.mapColor(MapColor.IRON_GRAY)
			.strength(2f, 2f);
	}

	public static AbstractBlock.Settings machine() {
		return metal("machine");
	}

	public static AbstractBlock.Settings nuke(String name) {
		return AbstractBlock.Settings.create()
			.registryKey(getRegistryKey(name))
			.strength(2F, 2F)
			.mapColor(MapColor.BRIGHT_RED);
	}

	public static AbstractBlock.Settings reinforcedGlass(String name) {
		return AbstractBlock.Settings.copy(Blocks.GLASS)
			.registryKey(getRegistryKey(name))
			.strength(4f, 60f)
			.sounds(BlockSoundGroup.STONE);
	}

	private static AbstractBlock.Settings rubber(String name, boolean noCollision, float hardness, float resistance) {
		var settings = AbstractBlock.Settings.create()
			.registryKey(getRegistryKey(name))
			.mapColor(MapColor.SPRUCE_BROWN)
			.strength(hardness, resistance)
			.sounds(BlockSoundGroup.WOOD);

		if (noCollision) {
			settings.noCollision();
		}

		return settings;
	}

	private static AbstractBlock.Settings rubber(String name, float hardness, float resistance) {
		return rubber(name, false, hardness, resistance);
	}

	public static AbstractBlock.Settings rubberWood(String name) {
		return rubber(name, 2f, 2f)
			.burnable();
	}

	public static AbstractBlock.Settings rubberWoodStripped(String name) {
		return rubberWood(name)
			.strength(2.0F, 15.0F);
	}

	public static AbstractBlock.Settings rubberLeaves(String name) {
		return AbstractBlock.Settings.copy(Blocks.SPRUCE_LEAVES)
			.registryKey(getRegistryKey(name))
			.mapColor(MapColor.SPRUCE_BROWN);
	}

	public static AbstractBlock.Settings rubberSapling(String name) {
		return AbstractBlock.Settings.copy(Blocks.SPRUCE_SAPLING)
			.registryKey(getRegistryKey(name))
			.mapColor(MapColor.SPRUCE_BROWN);
	}

	public static AbstractBlock.Settings rubberLog(String name) {
		return AbstractBlock.Settings.copy(Blocks.SPRUCE_LOG)
			.registryKey(getRegistryKey(name))
			.ticksRandomly()
			.mapColor(MapColor.SPRUCE_BROWN);
	}

	public static AbstractBlock.Settings rubberLogStripped(String name) {
		return rubberLog(name).strength(2.0F, 15.0F);
	}

	public static AbstractBlock.Settings rubberSlab(String name) {
		return rubberLog(name);
	}

	public static AbstractBlock.Settings rubberFence(String name) {
		return rubberLog(name);
	}

	public static AbstractBlock.Settings rubberFenceGate(String name) {
		return rubberLog(name);
	}

	public static AbstractBlock.Settings pottedRubberSapling(String name) {
		return AbstractBlock.Settings.copy(Blocks.POTTED_SPRUCE_SAPLING).registryKey(getRegistryKey(name));
	}

	public static AbstractBlock.Settings copperWall(String name) {
		return AbstractBlock.Settings.copy(Blocks.COPPER_BLOCK)
			.registryKey(getRegistryKey(name))
			.strength(2f, 2f);
	}

	public static AbstractBlock.Settings rubberTrapdoor(String name) {
		return rubber(name, 3.0F, 3.0F);
	}

	public static AbstractBlock.Settings rubberDoor(String name) {
		return rubber(name, 3.0F, 3.0F);
	}

	public static AbstractBlock.Settings rubberButton(String name) {
		return rubber(name, true, 0.5F, 0.5F);
	}

	public static AbstractBlock.Settings rubberPressurePlate(String name) {
		return rubber(name, true, 0.5F, 0.5F);
	}

	public static AbstractBlock.Settings refinedIronFence(String name) {
		return metal(name)
			.strength(2.0F, 3.0F);
	}

	public static AbstractBlock.Settings storageBlock(String name, boolean isHot, float hardness, float resistance) {
		AbstractBlock.Settings settings = AbstractBlock.Settings.create()
			.registryKey(getRegistryKey(name))
			.strength(hardness, resistance)
			.mapColor(MapColor.IRON_GRAY) // TODO 1.20 maybe set the color based off the block?
			.sounds(BlockSoundGroup.METAL);

		if (isHot) {
			settings = settings.luminance(state -> 15)
				.nonOpaque();
		}

		return settings;
	}

	public static AbstractBlock.Settings ore(String name) {
		boolean deepslate = name.startsWith("deepslate");
		return AbstractBlock.Settings.create()
			.registryKey(getRegistryKey(name))
			.requiresTool()
			.sounds(deepslate ? BlockSoundGroup.DEEPSLATE : BlockSoundGroup.STONE)
			.hardness(deepslate ? 4.5f : 3f)
			.resistance(3f);
	}

	public static AbstractBlock.Settings machineFrame(String name) {
		return metal(name)
			.strength(1f, 1f);
	}

	public static AbstractBlock.Settings machineCasing(String name) {
		return metal(name)
			.strength(2f, 2f)
			.requiresTool();
	}

	public static AbstractBlock.Settings energyStorage(String name) {
		return metal(name);
	}

	public static AbstractBlock.Settings lsuStorage(String name) {
		return metal(name);
	}

	public static AbstractBlock.Settings storageUnit(String name, boolean wooden) {
		if (!wooden) {
			return metal(name);
		}

		return AbstractBlock.Settings.create()
			.registryKey(getRegistryKey(name))
			.sounds(BlockSoundGroup.WOOD)
			.mapColor(MapColor.OAK_TAN)
			.strength(2f, 2f);
	}

	public static AbstractBlock.Settings fusionCoil(String name) {
		return metal(name);
	}

	public static AbstractBlock.Settings transformer(String name) {
		return metal(name);
	}

	public static AbstractBlock.Settings playerDetector(String name) {
		return metal(name);
	}

	public static AbstractBlock.Settings fluid(String name) {
		return AbstractBlock.Settings.copy(Blocks.WATER).registryKey(getRegistryKey(name));
	}

	public static AbstractBlock.Settings computerCube(String name) {
		return metal(name);
	}

	public static AbstractBlock.Settings alarm(String name) {
		return metal(name);
	}

	public static AbstractBlock.Settings genericMachine(String name) {
		return metal(name);
	}

	public static AbstractBlock.Settings tankUnit(String name) {
		return metal(name);
	}

	public static AbstractBlock.Settings fusionControlComputer(String name) {
		return metal(name);
	}

	public static AbstractBlock.Settings solarPanel(String name) {
		return metal(name);
	}

	public static AbstractBlock.Settings cable(String name) {
		return metal(name).strength(1f, 8f);
	}

	public static AbstractBlock.Settings resinBasin(String name) {
		return AbstractBlock.Settings.create()
			.registryKey(getRegistryKey(name))
			.mapColor(MapColor.OAK_TAN)
			.sounds(BlockSoundGroup.WOOD)
			.strength(2F, 2F);
	}

	public static AbstractBlock.Settings lightBlock(String name) {
		return AbstractBlock.Settings.copy(Blocks.REDSTONE_BLOCK)
			.registryKey(getRegistryKey(name))
			.strength(2f, 2f);
	}
}
