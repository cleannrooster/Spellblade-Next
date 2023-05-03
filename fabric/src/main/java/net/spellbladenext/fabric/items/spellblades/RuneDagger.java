package net.spellbladenext.fabric.items.spellblades;

import com.google.common.collect.Multimap;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.spell_engine.api.item.ConfigurableAttributes;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.internals.SpellContainerHelper;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.SpellRegistry;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.SpellPower;
import net.spell_power.api.attributes.EntityAttributes_SpellPower;
import net.spellbladenext.fabric.config.ItemConfig;

import java.util.ArrayList;
import java.util.List;

import static net.spell_power.api.SpellPower.getCriticalChance;
import static net.spell_power.api.SpellPower.getCriticalMultiplier;
import static net.spellbladenext.SpellbladeNext.MOD_ID;
import static net.spellbladenext.fabric.SpellbladesFabric.DIREHEX;

public class RuneDagger extends SwordItem implements ConfigurableAttributes {
    private Multimap<Attribute, AttributeModifier> attributes;
    private final EquipmentSlot slot = EquipmentSlot.MAINHAND;
    public RuneDagger(Tier material, Multimap<Attribute, AttributeModifier> attributes, Item.Properties settings, ArrayList<ItemConfig.SpellAttribute> school) {
        super(material,1,material.getAttackDamageBonus(),  settings);

        this.setAttributes(attributes);
    }
    public List<MagicSchool> getMagicSchools(ItemStack stack){

        List<MagicSchool> list = new ArrayList<>();
        if(SpellContainerHelper.containerFromItemStack(stack) != null && SpellContainerHelper.containerFromItemStack(stack).spell_ids.contains("spellbladenext:gleamingblade")){
            list.add(MagicSchool.ARCANE);
        }
        if(SpellContainerHelper.containerFromItemStack(stack) != null && SpellContainerHelper.containerFromItemStack(stack).spell_ids.contains("spellbladenext:frozenblade")){
            list.add(MagicSchool.FROST);
        }
        if(SpellContainerHelper.containerFromItemStack(stack) != null && SpellContainerHelper.containerFromItemStack(stack).spell_ids.contains("spellbladenext:searingblade")){
            list.add(MagicSchool.FIRE);
        }
        if(SpellContainerHelper.containerFromItemStack(stack) != null && SpellContainerHelper.containerFromItemStack(stack).spell_ids.contains("spellbladenext:steelblade")){
            list.add(MagicSchool.PHYSICAL_MELEE);
        }
        return list;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        double angle = target.getEyePosition().subtract(attacker.getEyePosition()).dot(target.getLookAngle());
        boolean sneak = angle > 0 || attacker.hasEffect(DIREHEX.get());


        for(MagicSchool school : this.getMagicSchools(stack)){
            SpellPower.Result result = SpellPower.getSpellPower(school,attacker);
            if(sneak){
                Attribute attribute = (Attribute)EntityAttributes_SpellPower.POWER.get(school);
                if (school.isExternalAttribute()) {
                    attribute = (Attribute)Registry.ATTRIBUTE.get(school.attributeId());
                }
                result = new SpellPower.Result(school, attacker.getAttributeValue(attribute), 1, getCriticalMultiplier(attacker, stack));
            }
            if(school == MagicSchool.ARCANE){

                Spell spell = SpellRegistry.getSpell(new ResourceLocation(MOD_ID,"gleamingblade"));
                SpellHelper.performImpacts(attacker.getLevel(),attacker,target, spell,new SpellHelper.ImpactContext(1.0F,1.0F,target.position(), result,SpellHelper.impactTargetingMode(spell)));
            }
            if(school == MagicSchool.FIRE){
                Spell spell = SpellRegistry.getSpell(new ResourceLocation(MOD_ID,"searingblade"));
                SpellHelper.performImpacts(attacker.getLevel(),attacker,target, spell,new SpellHelper.ImpactContext(1.0F,1.0F,target.position(), result,SpellHelper.impactTargetingMode(spell)));
            }
            if(school == MagicSchool.FROST){
                Spell spell = SpellRegistry.getSpell(new ResourceLocation(MOD_ID,"frozenblade"));
                SpellHelper.performImpacts(attacker.getLevel(),attacker,target, spell,new SpellHelper.ImpactContext(1.0F,1.0F,target.position(),result ,SpellHelper.impactTargetingMode(spell)));
            }
            if(school == MagicSchool.PHYSICAL_MELEE){
                Spell spell = SpellRegistry.getSpell(new ResourceLocation(MOD_ID,"steelblade"));
                SpellHelper.performImpacts(attacker.getLevel(),attacker,target, spell,new SpellHelper.ImpactContext(1.0F,1.0F,target.position(),result ,SpellHelper.impactTargetingMode(spell)));

            }
        }
        if(attacker.hasEffect(DIREHEX.get())){
            attacker.removeEffect(DIREHEX.get());
        }
        stack.hurtAndBreak(1, attacker, (e) -> {
            e.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        return true;
    }
    public static void lookAt(LivingEntity looker, Entity entity, float f, float g) {
        double d = entity.getX() - looker.getX();
        double e = entity.getZ() - looker.getZ();
        double h;
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            h = livingEntity.getEyeY() - looker.getEyeY();
        } else {
            h = (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2.0D - looker.getEyeY();
        }

        double i = Math.sqrt(d * d + e * e);
        float j = (float)(Mth.atan2(e, d) * 57.2957763671875D) - 90.0F;
        float k = (float)(-(Mth.atan2(h, i) * 57.2957763671875D));
        looker.setXRot(rotlerp(looker.getXRot(), k, g));
        looker.setYRot(rotlerp(looker.getYRot(), j, f));
        looker.setYBodyRot(rotlerp(looker.getXRot(), k, g));
        looker.setYHeadRot(rotlerp(looker.getXRot(), k, g));

    }
    private static float rotlerp(float f, float g, float h) {
        float i = Mth.wrapDegrees(g - f);
        if (i > h) {
            i = h;
        }

        if (i < -h) {
            i = -h;
        }

        return f + i;
    }
    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand interactionHand) {
        if(!player.getCooldowns().isOnCooldown(this) && !player.isShiftKeyDown()) {
            if(livingEntity instanceof Mob mob){
                mob.setTarget(null);
            }
            player.addEffect(new MobEffectInstance(DIREHEX.get(),20,0,false,false));
            Vec3 vec3 = livingEntity.position().add(player.getViewVector(1F).subtract(0, player.getViewVector(1F).y, 0).normalize().scale(livingEntity.getBoundingBox().getXsize() / 2)).add(player.getViewVector(1F).subtract(0, player.getViewVector(1F).y, 0).normalize().scale(1.5));

            float f = player.getXRot();
            float f1 = player.getYRot() + 180;
            float f2 = player.xRotO;
            float f3 = player.yRotO + 180;

                player.setXRot(f);
                player.setYRot(f1);
                player.xRotO = f2;
                player.yRotO = f3;
                player.hurtMarked = true;

            player.teleportTo(vec3.x, vec3.y, vec3.z);


            if (player.getLevel().isClientSide()) {

                KeyMapping.click(Minecraft.getInstance().options.keyAttack.getDefaultKey());
            }
            player.getCooldowns().addCooldown(this,160);
            return InteractionResult.PASS;
        }
        else{
            return InteractionResult.FAIL;
        }
    }
    public void setAttributes(Multimap<Attribute, AttributeModifier> attributes) {
        this.attributes = attributes;
    }

    public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player miner) {
        return !miner.isCreative();
    }


    public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (state.getDestroySpeed(world, pos) != 0.0F) {
            stack.hurtAndBreak(2, miner, (e) -> {
                e.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
        }

        return true;
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (this.attributes == null) {
            return super.getDefaultAttributeModifiers(slot);
        } else {
            return slot == EquipmentSlot.MAINHAND ? this.attributes : super.getDefaultAttributeModifiers(slot);
        }
    }
}
