package dev.ftb.mods.ftbic.screen.sync;

import java.util.function.IntSupplier;

public class IntValue extends SyncedDataValue<Integer> {
	private final IntSupplier value;

	public IntValue(IntSupplier v) {
		value = v;
	}

	@Override
	public int getSize() {
		return 2;
	}

	@Override
	public void write(int[] data) {
		int v = value.getAsInt();
		data[index] = (v >> 0) & 0xFFFF;
		data[index + 1] = (v >> 16) & 0xFFFF;
	}

	@Override
	public Integer read(int[] data) {
		return data[index] | (data[index + 1] << 16);
	}
}