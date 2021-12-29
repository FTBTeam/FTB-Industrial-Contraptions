package dev.ftb.mods.ftbic.screen.sync;

import java.util.function.DoubleSupplier;

public class DoubleValue extends SyncedDataValue<Double> {
	private final DoubleSupplier value;

	public DoubleValue(DoubleSupplier v) {
		value = v;
	}

	@Override
	public int getSize() {
		return 4;
	}

	@Override
	public void write(int[] data) {
		long bits = Double.doubleToLongBits(value.getAsDouble());
		data[index] = (int) ((bits >> 0L) & 0xFFFFL);
		data[index + 1] = (int) ((bits >> 16L) & 0xFFFFL);
		data[index + 2] = (int) ((bits >> 32L) & 0xFFFFL);
		data[index + 3] = (int) ((bits >> 48L) & 0xFFFFL);
	}

	@Override
	public Double read(int[] data) {
		long bits = ((long) data[index]) | (((long) data[index + 1]) << 16L) | (((long) data[index + 2]) << 32L) | (((long) data[index + 3]) << 48L);
		return Double.longBitsToDouble(bits);
	}
}