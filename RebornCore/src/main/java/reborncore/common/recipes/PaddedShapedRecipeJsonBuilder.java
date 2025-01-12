/*
 * This file is part of RebornCore, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2022 TeamReborn
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

package reborncore.common.recipes;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;

import java.util.Objects;

public class PaddedShapedRecipeJsonBuilder extends ShapedRecipeJsonBuilder {

	public PaddedShapedRecipeJsonBuilder(RegistryEntryLookup<Item> registryLookup, RecipeCategory category, ItemConvertible output, int outputCount) {
		super(registryLookup, category, output, outputCount);
	}

	public static PaddedShapedRecipeJsonBuilder create(RegistryEntryLookup<Item> registryLookup, RecipeCategory category, ItemConvertible output) {
		return create(registryLookup, category, output, 1);
	}

	public static PaddedShapedRecipeJsonBuilder create(RegistryEntryLookup<Item> registryLookup, RecipeCategory category, ItemConvertible output, int outputCount) {
		return new PaddedShapedRecipeJsonBuilder(registryLookup, category, output, outputCount);
	}
	@Override
	public void offerTo(RecipeExporter exporter, RegistryKey<Recipe<?>> recipeKey) {
		RawShapedRecipe raw = toRaw(recipeKey);

		AdvancementEntry advancementEntry = exporter.getAdvancementBuilder()
			.criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeKey))
			.rewards(AdvancementRewards.Builder.recipe(recipeKey))
			.criteriaMerger(AdvancementRequirements.CriterionMerger.OR)
			.build(recipeKey.getValue());

		PaddedShapedRecipe shapedRecipe = new PaddedShapedRecipe(
			Objects.requireNonNullElse(this.group, ""),
			CraftingRecipeJsonBuilder.toCraftingCategory(this.category),
			raw,
			new ItemStack(this.output, this.count),
			this.showNotification
		);

		exporter.accept(recipeKey, shapedRecipe, advancementEntry);
	}

	private RawShapedRecipe toRaw(RegistryKey<Recipe<?>> recipeKey) {
		if (this.criteria.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipeKey.getValue());
		} else {
			return PaddedShapedRecipe.create(this.inputs, this.pattern);
		}
	}
}
