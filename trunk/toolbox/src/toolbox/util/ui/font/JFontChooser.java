package toolbox.util.ui.font;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import toolbox.util.Assert;
import toolbox.util.Banner;
import toolbox.util.ui.JSmartCheckBox;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.list.JSmartList;

/**
 * JFontChooser is a UI component that provides the ability to select a font
 * and its associated characteristics.
 */
public class JFontChooser extends JPanel
{
    private static final Logger logger_ = Logger.getLogger(JFontChooser.class);
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * JList for font family. 
     */
    private JList fontFamilyList_;
    
    /** 
     * FontStlyeList (subclass of JList) for font style. 
     */
    private FontStyleList fontStyleList_;
    
    /** 
     * Font size textfield. The size cannot be fractional (must be an integer). 
     */
    private JTextField fontSize_;
    
    /** 
     * List containing predefined font sizes. 
     */
    private JList fontSizeList_;

    /** 
     * Check box that toggles anti-aliasing of the selected font. 
     */
    private JCheckBox antiAliasCheckBox_;
    
    /** 
     * PhraseCanvas in which font samples are displayed. 
     */
    private PhraseCanvas phraseCanvas_;

    /** 
     * List of font chooser listeners. 
     */
    private List listeners_ = new ArrayList();

    /** 
     * Maximum number of characters permissibile in a valid font size. 
     */
    private int maxNumCharsInFontSize_ = 3;

    //--------------------------------------------------------------------------
    //  Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JFontChooser.
     */
    public JFontChooser()
    {
        this(null);
    }


    /**
     * Like {@link #JFontChooser(java.awt.Font, String[], int[], boolean)}, 
     * except that a default list of styles{"Plain", "Bold", "Italic", 
     * "Bold Italic"} and font sizes {8, 9, 10, 12, 14} will be used.
     * 
     * @param initialFont Initial font to selected
     */
    public JFontChooser(Font initialFont)
    {
        // Don't change the following two values without changing the javadocs
        this(initialFont,
            new String[] {"Plain", "Bold", "Italic", "Bold Italic"},
            new int[] {7, 8, 9, 10, 11, 12, 14, 16, 18, 24, 36}, false);
    }


    /**
     * Construct a new JFontChooser whose family, style & size widget
     * selections are set according to the supplied initial Font. Additionally,
     * the style & size values available will be dictated by the values in
     * styleDisplayNames and predefinedSizes, respectively.
     * 
     * @param initialFont 
     * 
     *      Newly constructed JFontChooser's family, style, and size widgets 
     *      will be set according to this value. This value may be null, in 
     *      which case an initial font will be automatically created. This 
     *      auto-created font will have a family, style, and size corresponding
     *      to the first avaiable value in the widget form family, style, and 
     *      size respectively.
     * 
     * @param styleDisplayNames
     *  
     *      Must contain exactly four members. The members of this array 
     *      represent the following styles, in order: Font.PLAIN, Font.BOLD, 
     *      Font.ITALIC, and Font.BOLD+Font.ITALIC
     * 
     * @param predefinedSizes  
     * 
     *      Must contain one or more predefined font sizes which will be 
     *      available to the user as a convenience for populating the font 
     *      size text field; all values must be greater than 0.
     * 
     * @param antiAlias  Turns on antialiasing of fonts
     */    
    public JFontChooser(Font initialFont, 
                        String[] styleDisplayNames,
                        int[] predefinedSizes,     
                        boolean antiAlias)
    {
        buildView(initialFont, styleDisplayNames, predefinedSizes, antiAlias);
        wireView();
        setAntiAlias(false);
        phraseCanvas_.invalidate();
        phraseCanvas_.repaint();
        
        logger_.debug("\n" + Banner.getBanner("AntialiasX = " + antiAlias));
        
        setAntiAlias(antiAlias);
        phraseCanvas_.invalidate();
        phraseCanvas_.repaint();
        
    }

    //--------------------------------------------------------------------------
    //  Private
    //--------------------------------------------------------------------------

