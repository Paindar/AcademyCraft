package cn.academy.item.armor;

import cn.academy.ACConfig;
import cn.academy.energy.api.IFItemManager;
import cn.lambdalib2.util.StackUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

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
        }
        return true;
    }

    public void setLevel(ItemStack stack, int level)
    {
        StackUtils.loadTag(stack).setInteger("armor_level", level);
    }

    public double getAbsorbFactor(ItemStack stack) {
        ItemACArmor armor = (ItemACArmor) stack.getItem();
        return ARMOR_ABSORB_FACTOR[getArmorIndexFixed(armor)];
    }
}
