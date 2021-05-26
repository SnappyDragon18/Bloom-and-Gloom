package mod.schnappdragon.habitat.common.item;

import mod.schnappdragon.habitat.common.entity.item.HabitatBoatEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Predicate;

public class HabitatBoatItem extends Item {
    private static final Predicate<Entity> ENTITY_PREDICATE = EntityPredicates.NOT_SPECTATING.and(Entity::canBeCollidedWith);
    private final HabitatBoatEntity.Type type;

    public HabitatBoatItem(HabitatBoatEntity.Type type, Item.Properties properties) {
        super(properties);
        this.type = type;
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        RayTraceResult raytraceresult = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.ANY);
        if (raytraceresult.getType() == RayTraceResult.Type.MISS)
            return ActionResult.resultPass(itemstack);
        else {
            Vector3d vector3d = playerIn.getLook(1.0F);
            double d0 = 5.0D;
                List<Entity> list = worldIn.getEntitiesInAABBexcluding(playerIn, playerIn.getBoundingBox().expand(vector3d.scale(5.0D)).grow(1.0D), ENTITY_PREDICATE);
            if (!list.isEmpty()) {
                Vector3d vector3d1 = playerIn.getEyePosition(1.0F);

                for(Entity entity : list) {
                    AxisAlignedBB axisalignedbb = entity.getBoundingBox().grow((double)entity.getCollisionBorderSize());
                    if (axisalignedbb.contains(vector3d1))
                        return ActionResult.resultPass(itemstack);
                }
            }

            if (raytraceresult.getType() == RayTraceResult.Type.BLOCK) {
                HabitatBoatEntity boatentity = new HabitatBoatEntity(worldIn, raytraceresult.getHitVec().x, raytraceresult.getHitVec().y, raytraceresult.getHitVec().z);
                boatentity.setBoatType(this.type);
                boatentity.rotationYaw = playerIn.rotationYaw;
                if (!worldIn.hasNoCollisions(boatentity, boatentity.getBoundingBox().grow(-0.1D)))
                    return ActionResult.resultFail(itemstack);
                else {
                    if (!worldIn.isRemote) {
                        worldIn.addEntity(boatentity);
                        if (!playerIn.abilities.isCreativeMode)
                            itemstack.shrink(1);
                    }

                    playerIn.addStat(Stats.ITEM_USED.get(this));
                    return ActionResult.func_233538_a_(itemstack, worldIn.isRemote());
                }
            }
            else
                return ActionResult.resultPass(itemstack);
        }
    }
}
