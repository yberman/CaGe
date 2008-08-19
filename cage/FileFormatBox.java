
package cage;


import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import cage.writer.*;
import lisken.systoolbox.*;


public class FileFormatBox extends JComboBox
 implements ActionListener
{
  Vector writers = new Vector();
  int dimension = 0;
  JTextComponent filenameField;
  String oldExtension;

  public FileFormatBox(String variety, JTextComponent filenameField)
  {
    this.filenameField = filenameField;
    char firstChar = variety.charAt(0);
    if (Character.isDigit(firstChar)) {
      dimension = firstChar - '0';
    }
    Enumeration formats = Systoolbox.stringToVector(
     CaGe.config.getProperty("CaGe.Writers." + variety)).elements();
    while (formats.hasMoreElements())
    {
      String format = (String) formats.nextElement();
      CaGeWriter writer = createCaGeWriter(format);
      format = createCaGeWriter(format).getFormatName();
      addItem(format);
      writers.addElement(writer);
    }
    addActionListener(this);
    registerKeyboardAction(this, "",
     KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
     WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    // new onActionFocusSwitcher(filenameField, this);
    oldExtension = getCaGeWriter().getFileExtension();
  }

  CaGeWriter createCaGeWriter(String format)
  {
    CaGeWriter writer;
    writer = WriterFactory.createCaGeWriter(format);
    if (dimension > 0) {
      writer.setDimension(dimension);
    }
    return writer;
  }

  public CaGeWriter getCaGeWriter()
  {
    return (CaGeWriter) writers.elementAt(getSelectedIndex());
  }

  public void addExtension()
  {
    String currentName = filenameField.getText();
    if (currentName.trim().startsWith("|")) return;
    CaGeWriter writer = getCaGeWriter();
    String extension = writer.getFileExtension();
    filenameField.setText(currentName + "." + extension);
    oldExtension = extension;
  }

  public void actionPerformed(ActionEvent e)
  {
    String currentName = filenameField.getText();
    if (currentName.trim().startsWith("|")) return;
    int cut = currentName.length() - oldExtension.length() - 1;
    if (cut >= 0 &&
        currentName.substring(cut).equalsIgnoreCase("." + oldExtension)) {
      filenameField.setText(currentName.substring(0, cut));
    }
    addExtension();
    if (e.getActionCommand().length() == 0) {
      filenameField.requestFocus();
    }
  }
}


class onActionFocusSwitcher implements ActionListener
{
  JComponent component;
  public onActionFocusSwitcher(JComponent c)
  {
    component = c;
  }
  public onActionFocusSwitcher(JComponent c, JComponent target)
  {
    this(c);
    if (target instanceof JComboBox) {
      ((JComboBox) target).addActionListener(this);
    } else if (target instanceof AbstractButton) {
      ((AbstractButton) target).addActionListener(this);
    }
  }
  public void actionPerformed(ActionEvent e)
  {
    component.requestFocus();
    Component source = (Component) e.getSource();
    if (source instanceof AbstractButton) {
      if (! ((AbstractButton) source).isSelected()) return;
    }
//    source.transferFocus();
  }
}

