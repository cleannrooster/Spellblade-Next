package net.spellbladenext.fabric.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MonkeyStaff extends AxeItem {
    public MonkeyStaff(float f, float g, Properties properties) {
        super(ModTiers.WOOD, f, g, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("Strength is a king's greatest virtue.").withStyle(ChatFormatting.DARK_RED));
        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity livingEntity, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, (e) -> {
            e.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        return true;
    }
}
