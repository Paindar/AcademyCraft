package cn.academy.medicine.buffs;

import cn.academy.datapart.CooldownData;
import cn.academy.medicine.api.BuffApplyData;
import cn.academy.medicine.api.BuffPerTick;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.Map;


public class BuffCooldownRecovery extends BuffPerTick {

    private int counter = 0;
    public BuffCooldownRecovery(){super("cd_recovery");}
    public BuffCooldownRecovery(float percentPerTick)
    {
        this();
        this.perTick = 4*percentPerTick/30;
    }

    private Map<CooldownData.SkillCooldown,Float> accumMap = new HashMap<>();

    @Override
    public void onBegin(EntityPlayer player) {

    }

    @Override
    public void onTick(EntityPlayer player , BuffApplyData applyData){
        counter++;

        if (counter >10)
        {
            counter=0;
            CooldownData cdData = CooldownData.of(player);

            for (Map.Entry<Integer, CooldownData.SkillCooldown> integerSkillCooldownEntry : cdData.rawData().entrySet())
            {
                CooldownData.SkillCooldown cd = (CooldownData.SkillCooldown) ((Map.Entry) integerSkillCooldownEntry).getValue();

                if (!accumMap.containsKey(cd))
                {
                    accumMap.put(cd, 0f);
                }

                float next = accumMap.get(cd) + Math.abs(perTick) * cd.getMaxTick();
                cd.setTickLeft(cd.getTickLeft() - (int) next);
                accumMap.put(cd, next % 10);//每10tick结算一次，小数点部分保留下一次计算
            }
        }

    }

    @Override
    public void onEnd(EntityPlayer player) {

    }

    @Override
    public void load(NBTTagCompound tag ){
        counter = tag.getInteger("counter");
        perTick = tag.getFloat("pertick");
    }

    @Override
    public void store(NBTTagCompound tag ){
        tag.setInteger("counter", counter);
        tag.setFloat("pertick", perTick);
    }

    @Override
    public String toString()
    {
        return String.format("%s perTick:%f", "cd_recovery", perTick);
    }

}
