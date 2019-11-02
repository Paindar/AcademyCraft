package cn.academy.medicine.buffs;

import cn.academy.medicine.api.BuffApplyData;
import cn.academy.medicine.api.BuffPerTick;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;

public class BuffHeal extends BuffPerTick {

    private int counter = 0;
    public BuffHeal(){super("heal");}
    public BuffHeal(float healPerTick){
        this();
        perTick = 4*healPerTick/30;
    }

    @Override
    public void onBegin(EntityPlayer player) {

    }

    @Override
    public void onTick(EntityPlayer player, BuffApplyData applyData){
        counter++;
        if (counter >10)
        {
            counter=0;
            if (perTick >= 0)
            {
                player.heal(perTick);
            } else
            {
                player.attackEntityFrom(DamageSource.MAGIC, perTick);
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
        return String.format("%s perTick:%f", "heal", perTick);
    }
}