    /**
     * Builds the GUI.
     * 
     * @param initialFont Initial font selected
     * @param styleDisplayNames Font styles
     * @param predefinedSizes Default set of font sizes
     * @param antiAlias Turn on antialias checkbox
     */
    protected void buildView(
        Font     initialFont, 
        String[] styleDisplayNames,
        int[]    predefinedSizes, 
        boolean  antiAlias)
    {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();

        String[] availableFontFamilyNames = 
            GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();

        // Sets initial font if one is not provided
        if (initialFont == null)
            initialFont =  
                new Font(
                    availableFontFamilyNames[0], 
                    Font.PLAIN, 
                    predefinedSizes[0]);

        // Configure font family list
        fontFamilyList_ = new JSmartList(availableFontFamilyNames);
        fontFamilyList_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontFamilyList_.setVisibleRowCount(8);

        // Add to gridbag
        gbc.weightx    = 1; gbc.weighty   = 1;
        gbc.gridx      = 1; gbc.gridy     = 1;
        gbc.gridheight = 3; gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        add(wrapWithHeading("Font", new JScrollPane(fontFamilyList_)), gbc);

        // Configure font style list
        fontStyleList_ = new FontStyleList(styleDisplayNames);
        fontStyleList_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontStyleList_.setVisibleRowCount(4);

        // Add to gridbag
        gbc.weightx    = 0.75; gbc.weighty   = 1;
        gbc.gridx      = 2;    gbc.gridy     = 1;
        gbc.gridheight = 2;    gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 10, 0, 0);
        add(wrapWithHeading("Style", new JScrollPane(fontStyleList_)), gbc);

        // Configure anti-alias checkbox
        antiAliasCheckBox_ = new JSmartCheckBox(new AntiAliasAction());
        setAntiAlias(antiAlias);

        // Add to gridbag
        gbc.weightx    = 0.75; gbc.weighty   = 0;
        gbc.gridx      = 2;    gbc.gridy     = 3;
        gbc.gridheight = 1;    gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(antiAliasCheckBox_, gbc);

        // Configure font size field
        fontSize_ = new JSmartTextField();
        fontSize_.setColumns(4);
        
        // Add to gridbag
        gbc.weightx    = 0.5;  gbc.weighty   = 0;
        gbc.gridx      = 3;    gbc.gridy     = 1;
        gbc.gridheight = 1;    gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(wrapWithHeading("Size", fontSize_), gbc);
        
        // Configure font size list
        fontSizeList_ =
            new JSmartList(validateAndConvertPredefinedSizes(predefinedSizes));
            
        fontSizeList_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontSizeList_.setVisibleRowCount(4);
        
