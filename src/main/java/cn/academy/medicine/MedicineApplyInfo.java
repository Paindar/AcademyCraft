package cn.academy.medicine;

import org.lwjgl.util.Color;

public class MedicineApplyInfo{
    public Properties.Target target;
    public Properties.Strength strengthType;
    public float strengthModifier;
    public Properties.ApplyMethod method;
    public float sensitiveRatio;

    public MedicineApplyInfo(Properties.Target target, Properties.Strength strengthType, float strengthModifier,
                             Properties.ApplyMethod method, float sensitiveRatio)
    {
        this.target=target;
        this.strengthType=strengthType;
        this.strengthModifier=strengthModifier;
        this.method=method;
        this.sensitiveRatio=sensitiveRatio;

        float[] hsb = toHSB(target.baseColor);
        hsb[2] = Math.min(1f, strengthModifier * 0.6666f);
        displayColor=fromHSB(hsb);
    }




    private float[] toHSB(Color color){
        return color.toHSB(null);
    }

    private Color fromHSB(float[] hsb){
        org.lwjgl.util.Color ret = new org.lwjgl.util.Color();
        ret.fromHSB(hsb[0], hsb[1], hsb[2]);
        return ret;
    }

    Color displayColor;

    @Override
    public boolean equals(Object o)
    {
        return o instanceof MedicineApplyInfo &&
                ((MedicineApplyInfo) o).target == target &&
                ((MedicineApplyInfo) o).method == method &&
                ((MedicineApplyInfo) o).strengthType == strengthType &&
                ((MedicineApplyInfo) o).strengthModifier == strengthModifier &&
                ((MedicineApplyInfo) o).sensitiveRatio == sensitiveRatio;
    }
}