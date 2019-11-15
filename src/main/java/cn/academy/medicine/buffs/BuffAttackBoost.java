package cn.academy.medicine.buffs;

import cn.academy.medicine.api.Buff;
import cn.academy.medicine.api.BuffApplyData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BuffAttackBoost extends Buff {

    private String playerName;
    private float ratio;

    public BuffAttackBoost(){super("attack_boost");}
    public BuffAttackBoost(float ratio, String playerName){
        this();
        this.ratio = ratio;
        this.playerName = playerName;
    }

    @Override
    public void onBegin(EntityPlayer player){
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onTick(EntityPlayer player, BuffApplyData applyData) {

    }

    @Override
    public void onEnd(EntityPlayer player){
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void  onLivingHurt(LivingHurtEvent evt){
        if(evt.getSource().getTrueSource() instanceof EntityPlayer){
            if (evt.getSource().getTrueSource().getName().equals(playerName))
                evt.setAmount(evt.getAmount()*ratio);
        }
    }


    @Override
    public void load(NBTTagCompound tag ){
        playerName = tag.getString("name");
        ratio = tag.getFloat("ratio");
    }

    @Override
    public void store(NBTTagCompound tag ){
        tag.setString("name", playerName);
        tag.setFloat("ratio", ratio);
    }

    @Override
    public String toString()
    {
        return String.format("%s ratio:%f", "attack_boost", ratio);
    }

}