package cn.academy.medicine.items;

import cn.academy.medicine.MedSynth;
import cn.academy.medicine.MedicineApplyInfo;
import cn.academy.medicine.Properties;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

public class ItemMedicineBase extends Item {
    public ItemMedicineBase(){
        setMaxStackSize(1);
    }

    public MedicineApplyInfo getInfo(ItemStack stack){
        return MedSynth.readApplyInfo(stack);
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn){
        List<String> list = tooltip;
        MedicineApplyInfo info = getInfo(stack);

        if (info.target != Properties.instance.Targ_Disposed) {
            list.add(info.target.displayDesc() + " " + info.method.displayDesc());
            list.add(info.strengthType.displayDesc());
        } else {
            list.add(TextFormatting.RED + Properties.instance.Targ_Disposed.displayDesc());
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand handIn){
        ItemStack stack = player.getHeldItem(handIn);
        MedicineApplyInfo info = getInfo(stack);

        if (!world.isRemote) {
            info.target.apply(player, info);
        }

        if (!player.capabilities.isCreativeMode) {
            stack.grow(-1);
        }

        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(handIn));
    }
}

