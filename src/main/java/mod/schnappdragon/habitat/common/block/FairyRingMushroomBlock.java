package mod.schnappdragon.habitat.common.block;

import java.util.Random;

import mod.schnappdragon.habitat.common.block.state.properties.HabitatBlockStateProperties;
import mod.schnappdragon.habitat.core.registry.HabitatConfiguredFeatures;
import mod.schnappdragon.habitat.core.registry.HabitatItems;
import mod.schnappdragon.habitat.core.registry.HabitatParticleTypes;
import mod.schnappdragon.habitat.core.registry.HabitatSoundEvents;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShearsItem;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.server.ServerWorld;

public class FairyRingMushroomBlock extends BushBlock implements IGrowable {
    protected static final VoxelShape[] SHAPE = {Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 13.0D, 10.0D), Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 14.0D, 13.0D), Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D), Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D)};

    public static final IntegerProperty MUSHROOMS = HabitatBlockStateProperties.MUSHROOMS_1_4;
    public static final BooleanProperty DUSTED = HabitatBlockStateProperties.DUSTED;

    public FairyRingMushroomBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(MUSHROOMS, 1).with(DUSTED, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(MUSHROOMS, DUSTED);
    }

    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE[state.get(MUSHROOMS) - 1];
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.down()).isOpaqueCube(worldIn, pos.down());
    }

    /*
     * Particle Animation Method
     */

    public void animateTick(BlockState state, World worldIn, BlockPos pos, Random rand) {
        if (state.get(DUSTED) && rand.nextInt(24 - 2 * state.get(MUSHROOMS)) == 0)
            worldIn.addParticle(RedstoneParticleData.REDSTONE_DUST, pos.getX() + rand.nextDouble(), pos.getY() + rand.nextDouble(), pos.getZ() + rand.nextDouble(), 0.0D, 0.0D, 0.0D);

        if (rand.nextInt(12 - state.get(MUSHROOMS)) == 0)
            worldIn.addParticle(HabitatParticleTypes.FAIRY_RING_SPORE.get(), pos.getX() + rand.nextDouble(), pos.getY() + rand.nextDouble(), pos.getZ() + rand.nextDouble(), rand.nextGaussian() * 0.01D, 0.0D, rand.nextGaussian() * 0.01D);
    }

    /*
     * Right Click Method
     */

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (player.getHeldItem(handIn).getItem() instanceof ShearsItem && state.get(MUSHROOMS) > 1) {
            spawnAsEntity(worldIn, pos, new ItemStack(getBlock()));
            player.getHeldItem(handIn).damageItem(1, player, (playerIn) -> {
                playerIn.sendBreakAnimation(handIn);
            });
            worldIn.setBlockState(pos, state.with(MUSHROOMS, state.get(MUSHROOMS) - 1), 2);
            worldIn.playSound(null, pos, HabitatSoundEvents.BLOCK_FAIRY_RING_MUSHROOM_SHEAR.get(), SoundCategory.BLOCKS, 1.0F, 0.8F + worldIn.rand.nextFloat() * 0.4F);
            return ActionResultType.func_233537_a_(worldIn.isRemote);
        }
        if (player.getHeldItem(handIn).getItem() == HabitatItems.FAIRY_RING_MUSHROOM.get() && state.get(MUSHROOMS) < 4) {
            if (!player.abilities.isCreativeMode)
                player.getHeldItem(handIn).shrink(1);
            worldIn.setBlockState(pos, state.with(MUSHROOMS, state.get(MUSHROOMS) + 1), 2);
            worldIn.playSound(null, pos, SoundType.PLANT.getPlaceSound(), SoundCategory.BLOCKS, SoundType.PLANT.getVolume() + 1.0F / 2.0F, SoundType.PLANT.getPitch() * 0.8F);
            return ActionResultType.func_233537_a_(worldIn.isRemote);
        }
        if (player.getHeldItem(handIn).getItem() == Items.REDSTONE && !state.get(DUSTED)) {
            if (!player.abilities.isCreativeMode)
                player.getHeldItem(handIn).shrink(1);
            worldIn.setBlockState(pos, state.with(DUSTED, true), 2);
            worldIn.addParticle(RedstoneParticleData.REDSTONE_DUST, pos.getX() + 0.5D, pos.getY() + 0.125D, pos.getZ() + 0.5D, 0.0D, 0.0D, 0.0D);
            worldIn.playSound(null, pos, HabitatSoundEvents.BLOCK_FAIRY_RING_MUSHROOM_DUST.get(), SoundCategory.BLOCKS, 1.0F, 0.8F + worldIn.rand.nextFloat() * 0.4F);
            return ActionResultType.func_233537_a_(worldIn.isRemote);
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    /*
     * Redstone Power Method
     */

    @Override
    public int getWeakPower(BlockState state, IBlockReader worldIn, BlockPos pos, Direction side) {
        return state.get(DUSTED) ? state.get(MUSHROOMS) : 0;
    }

    /*
     * Growth Methods
     */

    public boolean ticksRandomly(BlockState state) {
        return state.get(MUSHROOMS) < 4 && !state.get(DUSTED);
    }

    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        if (state.get(MUSHROOMS) < 4 && random.nextInt(25) == 0)
            worldIn.setBlockState(pos, state.with(MUSHROOMS, state.get(MUSHROOMS) + 1), 2);
    }

    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
        return !state.get(DUSTED);
    }

    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
        return !state.get(DUSTED) && (state.get(MUSHROOMS) != 4 || rand.nextFloat() < (worldIn.getBlockState(pos.down()).isIn(BlockTags.MUSHROOM_GROW_BLOCK) ? 0.8F : 0.4F));
    }

    public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
        if (state.get(MUSHROOMS) < 4)
            worldIn.setBlockState(pos, state.with(MUSHROOMS, Math.min(4, state.get(MUSHROOMS) + MathHelper.nextInt(rand, 1, 2))), 2);
        else
            growHugeMushroom(worldIn, rand, pos, state);
    }

    private void growHugeMushroom(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
        world.removeBlock(pos, false);
        ConfiguredFeature<?, ?> configuredfeature = HabitatConfiguredFeatures.HUGE_FAIRY_RING_MUSHROOM;

        if (!configuredfeature.generate(world, world.getChunkProvider().getChunkGenerator(), rand, pos))
            world.setBlockState(pos, state, 3);
    }
}