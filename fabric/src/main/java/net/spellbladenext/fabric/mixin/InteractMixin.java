package net.spellbladenext.fabric.mixin;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.phys.HitResult;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.internals.SpellCast;
import net.spell_engine.internals.SpellCasterClient;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.SpellRegistry;
import net.spell_engine.mixin.client.ClientPlayerEntityMixin;
import net.spell_engine.network.Packets;
import net.spell_engine.utils.TargetHelper;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.SpellPower;
import net.spell_power.api.attributes.SpellAttributes;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.fabric.SpellbladesFabric;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.shadowed.eliotlash.mclib.math.functions.limit.Min;

import java.util.List;

import static net.spellbladenext.SpellbladeNext.MOD_ID;

@Mixin(Minecraft.class)
public class InteractMixin {
    @Inject(method = "startUseItem",
            at = @At("HEAD"),
            cancellable = true)
    public void use(CallbackInfo callbackInfo) {
        LocalPlayer player = ((Minecraft) (Object) this).player;
        if(((Minecraft) (Object) this).player != null && player.getAttributeValue(SpellAttributes.POWER.get(MagicSchool.FIRE).attribute) >= 10 && player instanceof SpellCasterClient client) {
            ItemStack itemStack = ((Minecraft) (Object) this).player.getItemInHand(InteractionHand.MAIN_HAND);
            Spell spell = SpellRegistry.getSpell(new ResourceLocation(MOD_ID,"soulfire"));
            Slot slot = new Slot(player.getInventory(),player.getInventory().selected,0,0);
            int[] a = new int[1];
            a[0] = player.getId();
            if (itemStack.isEmpty() &&
                    (((Minecraft) (Object) this).hitResult.getType() == null ||((Minecraft) (Object) this).hitResult.getType() == HitResult.Type.MISS)
                    &&  player.isShiftKeyDown()
                    && spell != null) {
                ClientPlayNetworking.send(Packets.SpellRequest.ID, (new Packets.SpellRequest(InteractionHand.MAIN_HAND, SpellCast.Action.RELEASE, new ResourceLocation(MOD_ID,"soulfire"), player.getInventory().selected, 0, a).write()));

            }
        }
    }
}
