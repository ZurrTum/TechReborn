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

package reborncore.client.gui.config.elements;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import reborncore.client.gui.GuiBase;
import reborncore.client.gui.GuiSprites;
import reborncore.common.blockentity.SlotConfiguration;
import reborncore.common.screen.slot.BaseSlot;

import java.util.Arrays;

public class ConfigSlotElement extends ParentElement {
	private final SlotType type;
	private final Inventory inventory;
	private final int id;
	private final int height;

	public ConfigSlotElement(Inventory slotInventory,
							BaseSlot slot,
							SlotType type,
							int x,
							int y,
							GuiBase<?> gui,
							Runnable closeConfig) {
		super(x, y, type.getButtonSprite());
		this.type = type;
		this.inventory = slotInventory;
		this.id = slot.getIndex();

		SlotConfigPopupElement popupElement;

		boolean inputEnabled = slot.canWorldBlockInsert();
		boolean filterEnabled = gui.getMachine() instanceof SlotConfiguration.SlotFilter slotFilter
								&& Arrays.stream(slotFilter.getInputSlots()).anyMatch(value -> value == id);
		this.height = 90 + (inputEnabled ? 15 : 0) + (filterEnabled ? 15 : 0);

		elements.add(popupElement = new SlotConfigPopupElement(this.id, x - 22, y - 22, height, inputEnabled));
		elements.add(new ButtonElement(x + 37, y - 25, GuiSprites.EXIT_BUTTON, closeConfig));

		int checkboxY = y + 27;
		if (inputEnabled) {
			elements.add(new CheckBoxElement(Text.translatable("reborncore.gui.slotconfig.autoinput"), x - 26, checkboxY += 15,
				checkBoxElement ->  gui.getMachine().getSlotConfiguration().getSlotDetails(id).autoInput(),
				() -> popupElement.updateCheckBox("input", gui)));
		}

		elements.add(new CheckBoxElement(Text.translatable("reborncore.gui.slotconfig.autooutput"), x - 26, checkboxY += 15,
			checkBoxElement ->  gui.getMachine().getSlotConfiguration().getSlotDetails(id).autoOutput(),
			() -> popupElement.updateCheckBox("output", gui)));

		if (filterEnabled) {
			elements.add(new CheckBoxElement(Text.translatable("reborncore.gui.slotconfig.filter_input"), x - 26, checkboxY + 15,
				checkBoxElement ->  gui.getMachine().getSlotConfiguration().getSlotDetails(id).filter(),
				() -> popupElement.updateCheckBox("filter", gui)));
		}
	}

	@Override
	public int getWidth() {
		return 85;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void draw(DrawContext drawContext, GuiBase<?> gui, int mouseX, int mouseY) {
		ItemStack stack = inventory.getStack(id);
		int xPos = getX() + 1 + gui.getGuiLeft();
		int yPos = getY() + 1 + gui.getGuiTop();

		drawContext.drawItemInSlot(gui.getTextRenderer(), stack, xPos, yPos);

		if (isMouseWithinRect(gui, mouseX, mouseY)) {
			drawSprite(drawContext, gui, type.getButtonHoverOverlay(), getX(), getY());
		}
		super.draw(drawContext, gui, mouseX, mouseY);
	}


	public int getId() {
		return id;
	}
}
