package B;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Z.A;
import Z.C;

import com.javio.webwindow.QC;

import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;

public class B extends JPanel
    implements C, A, ActionListener
{
    static
    {
        System.out.println(StringUtil.addBars("Loaded debug B.B"));
    }

    String I;
    JTextField addActionListener;
    JButton addComponent;

    public B()
    {
        setOpaque(false);
        setBackground(Color.black);
        addComponent = new JButton("Browse...");
        addComponent.addActionListener(this);
        addActionListener = new JTextField(20);
        Dimension dimension = addActionListener.getPreferredSize();
        Dimension dimension1 = addComponent.getPreferredSize();
        addComponent.setPreferredSize(new Dimension(dimension1.width, dimension.height));
        setLayout(new GridBagLayout());
        QC.addComponent(this, addActionListener, 17, 2, 0, 0, 1, 1, new Insets(5, 0, 0, 5), 0, 0, 1.0D, 0.0D);
        QC.addComponent(this, addComponent, 17, 0, 1, 0, 1, 1, new Insets(5, 0, 0, 5), 0, 0, 0.0D, 0.0D);
    }

    public final void reset()
    {
        addActionListener.setText(I);
    }

    public final Dimension getMinimumSize()
    {
        return getPreferredSize();
    }

    public final String getValue()
    {
        return addActionListener.getText();
    }

    public final void dereference()
    {
    }

    public final void actionPerformed(ActionEvent actionevent)
    {
        if (actionevent.getSource() == addComponent)
        {
            JFileChooser jfilechooser = new JFileChooser();
            jfilechooser.setDialogTitle("Choose File");
            jfilechooser.rescanCurrentDirectory();
            int i = jfilechooser.showOpenDialog(this);
            if (i == 0)
            {
                String s = jfilechooser.getSelectedFile().toString();
                addActionListener.setText(s);
                addActionListener.setCaretPosition(0);
                addActionListener.requestFocus();
            }
        }
    }

    public final void setFocused(boolean flag)
    {
        requestFocus();
    }
    
    //--------------------------------------------------------------------------
    // Overrides JComponent
    //--------------------------------------------------------------------------

    /**
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    public void paintComponent(Graphics gc)
    {
        SwingUtil.makeAntiAliased(gc, true);
        super.paintComponent(gc);
    }    
}
