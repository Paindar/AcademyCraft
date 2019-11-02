package cn.academy.medicine.buffs;

import cn.academy.datapart.CPData;
import cn.academy.medicine.api.BuffApplyData;
import cn.academy.medicine.api.BuffPerTick;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class BuffOverloadRecovery extends BuffPerTick {

    private int counter = 0;
    public BuffOverloadRecovery(){super("overload_recovery");}
    public BuffOverloadRecovery(float perTick)
    {
        this();
        this.perTick = 4*perTick/30;
    }

    @Override
    public void onBegin(EntityPlayer player) {

    }

    @Override
    public void onTick(EntityPlayer player, BuffApplyData applyData){
        counter++;
        CPData cpData = CPData.get(player);
        if (counter >10)
        {
            counter=0;
            cpData.setOverload(cpData.getOverload() - perTick);
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
        return String.format("%s perTick:%f", "ol_recovery", perTick);
    }
}