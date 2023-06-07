package net.spellbladenext.fabric.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.spell_engine.internals.SpellCasterEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.spellbladenext.SpellbladeNext.MOD_ID;
@Mixin(PlayerRenderer.class)
public class RenderMixin{
    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    public void rendermix(AbstractClientPlayer abstractClientPlayer, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo info) {
        if(abstractClientPlayer instanceof SpellCasterEntity caster) {
            if (caster.getCurrentSpellId() != null && caster.getCurrentSpellId().equals(new ResourceLocation(MOD_ID, "monkeyslam")) && caster.getCurrentCastProgress() > 17F / 160F) {
                    info.cancel();
            }
        }
    }
}
