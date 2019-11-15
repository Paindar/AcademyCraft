package cn.academy.medicine.blocks;

import cn.academy.block.tileentity.TileReceiverBase;
import cn.academy.energy.IFConstants;
import cn.academy.medicine.MatExtraction;
import cn.academy.medicine.Properties;
import cn.academy.medicine.items.ItemPowder;
import cn.lambdalib2.registry.mc.RegTileEntity;
import cn.lambdalib2.s11n.network.NetworkMessage;
import cn.lambdalib2.s11n.network.TargetPoints;
import cn.lambdalib2.util.RandUtils;
import cn.lambdalib2.util.TickScheduler;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

@RegTileEntity
public class TileMatExtractor extends TileReceiverBase
{

    private static float ProgPerTick = 0.04f;
    private static double ConsumePerSynth = 500;
    private static double ConsumePerTick = ConsumePerSynth * ProgPerTick;

    private boolean extracting = false;
    private double progress_ = 0.00;
    private MatExtraction.ItemMeta currentIn;
    private Properties.Property currentOut;
    TickScheduler scheduler = new TickScheduler();


    public TileMatExtractor()
    {
        super("mat_extractor", 4, 10000, IFConstants.LATENCY_MK2);
        scheduler.every(5).atOnly(Side.SERVER).run(this::sync);
    }

    @Override
    public void update(){
        World world = getWorld();
        if(world.isRemote)
        {
            super.update();
            return;
        }
        if(!canExtracting())
        {
            extracting = false;
            progress_  = 0;
        }
        if (extracting)
        {
            progress_ = Math.min(1, progress_ + ProgPerTick);
            if (progress_ >= 1.0f)
            {
                int slot = tryPushOut();
                if (slot != 0)
                {
                    ItemStack stackIn = getStackInSlot(0), stack = getStackInSlot(slot);
                    if( stackIn.getCount()==1)
                    {
                        setInventorySlotContents(0, ItemStack.EMPTY);
                    }
                    else
                    {
                        stackIn.grow(-1);
                    }
                    if(stack.isEmpty())
                    {
                        setInventorySlotContents(slot, new ItemStack(ItemPowder.getItemPowder(currentIn, currentOut),1));
                    }
                    else
                    {
                        stack.grow(1);
                    }
                    extracting = false;
                    progress_ = 0.0f;
                    currentIn=null;
                }
                else
                {
                    progress_=0.98f;
                }
            }
            else
            {
                if (canExtracting())
                {
                    boolean consEnergy = pullEnergy(ConsumePerTick) == ConsumePerTick;
                    if (!consEnergy)
                    {
                        extracting = false;
                        progress_ = 0.0f;
                    }
                }
            }
        }
        else {
            ItemStack stack = getStackInSlot(0);
            if(!stack.isEmpty())
            {
                MatExtraction.ItemMeta meta = new MatExtraction.ItemMeta(stack.getItem(), stack.getItemDamage());
                if (MatExtraction.allRecipes.containsKey(meta))
                {
                    currentIn = meta;
                    List<Properties.Property> results = MatExtraction.allRecipes.get(meta);
                    currentOut = results.get(RandUtils.nextInt(results.size()));
                    extracting = true;
                    progress_ = 0;
                }
            }

        }

        super.update();
        scheduler.runTick();
    }

    public double getWorkProgress()
    {
        return progress_;
    }
    private boolean canExtracting()
    {
        ItemStack stackIn = getStackInSlot(0);
        if (!stackIn.isEmpty() && currentIn !=null)
        {
            return currentIn.item == stackIn.getItem() && currentIn.meta == stackIn.getItemDamage();
        }
        return false;
    }

    private int tryPushOut()
    {
        int free = 0;
        for(int i=3;i>0;i--)
        {
            ItemStack output = getStackInSlot(i);
            if(output.isEmpty())
                free = i;
            else if (output.getItem() instanceof ItemPowder)
            {
                if (((ItemPowder) output.getItem()).source.equals(currentIn) &&
                        ((ItemPowder) output.getItem()).prop.equals(currentOut) &&
                        output.getCount()<=output.getMaxStackSize())
                {
                    return i;
                }
            }
        }
        return free;
    }

    private void sync(){
        NetworkMessage.sendToAllAround(TargetPoints.convert(this, 8), this, "synth_sync", extracting, progress_);
    }

    @NetworkMessage.Listener(channel="synth_sync", side=Side.CLIENT)
    private void hSyncSynth(Boolean ss, Double pr){
        extracting = ss;
        progress_ = pr;
    }
}
