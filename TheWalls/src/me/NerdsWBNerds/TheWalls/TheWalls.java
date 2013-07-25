package me.NerdsWBNerds.TheWalls;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import me.NerdsWBNerds.TheWalls.Commands.ChatCMD;
import me.NerdsWBNerds.TheWalls.Commands.CommandHub;
import me.NerdsWBNerds.TheWalls.Commands.GeneralCMD;
import me.NerdsWBNerds.TheWalls.Commands.RecordsCMD;
import me.NerdsWBNerds.TheWalls.Commands.SetupCMD;
import me.NerdsWBNerds.TheWalls.Commands.TeamCMD;
import me.NerdsWBNerds.TheWalls.Commands.TeleportCMD;
import me.NerdsWBNerds.TheWalls.Objects.Person;
import me.NerdsWBNerds.TheWalls.Objects.Record;
import me.NerdsWBNerds.TheWalls.Objects.Team;
import me.NerdsWBNerds.TheWalls.Objects.WallsGame;
import me.NerdsWBNerds.TheWalls.Timers.ConstantTimer;
import me.NerdsWBNerds.TheWalls.Timers.QueTimer;
import me.NerdsWBNerds.TheWalls.Timers.TPCount;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class TheWalls extends JavaPlugin{
	public Logger log;
	public Server server;

	// --------------- Configurables ------------- //

	public static int gameLength = 15;
	public static int minTillDeathmatch = 10;

	public static int maxTeamSize = 3;

	public static int min = 4;
	public static int max = 12;

	public static boolean removeOnLeave = true;
	public static boolean removeOnKick = true;
	
	// -------------- Non-configurables ------------ //
	
	private static ArrayList<Team> que = new ArrayList<Team>();
	public static HashMap<Player, Team> invites = new HashMap<Player, Team>();
	
	public static ArrayList<Player> tempGlobal = new ArrayList<Player>();
	
	public static ArrayList<Player> noPlay = new ArrayList<Player>();
	private static ArrayList<WallsGame> games = new ArrayList<WallsGame>();

	private static ArrayList<Record> records = new ArrayList<Record>();
	
	public static ArrayList<TPCount> tps = new ArrayList<TPCount>();

	private static String Path = "plugins/TheWalls/Worlds.dat";
	private static String PathTwo = "plugins/TheWalls/Records.dat";
	
	private static HashMap<String, Boolean> teamSpeak = new HashMap<String, Boolean>();
	
	private static Location waiting = null;
	public static Block backupCenter = null;
	
	public static QueTimer queCount = null;
	
	public boolean active = false;
	
	public static CommandHub hub = new CommandHub();
	
	public void onEnable(){
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e) {}

		CommandHub.plugin = this;
		
		getCommand("tw").setExecutor(hub);
		
		getCommand("map").setExecutor(new GeneralCMD());
		getCommand("lobby").setExecutor(new GeneralCMD());
		getCommand("join").setExecutor(new GeneralCMD());
		getCommand("quit").setExecutor(new GeneralCMD());

		getCommand("spec").setExecutor(new TeleportCMD());
		getCommand("wait").setExecutor(new TeleportCMD());

		getCommand("setwait").setExecutor(new SetupCMD());
		getCommand("setback").setExecutor(new SetupCMD());
		getCommand("add").setExecutor(new SetupCMD());
		getCommand("alldm").setExecutor(new SetupCMD());

		getCommand("record").setExecutor(new RecordsCMD());
		getCommand("top").setExecutor(new RecordsCMD());

		getCommand("invite").setExecutor(new TeamCMD());
		getCommand("accept").setExecutor(new TeamCMD());
		getCommand("quitteam").setExecutor(new TeamCMD());
		getCommand("queremove").setExecutor(new TeamCMD());
		getCommand("removeplayer").setExecutor(new TeamCMD());
		
		getCommand("g").setExecutor(new ChatCMD());
		getCommand("team").setExecutor(new ChatCMD());
		
		log = getServer().getLogger();
		
		load();

		if(getWaiting() == null || getGames() == null | getGames().isEmpty() || backupCenter == null){
			log.info("[ERROR] The Walls could not start, either no games, no waiting area, or no backup area.");
			return;
		}
		
		getServer().getPluginManager().registerEvents(new TWListener(this), this);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new ConstantTimer(this), 20L, 20L);
		
		shuffle();
	}
	
	public void onDisable(){
		this.getServer().getScheduler().cancelAllTasks();
		
		save();
	}
	
	public static void consoleMessage(String m){
		Bukkit.getConsoleSender().sendMessage(m);
	}
	
	public static Team getTeam(Player p){
		for(Team t: que){
			if(t.team.contains(p)){
				return t;
			}
		}
		
		return null;
	}
	
	public static void sendTeamChat(Player p, String s){
		if(inGame(p)){
			WallsGame g = getGame(p);
			
			for(Player pp: g.getPlayers()){
				if(g.getPerson(pp).getTeam() == g.getPerson(p).getTeam())
					pp.sendMessage(s);
			}
			
			consoleMessage(s);
		}else{
			p.sendMessage(ChatColor.RED + "You must be in a game to use team speak.");
		}
	}
	
	public static boolean inTeamSpeak(Player p){
		if(teamSpeak.containsKey(p.getName())){
			return teamSpeak.get(p.getName());
		}
		
		return false;
	}
	
	public static void addtoTeamSpeak(Player p){
		teamSpeak.put(p.getName(), true);
	}
	
	public static void removeFromTeamSpeak(Player p){
		if(teamSpeak.containsKey(p.getName())){
			teamSpeak.remove(p.getName());
		}
	}
	
	public static void tele(Player p, Location l){
		if(p.isOnline()){
			p.setSprinting(false);
			p.setSneaking(false);
			
			p.teleport(l);
		}else{
			removeFromEverything(p);
		}
	}
	
	public static void removeFromEverything(Player p){
		removePlayer(p);
		removeFromQue(p);
		removeFromNoPlay(p);
	}
	
	public static Record getRecord(Player p, boolean create){
		for(Record r: records){
			if(r.getName().equalsIgnoreCase(p.getName())){
				return r;
			}
		}
		
		Record newR = new Record(p.getName());
		
		if(create)
			records.add(newR);
		
		return newR;
	}
	
	public static ArrayList<Record> getRecords(){
		return records;
	}
	
	public static void playerDie(Player p){
		getRecord(p, true).die();
	}
	
	public static void playerWin(Player p){
		getRecord(p, true).winGame();
	}
	
	public void playerKill(Player p){
		getRecord(p, true).getKill();
	}
	
	public void shuffle(){
		for(Player p: getServer().getOnlinePlayers()){
			if(!p.isDead()){
				addPlayer(p, true);

				p.getInventory().clear();
				p.getInventory().setArmorContents(null);
			}
		}
	}
	
	public void save(){
		save_worlds();

		save_records();
		
		if(getWaiting()!=null)
			getConfig().set("WAITING", toString(getWaiting()));
		if(backupCenter!=null)
			getConfig().set("BACKUP", toString(backupCenter.getLocation()));
		getConfig().set("TIME_TILL_WALL_DROP", gameLength);
		getConfig().set("TIME_TILL_DEATHMATCH", minTillDeathmatch);
		getConfig().set("MIN_PEOPLE_TO_START", min);
		getConfig().set("MAX_PEOPLE_PER_GAME", max);
		getConfig().set("MAX_TEAM_SIZE", maxTeamSize);
		getConfig().set("REMOVE_ON_QUIT", removeOnLeave);
		getConfig().set("REMOVE_ON_KICK", removeOnKick);
		
		saveConfig();
	}
	
	public static void save_worlds(){
		if(games.size() == 0)
			return;
		
		ArrayList<String> formatted = new ArrayList<String>();
		
		for(WallsGame g: games){
			formatted.add(toString(g.getCenter().getLocation()));
		}
		
		File file = new File(Path);
		new File("plugins/").mkdir();
		new File("plugins/TheWalls/").mkdir();
	    if(!file.exists()){
	    	try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }

		try{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(Path));
			oos.writeObject(formatted);
			oos.flush();
			oos.close();
			//Handle I/O exceptions
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void save_records(){
		if(games.size() == 0)
			return;
		
		ArrayList<String> formatted = new ArrayList<String>();
		
		for(Record r: records){
			formatted.add(r.toString());
		}
		
		File file = new File(PathTwo);
		new File("plugins/").mkdir();
	    if(!file.exists()){
	    	try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }

		try{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PathTwo));
			oos.writeObject(formatted);
			oos.flush();
			oos.close();
			//Handle I/O exceptions
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void load(){
		String name = "WAITING";
		if(getConfig().contains(name)){
			waiting = getCenter(toLocation(getConfig().getString(name)));
		}
		
		name = "BACKUP";
		if(getConfig().contains(name)){
			String split[] = getConfig().getString(name).split(",");
			
			if(Bukkit.getWorld(split[0]) == null){
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv import backup normal");
			}
			if(Bukkit.getWorld(split[0]) != null){
				backupCenter = toLocation(getConfig().getString(name)).getBlock();
			}
		}
		
		ArrayList<String> form = load_worlds();
		
		for(String s: form){
			games.add(toGame(s));
		}

		if(games == null)
			games = new ArrayList<WallsGame>();
			
		form = load_records();
		
		for(String s: form){
			records.add(Record.getRecord(s));
		}

		if(records == null)
			records = new ArrayList<Record>();
		
		refresh();
	}
	
	public void refresh(){
		String name = "TIME_TILL_WALL_DROP";
		if(getConfig().contains(name)){
			gameLength = getConfig().getInt(name);
		}else{
			getConfig().set(name, gameLength);
		}
		
		name = "TIME_TILL_DEATHMATCH";
		if(getConfig().contains(name)){
			minTillDeathmatch = getConfig().getInt(name);
		}else{
			getConfig().set(name, minTillDeathmatch);
		}
		
		name = "MIN_PEOPLE_TO_START";
		if(getConfig().contains(name)){
			min = getConfig().getInt(name);
		}else{
			getConfig().set(name, min);
		}
		
		name = "MAX_PEOPLE_PER_GAME";
		if(getConfig().contains(name)){
			max = getConfig().getInt(name);
		}else{
			getConfig().set(name, max);
		}
		
		name = "MAX_TEAM_SIZE";
		if(getConfig().contains(name)){
			maxTeamSize = getConfig().getInt(name);
		}else{
			getConfig().set(name, maxTeamSize);
		}
		
		name = "REMOVE_ON_QUIT";
		if(getConfig().contains(name)){
			removeOnLeave = getConfig().getBoolean(name);
		}else{
			getConfig().set(name, removeOnLeave);
		}
		
		name = "REMOVE_ON_KICK";
		if(getConfig().contains(name)){
			removeOnKick = getConfig().getBoolean(name);
		}else{
			getConfig().set(name, removeOnKick);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<String> load_worlds(){
		if(new File(Path).exists()){
			try{
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(Path));
				Object result = ois.readObject();
	
				ois.close();
				if(result != null)
					return (ArrayList<String>)result;
				else
					return new ArrayList<String>();
			}catch(Exception e){
				return new ArrayList<String>();
			}
		}else{
			return new ArrayList<String>();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<String> load_records(){
		if(new File(PathTwo).exists()){
			try{
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PathTwo));
				Object result = ois.readObject();
	
				ois.close();
				if(result != null)
					return (ArrayList<String>)result;
				else
					return new ArrayList<String>();
			}catch(Exception e){
				return new ArrayList<String>();
			}
		}else{
			return new ArrayList<String>();
		}
	}
	
	public static ArrayList<Team> getQue(){
		return que;
	}
	
	public static int getQueSize(){
		int size = 0;
		
		for(Team t: getQue()){
			size+=t.team.size();
		}
		
		return size;
	}
	
	public static ArrayList<WallsGame> getGames(){
		return games;
	}
	
	public static void setWaiting(Location l){
		waiting = l;
	}
	
	public static Location getWaiting(){
		return waiting;
	}

	public boolean addGame(){
		String worldName = "world" + (getGames().size() + 1);
		
		File newWorld = new File(getFile().getParent() + "/../" + worldName + "/");
		
		if(!newWorld.exists()){
			return false;
		}

		File toDel = new File(getFile().getParent() + "/../" + worldName + "/uid.dat");
		
		if(toDel.exists()){
			toDel.delete();
		}
		
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv import backup normal");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mv import " + worldName + " normal");

		if(Bukkit.getWorld(worldName) == null){
			return false;
		}
		
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvm set animals true " + worldName);
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mvm set monsters false " + worldName);
		
		try{
			Location l = backupCenter.getLocation();
			
			WallsGame toAdd = new WallsGame(getServer().getWorld(worldName).getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
			
			games.add(toAdd);
		}catch(Exception e){
			return false;
		}
		
		return true;
	}
	
	public static int getGameID(WallsGame g){
		for(int i = 0; i < games.size(); i++){
			if(games.get(i) == g)
				return i;
		}
		
		return 0;
	}
	
	public static int getGameID(Player p){
		if(inGame(p)){
			return getGameID(getGame(p));
		}
		
		return 0;
	}
	
	public static ArrayList <Player> getActives(){
		ArrayList<Player> people = new ArrayList<Player>();
		
		for(WallsGame g: games){
			for(Person p: g.getPeople()){
				people.add(p.getPlayer());
			}
		}
		
		return people;
	}
	
	public static WallsGame getGame(Player p){
		for(WallsGame g: games){
			if(g.getPlayers().contains(p)){
				return g;
			}
		}
		
		return null;
	}
	
	public static WallsGame getGame(World w){
		for(WallsGame g: getGames()){
			if(g.getWorld() == w)
				return g;
		}
		
		return null;
	}
	
	public static boolean hasGame(World w){
		if(getGame(w) != null)
			return true;
		
		return false;
	}
	
	public static boolean inGame(Player p){
		if(getActives().contains(p))
			return true;
					
		return false;
	}
	
	public static boolean inQue(Player p){
		for(Team t: getQue()){
			for(Player pp: t.team){
				if(pp == p){
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static void addPlayer(Player p, boolean tp){
		if(noPlay.contains(p) || inGame(p) || inQue(p))
			return;
		
		que.add(new Team(p));
		
		if (tp){
			tele(p, getWaiting());
		}
		
		checkQue();
	}
	
	public static void addPlayer(Player p){
		if(noPlay.contains(p))
			return;
		
		addPlayer(p, false);
	}
	
	public static void removePlayer(Player p){
		if(inGame(p)){
			WallsGame removeFrom = getGame(p);
			
			removeFrom.removePlayer(p);
			removeFromTeamSpeak(p);
		}
		
		removeFromQue(p);
	}
	
	public static void playerLeftGame(Player p){
		TheWalls.playerDie(p);
		
		if(TheWalls.getGame(p).getPlayers().size() > 2){
			for(ItemStack i: p.getInventory().getContents()){
				if(i != null && i.getType() != Material.AIR)
					p.getWorld().dropItemNaturally(p.getLocation(), i);
			}
			for(ItemStack i: p.getInventory().getArmorContents()){
				if(i != null && i.getType() != Material.AIR)
					p.getWorld().dropItemNaturally(p.getLocation(), i);
			}
		}

		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
	}
	
	public static void removeFromNoPlay(Player p){
		if(noPlay.contains(p)){
			noPlay.remove(p);
		}
	}
	
	public static void removeFromQue(Player p){
		if(getTeam(p) != null){
			Team t = getTeam(p);
			t.removePlayer(p);
		}
	}
		
	public static boolean checkQue(){
		for(Player p: noPlay){
			for(Team t: que){
				if(t.team.contains(p))
					t.team.remove(p);
			}
		}
				
		if(queCount == null && que.size() >= 4 && getQueSize() >= min){
			startQueCount();
		}
		
		return false;
	}
	
	public static void startQueCount(){
		stopQueCount();
		
		queCount = new QueTimer();
		queCount.id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(new TheWalls(), queCount, 20L, 20L);
	}
	
	public static void startQueCount(int i){
		stopQueCount();
		
		queCount = new QueTimer(i);
		queCount.id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(new TheWalls(), queCount, 20L, 20L);
	}
	
	public static void stopQueCount(){
		if(queCount != null){
			Bukkit.getServer().getScheduler().cancelTask(queCount.id);
			queCount = null;
		}
	}
	
	public static WallsGame getNextGame(){
		for(int i = 0; i < games.size(); i++){
			WallsGame game = games.get(i);
			
			if(!game.inProg() && game.getPeople().isEmpty()){
				return game;
			}
		}
		
		return null;
	}
	
	public static String toString(Location l){
		String loc = "";
		

		loc += l.getWorld().getName() + ",";
		loc += l.getBlockX()+ ",";
		loc += l.getBlockY() + ",";
		loc += l.getBlockZ();
		
		return loc;
	}

	public static Location getCenter(Location l){
		return getBlockLoc(l).add(0.5, 0.5, 0.5);
	}
	
	public static Location getBlockLoc(Location l){
		return new Location(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}
	
	public static Location toLocation(String n){
		String[] split = n.split(",");
		
		return getCenter( new Location(Bukkit.getServer().getWorld(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3])) );
	}
	
	public WallsGame toGame(String s){
		WallsGame ret = new WallsGame(toLocation(s).getBlock());
		
		return ret;
	}

	public static void hidePlayer(Player p){
		for(Player pp: Bukkit.getServer().getOnlinePlayers()){
			if(p!=pp && pp.canSee(p))
				pp.hidePlayer(p);
		}
	}
	
	public static void showPlayer(Player p){
		for(Player pp: Bukkit.getServer().getOnlinePlayers()){
			if(p!=pp && !pp.canSee(p))
				pp.showPlayer(p);
		}
	}
}
