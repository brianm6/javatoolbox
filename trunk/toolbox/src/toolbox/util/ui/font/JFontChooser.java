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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import toolbox.util.SwingUtil;
import toolbox.util.ui.JSmartCheckBox;
import toolbox.util.ui.JSmartLabel;
import toolbox.util.ui.JSmartTextField;
import toolbox.util.ui.list.JSmartList;

/**
 * JFontChooser is a UI component that provides the ability to select a font
 * and its associated characteristics. Characteristics include:
 * <ul>
 *  <li>Font name
 *  <li>Font size
 *  <li>Font style (plain, bold, italic, bold + italic)
 *  <li>Antialiased rendering
 * </ul>
 * <br>
 * Tips:
 * <ul>
 *  <li>Use setMonospaceEmphasized() to make fixed width fonts stand out in the 
 *      font name list.
 *  <li>Use setRenderedUsingFont() to render a font name with the name that it 
 *      represents in the font name list.
 * </ul>
 * 
 * @see toolbox.util.ui.font.JFontChooserDialog
 */
public class JFontChooser extends JPanel
{
    private static final Logger logger_ = Logger.getLogger(JFontChooser.class);

    //--------------------------------------------------------------------------
    // Constants
    //--------------------------------------------------------------------------
    
    /** 
     * Maximum number of digits permissibile in a valid font size. 
     */
    private static final int MAX_DIGITS_IN_FONT_SIZE = 3;

    /**
     * List of default font styles.
     */
    private static final String[] DEFAULT_STYLES = 
        new String[] {"Plain", "Bold", "Italic", "Bold Italic"};
    
    /**
     * List of default font sizes.
     */
    private static final int[] DEFAULT_SIZES = 
        new int[] {7, 8, 9, 10, 11, 12, 14, 16, 18, 24, 36};

    
    //--------------------------------------------------------------------------
    // UI Name Constants
    //--------------------------------------------------------------------------
    
    public static final String NAME_FONT_LIST = "fontfamily.list";
    public static final String NAME_STYLE_LIST = "fontStyle.list";
    public static final String NAME_ANTIALIAS_CHECKBOX = "antialias.checkbox";
    public static final String NAME_SIZE_FIELD = "size.field";
    public static final String NAME_SIZE_LIST = "size.list";
    
    //--------------------------------------------------------------------------
    // Fields
    //--------------------------------------------------------------------------
    
    /** 
     * JList for font family. 
     */
    private JList fontFamilyList_;

    /**
     * Renderer for the font names in the list box.
     */
    private FontFamilyCellRenderer fontFamilyCellRenderer_;

    /** 
     * FontStlyeList (subclass of JList) for font style. 
     */
    private FontStyleList fontStyleList_;
    
    /** 
     * Font size textfield. The size cannot be fractional (must be an integer). 
     */
    private JTextField fontSizeField_;
    
    /** 
     * List containing predefined font sizes. 
     */
    private JList fontSizeList_;

    /** 
     * Check box that toggles anti-aliasing of the selected font. 
     */
    private JCheckBox antiAliasCheckBox_;
    
    /** 
     * Component in which font samples are displayed. 
     */
    private PhraseCanvas phraseCanvas_;

    /** 
     * List of font chooser listeners. 
     */
    private List listeners_;

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Creates a JFontChooser.
     */
    public JFontChooser()
    {
        this(null);
    }


    /**
     * Creates a JFontChooser with the given font and a default list of styles 
     * and font sizes.
     * 
     * @param initialFont Initial font to select.
     */
    public JFontChooser(Font initialFont)
    {
        this(initialFont, 
            DEFAULT_STYLES, 
            DEFAULT_SIZES, 
            SwingUtil.getDefaultAntiAlias());
    }


