package techreborn.init;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import techreborn.TechReborn;

public class TRItemSettings {
	public static Item.Settings item(String name) {
		return new Item.Settings().registryKey(key(name));
	}

	public static RegistryKey<Item> key(String name) {
		return RegistryKey.of(Registries.ITEM.getKey(), Identifier.of(TechReborn.MOD_ID, name));
	}
}
