package net.spellbladenext.fabric.client.item.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.spellbladenext.SpellbladeNext;
import net.spellbladenext.fabric.SpellbladesFabric;
import net.spellbladenext.fabric.client.entity.model.MonkeyCloneModel;
import net.spellbladenext.fabric.entities.MonkeyClone;
import net.spellbladenext.fabric.items.armors.Armors;
import org.jetbrains.annotations.Nullable;
import software.bernie.example.client.DefaultBipedBoneIdents;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

import static net.spellbladenext.fabric.entities.MonkeyClone.*;

public class MonkeyCloneRenderer<T extends MonkeyClone, M extends HumanoidModel<T>> extends ExtendedGeoEntityRenderer<MonkeyClone> {

    private static final ResourceLocation DEFAULT_LOCATION = new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/biped.png");
    private static final ResourceLocation FIRE = new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/hexblade_fire.png");
    private static final ResourceLocation FROST = new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/hexblade_cold.png");
    private static final ResourceLocation ARCANE = new ResourceLocation(SpellbladeNext.MOD_ID,"textures/mob/hexblade_arcane.png");

    protected ItemStack mainHandItem, offHandItem, helmetItem, chestplateItem, leggingsItem, bootsItem;
    public MonkeyCloneRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MonkeyCloneModel<>());

        //this.layerRenderers.add((GeoLayerRenderer<Reaver>) new GeoitemInHand<T,M>((IGeoRenderer<T>) this,renderManager.getItemInHandRenderer()));
    }

    @Override
    public void renderEarly(MonkeyClone animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource,
                            VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float partialTicks) {
        super.renderEarly(animatable, poseStack, partialTick, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, partialTicks);
        this.mainHandItem = new ItemStack(SpellbladesFabric.MONKEYSTAFF.get());
            this.helmetItem = new ItemStack(Armors.rimeblaze.head);
            this.chestplateItem = new ItemStack(Items.NETHERITE_CHESTPLATE);
            this.leggingsItem = new ItemStack(Items.NETHERITE_LEGGINGS);
            this.bootsItem = new ItemStack(Items.NETHERITE_BOOTS);


    }

    public ResourceLocation getTextureLocation(MonkeyClone p_114891_) {

        return DEFAULT_LOCATION;
    }

    @Override
    public boolean shouldShowName(MonkeyClone animatable) {
        return false;
    }

    @Override
    protected void renderNameTag(MonkeyClone entity, Component component, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
    }


    @Nullable
    @Override
    protected ResourceLocation getTextureForBone(String s, MonkeyClone reaver) {
        return null;
    }

    @Nullable
    @Override
    protected ItemStack getHeldItemForBone(String s, MonkeyClone reaver) {
        if(s.equals("rightItem")) {
            return this.mainHandItem;
        }
        else{
            return null;
        }
    }

    @Override
    protected boolean isArmorBone(GeoBone bone) {

        return bone.getName().startsWith("armor") && !bone.getName().contains("2");
    }
    @Override
    protected ModelPart getArmorPartForBone(String name, HumanoidModel<?> armorModel) {

        return switch (name) {

            case DefaultBipedBoneIdents.LEFT_FOOT_ARMOR_BONE_IDENT,
                    DefaultBipedBoneIdents.LEFT_LEG_ARMOR_BONE_IDENT -> armorModel.leftLeg;
            case DefaultBipedBoneIdents.RIGHT_FOOT_ARMOR_BONE_IDENT,
                            DefaultBipedBoneIdents.RIGHT_LEG_ARMOR_BONE_IDENT -> armorModel.rightLeg;
            case DefaultBipedBoneIdents.RIGHT_ARM_ARMOR_BONE_IDENT -> armorModel.rightArm;
            case DefaultBipedBoneIdents.LEFT_ARM_ARMOR_BONE_IDENT -> armorModel.leftArm;
            case DefaultBipedBoneIdents.BODY_ARMOR_BONE_IDENT -> armorModel.body;
            case DefaultBipedBoneIdents.HEAD_ARMOR_BONE_IDENT -> armorModel.head;
            default -> null;
        };
    }



    @Override
    protected ItemStack getArmorForBone(String boneName, MonkeyClone currentEntity) {
        return switch (boneName) {
            case DefaultBipedBoneIdents.LEFT_FOOT_ARMOR_BONE_IDENT,
                    DefaultBipedBoneIdents.RIGHT_FOOT_ARMOR_BONE_IDENT -> this.bootsItem;
            case DefaultBipedBoneIdents.LEFT_LEG_ARMOR_BONE_IDENT,
                    DefaultBipedBoneIdents.RIGHT_LEG_ARMOR_BONE_IDENT -> this.leggingsItem;
            case DefaultBipedBoneIdents.BODY_ARMOR_BONE_IDENT,
                    DefaultBipedBoneIdents.RIGHT_ARM_ARMOR_BONE_IDENT,
                    DefaultBipedBoneIdents.LEFT_ARM_ARMOR_BONE_IDENT -> this.chestplateItem;
            case DefaultBipedBoneIdents.HEAD_ARMOR_BONE_IDENT -> this.helmetItem;
            default -> null;
        };
    }
    @Override
    protected EquipmentSlot getEquipmentSlotForArmorBone(String boneName, MonkeyClone currentEntity) {
        return switch (boneName) {
            case DefaultBipedBoneIdents.LEFT_FOOT_ARMOR_BONE_IDENT,
                    DefaultBipedBoneIdents.RIGHT_FOOT_ARMOR_BONE_IDENT -> EquipmentSlot.FEET;
            case DefaultBipedBoneIdents.LEFT_LEG_ARMOR_BONE_IDENT,
                    DefaultBipedBoneIdents.RIGHT_LEG_ARMOR_BONE_IDENT -> EquipmentSlot.LEGS;
            case DefaultBipedBoneIdents.RIGHT_ARM_ARMOR_BONE_IDENT -> !currentEntity.isLeftHanded() ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            case DefaultBipedBoneIdents.LEFT_ARM_ARMOR_BONE_IDENT -> currentEntity.isLeftHanded() ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            case DefaultBipedBoneIdents.BODY_ARMOR_BONE_IDENT -> EquipmentSlot.CHEST;
            case DefaultBipedBoneIdents.HEAD_ARMOR_BONE_IDENT -> EquipmentSlot.HEAD;
            default -> null;
        };
    }
    @Override
    protected ItemTransforms.TransformType getCameraTransformForItemAtBone(ItemStack itemStack, String s) {
        return ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;
    }

    @Nullable
    @Override
    protected BlockState getHeldBlockForBone(String s, MonkeyClone reaver) {
        return null;
    }

    @Override
    protected void preRenderItem(PoseStack poseStack, ItemStack itemStack, String s, MonkeyClone reaver, IBone iBone) {
        if(s.equals("rightItem"))
        poseStack.translate(0,0.1,-0.1);
    }

    @Override
    protected void preRenderBlock(PoseStack poseStack, BlockState blockState, String s, MonkeyClone monkeyClone) {

    }

    @Override
    protected void postRenderItem(PoseStack poseStack, ItemStack itemStack, String s, MonkeyClone monkeyClone, IBone iBone) {

    }

    @Override
    protected void postRenderBlock(PoseStack poseStack, BlockState blockState, String s, MonkeyClone monkeyClone) {

    }

    @Override
    protected void setLimbBoneVisible(GeoArmorRenderer<? extends ArmorItem> armorRenderer, ModelPart limb, HumanoidModel<?> armorModel, EquipmentSlot slot) {
            if (limb == armorModel.head || limb == armorModel.hat) {
                armorRenderer.getGeoModelProvider().getBone(armorRenderer.headBone).setHidden(false);
            }
            else if (limb == armorModel.body) {
                armorRenderer.getGeoModelProvider().getBone(armorRenderer.bodyBone).setHidden(false);
                armorRenderer.getGeoModelProvider().getBone(armorRenderer.leftArmBone).setHidden(true);
                armorRenderer.getGeoModelProvider().getBone(armorRenderer.rightArmBone).setHidden(true);
            }
            else if (limb == armorModel.leftArm) {
                armorRenderer.getGeoModelProvider().getBone(armorRenderer.bodyBone).setHidden(true);
                armorRenderer.getGeoModelProvider().getBone(armorRenderer.leftArmBone).setHidden(false);
                armorRenderer.getGeoModelProvider().getBone(armorRenderer.rightArmBone).setHidden(true);
            }
            else if (limb == armorModel.leftLeg) {
                armorRenderer.getGeoModelProvider().getBone((slot == EquipmentSlot.FEET ? armorRenderer.leftBootBone : armorRenderer.leftLegBone)).setHidden(false);
                armorRenderer.getGeoModelProvider().getBone((slot == EquipmentSlot.FEET ? armorRenderer.rightBootBone : armorRenderer.rightLegBone)).setHidden(false);
            }
            else if (limb == armorModel.rightArm) {
                armorRenderer.getGeoModelProvider().getBone(armorRenderer.bodyBone).setHidden(true);
                armorRenderer.getGeoModelProvider().getBone(armorRenderer.leftArmBone).setHidden(true);
                armorRenderer.getGeoModelProvider().getBone(armorRenderer.rightArmBone).setHidden(false);
            }
            else if (limb == armorModel.rightLeg) {
                armorRenderer.getGeoModelProvider().getBone((slot == EquipmentSlot.FEET ? armorRenderer.rightBootBone : armorRenderer.rightLegBone)).setHidden(false);
                armorRenderer.getGeoModelProvider().getBone((slot == EquipmentSlot.FEET ? armorRenderer.leftBootBone : armorRenderer.leftLegBone)).setHidden(true);
            }
    }
}