package cn.academy.item;

import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

/**
 * @author WeAthFolD
 */
public class ItemMatrixCore extends Item {
    
    int LEVELS = 3;
    
//    IIcon icons[] = new IIcon[LEVELS];

    public ItemMatrixCore() {
        this.setHasSubtypes(true);
    }
    
    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "_" + stack.getItemDamage();
    }
    
//    @SideOnly(Side.CLIENT)
//    @Override
//    public void registerIcons(IIconRegister ir) {
//        for(int i = 0; i < LEVELS; ++i) {
//            icons[i] = ir.registerIcon("academy:matrix_core_" + i);
//        }
//    }
//
//    @SideOnly(Side.CLIENT)
//    @Override
//    public IIcon getIconFromDamage(int meta) {
//        return icons[meta];
//    }
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) {
            for(int i = 0; i < LEVELS; ++i)
                items.add(new ItemStack(this, 1, i));
        }
    }

}