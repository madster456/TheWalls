package me.NerdsWBNerds.TheWalls.Commands;

import me.NerdsWBNerds.TheWalls.Record;
import me.NerdsWBNerds.TheWalls.TheWalls;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RecordsCMD implements CommandExecutor{

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(sender instanceof Player){
			Player player = (Player) sender;

			
			if(cmd.getName().equalsIgnoreCase("record")){
				Player target = player;
				
				if(args.length == 1){
					target = null;
					target = Bukkit.getServer().getPlayer(args[0]);
				}
				
				if(target == null || !target.isOnline()){
					player.sendMessage(ChatColor.RED + "Error: Player not found.");
					return true;
				}

				player.sendMessage(ChatColor.GOLD + "** " + target.getName().toUpperCase() + "'S RECORD **");
				player.sendMessage(ChatColor.GREEN + "Wins: " + ChatColor.AQUA + TheWalls.getRecord(target, false).getWins());
				player.sendMessage(ChatColor.GREEN + "Kills: " + ChatColor.AQUA + TheWalls.getRecord(target, false).getKills());
				player.sendMessage(ChatColor.GREEN + "Deaths: " + ChatColor.AQUA + TheWalls.getRecord(target, false).getDeaths());
			
				return true;
			}
			
			if(cmd.getName().equalsIgnoreCase("top")){
				Record mW = null, mK = null, mD = null;
				
				for(Record r: TheWalls.getRecords()){
					if(mW == null)
						mW = r;
					
					if(mK == null)
						mK = r;
					
					if(mD == null)
						mD = r;

					if(r.getKills() > mK.getKills())
						mK = r;
					
					if(r.getWins() > mW.getWins())
						mW = r;

					if(r.getDeaths() > mD.getDeaths())
						mD = r;
				}
				
				player.sendMessage(ChatColor.GOLD + "** LEADERBOARDS **");
				
				if(mW != null)
					player.sendMessage(ChatColor.GREEN + "Most Wins: " + ChatColor.AQUA + mW.getName() + " with " + mW.getWins() + " win(s).");
				else
					player.sendMessage(ChatColor.GREEN + "Most Wins: " + ChatColor.AQUA + "None.");
				
				if(mK != null)
					player.sendMessage(ChatColor.GREEN + "Most Kills: " + ChatColor.AQUA + mK.getName() + " with " + mK.getKills() + " kill(s).");
				else
					player.sendMessage(ChatColor.GREEN + "Most Kills: " + ChatColor.AQUA + "None.");
				
				if(mD != null)
					player.sendMessage(ChatColor.GREEN + "Most Deaths: " + ChatColor.AQUA + mD.getName() + " with " + mD.getDeaths() + " death(s).");
				else
					player.sendMessage(ChatColor.GREEN + "Most Deaths: " + ChatColor.AQUA + "None.");
				
				return true;
			}
		}else{
			
		}
		
		return false;
	}
}