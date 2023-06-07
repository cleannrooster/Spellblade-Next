package net.spellbladenext.fabric;

import net.bettercombat.mixin.PlayerEntityMixin;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.internals.*;
import net.spell_engine.mixin.LivingEntityMixin;
import net.spell_engine.mixin.client.ClientPlayerEntityMixin;
import net.spell_power.api.attributes.SpellAttributes;
import net.spellbladenext.ClientMod;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.entities.*;
import net.spellbladenext.fabric.client.entity.model.ice_spike;
import net.spellbladenext.fabric.client.entity.model.icecrash_smallmodel;
import net.spellbladenext.fabric.client.entity.renderer.*;
import net.spellbladenext.fabric.client.item.renderer.*;
import net.spellbladenext.fabric.entities.IceCrashEntity;
import net.spellbladenext.fabric.entities.IceSpikeEntity;
import net.spellbladenext.fabric.items.*;
import net.spellbladenext.fabric.items.armors.Armors;
import net.spellbladenext.fabric.items.armors.InquisitorSet;
import net.spellbladenext.fabric.items.armors.Robes;
import net.spellbladenext.fabric.items.spellblades.Spellblades;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

import java.util.Objects;

import static net.spellbladenext.SpellbladeNext.MOD_ID;
import static net.spellbladenext.fabric.SpellbladesFabric.*;

