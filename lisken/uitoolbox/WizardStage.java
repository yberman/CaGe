
package lisken.uitoolbox;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class WizardStage
{
  JComponent content;
  ActionListener listener;

  public JButton previousButton;
  public JButton nextButton;
  public JButton finishButton;
  public JButton cancelButton;
  public JButton exitButton;
  public boolean setDefaultButton;
  public boolean hasAnyButtons;

  public WizardStage(String title, JComponent content,
   WindowListener windowListener, ActionListener escapeListener,
   ActionListener wizardListener,
   String previous, String next, String finish, String cancel, String exit,
   boolean setDefaultButton)
  {
    this.content           = content;
    this.listener          = wizardListener;
    this.setDefaultButton  = setDefaultButton;
    hasAnyButtons          = false;

    previousButton = createButton(previous, Wizard.PREVIOUS, "lisken/uitoolbox/WizardPrevious.gif", SwingConstants.RIGHT);
    nextButton     = createButton(next,     Wizard.NEXT,     "lisken/uitoolbox/WizardNext.gif",     SwingConstants.LEFT);
    finishButton   = createButton(finish,   Wizard.FINISH,   null, 0);
    cancelButton   = createButton(cancel,   Wizard.CANCEL,   null, 0);
    exitButton     = createButton(exit,     Wizard.EXIT,     null, 0);
  }

  JButton createButton(String buttonText, String actionCmd, String IconPath, int textPosition)
  {
    if (buttonText == null) return null;
    hasAnyButtons = true;
    JButton button = new JButton(buttonText);
    if (IconPath != null) {
      button.setIcon(new ImageIcon(ClassLoader.getSystemResource(IconPath)));
      button.setHorizontalTextPosition(textPosition);
    }
    button.setActionCommand(actionCmd);
    button.addActionListener(listener);
    return button;
  }

/*
  public void setDefaultButton(JButton defaultButton)
  {
    if (defaultButton != null || getRootPane().getDefaultButton() != null) {
      getRootPane().setDefaultButton(defaultButton);
    }
  }
*/

  public void setDefaultButton(JRootPane rootPane)
  {
    if (nextButton != null) {
      rootPane.setDefaultButton(nextButton);
    } else if (finishButton != null) {
      rootPane.setDefaultButton(finishButton);
    } else {
      // rootPane.setDefaultButton(null);
    }
  }
}

