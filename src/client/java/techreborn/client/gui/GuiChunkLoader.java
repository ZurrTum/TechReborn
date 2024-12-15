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

package techreborn.client.gui;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import reborncore.client.ClientChunkManager;
import reborncore.client.gui.GuiBase;
import reborncore.client.gui.widget.GuiButtonUpDown;
import reborncore.client.gui.widget.GuiButtonUpDown.UpDownButtonType;
import reborncore.common.screen.BuiltScreenHandler;
import techreborn.blockentity.machine.tier3.ChunkLoaderBlockEntity;
import techreborn.packets.serverbound.ChunkloaderPayload;

public class GuiChunkLoader extends GuiBase<BuiltScreenHandler> {

	final ChunkLoaderBlockEntity blockEntity;

	public GuiChunkLoader(int syncID, PlayerEntity player, ChunkLoaderBlockEntity blockEntity) {
		super(player, blockEntity, blockEntity.createScreenHandler(syncID, player));
		this.blockEntity = blockEntity;
	}

	public void init() {
		super.init();
		addDrawableChild(new GuiButtonUpDown(x + 64, y + 40, this, b -> onClick(5), UpDownButtonType.FASTFORWARD));
		addDrawableChild(new GuiButtonUpDown(x + 64 + 12, y + 40, this, b -> onClick(1), UpDownButtonType.FORWARD));
		addDrawableChild(new GuiButtonUpDown(x + 64 + 24, y + 40, this, b -> onClick(-1), UpDownButtonType.REWIND));
		addDrawableChild(new GuiButtonUpDown(x + 64 + 36, y + 40, this, b -> onClick(-5), UpDownButtonType.FASTREWIND));

		addDrawableChild(
			ButtonWidget.builder(getToogleText(ClientChunkManager.isShow()), button -> {
				button.setMessage(getToogleText(!ClientChunkManager.isShow()));
				ClientChunkManager.toggleLoadedChunks(blockEntity.getPos());
			})
			.position(x + 10, y + 70)
			.size(155, 20)
			.build()
		);
	}

	private Text getToogleText(Boolean show) {
		if (show) {
			return Text.translatable("gui.techreborn.chunk.hide_loaded_chunks");
		} else {
			return Text.translatable("gui.techreborn.chunk.show_loaded_chunks");
		}
	}

	@Override
	protected void drawBackground(DrawContext drawContext, float partialTicks, int mouseX, int mouseY) {
		super.drawBackground(drawContext, partialTicks, mouseX, mouseY);
		final Layer layer = Layer.BACKGROUND;

		if (hideGuiElements()) return;

		Text text = Text.translatable("gui.techreborn.chunk.radius")
				.append(": ")
				.append(String.valueOf(blockEntity.getRadius()));
		drawCentredText(drawContext, text, 25, theme.titleColor().rgba(), layer);
	}

	public void onClick(int amount) {
		ClientPlayNetworking.send(new ChunkloaderPayload(blockEntity.getPos(), amount, ClientChunkManager.hasChunksForLoader(blockEntity.getPos())));
	}
}
