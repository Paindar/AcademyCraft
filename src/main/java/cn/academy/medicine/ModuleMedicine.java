package cn.academy.medicine;

import cn.academy.AcademyCraft;
import cn.academy.block.tileentity.TileMetalFormer;
import cn.academy.crafting.MetalFormerRecipes;
import cn.academy.medicine.blocks.BlockMatExtractor;
import cn.academy.medicine.blocks.BlockMedSynthesizer;
import cn.academy.medicine.items.ItemMedicineBottle;
import cn.lambdalib2.registry.RegistryCallback;
import cn.lambdalib2.registry.StateEventCallback;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


/**
 * Initialization for medicine module.
 */
public class ModuleMedicine {

    public static BlockMatExtractor mat_extractor = new BlockMatExtractor();
    public static BlockMedSynthesizer medSynthesizer = new BlockMedSynthesizer();

    public static final ItemMedicineBottle medicineBottle = new ItemMedicineBottle();
    public static final ItemBlock itemMatExtractor = new ItemBlock(mat_extractor);
    public static final ItemBlock itemMedSynthesizer = new ItemBlock(medSynthesizer);

    public static Item emptyBottle = new Item();

    @RegistryCallback
    private static void registerItems(RegistryEvent.Register<Item> event) {
        emptyBottle.setRegistryName("academy:empty_med_bottle");
        emptyBottle.setTranslationKey("empty_med_bottle");
        emptyBottle.setCreativeTab(AcademyCraft.cct);
        event.getRegistry().register(emptyBottle);
        medicineBottle.setRegistryName("academy:medicine_bottle");
        medicineBottle.setTranslationKey("medicine_bottle");
        event.getRegistry().register(medicineBottle);
        itemMatExtractor.setRegistryName("academy:mat_extractor");
        itemMatExtractor.setTranslationKey("mat_extractor");
        itemMatExtractor.setCreativeTab(AcademyCraft.cct);
        event.getRegistry().register(itemMatExtractor);
        itemMedSynthesizer.setRegistryName("academy:medicine_synthesizer");
        itemMedSynthesizer.setTranslationKey("medicine_synthesizer");
        itemMedSynthesizer.setCreativeTab(AcademyCraft.cct);
        event.getRegistry().register(itemMedSynthesizer);
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            registerItemRenderers();
        }
    }


    @RegistryCallback
    private static void registerBlocks(RegistryEvent.Register<Block> event) {
        mat_extractor.setRegistryName("academy:mat_extractor");
        mat_extractor.setTranslationKey("mat_extractor");
        mat_extractor.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(mat_extractor);
        medSynthesizer.setRegistryName("academy:medicine_synthesizer");
        medSynthesizer.setTranslationKey("medicine_synthesizer");
        medSynthesizer.setCreativeTab(cn.academy.AcademyCraft.cct);
        event.getRegistry().register(medSynthesizer);
    }

    @SideOnly(Side.CLIENT)
    private static void registerItemRenderers() {
        ModelLoader.setCustomModelResourceLocation(emptyBottle, 0, new ModelResourceLocation("academy:empty_medicine_bottle", "inventory"));
        ModelLoader.setCustomModelResourceLocation(medicineBottle, 0, new ModelResourceLocation("academy:medicine_bottle", "inventory"));
        ModelLoader.setCustomModelResourceLocation(itemMatExtractor, 0, new ModelResourceLocation(mat_extractor.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(itemMedSynthesizer, 0, new ModelResourceLocation(medSynthesizer.getRegistryName(), "inventory"));
    }

    @StateEventCallback
    private static void init(FMLInitializationEvent evt)
    {
        MetalFormerRecipes mfr = MetalFormerRecipes.INSTANCE;
        mfr.add(new ItemStack(Blocks.GLASS, 1), new ItemStack(emptyBottle,2), TileMetalFormer.Mode.INCISE);
    }

}
