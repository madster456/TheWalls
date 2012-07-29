package me.NerdsWBNerds.TheWalls;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class QueCount implements Runnable{
	int time = 61;
	int id = 0;
	
	TheWalls plugin;

	public QueCount(TheWalls l){
		plugin = l;
		
		time = 61;
	}
	
	@Override
	public void run() {
		if(time == 60){
			broadcast(ChatColor.GOLD + "[TheWalls] " + ChatColor.GREEN + "1 minute until next game.");
		}
		if(time == 45){
			broadcast(ChatColor.GOLD + "[TheWalls] " + ChatColor.GREEN + "45 seconds until next game.");
		}
		if(time == 30){
			broadcast(ChatColor.GOLD + "[TheWalls] " + ChatColor.GREEN + "30 seconds until next game.");
		}
		if(time == 15){
			broadcast(ChatColor.GOLD + "[TheWalls] " + ChatColor.GREEN + "15 seconds until next game.");
		}
		
		if(time <= 0){
			plugin.stopQueCount();
			
			if(TheWalls.getQue().size() >= TheWalls.min && TheWalls.getQue().size() < TheWalls.max){
				if(plugin.getNextGame() != null)
					plugin.getNextGame().startGame();
			}
		}
		
		time--;
	}
	
	public void broadcast(String m){
		for(Player p: TheWalls.getQue()){
			p.sendMessage(m);
		}
	}
}
