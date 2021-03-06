package lisken.uitoolbox;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

/**
 * An extension of <code>JDialog</code> that can be asked whether the dialog
 * was closed successful or was cancelled.
 */
public class FlaggedJDialog extends JDialog {

    private boolean success = false;
    protected Component nearComponent = null;
    
    private ActionListener cancelListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            success = false;
            handleClosing();
            setVisible(false);
        }
    };
    
    private ActionListener defaultButtonListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            success = true;
            handleClosing();
            setVisible(false);
        }
    };

    public FlaggedJDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
        
        //cancel when user presses escape
        getRootPane().registerKeyboardAction(cancelListener,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public void setDefaultButton(JButton defaultButton) {
        defaultButton.addActionListener(defaultButtonListener);
        getRootPane().setDefaultButton(defaultButton);
    }

    public AbstractButton getDefaultButton() {
        return getRootPane().getDefaultButton();
    }

    public void setCancelButton(AbstractButton cancelButton) {
        cancelButton.addActionListener(cancelListener);
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setNearComponent(Component nearComponent) {
        this.nearComponent = nearComponent;
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible && !isVisible()) {
            UItoolbox.moveComponentNearComponent(this, nearComponent);
        }
        super.setVisible(visible);
    }
    
    /**
     * When pressing the default button or cancel button, this method gets called
     * after setting the success, but before hiding the dialog. By default nothing
     * happens in this method.
     */
    public void handleClosing(){
        //do nothing
    }
}