        // Add to gridbag
        gbc.weightx    = 0.5; gbc.weighty   = 1;
        gbc.gridx      = 3;   gbc.gridy     = 2;
        gbc.gridheight = 2;   gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 0, 0);
        gbc.fill = GridBagConstraints.BOTH;
        add(new JScrollPane(fontSizeList_), gbc);

        // Configure Phrase Canvas (displays current font selection)
        phraseCanvas_ = new PhraseCanvas(initialFont.getFamily(), initialFont, 
            Color.black, antiAlias);
        
        JPanel phrasePanel = new JPanel(new BorderLayout());
        phrasePanel.add(BorderLayout.CENTER, phraseCanvas_);
        phrasePanel.setBorder(BorderFactory.createEtchedBorder());
        phrasePanel.setPreferredSize(new Dimension(0, 100));
        
        // Add to gridbag
        gbc.weightx    = 1; gbc.weighty   = 1;
        gbc.gridx      = 1; gbc.gridy     = 4;
        gbc.gridheight = 1; gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 0, 0, 0);
        gbc.fill = GridBagConstraints.BOTH;
        add(phrasePanel, gbc);        

        // Set initial widget values here at the end of the constructor to 
        // ensure that all listeners have been added beforehand
        // fontFamilyList_.setSelectedValue(initialFont.getFamily(), true);
        fontFamilyList_.setSelectedValue(initialFont.getFontName(), true);
        fontStyleList_.setSelectedStyle(initialFont.getStyle());
        fontSize_.setText(String.valueOf(initialFont.getSize()));
    }


    /**
     * Wraps a component in a panel with a heading.
     * 
     * @param heading Heading
     * @param component Component to wrap with a heading
     * @return Wrapped component as a JPanel
     */
    protected JPanel wrapWithHeading(String heading, JComponent component)
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(BorderLayout.NORTH, new JSmartLabel(heading));
        panel.add(BorderLayout.CENTER, component);
        return panel;    
    }

    
    /**
     * Wires the GUI with appropriate event listeners.
     */
    protected void wireView()
    {
        fontFamilyList_.addListSelectionListener(new FontSelectionListener());
        fontStyleList_.addListSelectionListener(new FontSelectionListener());
        addFontSelectionListener(new PhraseFontSelectionListener());        
        
        // Use FontSizeSynchronizer to ensure consistency between text field &
        // list for font size
        FontSizeSynchronizer fontSizeSynchronizer =
            new FontSizeSynchronizer(fontSizeList_, fontSize_);
            
        fontSizeList_.addListSelectionListener(fontSizeSynchronizer);
        fontSize_.getDocument().addDocumentListener(fontSizeSynchronizer);
    }


    /**
     * Validates predefinted font sizes.
     * 
     * @param predefinedSizes Array of font sizes
     * @throws IllegalArgumentException thrown if predefinedSizes does not 
     *         contain one or more integer values or if it contains any 
     *         integers with a value of less than 1.
     * @return Integer Array.
     */
    private Integer[] validateAndConvertPredefinedSizes(int[] predefinedSizes)
    {
        if (predefinedSizes == null)
            throw new IllegalArgumentException(
                "int[] predefinedSizes may not be null");

        if (predefinedSizes.length < 1)
            throw new IllegalArgumentException(
                "int[] predefinedSizes must contain one or more values");
        
        Integer[] predefinedSizeIntegers = new Integer[predefinedSizes.length];
        
        for (int i = 0; i < predefinedSizes.length; i++)
        {
            if (predefinedSizes[i] < 1)
                throw new IllegalArgumentException(
                    "int[] predefinedSizes may not contain integers" +
                    " with value less than 1");
     
            predefinedSizeIntegers[i] = new Integer(predefinedSizes[i]);
        }
        
        return predefinedSizeIntegers;
    }

    //--------------------------------------------------------------------------
    // Event Listener Support
    //--------------------------------------------------------------------------

    /**
     * Adds an listener to this JFontChooser.
     * 
     * @param listener Font selection listener to add
     */
    public void addFontSelectionListener(IFontChooserListener listener)
    {
        listeners_.add(listener);
    }
    
    
    /**
     * Removes an listener from this JFontChooser.
     * 
     * @param listener Font selection listener to remove
     */
    public void removeFontSelectionListener(IFontChooserListener listener)
    {
        listeners_.remove(listener);
    }
    
    
    /**
     * Fires notification for font selection change.
     */
    protected void fireFontSelectionChanged()
    {
        for (Iterator i = listeners_.iterator(); i.hasNext();)
        {
            IFontChooserListener listener = (IFontChooserListener) i.next();
            listener.fontChanged();
        }
    }
    
    //--------------------------------------------------------------------------
    // Public 
    //--------------------------------------------------------------------------
    
    /**
     * Returns true if the antialias check box is selected, false otherwise.
     * 
     * @return Antialias flag
     */
    public boolean isAntiAliased()
    {
        return antiAliasCheckBox_.isSelected();
    }
    
    
    /**
     * Sets the antialias flag.
     * 
     * @param b True for antialias on, false otherwise
     */
    public void setAntiAlias(boolean b)
    {
        antiAliasCheckBox_.setSelected(b);
        
        if (phraseCanvas_ != null) 
            phraseCanvas_.setAntiAlias(b);
    }
    
    
    /**
     * Returns the currently selected font family.
     * 
     * @return Currently selected font family
     * @throws FontChooserException thrown if no font family is currently 
     *         selected
     */
    public String getSelectedFontFamily() throws FontChooserException
    {
        String fontFamily = (String) fontFamilyList_.getSelectedValue();
        
        if (fontFamily == null)
            throw new FontChooserException(
                "No font family is currently selected");

        return fontFamily;
    }

    /**
     * Returns the currently selected font style.
     * 
     * @return Currently selected font style. This value will correspond to one
     *         of the font styles specified in {@link java.awt.Font}
     * @throws FontChooserException thrown if no font style is currently 
     *         selected
     */
    public int getSelectedFontStyle() throws FontChooserException
    {
        return fontStyleList_.getSelectedStyle();
    }


    /**
     * Returns the currently selected font size.
     * 
     * @return Currently selected font size.
     * @throws FontChooserException thrown if no font size is currently 
     *         specified
     */
    public int getSelectedFontSize() throws FontChooserException 
    {
        String fontSize = fontSize_.getText();
        
        if ((fontSize == null) || (fontSize.equals("")))
            throw new FontChooserException("No font size specified");
        
        if (fontSize.length() > maxNumCharsInFontSize_)
            throw new FontChooserException(
                "Too many characters in font size");
            
        try
        {
            return Integer.parseInt(fontSize);
        }
        catch (NumberFormatException e)
        {
            throw new FontChooserException(
                "The number specified in the font size text field (" + 
                fontSize_.getText() + ") is not a valid integer.");
        }
    }


    /**
     * Returns the currently selected font.
     * 
     * @return Currently selected font.
     * @throws FontChooserException thrown if no valid font is currently 
     *         specified.
     */
    public Font getSelectedFont() throws FontChooserException
    {
        return new Font(
            getSelectedFontFamily(), 
            getSelectedFontStyle(), 
            getSelectedFontSize());
    }


    /**
     * Changes the currently selected font by assigning all widget values to 
     * match the family/style/size values of the supplied font.
     * 
     * @param font Font whose values should be used to set widgets
     * @throws IllegalArgumentException thrown if the family or style of the
     *         font supplied are not available or invalid
     */
    public void setSelectedFont(Font font) throws IllegalArgumentException
    {
        setSelectedFontFamily(font.getName());
        setSelectedFontStyle(font.getStyle());
        setSelectedFontSize(font.getSize());
    }


    /**
     * Sets the currently selected font family.
     * 
     * @param family Family to which selection should change
     * @throws IllegalArgumentException thrown if the supplied font family is
     *         not among the list of available font families
     */
    public void setSelectedFontFamily(String family) 
        throws IllegalArgumentException
    {
        ListModel familyListModel = fontFamilyList_.getModel();
        
        for (int i = 0; i < familyListModel.getSize(); i++)
        {
            if (familyListModel.getElementAt(i).equals(family))
            {
                fontFamilyList_.setSelectedValue(family, true);
                return;
            }
        }
        
        logger_.warn("The font family supplied, '" + family + 
            "', is not in the list of availalbe font families.");
    }


    /**
     * Sets the currently selected font style.
     * 
     * @param style Style to which selection should change
     * @throws IllegalArgumentException thrown if the supplied font style is
     *          not one of Font.PLAIN, Font.BOLD, Font.ITALIC, or 
     *          Font.BOLD+Font.ITALIC
     */
    public void setSelectedFontStyle(int style) throws IllegalArgumentException
    {
        fontStyleList_.setSelectedStyle(style);
    }


    /**
     * Sets the currently selected font size.
     * 
     * @param size Size to which selection should change
     */
    public void setSelectedFontSize(int size)
    {
        fontSize_.setText(String.valueOf(size));
    }

    //--------------------------------------------------------------------------
    // FontSizeSynchronizer
    //--------------------------------------------------------------------------
    
    /**
     * This class synchronizes font size value between the list containing
     * available font sizes & the text field in which font size is ultimately
     * specified.
     */
    protected class FontSizeSynchronizer implements DocumentListener, 
        ListSelectionListener
    {
        private JList list_;
        private JTextField textField_;
        private boolean updating_;
        
        /**
         * Creates a FontSizeSynchronizer.
         * 
         * @param list List containing predefined font sizes 
         * @param textField Text field in which font size is specified
         */
        public FontSizeSynchronizer(JList list, JTextField textField)
        {
            list_ = list;
            textField_ = textField;
        }

        /** 
         * Called when a value is changed.
         * 
         * @param e List event that caused change in value
         * @see javax.swing.event.ListSelectionListener 
         */
        public void valueChanged(ListSelectionEvent e)
        {
            if (updating_)
                return;

            updating_ = true;
            
            Object selectedValue =
                ((JList) e.getSource()).getSelectedValue();
                
            if (selectedValue != null)
                textField_.setText(selectedValue.toString());

            fireFontSelectionChanged();
            
            updating_ = false;
        }

        /** 
         * @see javax.swing.event.DocumentListener 
         */
        public void changedUpdate(DocumentEvent e)
        {
            handle(e);
        }
       
        /**
         *  @see javax.swing.event.DocumentListener 
         */
        public void insertUpdate(DocumentEvent e)
        {
            handle(e);
        }
        
        /** 
         * @see javax.swing.event.DocumentListener 
         */
        public void removeUpdate(DocumentEvent e)
        {
            handle(e);
        }
        
        /** 
         * Handles all DocumentEvents 
         * 
         * @param e Document event
         */
        protected void handle(DocumentEvent e)
        {
            Assert.notNull(e);
            
            if (updating_)
                return;

            updating_ = true;
            
            try
            {
                Integer currentFontSizeInteger =
                    Integer.valueOf(textField_.getText());
                    
                boolean currentSizeWasInList = false;
                Object listMember;
                
                for (int i = 0; i < list_.getModel().getSize(); i++)
                {
                    listMember = list_.getModel().getElementAt(i);
                    
                    if (listMember.equals(currentFontSizeInteger))
                    {
                        list_.setSelectedValue(currentFontSizeInteger, true);
                        currentSizeWasInList = true;
                        break;
                    }
                }
                
                if (!currentSizeWasInList)
                    list_.clearSelection();
            }
            catch (NumberFormatException nfe)
            {
                list_.clearSelection();
            }
            
            fireFontSelectionChanged();
            
            updating_ = false;
        }
    }
    
    //--------------------------------------------------------------------------
    // FontSelectionListener
    //--------------------------------------------------------------------------
    
    /**
     * Listener for the font name list.
     */
    protected class FontSelectionListener implements ListSelectionListener
    {
        /**
         * @see javax.swing.event.ListSelectionListener#valueChanged(
         *      javax.swing.event.ListSelectionEvent)
         */
        public void valueChanged(ListSelectionEvent e)
        {
            fireFontSelectionChanged();
        }
    }
    
    //--------------------------------------------------------------------------
    // PhraseFontSelectionListener
    //--------------------------------------------------------------------------
    
    /**
     * Listener that notifies the phraseCanvas of font changes.
     */
    protected class PhraseFontSelectionListener implements 
        IFontChooserListener
    {
        /**
         * @see toolbox.util.ui.font.IFontChooserListener#fontChanged()
         */
        public void fontChanged()
        {
            try
            {
                phraseCanvas_.setPhrase(
                    (String) fontFamilyList_.getSelectedValue());
                    
                phraseCanvas_.setFont(
                    JFontChooser.this.getSelectedFont());
                    
                phraseCanvas_.setAntiAlias(JFontChooser.this.isAntiAliased());
            }
            catch (FontChooserException e)
            {
                phraseCanvas_.setPhrase("");
            }
            
            phraseCanvas_.invalidate();
            phraseCanvas_.repaint();
        }
    }
    
    //--------------------------------------------------------------------------
    // AntiAliasAction
    //--------------------------------------------------------------------------
    
    /**
     * Action to toggle antialias of fonts.
     */
    private class AntiAliasAction extends AbstractAction
    {
        /**
         * Creates a AntiAliasAction.
         */
        public AntiAliasAction()
        {
            super("Anti-alias");
            putValue(MNEMONIC_KEY, new Integer('A'));
            putValue(SHORT_DESCRIPTION, "Toggles antialiasing of fonts");
            putValue(ACCELERATOR_KEY, 
                KeyStroke.getKeyStroke(KeyEvent.VK_A, Event.CTRL_MASK));
        }

        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(
         *      java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            phraseCanvas_.setAntiAlias(antiAliasCheckBox_.isSelected());
            phraseCanvas_.repaint();
            fireFontSelectionChanged();
        }
    }
}

/**
Based on work by:

Copyright (C) 2000, 2001 Greg Merrill (greghmerrill@yahoo.com)

This file is part of Follow (http://follow.sf.net).

Follow is free software; you can redistribute it and/or modify
it under the terms of version 2 of the GNU General Public
License as published by the Free Software Foundation.

Follow is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Follow; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
