package cn.academy.client.ui;

import cn.academy.Resources;
import cn.academy.block.container.ContainerRevProcessor;
import cn.academy.block.tileentity.TileRevProcessor;
import cn.academy.core.client.ui.InventoryPage;
import cn.academy.core.client.ui.TechUI;
import cn.academy.core.client.ui.WirelessPage;
import cn.academy.core.client.ui.TechUI.ContainerUI;
import cn.academy.core.client.ui.TechUI.Page;
import java.util.Arrays;
import java.util.List;

import cn.lambdalib2.cgui.Widget;
import cn.lambdalib2.cgui.component.ProgressBar;
import cn.lambdalib2.cgui.event.FrameEvent;
import cn.lambdalib2.cgui.loader.CGUIDocument;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.collection.Iterator;
import scala.collection.JavaConverters;
import scala.collection.Seq;
import scala.runtime.AbstractFunction0;

@SideOnly(Side.CLIENT)
public class GuiRevProcessor {
    private static Widget template = CGUIDocument.read(Resources.getGui("rework/page_rev_processor")).getWidget("main");

    public GuiRevProcessor() {
    }

    public static <T> Seq<T> toSeq(T... t) {
        List<T> pages = Arrays.asList(t);
        return ((Iterator)JavaConverters.asScalaIteratorConverter(pages.iterator()).asScala()).toSeq();
    }

    public static ContainerUI createNewScreen(ContainerRevProcessor container) {
        Widget window = template.copy();
        final TileRevProcessor tile = (TileRevProcessor)container.tile;
        Page invPage = InventoryPage.apply(window);
        Page wirelessPage = WirelessPage.userPage(tile);
        ContainerUI ret = new ContainerUI(container, toSeq(invPage, wirelessPage));
        window.getWidget("progress").listen(FrameEvent.class, (w, evt) -> {
            ((ProgressBar)w.getComponent(ProgressBar.class)).progress = tile.getWorkProgress();
        });
        ret.infoPage().histogram(toSeq(TechUI.histEnergy(new AbstractFunction0<Object>() {
            public Object apply() {
                return tile.getEnergy();
            }
        }, tile.bufferSize)));
        return ret;
    }
}