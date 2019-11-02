package cn.academy.medicine;

import cn.academy.client.sound.ACSounds;
import cn.academy.datapart.CPData;
import cn.academy.datapart.CooldownData;
import cn.academy.medicine.api.BuffData;
import cn.academy.medicine.buffs.*;
import cn.academy.util.LocalHelper;
import cn.lambdalib2.util.RandUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.lwjgl.util.Color;

import java.util.Arrays;
import java.util.List;

public class Properties {
    public abstract class Property {
        public abstract String stackDisplayHint();
        public abstract String internalID();
        public final String displayDesc(){
            return Properties.localProps.get(internalID());
        }
        @Override
        public boolean equals(Object o)
        {
            return o instanceof Property && ((Property) o).internalID().equals(internalID());
        }
    }

    public abstract class Target extends Property {
        public abstract void apply(EntityPlayer player, MedicineApplyInfo data);

        public Color baseColor;
        public float medSensitiveRatio;
        String id;

        @Override
        public String stackDisplayHint()
        {
            return Properties.formatItemDesc("targ", TextFormatting.GREEN, displayDesc());
        }
        @Override
        public String internalID()
        {
            return "targ_" + id;
        }

    }

    public abstract class Strength extends Property {

        public float baseValue;
        String id;
        @Override
        public String stackDisplayHint()
        {
            return Properties.formatItemDesc("str", TextFormatting.GREEN, displayDesc());
        }

        @Override
        public String internalID()
        {
            return "str_" + id;
        }

    }

    public abstract class ApplyMethod extends Property {
        public boolean instant;
        public boolean incr;
        public float strength;

        String id;
        @Override
        public String stackDisplayHint()
        {
            return Properties.formatItemDesc("app", TextFormatting.AQUA, displayDesc());
        }
        @Override
        public String internalID(){
            return "app_" + id;
        }
    }

    public abstract class Variation extends Property {
        String id;
        @Override
        public String internalID()
        {
            return "var_" + id;
        }
        @Override
        public String stackDisplayHint()
        {
            return Properties.formatItemDesc("var", TextFormatting.DARK_PURPLE, displayDesc());
        }
    }

    int ContApplyTime = 15 * 20;

    public Target Targ_Life = new Target(){
        @Override
        public void apply(EntityPlayer player, MedicineApplyInfo data){
            float amt = 5 * data.strengthModifier;
            BuffData buffData = BuffData.apply(player);
            float sens = buffData.getResistance();
            if (sens >= 1 && RandUtils.nextFloat() * sens >1)
            {
                data.method.incr=false;//药敏度过高导致过敏反应
            }
            if (data.method.instant)
            {
                if (data.method.incr) {
                    player.heal(amt*sens);
                } else {
                    player.attackEntityFrom(DamageSource.causePlayerDamage(player), amt);
                }
            } else { // Continuous recovery
                int time = ContApplyTime;

                BuffHeal buff = new BuffHeal((data.method.incr?1:-1)*amt);

                buffData.addBuff(buff, (int) (time*sens));
            }
            buffData.setResistance(sens*data.sensitiveRatio);
        }
        {
            id = "life";
            baseColor = new Color(255,0,0);
            medSensitiveRatio = 0.05f;
        }
    };

    public Target Targ_CP = new Target() {
        @Override
        public void apply(EntityPlayer player, MedicineApplyInfo data){
            CPData cpData = CPData.get(player);
            BuffData buffData = BuffData.apply(player);
            float sens = buffData.getResistance();
            float baseValue = cpData.getMaxCP() * 0.1f * data.strengthModifier;
            if (sens >= 1 && RandUtils.nextFloat() * sens >1)
            {
                baseValue*=-1;//药敏度过高导致过敏反应
            }
            if (data.method.instant) {
                cpData.setCP(cpData.getCP() + baseValue*sens);
            }
            else {
                int time = ContApplyTime;
                float perTick = baseValue;
                buffData.addBuff(new BuffCPRecovery(perTick), (int) (time*sens));
            }
            buffData.setResistance(sens*data.sensitiveRatio);
        }
        {
            id = "cp";
            baseColor = new Color(0,0,255);
            medSensitiveRatio = 0.05f;
        }
    };

