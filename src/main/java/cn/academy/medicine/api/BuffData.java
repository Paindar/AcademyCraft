package cn.academy.medicine.api;

import cn.lambdalib2.datapart.DataPart;
import cn.lambdalib2.datapart.EntityData;
import cn.lambdalib2.datapart.RegDataPart;
import cn.lambdalib2.s11n.network.NetworkMessage;
import cn.lambdalib2.util.TickScheduler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.relauncher.Side;
import scala.reflect.ClassTag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RegDataPart(value=EntityPlayer.class)
public class BuffData extends DataPart<EntityPlayer> {
    /*class ClientFakeBuff extends Buff {
        @Override
        public void onBegin(EntityPlayer player) {

        }

        @Override
        public void onTick(EntityPlayer player, BuffApplyData applyData) {

        }

        @Override
        public void onEnd(EntityPlayer player) {

        }

        @Override
        public void load(NBTTagCompound tag) {

        }

        @Override
        public void store(NBTTagCompound tag) {

        }

        public ClientFakeBuff(String id) {
            super(id);
        }
    }*/

    private List<BuffRuntimeData> activeBuffs = new ArrayList<>();

    private TickScheduler scheduler = new TickScheduler();
    private float resistance = 1.00f;
    private int counter = 0;
    private static final int MAX_COUNT = 200;

    public BuffData(){
        setClientNeedSync();
        setNBTStorage();
        setTick(true);
        scheduler.every(10).atOnly(Side.SERVER).run(()->{
            this.sync();
            NBTTagCompound nbt = new NBTTagCompound();
            toNBT(nbt);
            sendToLocal("sync_list", nbt);
        });

        scheduler.everyTick().atOnly(Side.SERVER).run(() -> {
                EntityPlayer player = getEntity();
                Iterator iter = activeBuffs.iterator();
                List<BuffRuntimeData> removing = new ArrayList<>();
                while (iter.hasNext()) {
                    BuffRuntimeData data = (BuffRuntimeData)iter.next();
                    if (data.applyData.tickLeft > 0 || data.applyData.isInfinite()) {
                        if (!data.applyData.isInfinite()) {
                            data.applyData.tickLeft -= 1;
                        }
                        data.buff.onTick(player, data.applyData);
                    } else {
                        data.buff.onEnd(player);
                        //iter.remove();
                        removing.add(data);
                    }
                }
                activeBuffs.removeAll(removing);
                counter++;
                if(counter >=MAX_COUNT)
                {
                    counter = 0;
                    if(Math.abs(1-resistance)>=1e-3)
                    {
                        resistance = adjustResistance(resistance);
                    }

                }
            });
    }


    public void addBuffInfinite(Buff buff){
        addBuff(buff, -1);
    }

    public void addBuff(Buff buff, int maxTicks){
        checkSide(Side.SERVER);
        buff.onBegin(getEntity());
        activeBuffs.add(new BuffRuntimeData(buff, new BuffApplyData(maxTicks, maxTicks)));
        sync();
    }

    public List<BuffRuntimeData> rawData()
    {
        return activeBuffs;
    }

    public <T extends Buff> T findBuff(ClassTag<T> tag){
        for(BuffRuntimeData buff : activeBuffs)
        {
            if(tag.runtimeClass().isInstance(buff)){
                return (T)(buff.buff);
            }
        }
        return null;
    }

    public void tick(){
        scheduler.runTick();
    }

    public float getResistance()
    {
        return resistance;
    }

    public void setResistance(float resistance)
    {
        this.resistance = resistance;
    }

    public float adjustResistance(float factor)
    {
        return 0.0001f + 2*factor/(1+factor);//Prevent factor = 0
    }

    public void toNBT(NBTTagCompound tag){
        NBTTagList list = new NBTTagList();
        for(BuffRuntimeData data:activeBuffs)
        {
            NBTTagCompound tag_ = new NBTTagCompound();
            NBTTagCompound buffTag = new NBTTagCompound();
            BuffRegistry.writeBuff(data.buff, buffTag);
            tag_.setInteger("maxTicks", data.applyData.maxTicks());
            tag_.setInteger("tickLeft", data.applyData.tickLeft);
            tag_.setTag("buff", buffTag);

            list.appendTag(tag_);
        }
        tag.setTag("buff",list);
        tag.setFloat("resistance", resistance);
        tag.setInteger("counter", counter);
    }

    public void  fromNBT(NBTTagCompound tag){
        resistance = tag.getFloat("resistance");
        counter = tag.getInteger("counter");
        activeBuffs.clear();
        NBTBase tBase = tag.getTag("buff");
        if (tBase instanceof NBTTagList){
            for(int i=0;i<((NBTTagList) tBase).tagCount();i++)
            {
                NBTTagCompound tag_=((NBTTagList) tBase).getCompoundTagAt(i);
                NBTTagCompound buffTag = (NBTTagCompound) tag_.getTag("buff");
                Buff buff = BuffRegistry.readBuff(buffTag);
                BuffApplyData applyData = new BuffApplyData(tag_.getInteger("tickLeft"),
                        tag_.getInteger("maxTicks"));
                activeBuffs.add(new BuffRuntimeData(buff, applyData));

            }
        }
    }

    @Override
    public void onPlayerDead()
    {
        activeBuffs.clear();
    }

    @NetworkMessage.Listener(channel="sync_list", side=Side.CLIENT)
    public void onReceive(NBTTagCompound nbt)
    {
        fromNBT(nbt);
    }

    public static BuffData apply(EntityPlayer player)
    {
        return EntityData.get(player).getPart(BuffData.class);
    }

}
