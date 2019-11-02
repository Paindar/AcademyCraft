package cn.academy.medicine.blocks;

import cn.academy.Resources;
import cn.academy.core.client.ui.InventoryPage;
import cn.academy.core.client.ui.TechUI;
import cn.academy.core.client.ui.TechUI.Page;
import cn.academy.core.client.ui.TechUI.ContainerUI;
import cn.academy.core.client.ui.WirelessPage;
import cn.academy.medicine.MSNetEvents;
import cn.lambdalib2.cgui.Widget;
import cn.lambdalib2.cgui.component.ProgressBar;
import cn.lambdalib2.cgui.event.FrameEvent;
import cn.lambdalib2.cgui.event.LeftClickEvent;
import cn.lambdalib2.cgui.loader.CGUIDocument;
import cn.lambdalib2.s11n.network.NetworkMessage;
import scala.collection.JavaConverters;
import scala.collection.Seq;
import scala.runtime.AbstractFunction0;

import java.util.Arrays;
import java.util.List;

public class GuiMedSynthesizer {
    private static Widget template = null;

    public static <T> Seq<T> toSeq(T... t) {
        List<T> pages = Arrays.asList(t);
        return JavaConverters.asScalaIteratorConverter(pages.iterator()).asScala().toSeq();
    }

    public static TechUI.ContainerUI apply(ContainerMedSynthesizer container){
        TileMedSynthesizer tile = container.tile;
        if(template==null)
            template = CGUIDocument.read(Resources.getGui("rework/page_med_synth")).getWidget("main");//lazy load

        Widget invWidget = template.copy();

        Page invPage = InventoryPage.apply(invWidget);
        Page wirelessPage = WirelessPage.userPage(tile);

        invWidget.getWidget("btn_go").listen(LeftClickEvent.class,
                ((w, e) -> NetworkMessage.sendToServer(MSNetEvents.instance, MSNetEvents.MSG_BEGIN_SYNTH, tile)));

        {
            Widget widget = invWidget.getWidget("progress");
            ProgressBar progress = widget.getComponent(ProgressBar.class);
            widget.listen(FrameEvent.class, (w, e) -> progress.progress = tile.synthProgress());
        }
        ContainerUI ret = new TechUI.ContainerUI(container,  toSeq(invPage, wirelessPage));
        ret.infoPage().histogram(toSeq(TechUI.histEnergy(new AbstractFunction0<Object>() {
            public Object apply() {
                return tile.getEnergy();
            }
        }, tile.getMaxEnergy())));
        return ret;
    }
}