    public Target Targ_Overload = new Target() {
        @Override
        public void apply(EntityPlayer player, MedicineApplyInfo data){
            CPData cpData = CPData.get(player);
            BuffData buffData = BuffData.apply(player);
            float sens = buffData.getResistance();
            float amt = cpData.getMaxOverload() * 0.1f * data.strengthModifier*sens;
            if (sens >= 1 && RandUtils.nextFloat() * sens >1)
            {
                amt*=-1;//药敏度过高导致过敏反应
            }

            if (data.method.instant) {
                cpData.setOverload(cpData.getOverload() - amt);
            } else {
                BuffData.apply(player).addBuff(new BuffOverloadRecovery(amt), ContApplyTime);
            }
            buffData.setResistance(sens*data.sensitiveRatio);
        }
        {
            id = "overload";
			baseColor =  new Color(255,255,0);
			medSensitiveRatio = 0.05f;
		}
    };

    public Target Targ_Jump = new Target(){
        @Override
        public void apply(EntityPlayer player, MedicineApplyInfo data){
            BuffData buffData = BuffData.apply(player);
            float sens = buffData.getResistance();
            if (sens >= 1 && RandUtils.nextFloat() * sens >1)
            {
                buffData.setResistance(sens*data.sensitiveRatio);
                return;//药敏度过高导致过敏反应
            }
            if(data.method == Apply_Instant_Incr) {

                int time = ContApplyTime;
                PotionEffect eff = new PotionEffect(Potion.getPotionById(8), (int) (time* sens), strenghToLevel(data.strengthType));
                player.addPotionEffect(eff);
            }
            else if (data.method == Apply_Continuous_Incr)
            {
                int time = (int) (ContApplyTime*sens*2);
                BuffData.apply(player).addBuff(
                        new BuffPotionEffect(8, strenghToLevel(data.strengthType), time),
                        time);
            }
            buffData.setResistance(sens*data.sensitiveRatio);
        }

        {
			id = "jump";
			baseColor =  new Color(255,255,255);
			medSensitiveRatio = 0.03f;
		}
    };

    public Target Targ_Cooldown = new Target(){
        @Override
        public void apply(EntityPlayer player, MedicineApplyInfo data){
            BuffData buffData = BuffData.apply(player);
            float sens = buffData.getResistance();
            float baseValue = 0.2f * data.strengthModifier*sens;
            if (sens >= 1 && RandUtils.nextFloat() * sens <1)
            {
                baseValue*=-1;//药敏度过高导致过敏反应
            }

            if (data.method.instant) {
                for (CooldownData.SkillCooldown cd : CooldownData.of(player).rawData().values()) {
                    cd.setTickLeft((int)(cd.getTickLeft() - baseValue * cd.getMaxTick()));
                }
            }
            else {
                BuffData.apply(player).addBuff(new BuffCooldownRecovery(baseValue), (int) (ContApplyTime*sens));
            }
            buffData.setResistance(sens*data.sensitiveRatio);

        }

        {
			id = "cooldown";
			baseColor =  new Color(0,0,255);
			medSensitiveRatio = 0.1f;
		}
    };

    public Target Targ_MoveSpeed = new Target(){
        @Override
        public void apply(EntityPlayer player, MedicineApplyInfo data){
            BuffData buffData = BuffData.apply(player);
            float sens = buffData.getResistance();
            if (sens >= 1 && RandUtils.nextFloat() * sens >1)
            {
                buffData.setResistance(sens*data.sensitiveRatio);
                return;//药敏度过高导致过敏反应
            }
            if(data.method.instant) {
                int time = ContApplyTime;
                Potion potion = (data.method.incr)? Potion.getPotionById(1): Potion.getPotionById(2);
                player.addPotionEffect(new PotionEffect(potion, (int) (time*sens), strenghToLevel(data.strengthType)));
            }
            else
            {
                int time = (int) (2*sens*ContApplyTime);
                int potion = (data.method.incr)? 1: 2;
                BuffData.apply(player).addBuff(
                        new BuffPotionEffect(potion, strenghToLevel(data.strengthType), time),
                        time);
            }
            buffData.setResistance(sens*data.sensitiveRatio);
        }
        {
            id = "move_speed";
            baseColor =  new Color(255,255,255);
            medSensitiveRatio = 0.3f;
        }
    };

