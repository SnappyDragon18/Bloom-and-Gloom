package mod.schnappdragon.habitat.core;

import mod.schnappdragon.habitat.client.renderer.entity.HabitatEntityRenderers;
import mod.schnappdragon.habitat.client.renderer.tileentity.HabitatTileEntityRenderers;
import mod.schnappdragon.habitat.common.block.misc.HabitatWoodTypes;
import mod.schnappdragon.habitat.core.api.conditions.RecipeConditions;
import mod.schnappdragon.habitat.core.misc.*;
import mod.schnappdragon.habitat.core.dispenser.HabitatDispenserBehaviours;
import mod.schnappdragon.habitat.core.registry.HabitatFeatures;
import mod.schnappdragon.habitat.core.registry.HabitatConfiguredFeatures;
import mod.schnappdragon.habitat.client.renderer.HabitatRenderLayers;
import mod.schnappdragon.habitat.core.registry.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Habitat.MOD_ID)
public class Habitat {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "habitat";
    public static final boolean DEV = !FMLLoader.isProduction();

    public Habitat() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        HabitatBlocks.BLOCKS.register(modEventBus);
        HabitatItems.ITEMS.register(modEventBus);
        HabitatTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
        HabitatSoundEvents.SOUND_EVENTS.register(modEventBus);
        HabitatEntityTypes.ENTITY_TYPES.register(modEventBus);
        HabitatEffects.EFFECTS.register(modEventBus);
        HabitatPotions.POTIONS.register(modEventBus);
        HabitatRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
        HabitatFeatures.FEATURES.register(modEventBus);
        HabitatStructures.STRUCTURE_FEATURES.register(modEventBus);
        HabitatParticleTypes.PARTICLE_TYPES.register(modEventBus);

        RecipeConditions.registerSerializers();

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            HabitatStructures.setupStructures();
            HabitatBrewingMixes.registerBrewingMixes();
            HabitatConfiguredFeatures.registerConfiguredFeatures();
            HabitatStructurePieceTypes.registerStructurePieceTypes();
            HabitatConfiguredStructures.registerConfiguredStructures();
            HabitatComposterChances.registerComposterChances();
            HabitatDispenserBehaviours.registerDispenserBehaviour();
            HabitatFireInfo.registerFireInfo();
            HabitatPOI.addBeehivePOI();
        });
    }

    private void clientSetup(FMLClientSetupEvent event) {
        HabitatRenderLayers.registerRenderLayers();
        HabitatTileEntityRenderers.registerRenderers();
        HabitatEntityRenderers.registerRenderers(event.getMinecraftSupplier());

        event.enqueueWork(HabitatWoodTypes::setupAtlas);
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }
}