package net.spellbladenext.fabric.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.spell_power.api.enchantment.Enchantments_SpellPower;
import net.spellbladenext.fabric.SpellbladesFabric;
import net.spellbladenext.fabric.items.armors.Armors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(Player.class)
public class PlayerMixin {
    @Inject(at = @At("HEAD"), method = "getItemBySlot", cancellable = true)
    private void armor(EquipmentSlot equipmentSlot, CallbackInfoReturnable<ItemStack> info) {
        Player player = ((Player) (Object) this);
        if(player.hasEffect(SpellbladesFabric.MAGEARMOR.get())) {
            if (equipmentSlot == EquipmentSlot.HEAD) {
                ItemStack stack = new ItemStack(Armors.spectral.head);
                CompoundTag tag = player.getInventory().armor.get(equipmentSlot.getIndex()).getOrCreateTag().copy();
                if (!tag.contains("Enchantments", 9)) {
                    tag.put("Enchantments", new ListTag());
                }

                ListTag listTag = tag.getList("Enchantments", 10);
                listTag.add(EnchantmentHelper.storeEnchantment(EnchantmentHelper.getEnchantmentId(Enchantments_SpellPower.MAGIC_PROTECTION), (byte)5));

                stack.setTag(tag);

                info.setReturnValue(stack);
            } else if (equipmentSlot == EquipmentSlot.CHEST) {
                ItemStack stack = new ItemStack(Armors.spectral.chest);
                CompoundTag tag = player.getInventory().armor.get(equipmentSlot.getIndex()).getOrCreateTag().copy();
                if (!tag.contains("Enchantments", 9)) {
                    tag.put("Enchantments", new ListTag());
                }

                ListTag listTag = tag.getList("Enchantments", 10);
                listTag.add(EnchantmentHelper.storeEnchantment(EnchantmentHelper.getEnchantmentId(Enchantments_SpellPower.MAGIC_PROTECTION), (byte)5));
                stack.setTag(tag);

                info.setReturnValue(stack);

            } else if (equipmentSlot == EquipmentSlot.LEGS) {
                ItemStack stack = new ItemStack(Armors.spectral.legs);
                CompoundTag tag = player.getInventory().armor.get(equipmentSlot.getIndex()).getOrCreateTag().copy();
                if (!tag.contains("Enchantments", 9)) {
                    tag.put("Enchantments", new ListTag());
                }

                ListTag listTag = tag.getList("Enchantments", 10);
                listTag.add(EnchantmentHelper.storeEnchantment(EnchantmentHelper.getEnchantmentId(Enchantments_SpellPower.MAGIC_PROTECTION), (byte)5));
                stack.setTag(tag);

                info.setReturnValue(stack);

            } else if (equipmentSlot == EquipmentSlot.FEET) {
                ItemStack stack = new ItemStack(Armors.spectral.feet);
                CompoundTag tag = player.getInventory().armor.get(equipmentSlot.getIndex()).getOrCreateTag().copy();
                if (!tag.contains("Enchantments", 9)) {
                    tag.put("Enchantments", new ListTag());
                }

                ListTag listTag = tag.getList("Enchantments", 10);
                listTag.add(EnchantmentHelper.storeEnchantment(EnchantmentHelper.getEnchantmentId(Enchantments_SpellPower.MAGIC_PROTECTION), (byte)5));
                stack.setTag(tag);
                info.setReturnValue(stack);
            }
        }
    }
}