    public Target Targ_Disposed = new Target(){
        @Override
        public void apply(EntityPlayer player, MedicineApplyInfo data){
            BuffData buffData = BuffData.apply(player);
            float sens = buffData.getResistance();
            float test = RandUtils.rangef(0, 1);
            World world = player.getEntityWorld();
            if(sens<1 || sens*test >1){// No effect but adds sensitivity
                buffData.setResistance(sens*1.125f);
            }
            else{// Fake Explosion
                buffData.setResistance(sens*sens);
                ACSounds.playClient(player.world,player.posX, player.posY, player.posZ, "random.explode", SoundCategory.PLAYERS,
                        4.0f, 1.0f);
                player.attackEntityFrom(DamageSource.causePlayerDamage(player), 10f);
            }
        }
        {
			id = "disposed";
			baseColor =  new Color(0,0,0);
			medSensitiveRatio = 0.5f;
		}
    };

    public Target Targ_Attack = new Target(){
        @Override
        public void apply(EntityPlayer player, MedicineApplyInfo data){
            BuffData buffData = BuffData.apply(player);
            float sens = buffData.getResistance();
            float boostRatio;
            if (sens >= 1 && RandUtils.nextFloat() * sens >1)
            {
                data.method.incr=false;//药敏度过高导致过敏反应
            }
            if(data.method.instant){
                int time = ContApplyTime;
                if (data.method.incr)
                    boostRatio = 1 + (0.2f * data.strengthModifier * sens);
                else
                    boostRatio = 1 - (0.2f * data.strengthModifier * sens);
                BuffData.apply(player).addBuff(new BuffAttackBoost(boostRatio, player.getName()), time);
            }
            else
            {
                int time = (int) (ContApplyTime*2*sens);
                if (data.method.incr)
                    boostRatio = 1 + (0.5f * data.strengthModifier * sens);
                else
                    boostRatio = 1 - (0.5f * data.strengthModifier * sens);
                BuffData.apply(player).addBuff(
                        new BuffAttackBoostCont(boostRatio,
                                player.getName(),
                                time,
                                strenghToLevel(data.strengthType)),
                        time);
            }
        }

        {
            id = "attack";
            medSensitiveRatio = 0;
			baseColor =  new Color(255,0,255);
		}
    };


    public Strength Str_Mild = new Strength() {
        {			
            baseValue = 0.3f;
            id = "mild";
		}
    };

    public Strength Str_Weak = new Strength(){
        {
			baseValue = 0.6f;
            id = "weak";
		}

    };

    public Strength Str_Normal = new Strength(){
        {
			baseValue = 0.9f;
            id = "normal";
		}

    };

    public Strength Str_Strong = new Strength(){
        {
           baseValue= 1.5f;
            id = "strong";
        }


    };

    public Strength Str_Infinity = new Strength(){
        {
            baseValue= 10000f;
            id = "infinity";
        }
    };




    public ApplyMethod Apply_Instant_Incr = new ApplyMethod() {
        {
            incr = true;
            instant = true;
            strength = 2f;
            id = "instant_incr";
        }
    };

    public ApplyMethod Apply_Instant_Decr = new ApplyMethod(){
        {
            incr = false;
            instant = true;
            strength = -1f;

            id = "instant_decr";
        }
    };

    public ApplyMethod Apply_Continuous_Incr = new ApplyMethod(){{
            incr = true;
            instant = false;
            strength = 0.01f;

            id = "cont_incr";
    }};

    public ApplyMethod Apply_Continuous_Decr = new ApplyMethod(){{
            incr = false;
            instant = false;
            strength = -0.005f;

            id = "cont_decr";
    }};



    public Variation Var_Infinity = new Variation(){
        {
            id = "infinity";
        }
    };

    public Variation Var_Neutralize = new Variation(){
        {
            id = "neutralize";
        }
    };

