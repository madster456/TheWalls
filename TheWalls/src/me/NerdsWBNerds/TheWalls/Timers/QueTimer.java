package me.NerdsWBNerds.TheWalls.Timers;

import me.NerdsWBNerds.TheWalls.TheWalls;
import me.NerdsWBNerds.TheWalls.Objects.Team;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class QueTimer implements Runnable{
	public int time = 61;
	public int id = 0;
	
	public QueTimer(int t){
		time = t;
	}
	
	public QueTimer() {
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
			TheWalls.stopQueCount();
			
			if(TheWalls.getQue().size() >= TheWalls.min){
				if(TheWalls.getNextGame() != null){
					TheWalls.getNextGame().startGame();
					TheWalls.checkQue();
				}
			}
		}
		
		time--;
	}
	
	public void broadcast(String m){
		for(Team t: TheWalls.getQue()){
			for(Player p: t.team)
				p.sendMessage(m);
		}
	}
}
