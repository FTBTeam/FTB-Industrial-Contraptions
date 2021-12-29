package dev.ftb.mods.ftbic.screen.sync;

import java.util.function.IntSupplier;

public class ShortValue extends SyncedDataValue<Integer> {
	private final IntSupplier value;

	public ShortValue(IntSupplier v) {
		value = v;
	}

	@Override
	public void write(int[] data) {
		data[index] = value.getAsInt();
	}

	@Override
	public Integer read(int[] data) {
		return data[index];
	}
}
