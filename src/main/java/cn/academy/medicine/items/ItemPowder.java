package cn.academy.medicine.items;

import cn.academy.medicine.MatExtraction;
import cn.academy.medicine.Properties;
import cn.lambdalib2.registry.RegistryCallback;
import cn.lambdalib2.util.SideUtils;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemPowder extends Item {
    private static List<ItemPowder> POWDERS = new ArrayList<>();

    public MatExtraction.ItemMeta source;
    public Properties.Property prop;

    final ItemStack dummyStack;

    public ItemPowder(MatExtraction.ItemMeta source, Properties.Property prop) {
        this.source=source;
        this.prop=prop;
        dummyStack = new ItemStack(source.item, 1, source.meta);
    }

    public String internalID()
    {
        String id = source.id;
        int test=id.indexOf(":");
        return id.substring(test+1) + "_" + prop.internalID();
    }

    public static ItemPowder getItemPowder(MatExtraction.ItemMeta meta, Properties.Property prop)
    {
        for(ItemPowder powder:POWDERS)
        {
            if (powder.source.equals(meta) && powder.prop == prop)
                return powder;
        }
        return null;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack){
        return source.item.getItemStackDisplayName(dummyStack) + " " + super.getItemStackDisplayName(stack);
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
        tooltip.add(prop.stackDisplayHint());
    }

    public static Properties.Property getProperty(ItemStack stack) {
        Item item = stack.getItem();
        if(item instanceof ItemPowder)
        {
            return ((ItemPowder) item).prop;
        }
        throw new IllegalArgumentException("Given itemStack is not a powder");
    }

    @RegistryCallback
    public static void registerItems(RegistryEvent.Register<Item> event) {
        for(Map.Entry<MatExtraction.ItemMeta, List<Properties.Property>> entry: MatExtraction.allRecipes.entrySet())
        {
            for(Properties.Property p:entry.getValue()) {
                ItemPowder item = new ItemPowder(entry.getKey(), p);
                POWDERS.add(item);
                item.setRegistryName("academy:"+item.internalID());
                item.setTranslationKey("powder");
                item.setCreativeTab(cn.academy.AcademyCraft.cct);
                event.getRegistry().register(item);
                if (SideUtils.isClient())
                {
                    ModelResourceLocation model =new ModelResourceLocation("academy:powder/"+item.internalID(), "inventory");
                    ModelLoader.setCustomModelResourceLocation(item, 0, model);

                }
            }
        }

    }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof ItemPowder && ((ItemPowder) o).source.equals(source)&&((ItemPowder) o).prop.equals(prop);
    }

}
