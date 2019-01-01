package electroblob.wizardry.entity.projectile;

import java.util.List;

import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.MagicDamage.DamageType;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import electroblob.wizardry.util.WizardryUtilities;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityForceOrb extends EntityBomb {
	
	public EntityForceOrb(World world){
		super(world);
	}

	@Override
	protected void onImpact(RayTraceResult par1RayTraceResult){

		if(par1RayTraceResult.entityHit != null){
			// This is if the force orb gets a direct hit
			this.playSound(SoundEvents.ENTITY_GENERIC_HURT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
		}

		// Particle effect
		if(this.world.isRemote){
			for(int j = 0; j < 20; j++){
				float brightness = 0.5f + (rand.nextFloat() / 2);
				ParticleBuilder.create(Type.SPARKLE, rand, posX, posY, posZ, 0.25, true).time(6)
				.clr(brightness, 1.0f, brightness + 0.2f).spawn(world);
			}
			this.world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.posX, this.posY, this.posZ, 0, 0, 0);
		}

		if(!this.world.isRemote){

			// 2 gives a cool flanging effect!
			float pitch = this.rand.nextFloat() * 0.2F + 0.3F;
			this.playSound(SoundEvents.ENTITY_FIREWORK_BLAST, 1.5F, pitch);
			this.playSound(SoundEvents.ENTITY_FIREWORK_BLAST, 1.5F, pitch - 0.01f);

			double blastRadius = 4.0d * blastMultiplier;

			List<EntityLivingBase> targets = WizardryUtilities.getEntitiesWithinRadius(blastRadius, this.posX,
					this.posY, this.posZ, this.world);

			for(EntityLivingBase target : targets){
				if(target != this.getThrower()){

					double velY = target.motionY;

					double dx = this.posX - target.posX > 0 ? -0.5 - (this.posX - target.posX) / 8
							: 0.5 - (this.posX - target.posX) / 8;
					double dz = this.posZ - target.posZ > 0 ? -0.5 - (this.posZ - target.posZ) / 8
							: 0.5 - (this.posZ - target.posZ) / 8;

					float damage = 4 * damageMultiplier;

					target.attackEntityFrom(
							MagicDamage.causeIndirectMagicDamage(this, this.getThrower(), DamageType.BLAST), damage);

					target.motionX = dx;
					target.motionY = velY + 0.4;
					target.motionZ = dz;
				}
			}

			this.setDead();
		}
	}
	
}
