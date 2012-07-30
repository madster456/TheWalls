package me.NerdsWBNerds.TheWalls;

import org.bukkit.entity.Player;

public class Person {
	private int team;
	private Player player;
	
	public Person(Player p){
		setPlayer(p);
	}
	
	public Person setTeam(int i){
		team = i;
		
		return this;
	}
	
	public void setPlayer(Player p){
		player = p;
	}
	
	public int getTeam(){
		return team;
	}
	
	public Player getPlayer(){
		return player;
	}
}
