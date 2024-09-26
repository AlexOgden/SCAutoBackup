package com.alexogden.command;

import com.alexogden.backup.BackupGenerator;
import com.alexogden.backup.BackupTask;
import com.alexogden.core.SCAutoBackup;
import com.alexogden.core.ServerTask;
import com.alexogden.core.logging.MessageLogger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

public class CommandHandler implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String subcommand, String[] arguments) {
		if (!commandSender.hasPermission("scbackup.admin")) {
			MessageLogger.sendPlayerMessage(commandSender, "<red>You do not have permission to use this command!</red>");
			return false;
		}

		if (arguments.length != 1) {
			MessageLogger.sendPlayerMessage(commandSender, "<red>ERROR:</red> command only accepts one sub-command! <save | backup | pause | resume>");
			return false;
		}

		String subCommand = arguments[0];
		switch (subCommand) {
			case "save" -> handleSaveCommand();
			case "backup" -> handleBackupCommand(commandSender);
			case "pause" -> handlePauseCommand(commandSender, true);
			case "resume" -> handlePauseCommand(commandSender, false);
			case "status" -> handleStatusCommand(commandSender);
			default -> MessageLogger.sendPlayerMessage(commandSender, "<red>Unknown sub-command: " + subCommand + "</red>");
		}
		return true;
	}

	private void handleSaveCommand() {
		SCAutoBackup.getInstance().getSaveTask().run();
	}

	private void handleBackupCommand(CommandSender commandSender) {
		BackupGenerator backupGenerator = BackupGenerator.getInstance();
		SCAutoBackup autoBackup = SCAutoBackup.getInstance();
		BackupTask backupTask = autoBackup.getBackupTask();

		if (backupGenerator.isBackupInProgress()) {
			MessageLogger.sendPlayerMessage(commandSender, "<red>Backup already in progress!</red>");
		} else {
			MessageLogger.sendPlayerMessage(commandSender, "<aqua>Starting Manual Backup</aqua>");
			boolean backupPaused = backupTask.isPaused();

			if (backupPaused) {
				backupTask.resume();
			}

			backupTask.run();

			if (backupPaused) {
				backupTask.pause();
			}
		}
	}

	private void handlePauseCommand(CommandSender commandSender, boolean pause) {
		ServerTask backupTask = SCAutoBackup.getInstance().getBackupTask();
		boolean isPaused = backupTask.isPaused();

		if (isPaused == pause) {
			String statusMessage = isPaused ? "<yellow>Auto Backup is already paused</yellow>"
					: "<yellow>Auto Backup is already in action</yellow>";
			MessageLogger.sendPlayerMessage(commandSender, statusMessage);
			return;
		}

		if (pause) {
			backupTask.pause();
			MessageLogger.sendPlayerMessage(commandSender, "Auto Backup <bold><red>PAUSED</red></bold>");
			MessageLogger.sendConsoleMessage(Level.INFO, "Auto Backup Paused");
		} else {
			backupTask.resume();
			MessageLogger.sendPlayerMessage(commandSender, "Auto Backup <bold><green>RESUMED</green></bold>");
			MessageLogger.sendConsoleMessage(Level.INFO, "Auto Backup Resumed");
		}
	}

	private void handleStatusCommand(CommandSender commandSender) {
		ServerTask backupTask = SCAutoBackup.getInstance().getBackupTask();
		boolean isPaused = backupTask.isPaused();

		String statusMessage = isPaused
				? "Auto Backup Status: <bold><red>PAUSED</red></bold>"
				: "Auto Backup Status: <bold><green>ACTIVE</green></bold>";

		String lastBackupTime = backupTask.getLastExecutionTime() != null
				? backupTask.getLastExecutionTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
				: "Never";

		MessageLogger.sendPlayerMessage(commandSender, statusMessage);
		MessageLogger.sendPlayerMessage(commandSender, "Last Backup: " + lastBackupTime);
	}
}
