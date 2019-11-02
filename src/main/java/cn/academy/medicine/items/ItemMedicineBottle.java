package cn.academy.medicine.items;

import cn.academy.medicine.MedSynth;
import cn.academy.medicine.MedicineApplyInfo;
import cn.academy.medicine.ModuleMedicine;
import cn.academy.medicine.Properties;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.List;

public class ItemMedicineBottle extends ItemMedicineBase {
    public ItemMedicineBottle() {

    }

    public static ItemStack create(MedicineApplyInfo info){
        ItemStack stack = new ItemStack(ModuleMedicine.medicineBottle);
        MedSynth.writeApplyInfo(stack, info);
        return stack;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items){
        List<ItemStack> list = items;

        // For debug
        /*
        list.add(create(new MedicineApplyInfo(Properties.instance.Targ_Life, Properties.instance.Str_Mild
                , 1.0f, Properties.instance.Apply_Instant_Incr, 0.5f)));
        list.add(create(new MedicineApplyInfo(Properties.instance.Targ_Life, Properties.instance.Str_Weak
                , 1.0f, Properties.instance.Apply_Instant_Incr, 0.5f)));
        list.add(create(new MedicineApplyInfo(Properties.instance.Targ_Life, Properties.instance.Str_Normal
                , 1.0f, Properties.instance.Apply_Instant_Incr, 0.5f)));
        list.add(create(new MedicineApplyInfo(Properties.instance.Targ_Life, Properties.instance.Str_Strong
                , 2.0f, Properties.instance.Apply_Instant_Decr, 0.5f)));

         */
    }
}