    /**
     * Constructs a new JFontChooser whose family, style & size widget
     * selections are set according to the supplied initial Font. Additionally,
     * the style and size values available will be dictated by the values in
     * styleDisplayNames and predefinedSizes, respectively.
     * 
     * @param initialFont Newly constructed JFontChooser's family, style, and
     *        size widgets will be set according to this value. This value may
     *        be null, in which case an initial font will be automatically
     *        created. This auto-created font will have a family, style, and
     *        size corresponding to the first avaiable value in the widget form
     *        family, style, and size respectively.
     * @param styleDisplayNames Must contain exactly four members. The members
     *        of this array represent the following styles, in order:
     *        Font.PLAIN, Font.BOLD, Font.ITALIC, and Font.BOLD+Font.ITALIC
     * @param predefinedSizes Must contain one or more predefined font sizes
     *        which will be available to the user as a convenience for
     *        populating the font size text field; all values must be greater
     *        than 0.
     * @param antiAlias Turns on antialiasing of fonts.
     */
    public JFontChooser(
        Font initialFont, 
        String[] styleDisplayNames,
        int[] predefinedSizes,     
        boolean antiAlias)
    {
        listeners_ = new ArrayList();
        buildView(initialFont, styleDisplayNames, predefinedSizes, antiAlias);
        wireView();
        setAntiAliased(false);
        phraseCanvas_.invalidate();
        phraseCanvas_.repaint();
        setAntiAliased(antiAlias);
        phraseCanvas_.invalidate();
        phraseCanvas_.repaint();
    }

    //--------------------------------------------------------------------------
    // Protected
    //--------------------------------------------------------------------------

    /**
     * Constructs the user interface.
     * 
     * @param initialFont Initial font selected.
     * @param styleDisplayNames Font styles.
     * @param predefinedSizes Default set of font sizes.
     * @param antiAlias Turn on antialias checkbox.
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
            GraphicsEnvironment
                .getLocalGraphicsEnvironment()
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
        fontFamilyCellRenderer_ = new FontFamilyCellRenderer();
        fontFamilyList_.setCellRenderer(fontFamilyCellRenderer_);
        fontFamilyList_.setName(NAME_FONT_LIST);
        
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
        fontStyleList_.setName(NAME_STYLE_LIST);

        // Add to gridbag
        gbc.weightx    = 0.75; gbc.weighty   = 1;
        gbc.gridx      = 2;    gbc.gridy     = 1;
        gbc.gridheight = 2;    gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 10, 0, 0);
        add(wrapWithHeading("Style", new JScrollPane(fontStyleList_)), gbc);

        // Configure anti-alias checkbox
        antiAliasCheckBox_ = new JSmartCheckBox(new AntiAliasAction());
        antiAliasCheckBox_.setName(NAME_ANTIALIAS_CHECKBOX);
        setAntiAliased(antiAlias);

        // Add to gridbag
        gbc.weightx    = 0.75; gbc.weighty   = 0;
        gbc.gridx      = 2;    gbc.gridy     = 3;
        gbc.gridheight = 1;    gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(antiAliasCheckBox_, gbc);

        // Configure font size field
        fontSizeField_ = new JSmartTextField();
        fontSizeField_.setColumns(4);
        fontSizeField_.setName(NAME_SIZE_FIELD);
        
        // Add to gridbag
        gbc.weightx    = 0.5;  gbc.weighty   = 0;
        gbc.gridx      = 3;    gbc.gridy     = 1;
        gbc.gridheight = 1;    gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(wrapWithHeading("Size", fontSizeField_), gbc);
        
        // Configure font size list
        fontSizeList_ =
            new JSmartList(validateAndConvertPredefinedSizes(predefinedSizes));
            
        fontSizeList_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontSizeList_.setVisibleRowCount(4);
        fontSizeList_.setName(NAME_SIZE_LIST);
        
        // Add to gridbag
        gbc.weightx    = 0.5; gbc.weighty   = 1;
        gbc.gridx      = 3;   gbc.gridy     = 2;
        gbc.gridheight = 2;   gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 0, 0);
        gbc.fill = GridBagConstraints.BOTH;
        add(new JScrollPane(fontSizeList_), gbc);

        // Configure Phrase Canvas (displays current font selection)
        phraseCanvas_ = 
            new PhraseCanvas(
                initialFont.getFamily(), 
                initialFont, 
                Color.black, 
                antiAlias);
        
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
        fontSizeField_.setText(String.valueOf(initialFont.getSize()));
    }


    /**
     * Wires the user interface components together with the appropriate event 
     * listeners.
     */
    protected void wireView()
    {
        fontFamilyList_.addListSelectionListener(new FontSelectionListener());
        fontStyleList_.addListSelectionListener(new FontSelectionListener());
        addFontSelectionListener(new PhraseFontSelectionListener());        
        
        // Use FontSizeSynchronizer to ensure consistency between text field &
        // list for font size
        FontSizeSynchronizer fontSizeSynchronizer =
            new FontSizeSynchronizer(fontSizeList_, fontSizeField_);
            
        fontSizeList_.addListSelectionListener(fontSizeSynchronizer);
        fontSizeField_.getDocument().addDocumentListener(fontSizeSynchronizer);
    }


