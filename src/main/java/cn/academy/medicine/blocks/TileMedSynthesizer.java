package cn.academy.medicine.blocks;

import cn.academy.block.tileentity.TileReceiverBase;
import cn.academy.medicine.MedicineApplyInfo;
import cn.academy.medicine.Properties;
import cn.academy.medicine.items.ItemMedicineBottle;
import cn.academy.medicine.items.ItemPowder;
import cn.lambdalib2.registry.mc.RegTileEntity;
import cn.lambdalib2.s11n.network.NetworkMessage;
import cn.lambdalib2.s11n.network.TargetPoints;
import cn.lambdalib2.util.RandUtils;
import cn.lambdalib2.util.TickScheduler;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.List;

@RegTileEntity
public class TileMedSynthesizer extends TileReceiverBase {

    private float progress_ = 0.0f;
    private boolean synthesizing_ = false;

    static float ProgPerTick = 0.04f;
    static double ConsumePerSynth = 2000;
    static double ConsumePerTick = ConsumePerSynth * ProgPerTick;
    static int bottleSlot = 4;

    TickScheduler scheduler = new TickScheduler();
    public TileMedSynthesizer() {
        super("medicine_synthesizer", 6, 10000, 100);
        scheduler.every(5).atOnly(Side.SERVER).run(this::sync);
    }


    @Override
    public void update(){
        World world = getWorld();

        if (synthesizing_) {
            progress_ = Math.min(1, progress_ + ProgPerTick);
            boolean consEnergy = false;

            if (!world.isRemote) {

                if (progress_ >= 1.0f) {
                    boolean result = doSynth();
                    if (result)
                    {
                        consEnergy = pullEnergy(ConsumePerTick) == ConsumePerTick;
                        if(consEnergy)
                        {
                            synthesizing_ = false;
                            progress_ = 0.0f;
                        }
                    }

                }
                else {
                    consEnergy = pullEnergy(ConsumePerTick) == ConsumePerTick;
                    if (!consEnergy || !canSynth()) {
                        synthesizing_ = false;
                        progress_ = 0.0f;
                    }
                }

            }
        }

        super.update();
        scheduler.runTick();
    }


    public void beginSynth(){
        if(!getWorld().isRemote) {
            if (!synthesizing_ && canSynth()) {
                progress_ = 0.0f;
                synthesizing_ = true;
                sync();
            }
        }
        else
            throw new IllegalArgumentException("requirement failed");
    }

    private void sync(){
        NetworkMessage.sendToAllAround(TargetPoints.convert(this, 8), this, "synth_sync",
                synthesizing_, progress_);
    }

    private boolean canSynth(){
        return (!inventory[0].isEmpty() ||!inventory[1].isEmpty() ||!inventory[2].isEmpty() ||!inventory[3].isEmpty() )
                && !inventory[bottleSlot].isEmpty() ;
    }

    public static MedicineApplyInfo synth(List<Properties.Property> mats){
        MedicineApplyInfo info=synthDirect(mats);
        if(info !=null)
            return info;
        else
        {
            return new MedicineApplyInfo(Properties.instance.Targ_Disposed, Properties.instance.Str_Strong,
                    1.5f, Properties.instance.Apply_Instant_Decr,
                    Properties.instance.Targ_Disposed.medSensitiveRatio * RandUtils.rangef(0.8f, 1.2f));
        }
    }

    private static <T extends Properties.Property> T findOne(List<Properties.Property> mats, Class<T> tag){
        List<Properties.Property> ret=new ArrayList<>();
        for(Properties.Property p:mats)
        {
            if(tag.isInstance(p))
                ret.add(p);
        }
        if (ret.size() == 1)
            return (T) ret.get(0);
        return null;
    }



    public static MedicineApplyInfo synthDirect(List<Properties.Property> mats){

        Properties.Target targ = findOne(mats,Properties.Target.class);
        Properties.Strength str = findOne(mats, Properties.Strength.class);
        Properties.ApplyMethod method = findOne(mats, Properties.ApplyMethod.class);
        Properties.Variation vars = findOne(mats, Properties.Variation.class);

        if(targ!=null && str!=null && method!=null){
            float variationMin = 0.8f, variationMax= 1.2f;
            float strValue = str.baseValue * method.strength;
            float medSensValue = targ.medSensitiveRatio;
            Properties.Strength rStrength = str;
            Properties.ApplyMethod rMethod = method;
            // Process variation
            if(vars==Properties.instance.Var_Infinity) {
                strValue = 10000;
                medSensValue = 0.99f;
                rStrength = Properties.instance.Str_Infinity;
            }
            else if (vars==Properties.instance.Var_Desens) {
                medSensValue = 1.5f * (medSensValue + 1);
                variationMin = 1.1f;
                variationMax = 1.7f;
            }
            else if (vars==Properties.instance.Var_Neutralize) {
                medSensValue = 0.99f;
                variationMin = 0.5f;
                variationMax = 1.1f;
            }
            else if (vars==Properties.instance.Var_Fluct) {
                medSensValue = 0.8f * (medSensValue + 1);
                variationMin = 0.6f;
                variationMax = 1.5f;
            }
            else if (vars==Properties.instance.Var_Stabilize) {
                medSensValue = 1.1f * (medSensValue + 1);
                variationMin = 0.9f;
                variationMax = 1.1f;
            }


            return new MedicineApplyInfo(targ, rStrength,
                    strValue * RandUtils.rangef(variationMin, variationMax),
                    rMethod, medSensValue);
        }
        return null;
    }

    public static boolean isInputSlot(int slotID)
    {
        return slotID >= 0 && slotID < 4;
    }

    public static int outputSlot = 5;

    private boolean doSynth(){
        if(!getWorld().isRemote) {
            List<Properties.Property> ps = new ArrayList<>();
            for(int i=0;i<4;i++){
                if(!inventory[i].isEmpty()){
                    ps.add(ItemPowder.getProperty(inventory[i]));
                }
            }
            MedicineApplyInfo result = synth(ps);
            ItemStack resultStack = ItemMedicineBottle.create(result);
            ItemStack outputStack = getStackInSlot(outputSlot);
            if( outputStack.isEmpty())
            {
                setInventorySlotContents(outputSlot, resultStack);
            }
            else
            {
                if (outputStack.getItem().equals(resultStack.getItem()) && outputStack.getCount()<outputStack.getMaxStackSize())
                {
                    outputStack.grow(1);
                }
                else
                    return false;
            }
            for(int i=0;i<4;i++){
                inventory[i].shrink(1);
                if (inventory[i].getCount() == 0) {
                    inventory[i]=ItemStack.EMPTY;
                }
            }

            inventory[bottleSlot].grow(-1);
            if (inventory[bottleSlot].getCount() == 0) {
                inventory[bottleSlot]=ItemStack.EMPTY;
            }
            return true;
        }
        else
            throw new IllegalArgumentException("requirement failed");

    }

    public float synthProgress(){
        return progress_;
    }
    public boolean synthesizing(){
        return synthesizing_;
    }

    @NetworkMessage.Listener(channel="synth_sync", side=Side.CLIENT)
    private void hSyncSynth(Boolean ss, Float pr){
        synthesizing_ = ss;
        progress_ = pr;
    }

}
