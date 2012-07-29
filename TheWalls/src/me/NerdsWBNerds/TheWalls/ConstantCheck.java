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
			if(!TheWalls.inGame(p) && !TheWalls.getQue().contains(p) && !TheWalls.noPlay.contains(p)){
				plugin.addPlayer(p);
			}
		}
		
		for(Player p: TheWalls.getActives()){
			TheWalls.showPlayer(p);
			
			if(p.getGameMode() == GameMode.CREATIVE)
				p.setGameMode(GameMode.SURVIVAL);

			ChatColor clr = ChatColor.WHITE;
			
			if(p.isOp())
				clr = ChatColor.RED;
			
			String newName = "(" + (plugin.getGameID(p) + 1) + ")" + clr + p.getName() + ChatColor.WHITE;
			
			if(newName.length() > 16){
				newName = newName.substring(0, 16);
			}
			
			try{
				if(!p.getPlayerListName().equalsIgnoreCase(newName + ChatColor.WHITE))
					p.setPlayerListName(newName);
			}catch(Exception e){}
		}
		
		for(Player p: TheWalls.getQue()){
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
				if(!p.getPlayerListName().equalsIgnoreCase(newName + ChatColor.WHITE))
					p.setPlayerListName(newName);
			}catch(Exception e){}
		}
		
		for(Player p: TheWalls.noPlay){
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
				if(!p.getPlayerListName().equalsIgnoreCase(newName + ChatColor.WHITE))
					p.setPlayerListName(newName);
			}catch(Exception e){}
		}
		
		plugin.checkQue();
		
		if(TheWalls.debug)
			System.out.println("CONST-CHECK");
	}
}
