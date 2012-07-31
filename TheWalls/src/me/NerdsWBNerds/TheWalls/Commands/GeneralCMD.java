package me.NerdsWBNerds.TheWalls.Commands;

import java.util.ArrayList;

import me.NerdsWBNerds.TheWalls.TheWalls;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GeneralCMD implements CommandExecutor{
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(sender instanceof Player){
			Player player = (Player) sender;
			
			// ------------- List commands / general info --------------------------- //
			
			if(cmd.getName().equalsIgnoreCase("tw") && args.length > 0 && args[0].equalsIgnoreCase("help")){
				if(!player.hasPermission("thewalls.gethelp")){
					player.sendMessage(ChatColor.RED + "Error, requires permission thewalls.gethelp");
					return true;
				}
				
				ArrayList<String> messages = new ArrayList<String>();
				
				messages.add(ChatColor.GREEN + "You will " + ChatColor.RED + "AUTOMATICALLY" + ChatColor.GREEN + " be put into a game.");
				messages.add(" ");
				messages.add(ChatColor.AQUA + "/lobby " + ChatColor.GREEN + " - Get general info about your lobby.");
				messages.add(ChatColor.AQUA + "/map " + ChatColor.GREEN + " - Get general info about the map.");
				messages.add(ChatColor.AQUA + "/quit /join " + ChatColor.GREEN + " - Quit and join games/que.");
				messages.add(ChatColor.AQUA + "/team /g " + ChatColor.GREEN + " - Switch between team and global chat.");
				messages.add(ChatColor.AQUA + "/spec <player> " + ChatColor.GREEN + " - Spectate player.");
				messages.add(ChatColor.AQUA + "/record <player> " + ChatColor.GREEN + " - Get a players record.");
				
				player.sendMessage(ChatColor.GOLD + "** THE WALLS HELP **");
				
				for(String m: messages){
					player.sendMessage(m);
				}
				
				return true;
			}
			
			// ------------------ List map info and link to download --------------------- //
			
			if(cmd.getName().equalsIgnoreCase("map")){
				if(!player.hasPermission("thewalls.getmapinfo")){
					player.sendMessage(ChatColor.RED + "Error, requires permission thewalls.getmapinfo");
					return true;
				}
				
				String mapLink = "http://tinyurl.com/thewallsmap";

				
				player.sendMessage(ChatColor.GOLD + "** THE WALLS MAP INFORMATION **");
				player.sendMessage(ChatColor.GREEN + "This map is a slightly modified version of " + ChatColor.AQUA + "The Walls" + ChatColor.GREEN + " map by " + ChatColor.AQUA + "Hypixel.");
				player.sendMessage(ChatColor.GREEN + "Map: " + ChatColor.AQUA + mapLink);
				
				return true;
			}

			// ---------------- List all lobby information ---------------------- //
			
			if(cmd.getName().equalsIgnoreCase("lobby")){
				if(!player.hasPermission("thewalls.getlobbyinfo")){
					player.sendMessage(ChatColor.RED + "Error, requires permission thewalls.getlobbyinfo");
					return true;
				}
				
				if(!TheWalls.inGame(player)){
					player.sendMessage(ChatColor.GOLD + "** LOBBY INFORMATION **");
					
				}else{
					player.sendMessage(ChatColor.GOLD + "** LOBBY INFORMATION (YOUR LOBBY # IS " + (TheWalls.getGameID(player) + 1)  + ") **");
				}

				if(TheWalls.queCount != null)
					player.sendMessage(ChatColor.GREEN + "Waiting Que: " + ChatColor.AQUA + TheWalls.getQueSize() + " people. " + TheWalls.queCount.time + " second(s).");
				else
					player.sendMessage(ChatColor.GREEN + "Waiting Que: " + ChatColor.AQUA + TheWalls.getQueSize() + " people.");
				
				for(int i = 0; i < TheWalls.getGames().size(); i++){
					player.sendMessage(ChatColor.GREEN + "Lobby #" + (i + 1) + ": " + ChatColor.AQUA + TheWalls.getGames().get(i).getPeople().size() + " / " + TheWalls.max + " people. " + TheWalls.getGames().get(i).getMin() + " min(s) " + TheWalls.getGames().get(i).getSec() + " sec(s)");
				}
				
				return true;
			}

			// ------------- Quit game and/or que ------------- //

			if(cmd.getName().equalsIgnoreCase("quit")){
				if(!player.hasPermission("thewalls.quit")){
					player.sendMessage(ChatColor.RED + "Error, requires permission thewalls.quit");
					return true;
				}
				
				if(TheWalls.inGame(player)){
					player.setHealth(0);
				}
				
				TheWalls.removePlayer(player);
				
				if(!TheWalls.noPlay.contains(player))
					TheWalls.noPlay.add(player);
				
				player.sendMessage(ChatColor.GREEN + "You have left the waiting que.");
				return true;
			}
			
			// --------- Join que ------------ //

			if(cmd.getName().equalsIgnoreCase("join")){
				if(!player.hasPermission("thewalls.join")){
					player.sendMessage(ChatColor.RED + "Error, requires permission thewalls.join");
					return true;
				}
				
				if(TheWalls.noPlay.contains(player))
					TheWalls.noPlay.remove(player);
				
				TheWalls.addPlayer(player);

				player.sendMessage(ChatColor.GREEN + "You have joined the waiting que.");
				return true;
			}
		}else{
			
		}
		
		return false;
	}
}