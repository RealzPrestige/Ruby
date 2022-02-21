package dev.zprestige.ruby.mixins.render;

import dev.zprestige.ruby.module.visual.NoRender;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = {LayerBipedArmor.class})
public abstract class MixinRenderArmor {

    @Shadow
    protected abstract void setModelVisible(ModelBiped var1);

    /**
     * @author ea
     * @reason  joe
     */

    @Overwrite
    protected void setModelSlotVisible(ModelBiped p_188359_1_, EntityEquipmentSlot slotIn) {
        setModelVisible(p_188359_1_);
        if (NoRender.Instance.isEnabled())
            switch (slotIn) {
                case HEAD:
                    p_188359_1_.bipedHead.showModel = !NoRender.Instance.armor.getValue();
                    p_188359_1_.bipedHeadwear.showModel = !NoRender.Instance.armor.getValue();
                    break;
                case CHEST:
                    p_188359_1_.bipedBody.showModel = !NoRender.Instance.armor.getValue();
                    p_188359_1_.bipedRightArm.showModel = !NoRender.Instance.armor.getValue();
                    p_188359_1_.bipedLeftArm.showModel = !NoRender.Instance.armor.getValue();
                    break;
                case LEGS:
                    p_188359_1_.bipedBody.showModel = !NoRender.Instance.armor.getValue();
                    p_188359_1_.bipedRightLeg.showModel = !NoRender.Instance.armor.getValue();
                    p_188359_1_.bipedLeftLeg.showModel = !NoRender.Instance.armor.getValue();
                    break;
                case FEET:
                    p_188359_1_.bipedRightLeg.showModel = !NoRender.Instance.armor.getValue();
                    p_188359_1_.bipedLeftLeg.showModel = !NoRender.Instance.armor.getValue();
            }
    }
}

