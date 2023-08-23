package com.alexogden.core;

import java.time.LocalDateTime;

public abstract class ServerTask implements Runnable {
	private boolean paused = false;
	private LocalDateTime lastExecutionTime;

	public ServerTask() {
		lastExecutionTime = LocalDateTime.of(2023, 8, 1, 0, 0);
	}

	public final void run() {
		executeTask();
		setLastExecutionTime();
	}

	protected abstract void executeTask();

	private void setLastExecutionTime() {
		lastExecutionTime = LocalDateTime.now();
	}

	public LocalDateTime getLastExecutionTime() {
		return lastExecutionTime;
	}

	public boolean isPaused() {
		return paused;
	}

	public void pause() {
		paused = true;
	}

	public void resume() {
		paused = false;
	}
}
