package com.alexogden.event;

import com.alexogden.core.SCAutoBackup;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

public class ServerShutdownEvent implements Listener {

	@EventHandler
	public void onServerCommand(ServerCommandEvent event) {
		String command = event.getCommand();
		if (command.equalsIgnoreCase("stop") || command.equalsIgnoreCase("shutdown")) {
			SCAutoBackup.getInstance().getBackupTask().pause();
		}
	}
}
