package me.NerdsWBNerds.TheWalls.Commands;

import me.NerdsWBNerds.TheWalls.Game;
import me.NerdsWBNerds.TheWalls.TheWalls;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetupCMD implements CommandExecutor{

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(sender instanceof Player){
			Player player = (Player) sender;

			if(cmd.getName().equalsIgnoreCase("alldm")){
				if(!player.hasPermission("thewalls.forcedm")){
					player.sendMessage(ChatColor.RED + "Error, requires permission thewalls.forcedm");
					return true;
				}
				
				for(Game g: TheWalls.getGames()){
					if(g.getPlayers().size() != 0){
						g.getTimer().time = (60 * -10) + 15;
					}
				}
				
				player.sendMessage(ChatColor.GREEN + "All worlds forced into deathmatch.");
				return true;
			}
			
			if(cmd.getName().equalsIgnoreCase("setwait")){
				if(!player.hasPermission("thewalls.setwait")){
					player.sendMessage(ChatColor.RED + "Error, requires permission thewalls.setwait");
					return true;
				}
				
				TheWalls.setWaiting(player.getLocation());
				player.sendMessage(ChatColor.GREEN + "Waiting location set at your position.");
				return true;
			}
			
			if(cmd.getName().equalsIgnoreCase("setback")){
				if(!player.hasPermission("thewalls.setbackup")){
					player.sendMessage(ChatColor.RED + "Error, requires permission thewalls.setbackup");
					return true;
				}
				
				TheWalls.backupCenter = player.getLocation().add(0, -1, 0).getBlock();
				player.sendMessage(ChatColor.GREEN + "Back area set at your position.");
				return true;
			}

			if(cmd.getName().equalsIgnoreCase("add")){
				if(!player.hasPermission("thewalls.addworld")){
					player.sendMessage(ChatColor.RED + "Error, requires permission thewalls.addworld");
					return true;
				}
				
				TheWalls.addGame(player);
				player.sendMessage(ChatColor.GREEN + "Game # " + TheWalls.getGames().size() + " added.");
				return true;
			}
		}else{
			
		}
		
		return false;
	}
}