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
	public List<String> groups;
	public List<String> worlds;
	private boolean broadcast;
	private boolean notifyRanked;
	private boolean notifySender;
	private static final String VERSION = " v1.5";
	private bPermsRankCommandExecutor cmdExecutor;

	public void onEnable() {
		this.log = getLogger();
		this.configFile = new File(getDataFolder(), "config.yml");
		try {
			firstRun();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.config = new YamlConfiguration();
		loadYamls();
		setConfigOptions();
		loadCommands();
		this.log.info("There were " + this.groups.size()
				+ " groups loaded from the config.yml");
		this.log.info(getName() + " v1.4" + " enabled!");
	}

	private void loadCommands() {
		this.cmdExecutor = new bPermsRankCommandExecutor(this);
		getCommand("bpermsrank").setExecutor(this.cmdExecutor);
		getCommand("rank").setExecutor(this.cmdExecutor);
		getCommand("rankoffline").setExecutor(this.cmdExecutor);
		getCommand("rankinfo").setExecutor(this.cmdExecutor);
	}

	private void setConfigOptions() {
		this.groups = this.config.getStringList("groups");
		this.worlds = this.config.getStringList("worlds");
		this.broadcast = this.config.getBoolean("broadcast", true);
		this.notifyRanked = this.config.getBoolean("notifyranked", true);
		this.notifySender = this.config.getBoolean("notifysender", true);
	}

	public boolean hasBroadcast() {
		return this.broadcast;
	}

	public boolean hasNotifyRanked() {
		return this.notifyRanked;
	}

	public boolean hasNotifySender() {
		return this.notifySender;
	}

	public void saveYamls() {
		try {
			this.config.save(this.configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadYamls() {
		try {
			this.config.load(this.configFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void firstRun() throws Exception {
		if (!this.configFile.exists()) {
			this.configFile.getParentFile().mkdirs();
			copy(getResource("config.yml"), this.configFile);
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

	@Override
	public void reloadConfig() {
		loadYamls();
		setConfigOptions();
	}

	@Override
	public void onDisable() {
		this.log.info(getName() + VERSION + " disabled!");
	}
}