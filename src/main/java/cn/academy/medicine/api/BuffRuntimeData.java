package cn.academy.medicine.api;

import cn.lambdalib2.s11n.network.NetworkS11nType;

@NetworkS11nType
public class BuffRuntimeData {
    public Buff buff;
    public BuffApplyData applyData;
    public BuffRuntimeData(){}
    public BuffRuntimeData(Buff buff, BuffApplyData data)
    {
        this.buff=buff;
        this.applyData=data;
    }
}
