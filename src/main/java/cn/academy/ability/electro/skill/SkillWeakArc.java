/**
 * 
 */
package cn.academy.ability.electro.skill;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import cn.academy.ability.electro.CatElectro;
import cn.academy.ability.electro.client.render.skill.SRSmallCharge;
import cn.academy.ability.electro.entity.EntityWeakArc;
import cn.academy.ability.electro.entity.fx.ChargeEffectS;
import cn.academy.api.ability.SkillBase;
import cn.academy.api.client.render.SkillRenderer;
import cn.academy.api.ctrl.RawEventHandler;
import cn.academy.api.ctrl.SkillState;
import cn.academy.api.ctrl.pattern.PatternDown;
import cn.academy.api.data.AbilityData;
import cn.academy.api.data.AbilityDataMain;
import cn.academy.core.client.render.SkillRenderManager;
import cn.academy.core.proxy.ACClientProps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 一般电弧攻击
 * @author WeathFolD
 *
 */
public class SkillWeakArc extends SkillBase {
	
	static final int MAX_HOLD_TIME = 200;
	
	@SideOnly(Side.CLIENT)
	static SkillRenderer charge = new SRSmallCharge(5, 0.8);
	
	public SkillWeakArc() {}
	
	@Override
	public void initPattern(RawEventHandler reh) {
		reh.addPattern(new PatternDown() {

			@Override
			public SkillState createSkill(EntityPlayer player) {
				return new StateArc(player);
			}
			
		});
	}
	
	public String getInternalName() {
		return "em_arc";
	}
	
	@SideOnly(Side.CLIENT)
	public ResourceLocation getLogo() {
		return ACClientProps.ELEC_ARC;
	}

	public static class StateArc extends SkillState {
		
		public StateArc(EntityPlayer player) {
			super(player);
		}

		@Override
		public void onStart() {
			if(!player.worldObj.isRemote) {
				if(consumeCP()){
					player.worldObj.spawnEntityInWorld(
						new EntityWeakArc(player, CatElectro.weakArc));
				}
			} else {
				if(consumeCP()) {
					player.worldObj.spawnEntityInWorld(
						new EntityWeakArc.OffSync(player, CatElectro.weakArc));
					SkillRenderManager.addEffect(charge, 500);
					player.worldObj.spawnEntityInWorld(new ChargeEffectS(player, 40, 5));
				}
			}
		}
		
		private boolean consumeCP() {
			AbilityData data = AbilityDataMain.getData(player);
			int id = data.getSkillID(CatElectro.weakArc), lv = data.getSkillLevel(id), clv = data.getLevelID() + 1;
			float need = 250 - lv * (21 - lv) + 10 * clv * (15 - clv);
			return data.decreaseCP(need);
		}

		@Override
		public void onFinish() {}
		
	}
}
