package mod.schnappdragon.habitat.common.item;

import mod.schnappdragon.habitat.common.entity.projectile.KabloomFruitEntity;
import mod.schnappdragon.habitat.core.registry.HabitatItems;
import mod.schnappdragon.habitat.core.registry.HabitatSoundEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class KabloomFruitItem extends Item {
    public KabloomFruitItem(Item.Properties builder) {
        super(builder);
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        worldIn.playSound(null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), HabitatSoundEvents.ENTITY_KABLOOM_FRUIT_THROW.get(), SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
        playerIn.getCooldownTracker().setCooldown(this, 20);

        if (!playerIn.abilities.isCreativeMode) {
            itemstack.shrink(1);
        }

        if (!worldIn.isRemote) {
            KabloomFruitEntity kabloomfruitentity = new KabloomFruitEntity(worldIn, playerIn);
            kabloomfruitentity.setItem(new ItemStack(HabitatItems.KABLOOM_FRUIT.get()));
            kabloomfruitentity.func_234612_a_(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 0.5F, 0.9F);
            worldIn.addEntity(kabloomfruitentity);
        }

        return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
    }
}