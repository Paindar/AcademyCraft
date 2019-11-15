package cn.academy.medicine.buffs;

import cn.academy.medicine.api.BuffApplyData;
import cn.academy.medicine.api.BuffPerTick;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BuffAttackBoostCont extends BuffPerTick
{
    private String playerName;
    private float ratio;
    private int duration;
    private int tick = 0;

    public BuffAttackBoostCont(){super("attack_boost_continuous");}
    public BuffAttackBoostCont(float boostRatio, String name, int time, int strength)
    {
        this();
        playerName = name;
        ratio = boostRatio;
        duration = time/strength;
    }

    @Override
    public void onBegin(EntityPlayer player)
    {
        MinecraftForge.EVENT_BUS.register(this);

    }

    @Override
    public void onTick(EntityPlayer player, BuffApplyData applyData)
    {
        tick++;
        if (tick >=duration)
        {
            tick = 0;
            ratio /=2;
        }
    }

    @Override
    public void onEnd(EntityPlayer player)
    {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent evt){
        if(evt.getSource().getTrueSource() instanceof EntityPlayer){
            if (evt.getSource().getTrueSource().getName().equals(playerName))
                evt.setAmount(evt.getAmount()*ratio);
        }
    }

    @Override
    public void load(NBTTagCompound tag ){
        playerName = tag.getString("name");
        ratio = tag.getFloat("ratio");
        duration = tag.getInteger("duration");
        tick = tag.getInteger("tick");
    }

    @Override
    public void store(NBTTagCompound tag ){
        tag.setString("name", playerName);
        tag.setFloat("ratio", ratio);
        tag.setInteger("duration", duration);
        tag.setInteger("tick", tick);
    }

    @Override
    public String toString()
    {
        return String.format("%s ratio:%f", "attack_boost_cont", ratio);
    }

}
