package cage.generator;

import cage.GeneratorInfo;
import cage.GeneratorPanel;

import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;

public class TubesConesPanel extends GeneratorPanel {

    private static final boolean debug = false;
    JTabbedPane pane = new JTabbedPane();

    public TubesConesPanel() {
        pane.addTab("nanotubes", new TubetypePanel());
        pane.addTab("nanocones", new NanoConesPanel());
        for (int i = 0; i < pane.getTabCount(); ++i) {
            ((GeneratorPanel) pane.getComponentAt(i)).setBorder(
                    BorderFactory.createEmptyBorder(20, 20, 20, 20));
        }
        add(pane);
    }

    public void showing() {
    }

    public GeneratorInfo getGeneratorInfo() {
        GeneratorPanel chosenPanel = (GeneratorPanel) pane.getSelectedComponent();
        GeneratorInfo info = chosenPanel.getGeneratorInfo();
        return info;
    }
}
