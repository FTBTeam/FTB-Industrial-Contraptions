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
		long b0 = data[index] & 0xFFFFL;
		long b1 = (data[index + 1] & 0xFFFFL) << 16L;
		long b2 = (data[index + 2] & 0xFFFFL) << 32L;
		long b3 = (data[index + 3] & 0xFFFFL) << 48L;
		return Double.longBitsToDouble(b0 | b1 | b2 | b3);
	}
}