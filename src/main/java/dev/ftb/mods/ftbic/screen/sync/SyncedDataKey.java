package dev.ftb.mods.ftbic.screen.sync;

public class SyncedDataKey<T> {
	public final String name;
	public final T defaultValue;

	public SyncedDataKey(String name, T defaultValue) {
		this.name = name;
		this.defaultValue = defaultValue;
	}

	@Override
	public String toString() {
		return name;
	}
}
