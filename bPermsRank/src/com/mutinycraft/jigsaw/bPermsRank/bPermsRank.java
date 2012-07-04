package com.mutinycraft.jigsaw.bPermsRank;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class bPermsRank extends JavaPlugin {
	
	Logger log;
	File configFile;
	FileConfiguration config;
	
	private List<String> groups;
	private List<String> worlds;
	private boolean broadcast;
	private boolean notifyRanked;
	private boolean notifySender;
	private String broadcastMessage;
	private String senderMessage;
	private String rankedMessage;
	private static final String VERSION = " v1.5";
	private bPermsRankCommandExecutor cmdExecutor;

	public void onEnable() {
		log = getLogger();
		configFile = new File(getDataFolder(), "config.yml");
		try {
			firstRun();
		} catch (Exception e) {
			e.printStackTrace();
		}
		config = new YamlConfiguration();
		loadYamls();
		setConfigOptions();
		loadCommands();
		log.info("There were " + groups.size() + " groups loaded from the config.yml");
		log.info("There were " + worlds.size() + " worlds loaded from the config.yml");
		log.info(getName() + VERSION + " enabled!");
	}

	private void loadCommands() {
		cmdExecutor = new bPermsRankCommandExecutor(this);
		getCommand("bpermsrank").setExecutor(cmdExecutor);
		getCommand("rank").setExecutor(cmdExecutor);
		getCommand("rankoffline").setExecutor(cmdExecutor);
		getCommand("rankinfo").setExecutor(cmdExecutor);
	}

	private void setConfigOptions() {
		groups = config.getStringList("groups");
		worlds = config.getStringList("worlds");
		broadcast = config.getBoolean("broadcast", true);
		notifyRanked = config.getBoolean("notifyranked", true);
		notifySender = config.getBoolean("notifysender", true);
		broadcastMessage = config.getString("broadcastmessage");
		rankedMessage = config.getString("rankedmessage");
		senderMessage = config.getString("sendermessage");
	}

	public void saveYamls() {
		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadYamls() {
		try {
			config.load(configFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void firstRun() throws Exception {
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			copy(getResource("config.yml"), configFile);
		}
	}

	private void copy(InputStream in, File file) {
		try {
			OutputStream fout = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				fout.write(buf, 0, len);
			}
			fout.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isBroadcast() {
		return broadcast;
	}

	public boolean isNotifyRanked() {
		return notifyRanked;
	}

	public boolean isNotifySender() {
		return notifySender;
	}
	
	public List<String> getGroups(){
		return groups;
	}
	
	public List<String> getWorlds(){
		return worlds;
	}
	
	public String getBroadcastMessage() {
		return broadcastMessage;
	}

	public String getSenderMessage() {
		return senderMessage;
	}

	public String getRankedMessage() {
		return rankedMessage;
	}

	@Override
	public void reloadConfig() {
		loadYamls();
		setConfigOptions();
	}

	@Override
	public void onDisable() {
		log.info(getName() + VERSION + " disabled!");
	}
}