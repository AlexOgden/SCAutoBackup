package com.alexogden.command;

import com.alexogden.backup.BackupGenerator;
import com.alexogden.core.SCAutoBackup;
import com.alexogden.core.logging.MessageLogger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandHandler implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String subcommand, String[] arguments) {
		if (!commandSender.hasPermission("scbackup.admin")) {
			MessageLogger.sendMessage(commandSender, "<red>You do not have permission to use this command!</red>");
			return false;
		}

		if (arguments.length != 1) {
			MessageLogger.sendMessage(commandSender, "<red>ERROR:</red> command only accepts one sub-command!");
			return false;
		}

		String subCommand = arguments[0];
		switch (subCommand) {
			case "save" -> handleSaveCommand();
			case "backup" -> handleBackupCommand(commandSender);
			default -> MessageLogger.sendMessage(commandSender, "<red>Unknown sub-command: " + subCommand + "</red>");
		}
		return true;
	}

	private void handleSaveCommand() {
		SCAutoBackup.getInstance().getSaveTask().run();
	}

	private void handleBackupCommand(CommandSender commandSender) {
		MessageLogger.sendMessage(commandSender, "<aqua>Starting Manual Backup</aqua>");
		if (BackupGenerator.getInstance().isBackupInProgress()) {
			MessageLogger.sendMessage(commandSender, "<red>Backup already in progress!</red>");
		} else {
			SCAutoBackup.getInstance().getBackupTask().run();
		}
	}
}
