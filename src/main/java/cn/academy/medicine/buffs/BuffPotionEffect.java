package cn.academy.medicine.buffs;

import cn.academy.medicine.api.BuffApplyData;
import cn.academy.medicine.api.BuffPerTick;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class BuffPotionEffect extends BuffPerTick
{
    private int type;
    private int level = 0;
    private int duration = 0;
    private int ticks=0;
    public BuffPotionEffect(){super("jump_boost");}
    public BuffPotionEffect(int type, int strength, int time)
    {
        this();
        this.type = type;
        duration = time / (strength + 1);
        level = strength;
    }

    @Override
    public void onBegin(EntityPlayer player) {
    }

    @Override
    public void onTick(EntityPlayer player, BuffApplyData applyData)
    {
        ticks --;
        if (ticks <=0)
        {
            if(level >= 0)
            {
                PotionEffect eff = new PotionEffect(Potion.getPotionById(type), duration, level);
                player.addPotionEffect(eff);
            }
            level --;
            ticks = duration;
        }
    }

    @Override
    public void onEnd(EntityPlayer player)
    {

    }

    @Override
    public void load(NBTTagCompound tag ){
        type = tag.getInteger("type");
        level = tag.getInteger("level");
        duration = tag.getInteger("duration");
        ticks = tag.getInteger("tick");
    }

    @Override
    public void store(NBTTagCompound tag ){
        tag.setInteger("type", type);
        tag.setFloat("level", level);
        tag.setInteger("duration", duration);
        tag.setInteger("tick", ticks);
    }

    @Override
    public String toString()
    {
        return String.format("[%s]%s level:%d", "potion", Potion.getPotionById(type).getName(), level+1);
    }
}
