package cage.utility;

import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.AbstractButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * An implementation of <code>GenericButtonGroup</code> that represents a
 * group where at least when button needs to be selected in case this group
 * is active.
 */
public class Min1ButtonGroup extends AbstractButtonGroup
        implements ChangeListener, KeyListener, MouseListener {

    String id;
    boolean active;
    AbstractButton deactivateButton;
    Hashtable selections;
    int lastModifiers;

    public Min1ButtonGroup() {
        this("", true, null);
    }

    public Min1ButtonGroup(String id) {
        this(id, true, null);
    }

    public Min1ButtonGroup(String id, boolean active) {
        this(id, active, null);
    }

    public Min1ButtonGroup(String id, boolean active, AbstractButton deactivateButton) {
        this.id = id;
        this.active = active;
        this.deactivateButton = deactivateButton;
        deactivateButton.addChangeListener(this);
        selections = new Hashtable();
    }

    public void add(AbstractButton button) {
        super.add(button);
        button.addKeyListener(this);
        button.addMouseListener(this);
        if (button.isSelected()) {
            selections.put(button, this);
        }
    }

    public void remove(AbstractButton button) {
        super.remove(button);
        button.removeKeyListener(this);
        button.removeMouseListener(this);
        if (button.isSelected()) {
            selections.remove(button);
        }
    }

    public void itemStateChanged(ItemEvent e) {
        AbstractButton button = (AbstractButton) e.getSource();
        if (button.isSelected()) {
            selections.put(button, this);
        } else {
            selections.remove(button);
        }
        int lastLowLevelModifiers = lastModifiers;
        lastModifiers = 0;
        if (active && selections.size() == 0) {
            if (deactivateButton != null && !button.hasFocus()) {
                deactivateButton.doClick();
                if (!active) {
                    return;
                }
            }
            button.setSelected(true);
        }
        if (lastLowLevelModifiers == InputEvent.SHIFT_MASK) {
            button.setSelected(true);
            Enumeration elements = getElements();
            while (elements.hasMoreElements()) {
                AbstractButton otherButton = (AbstractButton) elements.nextElement();
                if (otherButton != button && otherButton.isSelected()) {
                    otherButton.setSelected(false);
                }
            }
        }
    }

    public void setActive(boolean active) {
        if (this.active == active) {
            return;
        }
        this.active = active;
        if (active && selections.size() == 0 && buttons.size() > 0) {
            ((AbstractButton) buttons.elementAt(0)).setSelected(true);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void stateChanged(ChangeEvent e) {
        AbstractButton source = (AbstractButton) e.getSource();
        if (source == deactivateButton) {
            setActive(source.isSelected());
        }
    }

    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == ' ' && lastModifiers == InputEvent.SHIFT_MASK) {
            ((AbstractButton) e.getSource()).doClick();
        }
    }

    public void keyPressed(KeyEvent e) {
        lastModifiers = e.getModifiers();
    }

    public void keyReleased(KeyEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        lastModifiers = e.getModifiers() &
                ~(InputEvent.BUTTON1_MASK |
                InputEvent.BUTTON2_MASK |
                InputEvent.BUTTON3_MASK);
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}