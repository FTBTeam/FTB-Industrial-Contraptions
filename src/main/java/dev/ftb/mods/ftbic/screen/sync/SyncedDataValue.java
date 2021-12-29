package dev.ftb.mods.ftbic.screen.sync;

public abstract class SyncedDataValue<T> {
	public int index;
	private T cachedValue = null;

	public int getSize() {
		return 1;
	}

	public abstract void write(int[] data);

	public abstract T read(int[] data);

	public void clearCache() {
		cachedValue = null;
	}

	public T get(SyncedDataKey<T> key, int[] data) {
		if (cachedValue == null) {
			cachedValue = read(data);

			if (cachedValue == null) {
				throw new IllegalArgumentException("Failed to parse data for key '" + key.name + "'!");
			}
		}

		return cachedValue;
	}
}
