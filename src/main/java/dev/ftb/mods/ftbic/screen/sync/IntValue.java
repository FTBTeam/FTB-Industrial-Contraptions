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
		int b0 = data[index] & 0xFFFF;
		int b1 = (data[index + 1] & 0xFFFF) << 16;
		return b0 | b1;
	}
}