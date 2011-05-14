package com.afforess.growbie;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

public class GrowbiePlayerListener extends PlayerListener {

	public GrowbiePlayerListener() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		
		if (!Gardener.isBonemeal(player.getItemInHand())) {
			return;
		}
		
		//System.out.println("Growbie action detected, attempting growth.");
		
		int action = Gardener.growPlants(block);
		if (action == 0) {
			action = Gardener.growBlocks(block);
		}
		if (action == 0) {
			action = Gardener.spreadBlocks(block);
		}

		if (action == 2 ) {
			if (GrowbieConfiguration.getConsumeOnFail()) {
				action = 1;
			}
		}
		
		//System.out.println("Growbie Action is: "+ action);
		
		if (action == 1){
			Gardener.useItem(player);
			event.setCancelled(true);
			event.setUseInteractedBlock(Result.DENY);
			event.setUseItemInHand(Result.DENY);
		}
	}
}
