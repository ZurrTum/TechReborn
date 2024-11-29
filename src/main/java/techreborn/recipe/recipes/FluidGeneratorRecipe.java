/*
 * This file is part of TechReborn, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2024 TechReborn
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

package techreborn.recipe.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.dynamic.Codecs;
import reborncore.common.crafting.RebornRecipe;
import reborncore.common.crafting.SizedIngredient;
import techreborn.init.ModRecipes;
import techreborn.init.TRContent;

import java.util.List;
import java.util.function.Function;

public record FluidGeneratorRecipe(RecipeType<? extends FluidGeneratorRecipe> type, int power, Fluid fluid) implements RebornRecipe {
	public static Function<RecipeType<FluidGeneratorRecipe>, MapCodec<FluidGeneratorRecipe>> CODEC = type -> RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codecs.POSITIVE_INT.fieldOf("power").forGetter(RebornRecipe::power),
		Registries.FLUID.getEntryCodec().fieldOf("fluid").forGetter(FluidGeneratorRecipe::fluidRegistryEntry)
	).apply(instance, (power, fluid) -> new FluidGeneratorRecipe(type, power, fluid)));
	public static Function<RecipeType<FluidGeneratorRecipe>, PacketCodec<RegistryByteBuf, FluidGeneratorRecipe>> PACKET_CODEC = type -> PacketCodec.tuple(
		PacketCodecs.INTEGER, RebornRecipe::power,
		PacketCodecs.registryEntry(RegistryKeys.FLUID), FluidGeneratorRecipe::fluidRegistryEntry,
		(power, fluid) -> new FluidGeneratorRecipe(type, power, fluid)
	);

	public FluidGeneratorRecipe(RecipeType<? extends FluidGeneratorRecipe> type, int power, RegistryEntry<Fluid> fluid) {
		this(type, power, fluid.value());
	}

	@Override
	public List<SizedIngredient> ingredients() {
		return List.of();
	}

	@Override
	public List<ItemStack> outputs() {
		return List.of();
	}

	@Override
	public int time() {
		return 0;
	}

//	@Override
//	public ItemStack createIcon() {
//		final RecipeType<?> type =getType();
//
//		if (type == ModRecipes.THERMAL_GENERATOR) {
//			return new ItemStack(TRContent.Machine.THERMAL_GENERATOR);
//		} else if (type == ModRecipes.GAS_GENERATOR) {
//			return new ItemStack(TRContent.Machine.GAS_TURBINE);
//		} else if (type == ModRecipes.DIESEL_GENERATOR) {
//			return new ItemStack(TRContent.Machine.DIESEL_GENERATOR);
//		} else if (type == ModRecipes.SEMI_FLUID_GENERATOR) {
//			return new ItemStack(TRContent.Machine.SEMI_FLUID_GENERATOR);
//		} else if (type == ModRecipes.PLASMA_GENERATOR) {
//			return new ItemStack(TRContent.Machine.PLASMA_GENERATOR);
//		}
//
//		return RebornRecipe.super.createIcon();
//	}

	public Fluid getFluid() {
		return fluid;
	}

	public RegistryEntry<Fluid> fluidRegistryEntry() {
		return getFluid().getRegistryEntry();
	}
}
