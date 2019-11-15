package cn.academy.event;

import cn.academy.ACConfig.GetACCfgValue;
import cn.academy.block.tileentity.TileGeneratorBase;
import cn.academy.block.tileentity.TileReceiverBase;
import cn.academy.datapart.CPData;
import cn.academy.energy.api.block.IWirelessNode;
import cn.academy.event.ability.CalcEvent;
import cn.academy.item.armor.ACArmorHelper;
import cn.lambdalib2.registry.StateEventCallback;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class EventListener {
    public static EventListener instance = null;
    @StateEventCallback
    public static void init(FMLInitializationEvent evt){
        instance = new EventListener();
        MinecraftForge.EVENT_BUS.register(instance);
    }

    class WorldTilePos{
        int worldId=0;
        BlockPos pos;
        double energy;
        Block blockType;

        public WorldTilePos(int worldId, BlockPos pos)
        {
            this.worldId=worldId;
            this.pos = pos;
        }
        public void setEnergy(double energy)
        {
            this.energy = energy;
        }

        public double getEnergy()
        {
            return this.energy;
        }


    }

    List<WorldTilePos> worldTilePosList = new ArrayList<>();

    @SubscribeEvent
    public void onAcademyBlockDestroyed(BlockEvent.BreakEvent event)
    {
        WorldTilePos tilePos = new WorldTilePos(event.getWorld().provider.getDimension(),event.getPos());
        TileEntity tile = event.getWorld().getTileEntity(event.getPos());
        if (tile instanceof TileReceiverBase)
        {
            tilePos.energy=((TileReceiverBase) tile).energy;
            tilePos.blockType=event.getState().getBlock();
            worldTilePosList.add(tilePos);
        }
        else if (tile instanceof TileGeneratorBase)
        {
            tilePos.energy=((TileGeneratorBase) tile).getEnergy();
            tilePos.blockType=event.getState().getBlock();
            worldTilePosList.add(tilePos);
        }
        else if (tile instanceof IWirelessNode)
        {
            tilePos.energy=((IWirelessNode) tile).getEnergy();
            tilePos.blockType=event.getState().getBlock();
            worldTilePosList.add(tilePos);
        }
    }

    @SubscribeEvent
    public void onAcademyBlockHarvest(BlockEvent.HarvestDropsEvent event)
    {
        int worldId=event.getWorld().provider.getDimension();
        for(WorldTilePos tilePos:worldTilePosList)
        {
            if(tilePos.worldId==worldId && tilePos.pos == event.getPos() && event.getState().getBlock()==tilePos.blockType)
            {
                List<ItemStack> items = event.getDrops();
                ItemStack item=items.get(0);
                NBTTagCompound nbt=item.getTagCompound();
                if(nbt==null)
                    item.setTagCompound(new NBTTagCompound());
                NBTTagCompound tag = new NBTTagCompound();
                tag.setDouble("Energy",tilePos.energy);
                item.setTagInfo("Academy",tag);
                worldTilePosList.remove(tilePos);
                break;
            }
        }
    }

    @SubscribeEvent
    public void onAcademyBlockPlaced(BlockEvent.PlaceEvent event)
    {
        World world=event.getWorld();
        TileEntity tile = world.getTileEntity(event.getPos());
        if (tile instanceof TileReceiverBase)
        {
            ItemStack item=event.getItemInHand();
            if(item.getTagCompound()==null)
                return;
            NBTTagCompound tag = item.getTagCompound().getCompoundTag("Academy");
            ((TileReceiverBase) tile).energy=tag.getDouble("Energy");
        }
        else if (tile instanceof TileGeneratorBase)
        {
            ItemStack item=event.getItemInHand();
            if(item.getTagCompound()==null)
                return;
            NBTTagCompound tag = item.getTagCompound().getCompoundTag("Academy");
            ((TileGeneratorBase) tile).setEnergy(tag.getDouble("Energy"));
        }
        else if (tile instanceof IWirelessNode)
        {
            ItemStack item=event.getItemInHand();
            if(item.getTagCompound()==null)
                return;
            NBTTagCompound tag = item.getTagCompound().getCompoundTag("Academy");
            ((IWirelessNode) tile).setEnergy(tag.getDouble("Energy"));
        }
    }

    @GetACCfgValue(path="ac.ability.data.lose_cp_amount")
    private static double loseCPAmount = 0;
    @GetACCfgValue(path="ac.ability.data.lose_cp_per")
    private static double loseCPPercent = 0.05;
    @GetACCfgValue(path="ac.ability.data.lose_overload_amount")
    private static double loseOLAmount = 0;
    @GetACCfgValue(path="ac.ability.data.lose_overload_per")
    private static double loseOLPercent = 0.05;
    @SubscribeEvent
    public void onPlayerDied(LivingDeathEvent event)
    {
        if(event.getEntityLiving() instanceof EntityPlayer)
        {
            CPData cpData = CPData.get((EntityPlayer) event.getEntityLiving());
            double cpLoss = cpData.getAddMaxCP()*loseCPPercent + loseCPAmount;
            double overloadLoss = cpData.getMaxOverload()*loseOLPercent + loseOLAmount;
            if(cpLoss>0)
                cpData.setAddMaxCP((float) (cpData.getAddMaxCP() - cpLoss));
            if(overloadLoss>0)
                cpData.setAddMaxOverload((float) (cpData.getAddMaxOverload() - overloadLoss));
        }
    }
}
