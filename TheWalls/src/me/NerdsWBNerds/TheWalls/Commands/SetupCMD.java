package me.NerdsWBNerds.TheWalls.Commands;

import me.NerdsWBNerds.TheWalls.TheWalls;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetupCMD implements CommandExecutor{

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(sender instanceof Player){
			Player player = (Player) sender;

			if(cmd.getName().equalsIgnoreCase("alldm")){
				return TheWalls.hub.forceAllDM(player);
			}
			
			if(cmd.getName().equalsIgnoreCase("setwait")){
				return TheWalls.hub.setWaiting(player);
			}
			
			if(cmd.getName().equalsIgnoreCase("setback")){
				return TheWalls.hub.setBackup(player);
			}

			if(cmd.getName().equalsIgnoreCase("add")){
				return TheWalls.hub.addWorld(player);
			}
		}else{
			
		}
		
		return false;
	}
}