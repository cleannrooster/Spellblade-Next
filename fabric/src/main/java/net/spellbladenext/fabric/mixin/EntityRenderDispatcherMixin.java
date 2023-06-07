package net.spellbladenext.fabric.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.spell_engine.internals.SpellCasterClient;
import net.spell_engine.internals.SpellCasterEntity;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.fabric.interfaces.PlayerDamageInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.spellbladenext.SpellbladeNext.MOD_ID;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin<E extends Entity>{
    Entity entity2;
    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    public void renderMix(E entity, double d, double e, double f, float g, float h, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo info) {
        EntityRenderDispatcher renderer = (EntityRenderDispatcher) (Object) this;
        EntityRenderer entityRenderer = renderer.getRenderer(entity);

        if(Minecraft.getInstance() != null && !(Minecraft.getInstance().player != null && (Minecraft.getInstance().screen instanceof CreativeModeInventoryScreen || Minecraft.getInstance().screen instanceof InventoryScreen) && entity == Minecraft.getInstance().player) && entity.getLevel().dimension().equals(SpellbladeNext.DIMENSIONKEY) && entity.getY() >= 62) {
            try {
                double d1 = Mth.lerp((double)h, entity.yOld, entity.getY());

                Vec3 vec3 = entityRenderer.getRenderOffset(entity, h);
                double j = d + vec3.x();
                double k = - (e)+(-62.5+d1)*2-3F/8F;
                double l = f + vec3.z();
                poseStack.pushPose();

                Quaternion quaternion = Quaternion.ONE;
                //quaternion.mul(-1);
                poseStack.scale(1,-1,1);

                poseStack.translate(j, k, l);
                entityRenderer.render(entity, g, h, poseStack, multiBufferSource, i);


                poseStack.translate(-vec3.x(), -vec3.y(), -vec3.z());


                poseStack.popPose();
            } catch (Throwable var24) {
                CrashReport crashReport = CrashReport.forThrowable(var24, "Rendering entity in world");
                CrashReportCategory crashReportCategory = crashReport.addCategory("Entity being rendered");
                entity.fillCrashReportCategory(crashReportCategory);
                CrashReportCategory crashReportCategory2 = crashReport.addCategory("Renderer details");
                crashReportCategory2.setDetail("Assigned renderer", entityRenderer);
                crashReportCategory2.setDetail("Location", CrashReportCategory.formatLocation(entity.getLevel(), d, e, f));
                crashReportCategory2.setDetail("Rotation", g);
                crashReportCategory2.setDetail("Delta", h);
                throw new ReportedException(crashReport);
            }
        }
    }
}
