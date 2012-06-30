package com.mutinycraft.jigsaw.bPermsRank;

import de.bananaco.bpermissions.api.ApiLayer;
import de.bananaco.bpermissions.api.util.CalculableType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class bPermsRankCommandExecutor implements CommandExecutor {
	private bPermsRank plugin;
	private bPermsRankMessages msg;
	private bPermsRankValidation validate;
	private String[] data;
	private Player playerToRank;
	private Player player;

	public bPermsRankCommandExecutor(bPermsRank plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		this.player = null;
		this.data = args;
		if ((sender instanceof Player)) {
			this.player = ((Player) sender);
		}
		if (cmd.getName().equalsIgnoreCase("bpermsrank")) {
			return commandbPermsRank(sender);
		}
		if (cmd.getName().equalsIgnoreCase("rank")) {
			return commandRank(sender);
		}
		if (cmd.getName().equalsIgnoreCase("rankoffline")) {
			return commandRankOffline(sender);
		}
		if (cmd.getName().equalsIgnoreCase("rankinfo")) {
			return commandRankInfo(sender);
		}
		return false;
	}

	private boolean commandbPermsRank(CommandSender sender) {
		if ((this.data.length == 1)
				&& (this.data[0].equalsIgnoreCase("reload"))) {
			if (this.player == null) {
				this.plugin.reloadConfig();
				this.plugin.log.info("bPermsRank config.yml reloaded");
			} else if (this.player.hasPermission("bpermsrank.reload")) {
				this.plugin.reloadConfig();
				this.player.sendMessage(ChatColor.RED
						+ "bPermsRank config.yml reloaded");
			} else {
				msg.displayHelp(sender);
			}
			return true;
		}

		msg.displayHelp(sender);
		return true;
	}

	private boolean commandRank(CommandSender sender) {
		if ((this.data.length != 2) && (this.data.length != 3)) {
			sender.sendMessage(ChatColor.RED + "Usage: /rank name rank [world]");
			return true;
		}

		if (this.player == null)
			try {
				validate.isValidGroup(this.data[1].toLowerCase());
				this.playerToRank = this.plugin.getServer().getPlayerExact(
						this.data[0]);
				validate.isValidPlayer(this.playerToRank);
				if (this.data.length == 2) {
					rankPlayerAllWorlds(this.playerToRank.getName(),
							this.data[1].toLowerCase());
					msg.broadcast(sender, this.playerToRank, this.data, 0);
				} else {
					validate.isValidWorld(this.data[2].toLowerCase());
					rankPlayerInSingleWorld(this.playerToRank.getName(),
							this.data[1].toLowerCase(),
							this.data[2].toLowerCase());
					msg.broadcast(sender, this.playerToRank, this.data, 1);
				}
			} catch (CommandException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
			}
		else {
			try {
				validate.hasPermissionForPlugin(this.player);
				validate.isValidGroup(this.data[1].toLowerCase());
				this.playerToRank = this.plugin.getServer().getPlayerExact(
						this.data[0]);
				validate.isValidPlayer(this.playerToRank);
				validate.hasPermissionForGroup(this.player, this.data[1].toLowerCase());
				if (this.data.length == 2) {
					rankPlayerAllWorlds(this.playerToRank.getName(),
							this.data[1].toLowerCase());
					msg.broadcast(this.player, this.playerToRank, this.data, 0);
				} else {
					validate.isValidWorld(this.data[2].toLowerCase());
					rankPlayerInSingleWorld(this.playerToRank.getName(),
							this.data[1].toLowerCase(),
							this.data[2].toLowerCase());
					msg.broadcast(this.player, this.playerToRank, this.data, 1);
				}
			} catch (CommandException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
			}
		}
		return true;
	}

	private boolean commandRankOffline(CommandSender sender) {
		if ((this.data.length != 2) && (this.data.length != 3)) {
			sender.sendMessage(ChatColor.RED
					+ "Usage: /rankoffline name rank [world]");
			return true;
		}
		if (this.player == null)
			try {
				validate.isValidGroup(this.data[1].toLowerCase());
				if (this.data.length == 2) {
					rankPlayerAllWorlds(this.data[0], this.data[1]);
					msg.broadcastOffline(0, sender);
				}
				else{
					validate.isValidWorld(this.data[2].toLowerCase());
					rankPlayerInSingleWorld(this.data[0],
							this.data[1].toLowerCase(), this.data[2].toLowerCase());
					msg.broadcastOffline(1, sender);
				}
			} catch (CommandException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
			}
		else {
			try {
				validate.hasPermissionForPlugin(this.player);
				if (!this.player.hasPermission("bpermsrank.rankoffline")) {
					throw new CommandException(
							"You may not rank offline players!");
				}
				validate.isValidGroup(this.data[1].toLowerCase());
				validate.hasPermissionForGroup(this.player, this.data[1].toLowerCase());
				if (this.data.length == 2) {
					rankPlayerAllWorlds(this.data[0], this.data[1]);
					msg.broadcastOffline(0, sender);
				} else {
					validate.isValidWorld(this.data[2].toLowerCase());
					rankPlayerInSingleWorld(this.data[0],
							this.data[1].toLowerCase(),
							this.data[2].toLowerCase());
					msg.broadcastOffline(1, sender);
				}
			} catch (CommandException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
			}
		}
		return true;
	}

	private boolean commandRankInfo(CommandSender sender) {
		try {
			if ((this.player != null)
					&& (!this.player.hasPermission("bpermsrank.rankinfo"))) {
				throw new CommandException("You may not use this command!");
			}

			if (this.data.length != 2) {
				throw new CommandException("Usage: /rankinfo name world");
			}
			String playerName = this.data[0];
			String worldName = this.data[1];
			validate.isValidWorld(worldName);
			String groups = getPlayerGroups(worldName, playerName);
			sender.sendMessage(ChatColor.AQUA + "NOTICE: " + ChatColor.RED
					+ playerName + ChatColor.RED + " has the group(s): [ "
					+ groups + "] in world: " + worldName);
		} catch (CommandException e) {
			sender.sendMessage(ChatColor.RED + e.getMessage());
		}
		return true;
	}
	
	public String getData(int i){
		return this.data[i];
	}

	public String getGroup() {
		return this.data[1];
	}

	public String getSenderName() {
		if (this.player == null) {
			return "Console";
		}
		return this.player.getDisplayName();
	}

	public String getPlayerToRankName() {
		return this.playerToRank.getDisplayName();
	}

	public String getWorldName() {
		if (this.data.length == 3) {
			return this.data[2];
		}
		return null;
	}
	
	public bPermsRank getPlugin(){
		return plugin;
	}

	public String getPlayerGroups(String worldName, String playerName)
			throws CommandException {
		String[] groups = ApiLayer.getGroups(worldName, CalculableType.USER,
				playerName);

		if (groups == null) {
			throw new CommandException("Player not found!");
		}

		StringBuffer temp = new StringBuffer();
		for (int i = 0; i < groups.length; i++) {
			temp.append(groups[i] + " ");
		}
		String results = temp.toString();

		return results;
	}

	private void rankPlayerAllWorlds(String playerToRankName, String group) {
		for (int i = 0; i < this.plugin.worlds.size(); i++) {
			String worldName = (String) this.plugin.worlds.get(i);
			ApiLayer.setGroup(worldName, CalculableType.USER, playerToRankName,
					group);
		}
	}

	private void rankPlayerInSingleWorld(String playerToRankName, String group,
			String worldName) {
		ApiLayer.setGroup(worldName, CalculableType.USER, playerToRankName,
				group);
	}

}
