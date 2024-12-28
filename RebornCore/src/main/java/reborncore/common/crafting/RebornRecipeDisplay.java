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

package reborncore.common.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;

public record RebornRecipeDisplay(SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {
	public static final MapCodec<RebornRecipeDisplay> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(SlotDisplay.CODEC.fieldOf("result").forGetter(RebornRecipeDisplay::result), SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(RebornRecipeDisplay::craftingStation)).apply(instance, RebornRecipeDisplay::new));
	public static final PacketCodec<RegistryByteBuf, RebornRecipeDisplay> PACKET_CODEC;
	public static final RecipeDisplay.Serializer<RebornRecipeDisplay> SERIALIZER;

	public RebornRecipeDisplay(SlotDisplay craftingStation) {
		// TODO unlockedItem
		this(new SlotDisplay.StackSlotDisplay(ItemStack.EMPTY), craftingStation);
	}

	@Override
	public Serializer<? extends RecipeDisplay> serializer() {
		return SERIALIZER;
	}

	static {
		PACKET_CODEC = PacketCodec.tuple(SlotDisplay.PACKET_CODEC, RebornRecipeDisplay::result, SlotDisplay.PACKET_CODEC, RebornRecipeDisplay::craftingStation, RebornRecipeDisplay::new);
		SERIALIZER = new RecipeDisplay.Serializer<>(CODEC, PACKET_CODEC);
	}
}