    public Variation Var_Desens = new Variation(){
        {
            id = "desens";
        }
    };

    public Variation Var_Fluct = new Variation(){
        {
            id = "fluct";
        }
    };

    public Variation Var_Stabilize = new Variation(){
        {
            id = "stabilize";
        }
    };

    // Misc

    private static LocalHelper local = LocalHelper.at("ac.medicine");
    private static LocalHelper localTypes = local.subPath("prop_type");
    private static LocalHelper localProps = local.subPath("props");
    private List<ApplyMethod> applyMethodMapping = Arrays.asList(Apply_Instant_Incr, Apply_Instant_Decr,
            Apply_Continuous_Incr, Apply_Continuous_Decr);

    public ApplyMethod findApplyMethod(boolean instant, boolean incr)
    {
        for(ApplyMethod method:applyMethodMapping)
        {
            if(method.incr==incr && method.instant == instant)
                return method;
        }
        return null;

    }

    public int strenghToLevel(Strength strength)
    {
        if(strength==Str_Mild)
            return 0;
        else if(strength==Str_Weak)
            return 1;
        else if(strength==Str_Normal)
            return 2;
        else if(strength==Str_Strong)
            return 3;
        else if(strength==Str_Infinity)
            return 4;
        return -1;//invalid
    }

    private static String formatItemDesc(String propType, TextFormatting color, String name){
        return color + localTypes.get(propType) + ": " + TextFormatting.RESET + name;
    }

    // --- storage & s11n


    // For cross-version compatibility, only append new properties at the end of lists.

    private final List<Target> allTargets = Arrays.asList(Targ_Life, Targ_CP, Targ_Overload, Targ_Jump,
            Targ_MoveSpeed, Targ_Disposed, Targ_Attack, Targ_Cooldown);
    private final List<Strength> allStrengths = Arrays.asList(Str_Mild, Str_Weak, Str_Normal, Str_Strong, Str_Infinity);
    private final List<ApplyMethod> allMethods = Arrays.asList(Apply_Instant_Incr, Apply_Instant_Decr,
            Apply_Continuous_Decr, Apply_Continuous_Incr);
    private final List<Variation> allVariations = Arrays.asList(Var_Infinity, Var_Neutralize,
            Var_Desens, Var_Stabilize, Var_Fluct);

    public final List<Property> allProperties = Arrays.asList(Targ_Life, Targ_CP, Targ_Overload, Targ_Jump,
            Targ_Disposed, Targ_Attack, Targ_Cooldown,Str_Mild, Str_Weak, Str_Normal, Str_Strong, Str_Infinity,
            Apply_Instant_Incr, Apply_Instant_Decr, Apply_Continuous_Decr, Apply_Continuous_Incr, Var_Infinity,
            Var_Neutralize, Var_Desens, Var_Stabilize, Var_Fluct);

    public static Properties instance = new Properties();

    public int writeTarget(Target t)
    {
        return serialize(allTargets, t);
    }
    public Target readTarget(int i)
    {
        return deserialize(allTargets, i);
    }

    public int writeStrength(Strength s)
    {
        return serialize(allStrengths, s);
    }
    public Strength readStrength(int i)
    {
        return deserialize(allStrengths, i);
    }

    public int writeMethod(ApplyMethod m)
    {
        return serialize(allMethods, m);
    }
    public ApplyMethod readMethod(int i)
    {
        return deserialize(allMethods, i);
    }

    public int writeVariation(Variation m)
    {
        return serialize(allVariations, m);
    }
    public Variation readVariation(int i)
    {
        return deserialize(allVariations, i);
    }

    Property find(String name)
    {
        for(Property p :allProperties)
        {
            if(p.internalID().equals(name)){
                return p;
            }
        }
        return null;
    }

    private <T> int serialize(List<T> seq, T value){
        int idx = seq.indexOf(value);
        if (idx == -1) {
            throw new IllegalArgumentException("Can't serialize " + value);
        }
        return idx;
    }

    private <T> T deserialize(List<T> seq, int idx)
    {
        return seq.get(idx);
    }

}

