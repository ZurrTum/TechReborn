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

package techreborn.client.render;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.GeometryHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;
import reborncore.client.RenderUtil;
import reborncore.common.util.Color;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class BaseDynamicFluidBakedModel implements BakedModel, FabricBakedModel {
	public Fluid fluid;
	public BakedModel baseModel;
	public BakedModel backgroundModel;
	public BakedModel fluidModel;

	BaseDynamicFluidBakedModel(Fluid fluid, BakedModel baseModel, BakedModel fluidModel, BakedModel backgroundModel) {
		this.fluid = fluid;
		this.baseModel = baseModel;
		this.backgroundModel = backgroundModel;
		this.fluidModel = fluidModel;
	}

	@Override
	public void emitItemQuads(QuadEmitter emitter, Supplier<Random> randomSupplier) {
		baseModel.emitItemQuads(emitter, randomSupplier);
		backgroundModel.emitItemQuads(emitter, randomSupplier);

		if (fluid == Fluids.EMPTY) {
			return;
		}

		FluidRenderHandler fluidRenderHandler = FluidRenderHandlerRegistry.INSTANCE.get(fluid);
		if (fluidRenderHandler == null) {
			// do nothing. Yet another broken fluid. Fix for #3290
			return;
		}

		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (player == null){
			return;
		}

		int fluidColor = fluidRenderHandler.getFluidColor(MinecraftClient.getInstance().world, player.getBlockPos(), fluid.getDefaultState());
		Sprite fluidSprite = fluidRenderHandler.getFluidSprites(MinecraftClient.getInstance().world, BlockPos.ORIGIN, fluid.getDefaultState())[0];

		int color = new Color((float) (fluidColor >> 16 & 255) / 255.0F, (float) (fluidColor >> 8 & 255) / 255.0F, (float) (fluidColor & 255) / 255.0F).getColor();

		emitter.pushTransform(quad -> {
			quad.nominalFace(GeometryHelper.lightFace(quad));
			quad.color(color, color, color, color);
			// Some modded fluids doesn't have sprites. Fix for #2429
			if (fluidSprite == null) {
				quad.spriteBake(RenderUtil.getSprite(Identifier.of("minecraft", "missingno")), MutableQuadView.BAKE_LOCK_UV);
			}
			else {
				quad.spriteBake(fluidSprite, MutableQuadView.BAKE_LOCK_UV);
			}

			return true;
		});
		fluidModel.getQuads(null, null, randomSupplier.get()).forEach(q -> {
			emitter.fromVanilla(q.getVertexData(), 0);
			emitter.emit();
		});
		emitter.popTransform();
	}

	@Override
	public void emitBlockQuads(QuadEmitter emitter, BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, Predicate<@Nullable Direction> cullTest) {

	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
		return Collections.emptyList();
	}

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public boolean useAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean hasDepth() {
		return false;
	}

	@Override
	public ModelTransformation getTransformation() {
		return ModelHelper.DEFAULT_ITEM_TRANSFORMS;
	}

	@Override
	public boolean isSideLit() {
		return false;
	}

}
