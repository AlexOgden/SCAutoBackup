package com.alexogden.command;

import com.alexogden.backup.BackupGenerator;
import com.alexogden.core.SCAutoBackup;
import com.alexogden.core.logging.MessageLogger;
import com.alexogden.task.ServerTask;
import org.bukkit.Bukkit;
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
			MessageLogger.sendMessage(commandSender, "<red>ERROR:</red> command only accepts one sub-command! <save | backup | pause | resume>");
			return false;
		}

		String subCommand = arguments[0];
		switch (subCommand) {
			case "save" -> handleSaveCommand();
			case "backup" -> handleBackupCommand(commandSender);
			case "pause" -> handlePauseCommand(commandSender, true);
			case "resume" -> handlePauseCommand(commandSender, false);
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

	private void handlePauseCommand(CommandSender commandSender, boolean pause) {
		ServerTask backupTask = SCAutoBackup.getInstance().getBackupTask();
		boolean isCurrentlyPaused = backupTask.isPaused();

		if (isCurrentlyPaused == pause) {
			String statusMessage = isCurrentlyPaused ? "<yellow>Auto Backup is already paused</yellow>"
					: "<yellow>Auto Backup is already in action</yellow>";
			MessageLogger.sendMessage(commandSender, statusMessage);
			return;
		}

		if (pause) {
			SCAutoBackup.getInstance().getBackupTask().pause();
			MessageLogger.sendMessage(commandSender, "Auto Backup <bold><red>PAUSED</red></bold>");
			Bukkit.getLogger().info("Auto Backup Paused");
		} else {
			SCAutoBackup.getInstance().getBackupTask().resume();
			MessageLogger.sendMessage(commandSender, "Auto Backup <bold><green>RESUMED</green></bold>");
			Bukkit.getLogger().info("Auto Backup Resumed");
		}
	}
}