public class SpellbladesFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        ClientMod.initialize();
        ItemProperties.register(LASERBOW.get(), new ResourceLocation("pull"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0f;
            }
            if (entity.getUseItem() != stack) {
                return 0.0f;
            }
            if( entity instanceof SpellCasterEntity caster && caster.getCurrentSpellId() != null && caster.getCurrentSpellId().equals(new ResourceLocation(MOD_ID,"barrage"))){
                return ((float)(caster.getCurrentCastProgress()*((caster.getCurrentSpell().cast.duration*20)) % caster.getCurrentSpell().cast.channel_ticks))/(float)caster.getCurrentSpell().cast.channel_ticks;
            }
            return (float)(stack.getUseDuration() - entity.getUseItemRemainingTicks()) / 20.0f;
        });
        ItemProperties.register(LASERBOW.get(), new ResourceLocation("pulling"), (stack, world, entity, seed) -> entity != null && entity.isUsingItem()
                && entity.getUseItem() == stack ? 1.0f : 0.0f);
        ClientPlayNetworking.registerGlobalReceiver(new ResourceLocation(MOD_ID,"blockspell"), (client, handler, buf, responseSender) -> {
            client.execute(() -> {
                if(client.player instanceof SpellCasterClient client1) {
                    client1.setCurrentSpellId(client1.getSelectedSpellId(SpellContainerHelper.containerWithProxy
                    (
                            client.player.getItemBySlot(
                                    EquipmentSlot.MAINHAND),client.player)));
                    client1.castRelease(client.player.getItemBySlot(
                            EquipmentSlot.MAINHAND), InteractionHand.MAIN_HAND, 0);
                }

            });
        });
        ClientPlayNetworking.registerGlobalReceiver(new ResourceLocation(MOD_ID,"riposte"), (client, handler, buf, responseSender) -> {
            client.execute(() -> {
                if(client.player instanceof SpellCasterClient client1) {
                    client1.setCurrentSpellId(client1.getSelectedSpellId(SpellContainerHelper.containerFromItemStack(
                            client.player.getItemBySlot(
                                    EquipmentSlot.MAINHAND))));
                    client1.castRelease(client.player.getItemBySlot(
                            EquipmentSlot.MAINHAND), InteractionHand.MAIN_HAND, 0);
                }

            });
        });
        ClientTickEvents.START_CLIENT_TICK.register(server -> {
            Player player = server.player;
            Level level = server.level;

            if (player != null && level != null ) {
                double speed = player.getAttributeValue(Attributes.MOVEMENT_SPEED) * player.getAttributeValue(SpellAttributes.HASTE.attribute)*0.01 * 4;
                BlockHitResult result = level.clip(new ClipContext(player.position(), player.position().add(0, -2, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, player));
                if(player.isShiftKeyDown()){
                    speed *= 0;
                }
                double modifier = 0;
                if (result.getType() == HitResult.Type.BLOCK) {
                    modifier = 1;
                }

                if (player instanceof SpellCasterEntity caster && Objects.equals(caster.getCurrentSpellId(), new ResourceLocation(MOD_ID, "roll")) && SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "roll")) != null) {
                    Spell spell = SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "roll"));

                    speed *= 1.5;
                            player.setDeltaMovement(player.getViewVector(1).subtract(0, player.getViewVector(1).y, 0).normalize().multiply(speed, speed * modifier, speed).add(0,player.getDeltaMovement().y,0));
                }
                if (SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "whirlwind_polearm")) != null) {
                    Spell spell = SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "whirlwind_polearm"));

                    if (player instanceof SpellCasterEntity caster) {

                        if (Objects.equals(caster.getCurrentSpellId(), new ResourceLocation(MOD_ID, "whirlwind_polearm"))) {

                            player.setDeltaMovement(player.getViewVector(1).subtract(0, player.getViewVector(1).y, 0).normalize().multiply(speed, speed * modifier, speed).add(0,player.getDeltaMovement().y,0));
                        }
                    }
                }
                if (SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "whirlwind")) != null) {
                    Spell spell = SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "whirlwind"));

                    if (player instanceof SpellCasterEntity caster) {

                        if (Objects.equals(caster.getCurrentSpellId(), new ResourceLocation(MOD_ID, "whirlwind"))) {
                            if(!player.getOffhandItem().isEmpty()){
                                caster.clearCasting();
                                player.sendSystemMessage(Component.translatable("You can't do this technique with two weapons."));
                                player.getCooldowns().addCooldown(player.getMainHandItem().getItem(), 40);
                            }

                            player.setDeltaMovement(player.getViewVector(1).subtract(0, player.getViewVector(1).y, 0).normalize().multiply(speed, speed * modifier, speed).add(0,player.getDeltaMovement().y,0));
                        }
                    }
                }
                if (SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "dualwield_whirlwind")) != null) {
                    Spell spell = SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "dualwield_whirlwind"));

                    if (player instanceof SpellCasterEntity caster) {

                        if (Objects.equals(caster.getCurrentSpellId(), new ResourceLocation(MOD_ID, "dualwield_whirlwind"))) {

                            player.setDeltaMovement(player.getViewVector(1).subtract(0, player.getViewVector(1).y, 0).normalize().multiply(speed, speed * modifier, speed).add(0,player.getDeltaMovement().y,0));
                        }
                    }
                }
                if (SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "maelstrom")) != null) {
                    Spell spell = SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "maelstrom"));

                    if (player instanceof SpellCasterEntity caster) {
                        if (Objects.equals(caster.getCurrentSpellId(), new ResourceLocation(MOD_ID, "maelstrom"))) {

                            player.setDeltaMovement(player.getViewVector(1).subtract(0, player.getViewVector(1).y, 0).normalize().multiply(speed, speed * modifier, speed).add(0,player.getDeltaMovement().y,0));
                        }
                    }
                }
                if (SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "inferno")) != null) {
                    Spell spell = SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "inferno"));
                    if (player instanceof SpellCasterEntity caster) {

                        if (Objects.equals(caster.getCurrentSpellId(), new ResourceLocation(MOD_ID, "inferno"))) {


                            player.setDeltaMovement(player.getViewVector(1).subtract(0, player.getViewVector(1).y, 0).normalize().multiply(speed, speed * modifier, speed).add(0,player.getDeltaMovement().y,0));
                        }
                    }
                }
                if (SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "cyclone")) != null) {

                    Spell spell = SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "cyclone"));
                    if (player instanceof SpellCasterEntity caster) {
                        if (Objects.equals(caster.getCurrentSpellId(), new ResourceLocation(MOD_ID, "cyclone"))) {


                            player.setDeltaMovement(player.getViewVector(1).subtract(0, player.getViewVector(1).y, 0).normalize().multiply(speed, speed * modifier, speed).add(0,player.getDeltaMovement().y,0));
                        }
                    }
                }
                if (SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "ashes")) != null) {

                    Spell spell = SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "ashes"));
                    if (player instanceof SpellCasterEntity caster) {
                        if (Objects.equals(caster.getCurrentSpellId(), new ResourceLocation(MOD_ID, "ashes"))) {


                            player.setDeltaMovement(player.getViewVector(1).subtract(0, player.getViewVector(1).y, 0).normalize().multiply(speed, speed * modifier, speed).add(0,player.getDeltaMovement().y,0));
                        }
                    }
                }
                if (SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "monkeyspin")) != null) {

                    Spell spell = SpellRegistry.getSpell(new ResourceLocation(MOD_ID, "monkeyspin"));
                    if (player instanceof SpellCasterEntity caster) {
                        if (Objects.equals(caster.getCurrentSpellId(), new ResourceLocation(MOD_ID, "monkeyspin"))) {


                            player.setDeltaMovement(player.getViewVector(1).subtract(0, player.getViewVector(1).y, 0).normalize().multiply(speed, speed * modifier, speed).add(0,player.getDeltaMovement().y,0));
                        }
                    }
                }
            }

        });

        GeoItemRenderer.registerItemRenderer(Orbs.fireOrb.item(), new OrbRenderer());
        GeoItemRenderer.registerItemRenderer(Orbs.arcaneOrb.item(), new OrbRenderer());
        GeoItemRenderer.registerItemRenderer(Orbs.frostOrb.item(), new OrbRenderer());
        for (var entry: Armors.entries) {
            if(entry.armorSet().pieces().stream().allMatch(armor -> armor instanceof Robes)) {
                GeoArmorRenderer.registerArmorRenderer(new RobeRenderer(),
                        entry.armorSet().head,
                        entry.armorSet().chest,
                        entry.armorSet().legs,
                        entry.armorSet().feet);
                GeoItemRenderer.registerItemRenderer(entry.armorSet().head, new RobeItemRenderer());
                GeoItemRenderer.registerItemRenderer(entry.armorSet().chest, new RobeItemRenderer());
                GeoItemRenderer.registerItemRenderer(entry.armorSet().legs, new RobeItemRenderer());
                GeoItemRenderer.registerItemRenderer(entry.armorSet().feet, new RobeItemRenderer());


            }
            if(entry.armorSet().pieces().stream().allMatch(armor -> armor instanceof InquisitorSet)) {
                GeoArmorRenderer.registerArmorRenderer(new InquisitorRenderer(),
                        entry.armorSet().head,
                        entry.armorSet().chest,
                        entry.armorSet().legs,
                        entry.armorSet().feet);
                GeoItemRenderer.registerItemRenderer(entry.armorSet().head, new InquisitorItemRenderer());
                GeoItemRenderer.registerItemRenderer(entry.armorSet().chest, new InquisitorItemRenderer());
                GeoItemRenderer.registerItemRenderer(entry.armorSet().legs, new InquisitorItemRenderer());
                GeoItemRenderer.registerItemRenderer(entry.armorSet().feet, new InquisitorItemRenderer());


            }
        }
