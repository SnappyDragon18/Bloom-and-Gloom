package mod.schnappdragon.habitat.core.util;

import mod.schnappdragon.habitat.core.Habitat;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

public class CompatHelper {
    public static boolean checkMods(String... modids) {
        if (!Habitat.DEV) {
            ModList modList = ModList.get();
            for (String modid : modids) {
                if (!modList.isLoaded(modid))
                    return false;
            }
        }
        return true;
    }

    public static ItemGroup compatItemGroup(ItemGroup group, String... modids) {
        return checkMods(modids) ? group : null;
    }
}