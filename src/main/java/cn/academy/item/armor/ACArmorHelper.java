package cn.academy.item.armor;

import cn.academy.ACConfig;
import cn.academy.energy.api.IFItemManager;
import cn.lambdalib2.util.StackUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.Objects;

public class ACArmorHelper {
    @ACConfig.GetACCfgValue(path="ac.item.armor.fluid_need")
    private static int[] IMAG_FLUID_NEED = {
            1500, 2500, 4000, 6000, 8500,
            3000, 5000, 8000, 12000, 17000,
            2400, 4000, 6400, 9600, 13500,
            1200, 2000, 3200, 4800, 6800
            };
    @ACConfig.GetACCfgValue(path="ac.item.armor.aim_enchant")
    private static double[] AIM_ENHANCEMENT_VALUE = {
            20, 35, 60, 87, 113,
            42, 77, 157, 188, 221,
            36, 65, 99, 133, 165,
            17, 33, 50, 77, 98
            };
    @ACConfig.GetACCfgValue(path = "ac.item.armor.damage_absorb_factor")
    public static double[] ARMOR_ABSORB_FACTOR = {0.2, 0.4, 0.3, 0.1};

    public static ACArmorHelper instance = new ACArmorHelper();

    private ACArmorHelper() {
    }

    public int getArmorIndexFixed(ItemACArmor armor)
    {
        switch(armor.armorType)
        {
            case FEET:
                return 3;
            case LEGS:
                return 2;
            case CHEST:
                return 1;
            case HEAD:
                return 0;
        }
        return -1;
    }

    public double getEntityEnhancement(EntityLivingBase entity)
    {
        double value = 0;
        double rate;
        for(ItemStack is : entity.getArmorInventoryList())
        {
            if(is.getItem() instanceof ItemACArmor)
            {
                rate = IFItemManager.instance.getEnergy(is)/IFItemManager.instance.getMaxEnergy(is);
                value += (getAIMEnhancement(is)* rate);
            }
        }
        return value;
    }

    public int getArmorLevel(ItemStack stack)
    {
        return StackUtils.loadTag(stack).getInteger("armor_level");
    }

    public int getWateredNeed(ItemStack stack)
    {
        if(getArmorLevel(stack)>=5)return 0;
        ItemACArmor item = (ItemACArmor)stack.getItem();
        return IMAG_FLUID_NEED[getArmorIndexFixed(item)*5+getArmorLevel(stack)];
    }
    public double getAIMEnhancement(ItemStack stack)
    {
        if(getArmorLevel(stack)==0)return 0;
        ItemACArmor item = (ItemACArmor)stack.getItem();
        return AIM_ENHANCEMENT_VALUE[getArmorIndexFixed(item)*5+getArmorLevel(stack)-1];
    }

    public double getProgress(ItemStack stack)
    {
        return 1.0*getWatered(stack)/getWateredNeed(stack);
    }

    public int getWatered(ItemStack stack)
    {
        return StackUtils.loadTag(stack).getInteger("watered");
    }

    public boolean water(ItemStack stack, int amt)
    {
        int level = getArmorLevel(stack);
        if(level == 5)
            return false;
        int watered = getWatered(stack);
        int maxWateredNeed = getWateredNeed(stack);
        watered += amt;
        StackUtils.loadTag(stack).setInteger("watered", watered);
        while(watered>=maxWateredNeed&&level<5)
        {
            watered -= maxWateredNeed;
            maxWateredNeed = getWateredNeed(stack);
            StackUtils.loadTag(stack).setInteger("watered", watered);
            level = level +1;
            StackUtils.loadTag(stack).setInteger("armor_level", level);
            appendEnchanting(stack, level);
        }
        return true;
    }

    private void findEnchanting(NBTTagList list, String name, int level)
    {
        Enchantment e = Enchantment.getEnchantmentByLocation(name);
        int id = Enchantment.getEnchantmentID(e);
        NBTTagCompound tag=null;
        for(NBTBase base:list)
        {
            if(base instanceof NBTTagCompound)
            {
                tag = (NBTTagCompound) base;
                if(tag.getShort("id")==id)
                {
                    tag.setShort("lvl", (byte)level);
                    return;
                }
                else
                    tag = null;
            }
        }
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setShort("id", (short)id);
        nbttagcompound.setShort("lvl",(byte)level);
        list.appendTag(nbttagcompound);
    }

    private void appendEnchanting(ItemStack stack, int level)
    {
        NBTTagCompound nbt = stack.getTagCompound();
        NBTTagList nbttaglist;
        if(nbt == null)
        {
            nbt = new NBTTagCompound();
            nbt.setTag("ench", new NBTTagList());
        }
        if (!nbt.hasKey("ench", 9))
        {
            nbt.setTag("ench", new NBTTagList());
        }
        nbttaglist = nbt.getTagList("ench",10);
        switch (Objects.requireNonNull(stack.getItem().getEquipmentSlot(stack))) {
            case FEET:
                if(level >1)
                {
                    findEnchanting(nbttaglist,"feather_falling", Math.min(level-1, 4));
                    findEnchanting(nbttaglist,"depth_strider", Math.min(level-2, 3));
                }
                break;
            case LEGS:
                if(level >1)
                {
                    findEnchanting(nbttaglist,"projectile_protection", Math.min(level-1, 4));
                    findEnchanting(nbttaglist,"thorns", Math.min(level-2, 3));
                }
                break;
            case CHEST:
                if(level >1)
                {
                    findEnchanting(nbttaglist,"fire_protection", Math.min(level-1, 4));
                    findEnchanting(nbttaglist,"blast_protection", Math.min(level-1, 4));
                }
                break;
            case HEAD:
                if(level >=3)
                {
                    findEnchanting(nbttaglist,"aqua_affinity", 1);
                    findEnchanting(nbttaglist,"respiration", Math.min(level-2, 3));
                }
                break;
        }
        stack.setTagCompound(nbt);
    }

    public void setLevel(ItemStack stack, int level)
    {
        StackUtils.loadTag(stack).setInteger("armor_level", level);
        appendEnchanting(stack, level);
    }

    public double getAbsorbFactor(ItemStack stack) {
        ItemACArmor armor = (ItemACArmor) stack.getItem();
        return ARMOR_ABSORB_FACTOR[getArmorIndexFixed(armor)];
    }
}
