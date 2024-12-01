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

package techreborn.datagen.recipes.crafting

import net.minecraft.data.recipe.RecipeGenerator
import net.minecraft.data.recipe.ShapedRecipeJsonBuilder
import net.minecraft.item.Item
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.RegistryEntryLookup
import net.minecraft.registry.tag.TagKey

class ShapedRecipeFactory {
	public RecipeGenerator generator
	public RegistryEntryLookup<Item> itemLookup
	int width
	int height

	Object[] pattern
	ItemStack output
	RecipeCategory category = RecipeCategory.MISC

	ShapedRecipeFactory(RecipeGenerator generator, RegistryEntryLookup<Item> itemLookup, int width, int height) {
		this.generator = generator
		this.itemLookup = itemLookup
		this.width = width
		this.height = height
	}

	def _ = null

	def output(ItemStack output) {
		this.output = output
	}

	def category(RecipeCategory category) {
		this.category = category
	}

	def size(int width, int height) {
		this.width = width
		this.height = height
	}

	def pattern(Object... pattern) {
		if (this.pattern != null) {
			throw new IllegalStateException("Pattern already set")
		}

		for (def object : pattern) {
			if (object == _) continue
			toIngredient(object)
		}

		this.pattern = pattern
	}

	ShapedRecipeJsonBuilder build() {
		Objects.requireNonNull(output, "Output not set")
		Objects.requireNonNull(pattern, "Pattern not set")

		ShapedRecipeJsonBuilder builder = ShapedRecipeJsonBuilder.create(itemLookup, category, output.item, output.count)

		List<String> rows = []
		Map<Object, Character> ingredients = makeIngredients()

		def split = splitPattern()
		for (def row : split) {
			StringBuilder sb = new StringBuilder()
			for (def element : row) {
				if (element == null) {
					sb.append(' ')
				} else {
					sb.append(ingredients.get(element))
				}
			}
			rows.add(sb.toString())
		}

		rows.each { builder.pattern(it) }
		ingredients.each { builder.input(it.value, toIngredient(it.key)) }

		// TODO, this is just to make the validation pass
		builder.criterion(RecipeGenerator.hasItem(Items.AIR), generator.conditionsFromItem(Items.AIR))

		return builder
	}

	private Map<Object, Character> makeIngredients() {
		Map<Object, Character> ingredients = new HashMap<>()
		def inputs = pattern.toList().stream().distinct().toArray(Object[]::new)
		for (int i = 0; i < inputs.length; i++) {
			Object ingredient = inputs[i]
			if (ingredient != null) {
				char nextChar = (char)'A' + i
				ingredients.put(ingredient, nextChar)
			}
		}
		return ingredients
	}

	// Split the pattern into rows and columns
	// When the size is 3 x 3 the output will be 3 arrays with 3 elements each
	// When the size is 1 x 2 the output will be 1 array with 2 elements
	private Object[][] splitPattern() {
		if (pattern.size() != width * height) {
			throw new IllegalArgumentException("Pattern size does not match width and height")
		}

		return pattern.collate(width)
	}

	private toIngredient(Object object) {
		if (object instanceof Ingredient) {
			return object
		} else if (object instanceof ItemStack) {
			return Ingredient.ofStacks(object)
		} else if (object instanceof ItemConvertible) {
			return Ingredient.ofItems(object)
		} else if (object instanceof TagKey) {
			return Ingredient.fromTag(itemLookup.getOrThrow(object))
		} else {
			throw new IllegalArgumentException("Invalid pattern element: $object")
		}
	}
}
