package me.NerdsWBNerds.TheWalls;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LoginTP implements Runnable{
	int time = 1, id;
	Player player;
	Location loc;
	
	public LoginTP(Player p, Location l) {
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
