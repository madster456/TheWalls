package me.NerdsWBNerds.TheWalls.Commands;

import me.NerdsWBNerds.TheWalls.Team;
import me.NerdsWBNerds.TheWalls.TheWalls;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamCMD implements CommandExecutor{

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(sender instanceof Player){
			Player player = (Player) sender;
			
			if(cmd.getName().equalsIgnoreCase("invite")){
				if(args.length != 1)
					return false;
				
				if(!TheWalls.inGame(player)){
					Player target = Bukkit.getServer().getPlayer(args[0]);
					
					if(target == null || !target.isOnline()){
						player.sendMessage(ChatColor.RED + "Player not found.");
						return true;
					}
					
					if(target == player){
						player.sendMessage(ChatColor.RED + "No.");
						return true;
					}
					
					if(TheWalls.getTeam(player).invitePlayer(target)){
						player.sendMessage(ChatColor.GOLD + "[TheWalls] " + ChatColor.AQUA + target.getName() + ChatColor.GREEN + " invited to team.");
					}else{
						player.sendMessage(ChatColor.RED + "Error inviting player to team.");
					}
				}
				
				return true;
			}
			
			if(cmd.getName().equalsIgnoreCase("accept")){
				if(TheWalls.invites.containsKey(player)){
					Team invite = TheWalls.invites.get(player);
					
					if(TheWalls.getQue().contains(invite) && !TheWalls.inGame(player)){
						if(invite.team.size() < 3){
							TheWalls.removeFromQue(player);
							TheWalls.invites.remove(player);
							invite.addPlayer(player);
						}
					}
				}
				
				return true;
			}
			
			if(cmd.getName().equalsIgnoreCase("quitteam")){
				TheWalls.removePlayer(player);
				TheWalls.addPlayer(player);
				
				player.sendMessage(ChatColor.GOLD + "[TheWalls] " + "You have quit your team.");
				
				return true;
			}
		}else{
			
		}
		
		return false;
	}
}