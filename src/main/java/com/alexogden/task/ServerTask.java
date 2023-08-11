package com.alexogden.task;

public abstract class ServerTask implements Runnable {
	private boolean paused = false;

	public boolean isPaused() {
		return paused;
	}

	public void pause() {
		paused = true;
	}

	public void resume() {
		paused = false;
	}

	public abstract void run();
}