/*
        GeoArmorRenderer.registerArmorRenderer(new RobeRenderer(), ExampleModFabric.HOOD,
                ExampleModFabric.ROBE, ExampleModFabric.PANTS, ExampleModFabric.BOOTS);
        GeoItemRenderer.registerItemRenderer(ExampleModFabric.HOOD, new RobeItemRenderer());
        GeoItemRenderer.registerItemRenderer(ExampleModFabric.PANTS, new RobeItemRenderer());

        GeoItemRenderer.registerItemRenderer(ExampleModFabric.ROBE, new RobeItemRenderer());

        GeoItemRenderer.registerItemRenderer(ExampleModFabric.BOOTS, new RobeItemRenderer());*/

       /* FabricLoader.getInstance().getModContainer(SpellbladeNext.MOD_ID).ifPresent(modContainer -> {
            ResourceManagerHelper.registerBuiltinResourcePack(new ResourceLocation(SpellbladeNext.MOD_ID, "alternateswords"), modContainer, ResourcePackActivationType.NORMAL);
            //System.out.println("Registering Classic style resourcepack for Simply Swords");
        });*/
        EntityRendererRegistry.register(SpellbladeNext.AMETHYST, AmethystRenderer::new);
        EntityRendererRegistry.register(ICECRASH, context -> { return new IceCrashSmallRenderer<IceCrashEntity>(context,2,false);});
        EntityRendererRegistry.register(ICECRASH2, context -> { return new IceCrashSmallRenderer<IceCrashEntity>(context,4,false);});
        EntityRendererRegistry.register(ICECRASH3, context -> { return new IceCrashSmallRenderer<IceCrashEntity>(context,6,false);});
        EntityRendererRegistry.register(ICESPIKE, context -> { return new IceSpikeRenderer<IceSpikeEntity>(context,1,false);});

        EntityRendererRegistry.register(SpellbladeNext.AMETHYST2, AmethystRenderer::new);
        EntityRendererRegistry.register(SpellbladeNext.AMETHYST2, AmethystRenderer::new);

        //EntityRendererRegistry.register(SpellbladeNext.AMETHYST2, AmethystRenderer::new);

        EntityRendererRegistry.register(SpellbladeNext.ICICLEBARRIER, IcicleRenderer::new);
        EntityRendererRegistry.register(SpellbladeNext.ICETHORN, ThrownItemRenderer::new);

        EntityRendererRegistry.register(SpellbladeNext.GAZE, ThrownItemRenderer::new);
        EntityRendererRegistry.register(SpellbladeNext.GAZEHITTER, ThrownItemRenderer::new);
        EntityRendererRegistry.register(SpellbladeNext.LASERARROW, (context) -> new ThrownRendererEmissive(context,2.0F,true));

        EntityRendererRegistry.register(SpellbladesFabric.REAVER, CivilizedPiglinRenderer::new);
        EntityRendererRegistry.register(SpellbladesFabric.MAGUS, MagusRenderer::new);
        EntityRendererRegistry.register(SpellbladesFabric.ARCHMAGUS, ArchmagusRenderer::new);

        EntityRendererRegistry.register(SpellbladesFabric.MONKEYCLONE, MonkeyCloneRenderer::new);

        EntityRendererRegistry.register(SpellbladesFabric.SPIN, SpinRenderer::new);
        EntityRendererRegistry.register(SpellbladesFabric.COLDATTACK, ColdRenderer::new);

        EntityRendererRegistry.register(SpellbladesFabric.NETHERPORTAL, FallingBlockRenderer::new);
        EntityRendererRegistry.register(SpellbladesFabric.NETHERPORTALFRAME, FallingBlockRenderer::new);

        EntityRendererRegistry.register(SpellbladeNext.MAGMA, (context) -> new ThrownItemRenderer<>(context,2.0F,true));
        EntityModelLayerRegistry.registerModelLayer(AmethystModel.LAYER_LOCATION, AmethystModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(IcicleModel.LAYER_LOCATION, IcicleModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(icecrash_smallmodel.LAYER_LOCATION, icecrash_smallmodel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ice_spike.LAYER_LOCATION, ice_spike::createBodyLayer);

        EntityRendererRegistry.register(SpellbladeNext.CLEANSINGFLAME, (context) -> new ThrownRenderer<>(context,2.0F,true));
        EntityRendererRegistry.register(SpellbladeNext.ERUPTION, ThrownItemRenderer::new);

        EntityRendererRegistry.register(SpellbladeNext.FLAMEWINDS, ThrownItemRenderer::new);
        EntityRendererRegistry.register(SpellbladeNext.EXPLOSIONDUMMY, (context) -> new ThrownItemRenderer<>(context,3.0F,true));


    }
}
