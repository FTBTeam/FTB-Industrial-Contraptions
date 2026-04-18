package dev.ftb.mods.ftbic.test;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class DirectGameTestInstance extends GameTestInstance {

	private static final Map<String, Consumer<GameTestHelper>> FUNCTIONS = new ConcurrentHashMap<>();
	private static final Consumer<GameTestHelper> NOOP = GameTestHelper::succeed;

	static final MapCodec<DirectGameTestInstance> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
			Codec.STRING.fieldOf("name").forGetter(d -> d.name),
			TestData.CODEC.forGetter(d -> d.testData)
	).apply(i, DirectGameTestInstance::fromCodec));

	private final Consumer<GameTestHelper> testFunction;
	private final String name;
	private final TestData<Holder<TestEnvironmentDefinition<?>>> testData;

	public DirectGameTestInstance(String name, Consumer<GameTestHelper> testFunction,
								  TestData<Holder<TestEnvironmentDefinition<?>>> info) {
		super(info);
		this.name = name;
		this.testFunction = testFunction;
		this.testData = info;
		FUNCTIONS.put(name, testFunction);
	}

	private static DirectGameTestInstance fromCodec(String name, TestData<Holder<TestEnvironmentDefinition<?>>> info) {
		return new DirectGameTestInstance(name, FUNCTIONS.getOrDefault(name, NOOP), info);
	}

	@Override
	public void run(GameTestHelper helper) {
		testFunction.accept(helper);
	}

	@Override
	public MapCodec<? extends GameTestInstance> codec() {
		return CODEC;
	}

	@Override
	protected MutableComponent typeDescription() {
		return Component.literal(name);
	}
}
