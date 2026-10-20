package com.example.rightclickreplant;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class RightClickReplantClient implements ClientModInitializer {

	private static final Set<BlockPos> PENDING = new LinkedHashSet<>();

	@Override
	public void onInitializeClient() {
		UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
			AutoPlantConfig config = AutoPlantConfig.get();
			if (!config.enabled) {
				return InteractionResult.PASS;
			}
			if (!level.isClientSide() || hand != InteractionHand.MAIN_HAND) {
				return InteractionResult.PASS;
			}
			if (player.isSpectator() || player.isShiftKeyDown()) {
				return InteractionResult.PASS;
			}
			if (config.onlyWithEmptyHand && !player.getMainHandItem().isEmpty()) {
				return InteractionResult.PASS;
			}

			BlockPos pos = hitResult.getBlockPos();
			BlockState state = level.getBlockState(pos);
			if (seedFor(state) == null || !isMature(state)) {
				return InteractionResult.PASS;
			}

			PENDING.add(pos.immutable());
			return InteractionResult.SUCCESS;
		});

		// Processa fora do callback para evitar interações aninhadas
		ClientTickEvents.END_CLIENT_TICK.register(mc -> {
			if (PENDING.isEmpty()) {
				return;
			}
			List<BlockPos> work = new ArrayList<>(PENDING);
			PENDING.clear();
			for (BlockPos pos : work) {
				harvestAndReplant(mc, pos);
			}
		});
	}

	private static void harvestAndReplant(Minecraft mc, BlockPos pos) {
		LocalPlayer player = mc.player;
		MultiPlayerGameMode gameMode = mc.gameMode;
		ClientLevel level = mc.level;
		if (player == null || gameMode == null || level == null) {
			return;
		}

		BlockState state = level.getBlockState(pos);
		Item seed = seedFor(state);
		if (seed == null || !isMature(state)) {
			return;
		}
		if (!level.mayInteract(player, pos)) {
			return;
		}

		// Colher (culturas têm dureza 0, partem instantaneamente)
		gameMode.startDestroyBlock(pos, Direction.UP);

		// Replantar
		AutoPlantConfig config = AutoPlantConfig.get();
		if (!config.replantAutomatically) {
			return;
		}
		int slot = findHotbarSlot(player, seed);
		if (slot < 0) {
			return;
		}

		BlockPos soil = pos.below();
		Vec3 hit = new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
		BlockHitResult hitResult = new BlockHitResult(hit, Direction.UP, soil, false);

		int previousSlot = player.getInventory().getSelectedSlot();
		player.getInventory().setSelectedSlot(slot);
		gameMode.useItemOn(player, InteractionHand.MAIN_HAND, hitResult);
		player.getInventory().setSelectedSlot(previousSlot);
	}

	private static int findHotbarSlot(LocalPlayer player, Item item) {
		int current = player.getInventory().getSelectedSlot();
		if (player.getInventory().getItem(current).is(item)) {
			return current;
		}
		for (int i = 0; i < 9; i++) {
			if (player.getInventory().getItem(i).is(item)) {
				return i;
			}
		}
		return -1;
	}

	private static Item seedFor(BlockState state) {
		if (state.is(Blocks.WHEAT)) return Items.WHEAT_SEEDS;
		if (state.is(Blocks.CARROTS)) return Items.CARROT;
		if (state.is(Blocks.POTATOES)) return Items.POTATO;
		if (state.is(Blocks.BEETROOTS)) return Items.BEETROOT_SEEDS;
		if (state.is(Blocks.NETHER_WART)) return Items.NETHER_WART;
		return null;
	}

	private static boolean isMature(BlockState state) {
		if (state.getBlock() instanceof CropBlock crop) {
			return crop.isMaxAge(state);
		}
		if (state.is(Blocks.NETHER_WART)) {
			return state.getValue(NetherWartBlock.AGE) >= 3;
		}
		return false;
	}
}
