package me.NerdsWBNerds.TheWalls.Objects;

public class Record {
	private String name = "";
	private int kills = 0, wins = 0, deaths = 0;
	
	public Record(String n){
		name = n;
	}
	
	public Record(String n, int k, int w, int d){
		name = n;
		
		kills = k;
		wins = w;
		deaths = d;
	}
	
	public Record(String n, String k, String w, String d){
		name = n;
		
		kills = Integer.parseInt(k);
		wins = Integer.parseInt(w);
		deaths = Integer.parseInt(d);
	}
	
	public String getName(){
		return name;
	}
	
	public Record setName(String n){
		name = n;
		
		return this;
	}
	
	public void getKill(){
		kills++;
	}
	
	public void winGame(){
		wins++;
	}
	
	public void die(){
		deaths++;
	}
	
	public int getDeaths(){
		return deaths;
	}
	
	public int getWins(){
		return wins;
	}
	
	public int getKills(){
		return kills;
	}

	public String toString(){
		String ret = "";
		
		ret += name + ",";
		ret += kills + ",";
		ret += wins + ",";
		ret += deaths;
		
		return ret;
	}
	
	public static Record getRecord(String s){
		String[] split = s.split(",");
		
		Record ret = new Record(split[0], split[1], split[2], split[3]);
		return ret;
	}
	
	public static String toString(Record r){
		String ret = "";
		
		ret += r.getName() + ",";
		ret += r.getKills() + ",";
		ret += r.getWins() + ",";
		ret += r.getDeaths();
		
		return ret;
	}
}
