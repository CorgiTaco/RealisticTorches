package com.chaosthedude.realistictorches.items;

import com.chaosthedude.realistictorches.config.ConfigHandler;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class MatchboxItem extends Item {

	public static final String NAME = "matchbox";

	public MatchboxItem(int maxDamage) {
		super(new Item.Properties().group(ItemGroup.TOOLS).maxDamage(maxDamage).defaultMaxDamage(maxDamage));
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		if (!ConfigHandler.matchboxCreatesFire.get()) {
			return ActionResultType.FAIL;
		}

		PlayerEntity playerentity = context.getPlayer();
		World world = context.getWorld();
		BlockPos blockpos = context.getPos();
		BlockState blockstate = world.getBlockState(blockpos);
		if (CampfireBlock.canBeLit(blockstate)) {
			world.playSound(playerentity, blockpos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
			world.setBlockState(blockpos, blockstate.with(BlockStateProperties.LIT, Boolean.TRUE), 11);
			if (playerentity != null) {
				context.getItem().damageItem(1, playerentity, (player) -> {
					player.sendBreakAnimation(context.getHand());
				});
			}

			return ActionResultType.func_233537_a_(world.isRemote());
		} else {
			BlockPos blockpos1 = blockpos.offset(context.getFace());
			if (AbstractFireBlock.canLightBlock(world, blockpos1, context.getPlacementHorizontalFacing())) {
				world.playSound(playerentity, blockpos1, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
				BlockState blockstate1 = AbstractFireBlock.getFireForPlacement(world, blockpos1);
				world.setBlockState(blockpos1, blockstate1, 11);
				ItemStack itemstack = context.getItem();
				if (playerentity instanceof ServerPlayerEntity) {
					CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)playerentity, blockpos1, itemstack);
					itemstack.damageItem(1, playerentity, (player) -> {
						player.sendBreakAnimation(context.getHand());
					});
				}

				return ActionResultType.func_233537_a_(world.isRemote());
			} else {
				return ActionResultType.FAIL;
			}
		}
	}
}
