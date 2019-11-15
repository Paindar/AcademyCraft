package cn.academy.medicine;

import cn.lambdalib2.util.StackUtils;
import com.google.common.base.Preconditions;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Handles medicine synthesizing logic.
 *
 * TODO Debug display for medicine data
 *
 */
public class MedSynth {

    public static void writeApplyInfo(ItemStack stack, MedicineApplyInfo info){
        NBTTagCompound tag0 = StackUtils.loadTag(stack);
        NBTTagCompound tag = new NBTTagCompound();
        tag0.setTag("medicine", tag);
        tag.setInteger("target", Properties.instance.writeTarget(info.target));
        tag.setInteger("strengthType", Properties.instance.writeStrength(info.strengthType));
        tag.setFloat("strengthMod", info.strengthModifier);
        tag.setInteger("method", Properties.instance.writeMethod(info.method));
        tag.setFloat("sens", info.sensitiveRatio);
    }

    public static MedicineApplyInfo readApplyInfo(ItemStack stack){

        if(Preconditions.checkNotNull(StackUtils.loadTag(stack)).getTag("medicine") instanceof NBTTagCompound){
            NBTTagCompound tag = (NBTTagCompound) StackUtils.loadTag(stack).getTag("medicine");
            Properties.Target target = Properties.instance.readTarget(tag.getInteger("target"));
            Properties.Strength strengthType = Properties.instance.readStrength(tag.getInteger("strengthType"));
            float strengthMod=tag.getFloat("strengthMod");
            Properties.ApplyMethod method= Properties.instance.readMethod(tag.getInteger("method"));
            float sensitiveRatio = tag.getFloat("sens");
            return new MedicineApplyInfo(target, strengthType, strengthMod, method, sensitiveRatio);
        }
        throw new IllegalArgumentException("Invalid stack tag to read medicine info");
    }

    public static boolean compare(ItemStack stack1, ItemStack stack2)
    {
        try
        {
            MedicineApplyInfo info1 = readApplyInfo(stack1), info2 = readApplyInfo(stack2);
            return info1.isSimilarly(info2);
        }
        catch (IllegalArgumentException e)
        {
            return false;
        }
    }

    //Merge target into source
    public static void merge(ItemStack source, ItemStack target)
    {
        MedicineApplyInfo info1 = readApplyInfo(source),
            info2 = readApplyInfo(target);
        float sensitive = info1.sensitiveRatio * source.getCount() + info2.sensitiveRatio * target.getCount();
        sensitive /= ( source.getCount() + target.getCount());
        float strength = info1.strengthModifier * source.getCount() + info2.strengthModifier * target.getCount();
        strength /= ( source.getCount() + target.getCount());
        info1.sensitiveRatio = sensitive;
        info1.strengthModifier = strength;
        writeApplyInfo(source, info1);
        source.grow(target.getCount());
    }

}
