package me.NerdsWBNerds.TheWalls.Timers;

import me.NerdsWBNerds.TheWalls.TheWalls;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TPCount implements Runnable{
	int time = 1, id;
	Player player;
	Location loc;
	
	public TPCount(Player p, Location l) {
		player = p;
		loc = l;
	}

	@Override
	public void run(){
		if(!TheWalls.inGame(player)){
			if(player != null && player.isOnline() && !player.isDead() && player.getHealth() > 0 && loc != null){
				try{
					TheWalls.tele(player, loc);
				}catch(Exception e){};
			}
		}
		
		TheWalls.tps.remove(this);
	}
}
