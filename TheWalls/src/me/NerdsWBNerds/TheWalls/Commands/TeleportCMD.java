package me.NerdsWBNerds.TheWalls.Commands;

import me.NerdsWBNerds.TheWalls.TheWalls;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCMD implements CommandExecutor{

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(sender instanceof Player){
			Player player = (Player) sender;

			if(cmd.getName().equalsIgnoreCase("spec")){
				if(args.length != 1){
					return false;
				}
				
				return TheWalls.hub.specPlayer(player, args[0]);
			}
			
			if(cmd.getName().equalsIgnoreCase("wait")){
				return TheWalls.hub.toWait(player);
			}
		}else{
			
		}
		
		return false;
	}
}