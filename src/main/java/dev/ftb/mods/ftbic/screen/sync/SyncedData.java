package dev.ftb.mods.ftbic.screen.sync;

import net.minecraft.world.inventory.ContainerData;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;

public class SyncedData implements ContainerData {
	public static final SyncedDataKey<Double> ENERGY = new SyncedDataKey<>("energy", 0D);
	public static final SyncedDataKey<Double> ENERGY_CAPACITY = new SyncedDataKey<>("energy_capacity", 1000D);
	public static final SyncedDataKey<Integer> BAR = new SyncedDataKey<>("bar", 0);
	public static final SyncedDataKey<Integer> ACCELERATION = new SyncedDataKey<>("acceleration", 0);
	public static final SyncedDataKey<Boolean> PAUSED = new SyncedDataKey<>("paused", false);
	public static final SyncedDataKey<Boolean> ALLOW_REDSTONE_CONTROL = new SyncedDataKey<>("allow_redstone_control", false);

	private final Map<SyncedDataKey<?>, SyncedDataValue<?>> values;
	private int[] data;

	public SyncedData() {
		values = new LinkedHashMap<>();
		data = null;
	}

	public <T> void add(SyncedDataKey<T> key, SyncedDataValue<T> value) {
		values.put(key, value);
	}

	public void remove(SyncedDataKey<?> key) {
		values.remove(key);
	}

	public void setup() {
		int i = 0;

		for (SyncedDataValue<?> value : values.values()) {
			value.index = i;
			i += value.getSize();
		}

		data = new int[i];
	}

	public void update() {
		for (SyncedDataValue<?> value : values.values()) {
			value.write(data);
		}
	}

	public void addShort(SyncedDataKey<Integer> key, IntSupplier value) {
		add(key, new ShortValue(value));
	}

	public void addInt(SyncedDataKey<Integer> key, IntSupplier value) {
		add(key, new IntValue(value));
	}

	public void addDouble(SyncedDataKey<Double> key, DoubleSupplier value) {
		add(key, new DoubleValue(value));
	}

	public void addBoolean(SyncedDataKey<Boolean> key, BooleanSupplier value) {
		add(key, new BooleanValue(value));
	}

	@SuppressWarnings("unchecked")
	public <T> T get(SyncedDataKey<T> key) {
		SyncedDataValue<T> value = (SyncedDataValue<T>) values.get(key);
		return value == null ? key.defaultValue : value.get(key, data);
	}

	@Override
	public int get(int i) {
		return data[i];
	}

	@Override
	public void set(int i, int v) {
		data[i] = v;

		for (SyncedDataValue<?> value : values.values()) {
			value.clearCache();
		}
	}

	@Override
	public int getCount() {
		return data.length;
	}
}
