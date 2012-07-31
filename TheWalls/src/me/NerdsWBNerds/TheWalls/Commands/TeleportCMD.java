package me.NerdsWBNerds.TheWalls.Commands;

import me.NerdsWBNerds.TheWalls.TheWalls;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCMD implements CommandExecutor{

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(sender instanceof Player){
			Player player = (Player) sender;

			if(cmd.getName().equalsIgnoreCase("spec")){
				if(args.length != 1)
					return false;
				
				if(!player.hasPermission("thewalls.spectate")){
					player.sendMessage(ChatColor.RED + "Error, requires permission thewalls.spectate");
					return true;
				}
				
				if(!TheWalls.inGame(player)){
					Player target = Bukkit.getServer().getPlayer(args[0]);
					
					if(target == null || !target.isOnline()){
						player.sendMessage(ChatColor.RED + "Player not found.");
						return true;
					}
					
					if(TheWalls.inGame(target)){
						TheWalls.tele(player, target.getLocation());
						player.sendMessage(ChatColor.GREEN + "Now spectating " + ChatColor.AQUA + target.getName());
						
						return true;
					}else{
						player.sendMessage(ChatColor.RED + "You can only spectate living players.");
						
						return true;
					}
				}
			}
			
			if(cmd.getName().equalsIgnoreCase("wait")){
				if(!TheWalls.inGame(player)){
					if(!player.hasPermission("thewalls.tptowait")){
						player.sendMessage(ChatColor.RED + "Error, requires permission thewalls.tptowait");
						return true;
					}
					
					TheWalls.tele(player, TheWalls.getWaiting());
					player.sendMessage(ChatColor.GOLD + "[TheWalls] " + ChatColor.GREEN + "You have been teleported to the waiting area.");
				}
				
				return true;
			}
		}else{
			
		}
		
		return false;
	}
}