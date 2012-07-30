package me.NerdsWBNerds.TheWalls;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Team {
	public ArrayList<Player> team = new ArrayList<Player>();
	
	public Team(Player p){
		addPlayer(p);
	}
	
	public void removePlayer(Player p){
		if(team.contains(p)){
			team.remove(p);
			
			if(team.isEmpty()){
				if(TheWalls.getQue().contains(this))
					TheWalls.getQue().remove(this);
				if(TheWalls.invites.containsValue(this)){
					for(Entry<Player, Team> e: TheWalls.invites.entrySet()){
						if(e.getValue() == this){
							TheWalls.invites.remove(e.getKey());
						}
					}
				}
			}
		}
	}
	
	public void addPlayer(Player p){
		if(team.size() < 3){
			for(Player pl: team){
				pl.sendMessage(ChatColor.GOLD + "[TheWalls] " + ChatColor.AQUA + p.getName() + ChatColor.GREEN + " has joined your team.");
				if(TheWalls.noPlay.contains(p))
					TheWalls.noPlay.remove(p);
			}
			
			team.add(p);
			p.sendMessage(ChatColor.GOLD + "[TheWalls] " + ChatColor.GREEN + "Successfully added to team.");
		}
	}
	
	public boolean invitePlayer(Player p){
		if(team.size() < 3 && !TheWalls.inGame(p)){
			TheWalls.invites.put(p, this);
			p.sendMessage(ChatColor.GOLD + "[TheWalls] " + ChatColor.GREEN + "You have been invited to " + ChatColor.AQUA + team.get(0).getName() + "'s" + ChatColor.GREEN + "team, type /accept to join.");
			
			return true;
		}
		
		return false;
	}
}
