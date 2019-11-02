package cn.academy.medicine;

import cn.academy.medicine.blocks.TileMedSynthesizer;
import cn.lambdalib2.registry.StateEventCallback;
import cn.lambdalib2.s11n.network.NetworkMessage;
import cn.lambdalib2.s11n.network.NetworkS11n;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

public class MSNetEvents {

    public static MSNetEvents instance = new MSNetEvents();
    @StateEventCallback
    public static void init(FMLInitializationEvent evt){
        NetworkS11n.addDirectInstance(instance);
    }

    public static final String MSG_BEGIN_SYNTH = "begin";

    @NetworkMessage.Listener(channel=MSG_BEGIN_SYNTH, side= Side.SERVER)
    public void hBegin(TileMedSynthesizer tile){
        tile.beginSynth();
    }

}
