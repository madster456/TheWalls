package me.NerdsWBNerds.TheWalls;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class ConstantCheck implements Runnable{
	TheWalls plugin;
	
	public ConstantCheck(TheWalls g){
		plugin = g;
	}
	
	@Override
	public void run() {
		for(Player p: Bukkit.getServer().getOnlinePlayers()){
			if(!TheWalls.inGame(p) && TheWalls.getTeam(p)==null && !TheWalls.noPlay.contains(p)){
				TheWalls.addPlayer(p);
			}
		}
		
		for(Player p: plugin.getServer().getOnlinePlayers()){
			if(TheWalls.inGame(p)){
				TheWalls.showPlayer(p);
				
				if(p.getGameMode() == GameMode.CREATIVE)
					p.setGameMode(GameMode.SURVIVAL);
	
				ChatColor clr = ChatColor.WHITE;
				
				if(p.isOp())
					clr = ChatColor.RED;
				
				String newName = "(" + (TheWalls.getGameID(p) + 1) + ")" + clr + p.getName();
				
				if(newName.length() > 16){
					newName = newName.substring(0, 16);
				}
				
				try{
					if(!p.getPlayerListName().equalsIgnoreCase(newName + ChatColor.RESET))
						p.setPlayerListName(newName);
				}catch(Exception e){}
			
			}else if(TheWalls.getTeam(p) != null){
				if(TheWalls.getWaiting().getWorld() != p.getWorld())
					TheWalls.hidePlayer(p);
				
				if(p.getWorld() == TheWalls.getWaiting().getWorld() && p.hasPermission("thewalls.creativewaiting") || p.getWorld() != TheWalls.getWaiting().getWorld()){
					if(p.getGameMode() == GameMode.SURVIVAL)
						p.setGameMode(GameMode.CREATIVE);
				}else{
					if(p.getGameMode() == GameMode.CREATIVE)
						p.setGameMode(GameMode.SURVIVAL);
				}

				ChatColor clr = ChatColor.WHITE;
				
				if(p.isOp())
					clr = ChatColor.RED;
				
				String newName = "(SPEC)" + clr + p.getName();
				
				if(newName.length() > 16){
					newName = newName.substring(0, 16);
				}
				
				try{
					if(!p.getPlayerListName().equalsIgnoreCase(newName + ChatColor.RESET))
						p.setPlayerListName(newName);
				}catch(Exception e){}
				
			}else if(TheWalls.noPlay.contains(p)){
				if(TheWalls.getWaiting().getWorld() != p.getWorld())
					TheWalls.hidePlayer(p);
				
				if(p.getGameMode() == GameMode.SURVIVAL)
					p.setGameMode(GameMode.CREATIVE);

				ChatColor clr = ChatColor.WHITE;
				
				if(p.isOp())
					clr = ChatColor.RED;
				
				String newName = "(SPEC)" + clr + p.getName();
				
				if(newName.length() > 16){
					newName = newName.substring(0, 16);
				}
				
				try{
					if(!p.getPlayerListName().equalsIgnoreCase(newName + ChatColor.RESET))
						p.setPlayerListName(newName);
				}catch(Exception e){}
				
			}
			
			if(p.getWorld() == TheWalls.getWaiting().getWorld()){
				for(Player pp: TheWalls.getWaiting().getWorld().getPlayers()){
					p.showPlayer(pp);
				}	
			}
		}
		
		TheWalls.checkQue();
	}
}
