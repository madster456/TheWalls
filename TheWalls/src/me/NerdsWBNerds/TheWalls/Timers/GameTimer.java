package me.NerdsWBNerds.TheWalls.Timers;

import me.NerdsWBNerds.TheWalls.TheWalls;
import me.NerdsWBNerds.TheWalls.Objects.WallsGame;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GameTimer implements Runnable{
	public int time = 60 * 10;
	public int id = 0;
	
	WallsGame game;
	public boolean inProg = false;

	public GameTimer(WallsGame l){
		game = l;
		time = 60 * TheWalls.gameLength;
	}

	public GameTimer(WallsGame l, int t){
		game = l;
		time = t * 60;
	}
	
	@Override
	public void run() {
		inProg = true;

		if(time % (60 * 15) == 0){
			broadcast(ChatColor.GOLD + "[TheWalls] " + ChatColor.AQUA + (time / (60)) + ChatColor.GREEN + " minutes remaining.");
		}
		if(time == 60 * 10){
			broadcast(ChatColor.GOLD + "[TheWalls] " + ChatColor.AQUA + "10" + ChatColor.GREEN + " minutes remaining.");
		}
		if(time == 60 * 5){
			broadcast(ChatColor.GOLD + "[TheWalls] " + ChatColor.AQUA + "5" + ChatColor.GREEN + " minutes remaining.");
		}
		if(time == 60 * 4){
			broadcast(ChatColor.GOLD + "[TheWalls] " + ChatColor.AQUA + "4" + ChatColor.GREEN + " minutes remaining.");
		}
		if(time == 60 * 3){
			broadcast(ChatColor.GOLD + "[TheWalls] " + ChatColor.AQUA + "3" + ChatColor.GREEN + " minutes remaining.");
		}
		if(time == 60 * 2){
			broadcast(ChatColor.GOLD + "[TheWalls] " + ChatColor.AQUA + "2" + ChatColor.GREEN + " minutes remaining.");
		}
		if(time == 60 * 1){
			broadcast(ChatColor.GOLD + "[TheWalls] " + ChatColor.AQUA + "1" + ChatColor.GREEN + " minute remaining.");
		}
		if(time == 0){
			broadcast(ChatColor.GOLD + "[TheWalls] " + ChatColor.GREEN + "The Walls have been removed! Let the PvP begin!");
			broadcast(ChatColor.GOLD + "[TheWalls] " + ChatColor.GREEN + "10 minutes until deathmatch.");
			game.startPvP();
		}

		if(time == 60 * -(TheWalls.minTillDeathmatch - 15)){
			broadcast(ChatColor.GOLD + "[TheWalls] " + ChatColor.AQUA + "15" + ChatColor.GREEN + " minutes until deathmatch.");
		}
		if(time == 60 * -(TheWalls.minTillDeathmatch - 5)){
			broadcast(ChatColor.GOLD + "[TheWalls] " + ChatColor.AQUA + "5" + ChatColor.GREEN + " minutes until deathmatch.");
		}
		if(time == 60 * -(TheWalls.minTillDeathmatch - 4)){
			broadcast(ChatColor.GOLD + "[TheWalls] " + ChatColor.AQUA + "4" + ChatColor.GREEN + " minutes until deathmatch.");
		}
		if(time == 60 * -(TheWalls.minTillDeathmatch - 3)){
			broadcast(ChatColor.GOLD + "[TheWalls] " + ChatColor.AQUA + "3" + ChatColor.GREEN + " minutes until deathmatch.");
		}
		if(time == 60 * -(TheWalls.minTillDeathmatch - 2)){
			broadcast(ChatColor.GOLD + "[TheWalls] " + ChatColor.AQUA + "2" + ChatColor.GREEN + " minutes until deathmatch.");
		}
		if(time == 60 * -(TheWalls.minTillDeathmatch - 1)){
			broadcast(ChatColor.GOLD + "[TheWalls] " + ChatColor.AQUA + "1" + ChatColor.GREEN + " minute until deathmatch.");
		}
		if(time == 60 * -TheWalls.minTillDeathmatch + 15){
			broadcast(ChatColor.GOLD + "[TheWalls] " + ChatColor.AQUA + "15" + ChatColor.GREEN + " seconds until deathmatch.");
		}
		if(time == 60 * -TheWalls.minTillDeathmatch){
			game.startDeathMatch();
		}

		if(time <= 60 * -2){
			for(Player p: game.getPlayers()){
				if(p.isSneaking() && !p.hasPermission("thewalls.nosneakbypass")){
					p.setSneaking(false);
					p.sendMessage(ChatColor.RED + "You cannot sneak after wall has been down for 2 minutes.");
				}
			}
		}
		
		for(Player p: game.getPlayers()){
			if(p.isDead() || TheWalls.getGame(p.getLocation().getWorld()) != game){
				game.removePlayer(p);
			}
		}
		
		time--;
	}
	
	public void broadcast(String m){
		for(Player p: game.getPlayers()){
			p.sendMessage(m);
		}
	}
	
	public boolean inProg(){
		return inProg;
	}
}
