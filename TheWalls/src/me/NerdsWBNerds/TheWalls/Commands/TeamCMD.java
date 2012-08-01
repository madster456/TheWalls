package me.NerdsWBNerds.TheWalls.Commands;

import me.NerdsWBNerds.TheWalls.TheWalls;

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
				
				return TheWalls.hub.inviteToTeam(player, args[0]);
			}
			
			if(cmd.getName().equalsIgnoreCase("accept")){
				return TheWalls.hub.acceptInvite(player);
			}
			
			if(cmd.getName().equalsIgnoreCase("quitteam")){
				return TheWalls.hub.quitTeam(player);
			}
		}else{
			
		}
		
		return false;
	}
}