package core.guiaction;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.fest.swing.fixture.ContainerFixture;
import org.fest.swing.fixture.FrameFixture;

import core.TitleFinder;

public class SelectLibraryFrameAction extends AbstractGuiAction {

    public SelectLibraryFrameAction(FrameFixture testFrame) {
        super(testFrame);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void perform() {
        Map<String, ContainerFixture> windowTitles =
                TitleFinder.getWindowTitles(testFrame);
        Iterator<Entry<String, ContainerFixture>> iterator =
                windowTitles.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, ContainerFixture> entry = iterator.next();
            if (entry.getKey().contains("Unsaved Library")) {
                log.info("Selecting library frame " + entry.getKey());
                entry.getValue().table().click();
                break;
            }
        }
    }

}
