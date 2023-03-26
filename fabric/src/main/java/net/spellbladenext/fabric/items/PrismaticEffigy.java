package net.spellbladenext.fabric.items;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.spellbladenext.fabric.ExampleModFabric;
import net.spellbladenext.fabric.Magus;
import net.spellbladenext.fabric.piglinsummon;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.StreamSupport;

public class PrismaticEffigy extends Item {
    public PrismaticEffigy(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        if(level instanceof ServerLevel level1 && StreamSupport.stream(level1.getAllEntities().spliterator(),true).filter(entity -> entity instanceof Magus).count() < 1) {
            for (int i = 0; i < 10; i++) {
                BlockPos vec3 = piglinsummon.getSafePositionAroundPlayer(level, player.getOnPos(), 10);
                if (vec3 != null && !level.isClientSide()) {
                    Magus magus = new Magus(ExampleModFabric.MAGUS, level);
                    magus.setPos(vec3.getX(), vec3.getY(), vec3.getZ());
                    if (!player.isCreative()) {
                        player.getItemInHand(interactionHand).shrink(1);
                        if (player.getItemInHand(interactionHand).isEmpty()) {
                            player.getInventory().removeItem(player.getItemInHand(interactionHand));
                        }
                        magus.spawnedfromitem = true;
                    }
                    level.addFreshEntity(magus);
                    return InteractionResultHolder.consume(player.getItemInHand(interactionHand));

                }
            }
            player.sendSystemMessage(Component.translatable("Magus has no room at your location"));
        }
        else{
            if(level instanceof ServerLevel) {
                player.sendSystemMessage(Component.translatable("Magus is busy elsewhere"));
            }
        }
        return super.use(level, player, interactionHand);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("Use to summon Magus, if available."));
        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }
}
