package net.spellbladenext.fabric.items;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Tier;

import net.spell_engine.api.item.weapon.StaffItem;
import net.spell_power.api.MagicSchool;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.builder.RawAnimation;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.network.ISyncable;
import software.bernie.geckolib3.util.GeckoLibUtil;

import static software.bernie.example.item.PistolItem.ANIM_OPEN;

public class Orb extends StaffItem implements IAnimatable {
    private final MagicSchool magicSchool;
    public AnimationFactory factory = GeckoLibUtil.createFactory(this);
    public Orb(Tier material, Properties settings, MagicSchool magicSchool) {
        super(material, settings);
        this.magicSchool = magicSchool;
    }
    public static final RawAnimation IDLE = new RawAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP);

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<Orb>(this,"controller",0,this::predicate));

    }

    public MagicSchool getMagicSchool() {
        return magicSchool;
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event){
        AnimationBuilder asdf2 = new AnimationBuilder();
        asdf2.getRawAnimationList().add(IDLE);
        event.getController().setAnimation(asdf2);
        return PlayState.CONTINUE;
    }
    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

}
