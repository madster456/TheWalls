package me.NerdsWBNerds.TheWalls.Commands;

import me.NerdsWBNerds.TheWalls.TWListener;
import me.NerdsWBNerds.TheWalls.TheWalls;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatCMD implements CommandExecutor{

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(sender instanceof Player){
			Player player = (Player) sender;

			if(cmd.getName().equalsIgnoreCase("g")){
				if(!player.hasPermission("thewalls.chat.global")){
					player.sendMessage(ChatColor.RED + "Error, requires permission thewalls.chat.global");
					return true;
				}
				
				if(args.length == 0){
					TheWalls.removeFromTeamSpeak(player);
					
					player.sendMessage(ChatColor.GOLD + "[TheWalls] " + ChatColor.GREEN + "Now in global chat. Use /team to switch to team chat.");
				}else{
					ChatColor clr = ChatColor.WHITE;
					
					if(player.isOp())
						clr = ChatColor.RED;
					
					String xtra = "";					
					
					if(!TheWalls.inGame(player))
						xtra = TWListener.spec + "[SPEC]";
					
					String msg = xtra + ChatColor.GRAY + "<" + clr + player.getName() + ChatColor.GRAY + "> " + ChatColor.WHITE;
					
					for(String s: args){
						msg += s + " ";
					}
					
					TheWalls.sendGlobalChat(player, msg);
				}
				
				return true;
			}
			
			if(cmd.getName().equalsIgnoreCase("team")){
				if(!player.hasPermission("thewalls.chat.team")){
					player.sendMessage(ChatColor.RED + "Error, requires permission thewalls.chat.team");
					return true;
				}
				
				if(args.length == 0){
					if(TheWalls.inGame(player)){
						TheWalls.addtoTeamSpeak(player);
						
						player.sendMessage(ChatColor.GOLD + "[TheWalls] " + ChatColor.GREEN + "Now in team chat. Use /g to switch to global chat.");
					}else{
						player.sendMessage(ChatColor.RED + "You must be in a game to switch to team chat.");
					}
				}else{
					ChatColor clr = ChatColor.WHITE;
					
					if(player.isOp())
						clr = ChatColor.RED;
					
					String msg = TWListener.team + "[TEAM]" + ChatColor.GRAY + "<" + clr + player.getName() + ChatColor.GRAY + "> " + ChatColor.WHITE;
					
					for(String s: args){
						msg += s + " ";
					}
					
					TheWalls.sendTeamChat(player, msg);
				}
				
				return true;
			}
		}else{
			
		}
		
		return false;
	}
}