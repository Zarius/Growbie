package com.afforess.growbie;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * A utility class for handling Growbie events
 */
public abstract class Gardener {
	
	public static boolean isBonemeal(ItemStack item) {
		return item.getType() == Material.INK_SACK && item.getDurability() == 15;
	}
	
	public static int growPlants(Block block) {
		int didGrow = 0;
		if (GrowbieConfiguration.isGrowablePlant(block.getType())) {
			
			int chance = GrowbieConfiguration.getGrowablePlantsSuccessChance();
			if ((new Random()).nextInt(100) > chance) {
				return 2;
			}
			
			int plantsToGrow = GrowbieConfiguration.plantGrowthRate(block.getType());

			//Populate list of suitable blocks adjacent to us
			ArrayList<Block> growInBlocks = new ArrayList<Block>(27);
			int range = 2;
			for (int dx = -(range); dx <= range; dx++){
				for (int dy = -(range); dy <= range; dy++){
					for (int dz = -(range); dz <= range; dz++){
						growInBlocks.add(block.getRelative(dx, dy, dz));
					}
				}
			}

			while (plantsToGrow > 0 && !growInBlocks.isEmpty()) {
				// get a random block from the list
				int i = Math.round((float)Math.random() * (growInBlocks.size()-1));
				Block growBlock = growInBlocks.get(i);
				growInBlocks.remove(i);
				
				if (GrowbieConfiguration.canGrowPlantOnBlock(growBlock)) {
					// grow plant
					growBlock.setType(block.getType());
					didGrow = 1;
					plantsToGrow--;
				}
			}
		}
		return didGrow;
	}
	
	public static int growBlocks(Block block) {
		int didGrow = 0;
		if (GrowbieConfiguration.isGrowableBlock(block.getType())) {
			
			int chance = GrowbieConfiguration.getGrowableBlocksSuccessChance();
			if ((new Random()).nextInt(100) > chance) {
				return 2;
			}
			
			//Leaves is a special case (really just a special case of me abusing the config file, but whatever)
			if (block.getType().equals(Material.LOG) && GrowbieConfiguration.blockForGrowableBlock(block.getType()).equals(Material.LEAVES)) {
				int range = 1;
				for (int dx = -(range); dx <= range; dx++){
					for (int dy = -(range); dy <= range; dy++){
						for (int dz = -(range); dz <= range; dz++){
							Block loop = block.getRelative(dx, dy, dz);
							if (loop.getTypeId() == Material.AIR.getId()) {
								loop.setType(GrowbieConfiguration.blockForGrowableBlock(block.getType()));
								didGrow = 1;
							}
						}
					}
				}
			}
			// if the target is grass and no air above, do not do
			else if(block.getRelative(BlockFace.UP).getType() == Material.AIR || GrowbieConfiguration.blockForGrowableBlock(block.getType()) != Material.GRASS) {
				// transform block
				block.setType(GrowbieConfiguration.blockForGrowableBlock(block.getType()));
				didGrow = 1;
			}
		}
		return didGrow;
	}
	
	public static int spreadBlocks(Block block) {
		int didGrow = 0;
		if (GrowbieConfiguration.isSpreadableBlock(block.getType())) {
			
			int chance = GrowbieConfiguration.getSpreadableBlocksSuccessChance();
			if ((new Random()).nextInt(100) > chance) {
				return 2;
			}

			// Let's loop over three surrounding dimensions
			int range = 1;
			for (int dx = -(range); dx <= range; dx++){
				for (int dy = -(range); dy <= range; dy++){
					for (int dz = -(range); dz <= range; dz++){
						Block loop = block.getRelative(dx, dy, dz);
						if (loop.getType() == GrowbieConfiguration.blockForSpreadableBlock(block.getType())) {
							// Special check for grass - only grow if air on block above
							if(block.getType() == Material.GRASS && loop.getRelative(BlockFace.UP).getType() != Material.AIR) {
								continue;
							}
							loop.setType(block.getType());
							didGrow = 1;
						}
					}
				}
			}
		}
		return didGrow;
	}
	
	public static void useItem(Player player) {
		/* Make the bone meal decrease on use, as it normally would. */
		int amt = player.getItemInHand().getAmount();
		if (amt > 1) {
			--amt;
			player.getItemInHand().setAmount(amt);
		} else {
			player.getInventory().remove(player.getItemInHand());
		}
	}

}
