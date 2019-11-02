package cn.academy.medicine.api;

import cn.academy.Resources;
import cn.lambdalib2.s11n.network.NetworkS11nType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

@NetworkS11nType
public abstract class Buff {

    public abstract void onBegin(EntityPlayer player);

    public abstract void onTick(EntityPlayer player, BuffApplyData applyData);

    public abstract void onEnd(EntityPlayer player);

    public abstract void load(NBTTagCompound tag);

    public abstract void store(NBTTagCompound tag);

    String id;

    ResourceLocation icon;

    boolean shouldDisplay = true;

    public Buff(String id)
    {
        this.id = id;
        icon = Resources.getTexture("buff/" + id);
    }

    @Override
    public String toString()
    {
        return "Unknown";
    }
}
