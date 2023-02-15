package net.spellbladenext.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.spellbladenext.ClientMod;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.entities.*;

public class ExampleModFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientMod.initialize();
       /* FabricLoader.getInstance().getModContainer(SpellbladeNext.MOD_ID).ifPresent(modContainer -> {
            ResourceManagerHelper.registerBuiltinResourcePack(new ResourceLocation(SpellbladeNext.MOD_ID, "alternateswords"), modContainer, ResourcePackActivationType.NORMAL);
            //System.out.println("Registering Classic style resourcepack for Simply Swords");
        });*/
        EntityRendererRegistry.register(SpellbladeNext.AMETHYST, AmethystRenderer::new);
        EntityRendererRegistry.register(SpellbladeNext.AMETHYST2, AmethystRenderer::new);
        //EntityRendererRegistry.register(SpellbladeNext.AMETHYST2, AmethystRenderer::new);

        EntityRendererRegistry.register(SpellbladeNext.ICICLEBARRIER, IcicleRenderer::new);
        EntityRendererRegistry.register(SpellbladeNext.ICETHORN, ThrownItemRenderer::new);

        EntityRendererRegistry.register(SpellbladeNext.GAZE, ThrownItemRenderer::new);
        EntityRendererRegistry.register(SpellbladeNext.GAZEHITTER, ThrownItemRenderer::new);
        EntityRendererRegistry.register(ExampleModFabric.REAVER, CivilizedPiglinRenderer::new);
        EntityRendererRegistry.register(ExampleModFabric.SPIN, SpinRenderer::new);
        EntityRendererRegistry.register(ExampleModFabric.COLDATTACK, ColdRenderer::new);

        EntityRendererRegistry.register(ExampleModFabric.NETHERPORTAL, FallingBlockRenderer::new);
        EntityRendererRegistry.register(ExampleModFabric.NETHERPORTALFRAME, FallingBlockRenderer::new);

        EntityRendererRegistry.register(SpellbladeNext.MAGMA, (asdf) -> new ThrownItemRenderer<>(asdf,2.0F,true));
        EntityModelLayerRegistry.registerModelLayer(AmethystModel.LAYER_LOCATION, AmethystModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(IcicleModel.LAYER_LOCATION, IcicleModel::createBodyLayer);

        EntityRendererRegistry.register(SpellbladeNext.CLEANSINGFLAME, (asdf) -> new ThrownRenderer<>(asdf,2.0F,true));
        EntityRendererRegistry.register(SpellbladeNext.ERUPTION, ThrownItemRenderer::new);

        EntityRendererRegistry.register(SpellbladeNext.FLAMEWINDS, ThrownItemRenderer::new);
        EntityRendererRegistry.register(SpellbladeNext.EXPLOSIONDUMMY, (asdf) -> new ThrownItemRenderer<>(asdf,3.0F,true));


    }
}
