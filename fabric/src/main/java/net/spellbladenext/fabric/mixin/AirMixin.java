package net.spellbladenext.fabric.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.spell_engine.entity.SpellProjectile;
import net.spell_engine.internals.SpellHelper;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.SpellPower;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.entities.Eruption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public class AirMixin {
    @Inject(at = @At("HEAD"), method = "addFreshEntity", cancellable = true)
    private void setFlaming(Entity entity, CallbackInfoReturnable<Boolean> info) {
        if(entity instanceof SpellProjectile spellProjectile && !(entity instanceof Eruption)){

            if(spellProjectile.getItem().getItem().equals(Items.IRON_BLOCK) && spellProjectile.getOwner() instanceof Player player) {
                if(player.getInventory().contains(Items.IRON_BLOCK.getDefaultInstance())){
                    for(int i = 0; i < player.getInventory().getContainerSize(); ++i) {
                        ItemStack stack = player.getInventory().getItem(i);
                        if (stack.is(Items.IRON_BLOCK)) {
                            stack.shrink(1);
                            if (stack.isEmpty()) {
                                player.getInventory().removeItem(stack);
                            }
                            ItemStack stack1 = SpellbladeNext.RUNEFROSTPLATING.get().getDefaultInstance();
                            stack1.setCount(4);

                            ItemEntity entity1 = new ItemEntity(player.getLevel(),player.getX(),player.getY(),player.getZ(),stack1);
                            player.getLevel().addFreshEntity(entity1);
                            break;
                        }
                    }
                }
                info.cancel();
            }
            if(spellProjectile.getItem().getItem().equals(Items.COPPER_BLOCK) && spellProjectile.getOwner() instanceof Player player) {
                if(player.getInventory().contains(Items.COPPER_BLOCK.getDefaultInstance())){
                    for(int i = 0; i < player.getInventory().getContainerSize(); ++i) {
                        ItemStack stack = player.getInventory().getItem(i);
                        if (stack.is(Items.COPPER_BLOCK)) {
                            stack.shrink(1);
                            if (stack.isEmpty()) {
                                player.getInventory().removeItem(stack);
                            }
                            ItemStack stack1 = SpellbladeNext.RUNEBLAZEPLATING.get().getDefaultInstance();
                            stack1.setCount(4);
                            ItemEntity entity1 = new ItemEntity(player.getLevel(),player.getX(),player.getY(),player.getZ(),stack1);
                            player.getLevel().addFreshEntity(entity1);
                            break;
                        }
                    }
                }
                info.cancel();
            }
            if(spellProjectile.getItem().getItem().equals(Items.GOLD_BLOCK) && spellProjectile.getOwner() instanceof Player player) {
                if(player.getInventory().contains(Items.GOLD_BLOCK.getDefaultInstance())){
                    for(int i = 0; i < player.getInventory().getContainerSize(); ++i) {
                        ItemStack stack = player.getInventory().getItem(i);
                        if (stack.is(Items.GOLD_BLOCK)) {
                            stack.shrink(1);
                            if (stack.isEmpty()) {
                                player.getInventory().removeItem(stack);
                            }
                            ItemStack stack1 = SpellbladeNext.RUNEGLINTPLATING.get().getDefaultInstance();
                            stack1.setCount(4);
                            ItemEntity entity1 = new ItemEntity(player.getLevel(),player.getX(),player.getY(),player.getZ(),stack1);
                            player.getLevel().addFreshEntity(entity1);
                            break;
                        }
                    }

                }
                info.cancel();
            }
        }
    }
}