    /**
     * Wraps a component in a panel with a heading.
     * 
     * @param heading Heading text.
     * @param component Component to wrap with a heading.
     * @return Wrapped component as a JPanel.
     */
    protected JPanel wrapWithHeading(String heading, JComponent component)
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(BorderLayout.NORTH, new JSmartLabel(heading));
        panel.add(BorderLayout.CENTER, component);
        return panel;    
    }

    
    /**
     * Validates predefined font sizes.
     * 
     * @param predefinedSizes Array of font sizes.
     * @return Integer[]
     * @throws IllegalArgumentException if predefinedSizes does not contain one
     *         or more integer values or if it contains any integers with a 
     *         value less than 1.
     */
    protected Integer[] validateAndConvertPredefinedSizes(int[] predefinedSizes)
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
     * @param listener Font selection listener to add.
     */
    public void addFontSelectionListener(IFontChooserListener listener)
    {
        listeners_.add(listener);
    }
    
    
    /**
     * Removes an listener from this JFontChooser.
     * 
     * @param listener Font selection listener to remove.
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
     * @return boolean
     */
    public boolean isAntiAliased()
    {
        return antiAliasCheckBox_.isSelected();
    }
    
    
    /**
     * Sets the antialias flag.
     * 
     * @param b True for antialias on, false otherwise.
     */
    public void setAntiAliased(boolean b)
    {
        antiAliasCheckBox_.setSelected(b);
        
        if (phraseCanvas_ != null) 
            phraseCanvas_.setAntiAlias(b);
    }
    
    
    /**
     * Returns the currently selected font family.
     * 
     * @return Currently selected font family.
     * @throws FontChooserException thrown if no font family is currently 
     *         selected.
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
     *         of the font styles specified in {@link java.awt.Font}.
     * @throws FontChooserException thrown if no font style is currently
     *         selected.
     */
    public int getSelectedFontStyle() throws FontChooserException
    {
        return fontStyleList_.getSelectedStyle();
    }


    /**
     * Returns the currently selected font size.
     * 
     * @return int
     * @throws FontChooserException thrown if no font size is currently 
     *         specified.
     */
    public int getSelectedFontSize() throws FontChooserException 
    {
        String fontSize = fontSizeField_.getText();
        
        if (StringUtils.isBlank(fontSize))
            throw new FontChooserException("No font size specified");
        
        if (fontSize.length() > MAX_DIGITS_IN_FONT_SIZE)
            throw new FontChooserException("Too many characters in font size");
            
        try
        {
            return Integer.parseInt(fontSize);
        }
        catch (NumberFormatException e)
        {
            throw new FontChooserException(
                "The value '" + fontSize + "' is not a valid font size.");
        }
    }


    /**
     * Returns the currently selected font.
     * 
     * @return Font
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
     * @param font Font whose values should be used to set widgets.
     * @throws IllegalArgumentException thrown if the family or style of the
     *         font supplied are not available or invalid.
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
     * @param family Family to which selection should change.
     * @throws IllegalArgumentException thrown if the supplied font family is
     *         not among the list of available font families.
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
     * @param style Style to which selection should change.
     * @throws IllegalArgumentException thrown if the supplied font style is
     *         not one of Font.PLAIN, Font.BOLD, Font.ITALIC, or
     *         Font.BOLD+Font.ITALIC.
     */
    public void setSelectedFontStyle(int style) throws IllegalArgumentException
    {
        fontStyleList_.setSelectedStyle(style);
    }


    /**
     * Sets the currently selected font size.
     * 
     * @param size Size to which selection should change.
     */
    public void setSelectedFontSize(int size)
    {
        fontSizeField_.setText(String.valueOf(size));
    }

    
    /**
     * Sets the flag to render a font family cell using the font name occupying
     * that cell.
     * 
     * @param b True to use the font, false to use the default font.
     */
    public void setRenderedUsingFont(boolean b)
    {
        fontFamilyCellRenderer_.setRenderedUsingFont(b);
    }

    
    /**
     * Returns true if a font family cell is rendered using the font name 
     * occupying that cell. False otherwise.
     *   
     * @return boolean
     */
    public boolean isRenderedUsingFont()
    {
        return fontFamilyCellRenderer_.isRenderedUsingFont();
    }
    

    /**
     * Sets the flag to emphasize monospaced fonts in the font family list box
     * by making them bold. 
     * 
     * @param b True to emphasize monospaced fonts, false otherwise.
     */
    public void setMonospaceEmphasized(boolean b)
    {
        fontFamilyCellRenderer_.setMonospacedEmphasized(b);
    }
    

    /**
     * Returns true if monospaced fonts are emphasized in the font family list
     * box by being made bold. False otherwise.
     * 
     * @return booelean
     */
    public boolean isMonospaceEmphasized()
    {
        return fontFamilyCellRenderer_.isMonospacedEmphasized();
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
        private JList _list;
        private JTextField _textField;
        private boolean _updating;
        
        /**
         * Creates a FontSizeSynchronizer.
         * 
         * @param list List containing predefined font sizes. 
         * @param textField Text field in which font size is specified.
         */
        public FontSizeSynchronizer(JList list, JTextField textField)
        {
            _list = list;
            _textField = textField;
        }

        
        /** 
         * Called when a value is changed.
         * 
         * @param e List event that caused change in value.
         * @see javax.swing.event.ListSelectionListener 
         */
        public void valueChanged(ListSelectionEvent e)
        {
            if (_updating)
                return;

            try
            {
                _updating = true;
                
                Object selectedValue = 
                    ((JList) e.getSource()).getSelectedValue();
                    
                if (selectedValue != null)
                    _textField.setText(selectedValue.toString());
    
                fireFontSelectionChanged();
            }
            finally
            {
                _updating = false;
            }
        }

        
        /** 
         * @see javax.swing.event.DocumentListener#changedUpdate(
         *      javax.swing.event.DocumentEvent) 
         */
        public void changedUpdate(DocumentEvent e)
        {
            handle(e);
        }
       
        
        /**
         * @see javax.swing.event.DocumentListener#insertUpdate(
         *      javax.swing.event.DocumentEvent) 
         */
        public void insertUpdate(DocumentEvent e)
        {
            handle(e);
        }
        
        
        /** 
         * @see javax.swing.event.DocumentListener#removeUpdate(
         *      javax.swing.event.DocumentEvent) 
         */
        public void removeUpdate(DocumentEvent e)
        {
            handle(e);
        }
        
        
        /** 
         * Handles all DocumentEvents. 
         * 
         * @param e Document event.
         */
        protected void handle(DocumentEvent e)
        {
            Validate.notNull(e, "Document event is null");
            
            if (_updating)
                return;

            
            try
            {
                _updating = true;
                boolean currentSizeWasInList = false;
                Object listMember;
                
                Integer currentFontSizeInteger = 
                    Integer.valueOf(_textField.getText());
                
                for (int i = 0; i < _list.getModel().getSize(); i++)
                {
                    listMember = _list.getModel().getElementAt(i);
                    
                    if (listMember.equals(currentFontSizeInteger))
                    {
                        _list.setSelectedValue(currentFontSizeInteger, true);
                        currentSizeWasInList = true;
                        break;
                    }
                }
                
                if (!currentSizeWasInList)
                    _list.clearSelection();
                
                fireFontSelectionChanged();
            }
            catch (NumberFormatException nfe)
            {
                _list.clearSelection();
            }
            finally
            {
                _updating = false;
            }
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
    protected class PhraseFontSelectionListener 
        implements IFontChooserListener
    {
        /**
         * @see toolbox.util.ui.font.IFontChooserListener#fontChanged()
         */
        public void fontChanged()
        {
            try
            {
                phraseCanvas_.setPhrase((String) 
                    fontFamilyList_.getSelectedValue());
                    
                phraseCanvas_.setFont(JFontChooser.this.getSelectedFont());
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

/*
Based on work originally by:

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
