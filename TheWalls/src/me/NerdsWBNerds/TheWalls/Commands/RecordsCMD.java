package me.NerdsWBNerds.TheWalls.Commands;

import me.NerdsWBNerds.TheWalls.TheWalls;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RecordsCMD implements CommandExecutor{

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(sender instanceof Player){
			Player player = (Player) sender;
			
			if(cmd.getName().equalsIgnoreCase("record")){
				String target = null;

				if(args.length == 1){
					target = args[0];
				}
				
				return TheWalls.hub.checkRecord(player, target);
			}
			
			if(cmd.getName().equalsIgnoreCase("top")){
				return TheWalls.hub.showTop(player);
			}
		}else{
			
		}
		
		return false;
	}
}