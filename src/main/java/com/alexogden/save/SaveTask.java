package com.alexogden.save;

import com.alexogden.core.SCAutoBackup;
import com.alexogden.core.logging.MessageLogger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SaveTask implements Runnable {

	@Override
	public void run() {
		MessageLogger.broadcast("<gray>Saving World...</gray>");
		saveWorlds();
		savePlayers();
	}

	private void saveWorlds() {
		for (World world : Bukkit.getWorlds()) {
			Bukkit.getScheduler().runTask(SCAutoBackup.getInstance(), world::save);
		}
	}

	private void savePlayers() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			Bukkit.getScheduler().runTask(SCAutoBackup.getInstance(), player::saveData);
		}
	}
}
