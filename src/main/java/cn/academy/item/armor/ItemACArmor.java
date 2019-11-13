package cn.academy.item.armor;

import cn.academy.AcademyCraft;
import cn.academy.energy.api.IFItemManager;
import cn.academy.energy.api.item.ImagEnergyItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemACArmor  extends ItemArmor implements ImagEnergyItem, ISpecialArmor {
    protected static IFItemManager itemManager = IFItemManager.instance;
    protected static ACArmorHelper armorHelper = ACArmorHelper.instance;
    private static double ENERGY_PER_DAMAGE = 100;

    public ItemACArmor(EntityEquipmentSlot equipmentSlotIn) {
        super(ArmorMaterial.IRON, -1, equipmentSlotIn);
        this.setMaxDamage(27);
        this.setCreativeTab(AcademyCraft.cct);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type)
    {
        if(slot==EntityEquipmentSlot.LEGS)
            return "academy:textures/models/armor/cons_ingot_layer_2.png";
        return "academy:textures/models/armor/cons_ingot_layer_1.png";
    }

    @Override
    public int getItemEnchantability() {
        return 0;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
    {
        return false;
    }

    @Override
    public void setDamage(ItemStack stack, int damage)
    {
        super.setDamage(stack, damage);
        if (stack.getItemDamage() > getMaxDamage())
        {
            stack.setItemDamage(getMaxDamage());
        }
    }

    @Override
    public ArmorProperties getProperties(EntityLivingBase player, @Nonnull ItemStack armor, DamageSource source, double damage, int slot) {
        if (source.isUnblockable()) {
            return new ArmorProperties(0, 0.0D, 0);
        }
        else {
            double absorptionRatio = armorHelper.getAbsorbFactor(armor) * getDamageAbsorptionRatio();
            double energyPerDamage = ENERGY_PER_DAMAGE;
            int damageLimit = 2147483647;
            if (energyPerDamage > 0) {
                damageLimit = (int)Math.min(damageLimit, 25.0D * itemManager.getEnergy(armor) / energyPerDamage);
            }

            return new ArmorProperties(0, absorptionRatio, damageLimit);
        }
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, @Nonnull ItemStack armor, int slot) {
        return itemManager.getEnergy(armor) >= ENERGY_PER_DAMAGE ? (int)Math.round(20.0D * armorHelper.getAbsorbFactor(armor) * this.getDamageAbsorptionRatio()) : 0;
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
        itemManager.pull(stack, damage * ENERGY_PER_DAMAGE, true);
    }

    @Override
    public EntityEquipmentSlot getEquipmentSlot()
    {
        return armorType;
    }

    public double getDamageAbsorptionRatio() {
        return 0.95D;
    }

    @Override
    public double getMaxEnergy() {
        return 10000;
    }

    @Override
    public double getBandwidth() {
        return 20;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (isInCreativeTab(tab)) {
            ItemStack is = new ItemStack(this);
            items.add(is);
            itemManager.charge(is, 0, true);

            is = new ItemStack(this);
            itemManager.charge(is, Double.MAX_VALUE, true);
            armorHelper.setLevel(is, 5);
            items.add(is);
        }
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world,
                               List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(itemManager.getDescription(stack));
        tooltip.add(String.format(I18n.translateToLocal("ac.item.armor.level.tip"),
                armorHelper.getArmorLevel(stack)));
        tooltip.add(String.format(I18n.translateToLocal("ac.item.armor.watered.tip"),
                armorHelper.getWatered(stack), armorHelper.getWateredNeed(stack)));
        tooltip.add(String.format(I18n.translateToLocal("ac.item.armor.offset.tip"),
                armorHelper.getAIMEnhancement(stack)));
    }
}
