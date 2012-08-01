package me.NerdsWBNerds.TheWalls.Commands;

import me.NerdsWBNerds.TheWalls.TheWalls;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GeneralCMD implements CommandExecutor{
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(sender instanceof Player){
			Player player = (Player) sender;
			
			// ------------------ List map info and link to download --------------------- //
			
			if(cmd.getName().equalsIgnoreCase("map")){
				return TheWalls.hub.tellMapInfo(player);
			}

			// ---------------- List all lobby information ---------------------- //
			
			if(cmd.getName().equalsIgnoreCase("lobby")){
				return TheWalls.hub.tellLobbyInfo(player);
			}

			// ------------- Quit game and/or que ------------- //

			if(cmd.getName().equalsIgnoreCase("quit")){
				return TheWalls.hub.quitGame(player);
			}
			
			// --------- Join que ------------ //

			if(cmd.getName().equalsIgnoreCase("join")){
				return TheWalls.hub.joinGame(player);
			}
		}else{
			
		}
		
		return false;
	}
}