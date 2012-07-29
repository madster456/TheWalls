package me.NerdsWBNerds.TheWalls;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Game {
	private Block center;
	private ArrayList<Person> people = new ArrayList<Person>();
	
	private GameCount timer = null;
	public int sandHeight = 35;
	
	public TheWalls plugin;
	
	public Game(TheWalls p){
		plugin = p;
	}
	
	public ArrayList<Person> getPeople(){
		return people;
	}
	
	public ArrayList<Player> getPlayers(){
		ArrayList<Player> ret = new ArrayList<Player>();
		
		for(Person p: getPeople()){
			ret.add(p.getPlayer());
		}
		
		return ret;
	}
	
	public boolean inBorder(Location l){
		return inBorder(l.getBlock());
	}
	
	public boolean inBorder(Block l){
		if(l.getWorld() != getWorld())
			return false;
		
		if(l.getX() > getLeftCenter().getX() && l.getX() < getRightCenter().getX()){
			if(l.getZ() > getFrontCenter().getZ() && l.getZ() < getBackCenter().getZ()){
				return true;
			}
		}
		
		return false;
	}
	
	public void setCenter(Block b){
		center = b;
	}
	
	public Block getCenter(){
		return center;
	}
	
	public Block getLeftCenter(){
		return getCenter().getLocation().add(-61, 0, 0).getBlock();
	}
	
	public Block getRightCenter(){
		return getCenter().getLocation().add(61, 0, 0).getBlock();
	}
	
	public Block getFrontCenter(){
		return getCenter().getLocation().add(0, 0, -61).getBlock();
	}
	
	public Block getBackCenter(){
		return getCenter().getLocation().add(61, 0, 61).getBlock();
	}
	
	public Location getTeamOneSpawn(){
		return getCenter().getLocation().add(10, 2, 10);
	}
	
	public Location getTeamTwoSpawn(){
		return getCenter().getLocation().add(10, 2, -10);
	}
	
	public Location getTeamThreeSpawn(){
		return getCenter().getLocation().add(-10, 2, 10);
	}
	
	public Location getTeamFourSpawn(){
		return getCenter().getLocation().add(-10, 2, -10);
	}
	
	public World getWorld(){
		return getCenter().getWorld();
	}

	public boolean hasPerson(Player p){
		for(Person pp: people){
			if(pp.getPlayer() == p){
				return true;
			}
		}
		
		return false;
	}
	
	public Person getPerson(Player p){
		for(Person pp: people){
			if(pp.getPlayer() == p){
				return pp;
			}
		}
		
		return null;
	}
	
	public void addPlayer(Player p){
		people.add(new Person(p));
	}
	
	public void removePlayer(Player p){
		if(getPlayers().contains(p))
			people.remove(getPerson(p));
		
		if(people.size() == 1){
			endGame();
		}
	}
	
	public GameCount getTimer(){
		return timer;
	}
	
	public boolean inProg(){
		if(timer == null)
			return false;
		
		return timer.inProg();
	}
	
	public void restoreMap(){
		int minY = 50, maxY = 200;
		int minYY = 50;

		int minX = TheWalls.backupCenter.getX() - 61;
		int maxX = TheWalls.backupCenter.getX() + 62;
		int minZ = TheWalls.backupCenter.getZ() - 61;
		int maxZ = TheWalls.backupCenter.getZ() + 62;

		int minXX = getCenter().getX() - 61;
		int minZZ = getCenter().getZ() - 61;
		
		for(int x = 0; x < maxX - minX; x++){
			for(int y = 0; y < maxY - minY; y++){
				for(int z = 0; z < maxZ - minZ; z++){
					Block old = new Location(TheWalls.backupCenter.getWorld(), x + minX, y + minY, z + minZ).getBlock();
					Block newBlock = getWorld().getBlockAt(x + minXX, y + minYY, z + minZZ);					
					
					if(old.getType() != newBlock.getType()){
						newBlock.setType(old.getType());
						newBlock.setData(old.getData());
					}
					
					if(old.getType() == Material.CHEST){
						Chest chest = (Chest) old.getState();
						
						Chest newChest = (Chest) newBlock.getState();
						newChest.getInventory().setContents(chest.getInventory().getContents());
					}	
				}
			}
		}
		
		for(Entity e: getWorld().getEntities()){
			if(!(e instanceof Player))
				e.remove();
		}

		addWall();
		
		getWorld().setTime(0);
	}
	
	public void setupRestore(){
		restoreMap();
		
		return;
		
		//new Thread(new WorldRestore(this)).start();
	}
	
	public void addWall(){
		int chunkSize = 61;
		
		int minY = getCenter().getY() + 1;
		int maxY = minY + sandHeight;
		
		int minX = getCenter().getX() - chunkSize + 1;
		int maxX = getCenter().getX() + chunkSize;
		
		for(int x = minX; x < maxX; x++){
			for(int y = minY; y < maxY; y++){
				if(getWorld().getBlockAt(x, y, getCenter().getZ()).getType() != Material.SAND)
					getWorld().getBlockAt(x, y, getCenter().getZ()).setType(Material.SAND);
			}
		}
		
		int minZ = getCenter().getZ() - chunkSize + 1;
		int maxZ = getCenter().getZ() + chunkSize;
		
		for(int z = minZ; z < maxZ; z++){
			for(int y = minY; y < maxY; y++){
				if(getWorld().getBlockAt(getCenter().getX(), y, z).getType() != Material.SAND);
					getWorld().getBlockAt(getCenter().getX(), y, z).setType(Material.SAND);
			}
		}
	}
	
	public void delWall(){
		int chunkSize = 61;
		
		int minY = getCenter().getY() + 1;
		int maxY = minY + sandHeight;
		
		int minX = getCenter().getX() - chunkSize + 1;
		int maxX = getCenter().getX() + chunkSize;
		
		for(int x = minX; x < maxX; x++){
			for(int y = minY; y < maxY; y++){
				getWorld().getBlockAt(x, y, getCenter().getZ()).setType(Material.AIR);
			}
		}
		
		int minZ = getCenter().getZ() - chunkSize + 1;
		int maxZ = getCenter().getZ() + chunkSize;
		
		for(int z = minZ; z < maxZ; z++){
			for(int y = minY; y < maxY; y++){
				getWorld().getBlockAt(getCenter().getX(), y, z).setType(Material.AIR);
			}
		}
	}
	
	public void startTimer(){
		timer = new GameCount(this, 15);
		timer.id = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, timer, 20L, 20L);
	}
	
	public void startGame(){
		people.clear();
		restoreMap();

		for(Player p: TheWalls.noPlay){
			if(TheWalls.getQue().contains(p))
				TheWalls.getQue().remove(p);
		}
		
		int maxx = Math.min(TheWalls.max, TheWalls.getQue().size());
		for(int i = 0 ; i < maxx; i++){
			addPlayer(TheWalls.getQue().get(i));

		}
		
		genTeams();
		
		for(Player p: getPlayers()){
			TheWalls.getQue().remove(p);

			TheWalls.tele(p, getSpawn(p));

			p.setHealth(20);
			p.setFoodLevel(20);
			p.getInventory().clear();
			p.getInventory().setArmorContents(null);
			p.setFireTicks(0);
			p.setExp(0);
			p.setGameMode(GameMode.SURVIVAL);
			p.sendMessage(ChatColor.GOLD + "[TheWalls] " + ChatColor.GREEN + "You are now in a game, if you aren't in the game area please tell an admin.");
		}
		
		startTimer();
	}
	
	
	public void startPvP(){
		delWall();
	}

	public boolean inDeathmatch(){
		if(timer == null)
			return false;
		
		if(timer.time < -10 * 60){
			return true;
		}
		
		return false;
	}

	public boolean inPvP(){
		if(timer == null)
			return false;
		
		if(timer.time < 0){
			return true;
		}
		
		return false;
	}
	
	public void startDeathMatch(){
		for(Player p: getPlayers()){
			TheWalls.tele(p, getCenter().getLocation().add(0, 40, 0));
		
			p.sendMessage(ChatColor.GOLD + "[TheWalls] " + ChatColor.GREEN + "You are now in deathmatch.");
			
			p.setHealth(20);
			p.setFoodLevel(20);
			p.setFireTicks(0);
			p.setNoDamageTicks(30);
			p.setExp(0);
			p.setGameMode(GameMode.SURVIVAL);
		}
	}
	
	public void endGame(){
		plugin.getServer().broadcastMessage(ChatColor.GOLD + "[TheWalls] " + ChatColor.AQUA + people.get(0).getPlayer().getName() + ChatColor.GREEN + " has won in lobby # " + ChatColor.AQUA + (plugin.getGameID(this) + 1));
		plugin.getServer().getScheduler().cancelTask(timer.id);
		
		timer = null;
		
		Player winner = people.get(0).getPlayer();
		
		plugin.playerWin(winner);
		TheWalls.removeFromTeamSpeak(winner);
		
		plugin.addPlayer(winner);
		people.clear();
		
		TheWalls.tele(winner, TheWalls.getWaiting());

		restoreMap();
		addWall();
		
		plugin.checkQue();
	}
	
	public int getMin(){
		int min = 15;
		
		if(timer != null){
			min = timer.time / 60;
		}
		
		return min;
	}
	
	public int getSec(){
		int sec = 0;
		
		if(timer != null){
			sec = timer.time % 60;
		}
		
		return sec;
	}
	
	public void genTeams(){
		int ii = 0;
		for(int i = 0; i < getPeople().size(); i++){
			Person p = getPeople().get(i);
			
			if(ii > 3)
				ii = 0;
			
			p.setTeam(ii + 1);
			
			ii++;
		}
	}
	
	public Location getSpawn(Player p){
		if(getPerson(p).getTeam() == 1)
			return getTeamOneSpawn();
		if(getPerson(p).getTeam() == 2)
			return getTeamTwoSpawn();
		if(getPerson(p).getTeam() == 3)
			return getTeamThreeSpawn();
		if(getPerson(p).getTeam() == 4)
			return getTeamFourSpawn();
		
		return null;
	}
}
