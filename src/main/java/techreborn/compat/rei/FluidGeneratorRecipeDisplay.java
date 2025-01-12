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

package techreborn.compat.rei;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import techreborn.recipe.recipes.FluidGeneratorRecipe;

import java.util.List;
import java.util.Optional;

public class FluidGeneratorRecipeDisplay implements Display {
	public static final DisplaySerializer<FluidGeneratorRecipeDisplay> SERIALIZER = DisplaySerializer.of(
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			EntryIngredient.codec().listOf().fieldOf("inputs").forGetter(FluidGeneratorRecipeDisplay::getInputEntries),
			Codec.STRING.fieldOf("category").forGetter(d -> d.category.getIdentifier().toString()),
			Codec.INT.fieldOf("totalEnergy").forGetter(FluidGeneratorRecipeDisplay::getTotalEnergy)
		).apply(instance, FluidGeneratorRecipeDisplay::new)),
		PacketCodec.tuple(
			EntryIngredient.streamCodec().collect(PacketCodecs.toList()),
			FluidGeneratorRecipeDisplay::getInputEntries,
			PacketCodecs.STRING,
			d -> d.category.getIdentifier().toString(),
			PacketCodecs.INTEGER,
			FluidGeneratorRecipeDisplay::getTotalEnergy,
			FluidGeneratorRecipeDisplay::new
		)
	);

	private final List<EntryIngredient> inputs;
	private final CategoryIdentifier<?> category;
	private final int totalEnergy;

	public FluidGeneratorRecipeDisplay(List<EntryIngredient> inputs, String category, int totalEnergy) {
		this.inputs = inputs;
		this.category = CategoryIdentifier.of(category);
		this.totalEnergy = totalEnergy;
	}

	public FluidGeneratorRecipeDisplay(FluidGeneratorRecipe recipe, Identifier category) {
		this.category = CategoryIdentifier.of(category);
		this.inputs = Lists.newArrayList();
		this.totalEnergy = recipe.power() * 1000;
		inputs.add(EntryIngredients.of(recipe.getFluid(), 1000));
	}

	@Override
	public List<EntryIngredient> getInputEntries() {
		return inputs;
	}

	@Override
	public List<EntryIngredient> getOutputEntries() {
		return Lists.newArrayList();
	}

	@Override
	public CategoryIdentifier<?> getCategoryIdentifier() {
		return category;
	}

	@Override
	public Optional<Identifier> getDisplayLocation() {
		return Optional.empty();
	}

	@Override
	public DisplaySerializer<? extends Display> getSerializer() {
		return SERIALIZER;
	}

	public int getTotalEnergy() {
		return totalEnergy;
	}
}
