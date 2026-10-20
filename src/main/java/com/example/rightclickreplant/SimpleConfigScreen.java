package com.example.rightclickreplant;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SimpleConfigScreen extends Screen {
	private final Screen parent;
	private final AutoPlantConfig config;

	public SimpleConfigScreen(Screen parent) {
		super(Component.literal("Right Click Replant Config"));
		this.parent = parent;
		this.config = AutoPlantConfig.get();
	}

	@Override
	protected void init() {
		int y = this.height / 6;
		int buttonWidth = 200;
		int buttonHeight = 20;
		int x = this.width / 2 - buttonWidth / 2;

		this.addRenderableWidget(Button.builder(
				getEnabledText(),
				button -> {
					config.enabled = !config.enabled;
					config.save();
					button.setMessage(getEnabledText());
				})
			.bounds(x, y, buttonWidth, buttonHeight)
			.build());
		y += 24;

		this.addRenderableWidget(Button.builder(
				getReplantText(),
				button -> {
					config.replantAutomatically = !config.replantAutomatically;
					config.save();
					button.setMessage(getReplantText());
				})
			.bounds(x, y, buttonWidth, buttonHeight)
			.build());
		y += 24;

		this.addRenderableWidget(Button.builder(
				getEmptyHandText(),
				button -> {
					config.onlyWithEmptyHand = !config.onlyWithEmptyHand;
					config.save();
					button.setMessage(getEmptyHandText());
				})
			.bounds(x, y, buttonWidth, buttonHeight)
			.build());
		y += 32;

		this.addRenderableWidget(Button.builder(
				Component.literal("Done"),
				button -> {
					Minecraft mc = Minecraft.getInstance();
					if (mc != null) {
						mc.setScreen(parent);
					}
				})
			.bounds(x, y, buttonWidth, buttonHeight)
			.build());
	}

	private Component getEnabledText() {
		return Component.literal("Enabled: " + (config.enabled ? "ON" : "OFF"));
	}

	private Component getReplantText() {
		return Component.literal("Replant: " + (config.replantAutomatically ? "ON" : "OFF"));
	}

	private Component getEmptyHandText() {
		return Component.literal("Only empty hand: " + (config.onlyWithEmptyHand ? "ON" : "OFF"));
	}

	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
		super.render(poseStack, mouseX, mouseY, partialTick);
		this.font.drawShadow(poseStack, this.title, (float)(this.width / 2 - this.font.width(this.title) / 2), 20.0F, 0xFFFFFF);
	}
}
