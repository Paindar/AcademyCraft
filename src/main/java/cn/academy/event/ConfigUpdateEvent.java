package cn.academy.event;

import net.minecraftforge.fml.common.eventhandler.Event;

public class ConfigUpdateEvent  extends Event {
    public enum Phase {START, END}
    public final Phase phase;
    public ConfigUpdateEvent(Phase phase) {
        this.phase = phase;
    }
}
