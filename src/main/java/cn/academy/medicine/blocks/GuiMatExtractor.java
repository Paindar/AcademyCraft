package cn.academy.medicine.blocks;

import cn.academy.Resources;
import cn.academy.core.client.ui.InventoryPage;
import cn.academy.core.client.ui.TechUI;
import cn.academy.core.client.ui.TechUI.*;
import cn.academy.core.client.ui.WirelessPage;
import cn.lambdalib2.cgui.CGuiScreen;
import cn.lambdalib2.cgui.Widget;
import cn.lambdalib2.cgui.component.ProgressBar;
import cn.lambdalib2.cgui.event.FrameEvent;
import cn.lambdalib2.cgui.loader.CGUIDocument;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.collection.JavaConverters;
import scala.collection.Seq;
import scala.runtime.AbstractFunction0;

import java.util.Arrays;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiMatExtractor extends CGuiScreen {
    private static Widget template = CGUIDocument.read(Resources.getGui("rework/page_mat_extractor")).getWidget("main");

    public GuiMatExtractor() {
    }

    public static <T> Seq<T> toSeq(T... t) {
        List<T> pages = Arrays.asList(t);
        return JavaConverters.asScalaIteratorConverter(pages.iterator()).asScala().toSeq();
    }

    public static ContainerUI createNewScreen(ContainerMatExtractor container) {
        Widget window = template.copy();
        final TileMatExtractor tile = container.tile;
        Page invPage = InventoryPage.apply(window);
        Page wirelessPage = WirelessPage.userPage(tile);
        ContainerUI ret = new ContainerUI(container, toSeq(invPage, wirelessPage));
        window.getWidget("progress").listen(FrameEvent.class, (w, evt) -> {
            w.getComponent(ProgressBar.class).progress = tile.getWorkProgress();
        });
        ret.infoPage().histogram(toSeq(TechUI.histEnergy(new AbstractFunction0<Object>() {
            public Object apply() {
                return tile.getEnergy();
            }
        }, tile.getMaxEnergy())));
        return ret;
    }
}