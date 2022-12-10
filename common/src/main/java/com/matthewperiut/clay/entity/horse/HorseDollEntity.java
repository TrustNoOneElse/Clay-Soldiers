package com.matthewperiut.clay.entity.horse;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class HorseDollEntity extends PathAwareEntity implements GeoAnimatable
{
    private AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    public HorseDollEntity(EntityType<? extends PathAwareEntity> entityType, World world)
    {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributesBuilder()
    {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 5.00f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0.0f)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 0.8f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.32f);
    }
    public static DefaultAttributeContainer setAttributes()
    {
        return setAttributesBuilder().build();
    }

    private <E extends GeoAnimatable> PlayState predicate(AnimationState<E> event)
    {
        if (event.isMoving())
        {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("animation.doll_horse.run"));
            return PlayState.CONTINUE;
        }

        event.getController().setAnimation(RawAnimation.begin().thenPlay("animation.doll_horse.idle"));

        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return factory;
    }

    @Override
    protected void initGoals()
    {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.0));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        super.initGoals();
    }

    @Override
    public double getTick(Object o)
    {
        return age;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource src)
    {
        return SoundEvents.BLOCK_GRAVEL_BREAK;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.BLOCK_GRAVEL_STEP;
    }

    @Override
    public boolean handleAttack(Entity attacker)
    {
        if (attacker instanceof PlayerEntity)
            kill();
        return super.handleAttack(attacker);
    }

    boolean dropBrick = false;
    @Override
    protected Identifier getLootTableId()
    {
        if (dropBrick)
            return new Identifier("clay:entities/horse/brick");
        return super.getLootTableId();
    }

    @Override
    public void onDeath(DamageSource damageSource)
    {
        if(damageSource.isFire())
            dropBrick = true;
        super.onDeath(damageSource);
    }
}
