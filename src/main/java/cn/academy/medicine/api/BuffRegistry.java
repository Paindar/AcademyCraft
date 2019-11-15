package cn.academy.medicine.api;

import cn.academy.medicine.buffs.*;
import cn.lambdalib2.registry.StateEventCallback;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BuffRegistry {
    private static List<Class<? extends Buff>> buffTypes = new ArrayList<>();

    public static void _register(Class<? extends Buff> klass){
        buffTypes.add(klass);
    }

    @StateEventCallback
    public static void preInit(FMLPreInitializationEvent evt)
    {
        BuffRegistry._register(BuffAttackBoost.class);
        BuffRegistry._register(BuffAttackBoostCont.class);
        BuffRegistry._register(BuffCooldownRecovery.class);
        BuffRegistry._register(BuffCPRecovery.class);
        BuffRegistry._register(BuffHeal.class);
        BuffRegistry._register(BuffOverloadRecovery.class);
        BuffRegistry._register(BuffPotionEffect.class);
    }

    @StateEventCallback
    public static void _init(FMLInitializationEvent evt){
        buffTypes.sort(Comparator.comparing(Class::toString));
    }

    public static void writeBuff(Buff buff, NBTTagCompound tag){
        int id = buffTypes.indexOf(buff.getClass());
        tag.setInteger("id", id);
        buff.store(tag);
    }

    public static Buff readBuff(NBTTagCompound tag){
        int id = tag.getInteger("id");
        Buff buff = null;
        try {
            buff = buffTypes.get(id).newInstance();
            buff.load(tag);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return buff;
    }
}
