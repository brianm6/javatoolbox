package com.javio.webwindow;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import D.B;
import D.C;
import D.D;
import D.J;
import D.S;

import toolbox.util.StringUtil;
import toolbox.util.SwingUtil;

// Referenced classes of package com.javio.webwindow:
//            K, ZC, G, RZ, 
//            EI, L, QC, HTMLPane, 
//            FZ, TZ, Document, LayoutInfo

public abstract class UC
{
    static
    {
        System.out.println(StringUtil.addBars(
            "Loaded debug com.javio.webwindow.UC"));
    }
    
    protected Rectangle G;
    protected Insets H;
    protected int K;
    protected boolean L;
    protected int M;
    protected int N;
    protected int O;
    protected int P;
    protected boolean Q;
    protected Document R;
    protected UC T;
    protected UC U[];
    protected EI V;
    protected HTMLPane W;
    protected int X;
    protected int Y;
    protected int i;
    protected int z;
    protected int c;
    protected float b;
    protected float d;
    protected float containsKey;
    protected float createInstance;
    protected K f;
    protected int j;
    protected int drawDottedRectangle;
    protected int forceLayout;
    protected int get;
    protected int getBorderSideProperties;
    protected int getContainingBox;

    public UC(EI ei, UC uc, Document document, HTMLPane htmlpane)
    {
        X = 5;
        j = 0;
        I(ei, uc, document, htmlpane);
    }

    protected void I()
    {
    }

    protected void I(EI ei, UC uc, Document document, HTMLPane htmlpane)
    {
        W = htmlpane;
        V = ei;
        if (uc != null)
            V.setParent(uc.getElement());
        T = uc;
        U = new UC[0];
        R = document;
        L = true;
        f = new K();
        H = new Insets(0, 0, 0, 0);
        G = new Rectangle();
        K = 0;
        M = -1;
        N = -1;
        O = -1;
        P = -1;
        Q = false;
        getContainingBox = -1;
    }

    protected void Z(EI ei, UC uc, Document document, HTMLPane htmlpane)
    {
        I(ei, uc, document, htmlpane);
    }

    public void init()
    {
        Q = true;
    }

    protected final void j()
    {
        Y = 0x80000000;
        i = 0x80000000;
        z = -1;
        c = -1;
        b = -1F;
        d = -1F;
        containsKey = -1F;
        createInstance = -1F;
        D d1 = s();
        D d2 = a();
        D d3 = e();
        J(d1, d2, d3);
        C(d1, d2, d3);
        D(d1, d2, d3);
        addElement(d1, d2, d3);
        Z(d1, d2, d3);
        equals(d1, d2, d3);
        E(d1, d2, d3);
        S(d1, d2, d3);
    }

    protected void S(D d1, D d2, D d3)
    {
    }

    protected final void A(int i1)
    {
        f.I.m_thickness = i1;
        f.Z.m_thickness = i1;
        f.C.m_thickness = i1;
        f.B.m_thickness = i1;
    }

    public D s()
    {
        return V.getStyle();
    }

    protected final D a()
    {
        S s1 = ((ZC)R).getStyleSheet();
        D d1 = null;
        if (V.isAttributeDefined("class"))
        {
            String s2 = (String)V.getAttribute("class");
            if (s2 != null)
            {
                d1 = s1.getStyle("." + s2);
                if (d1 == null)
                    d1 = s1.getStyle(V.getName() + "." + s2);
            }
        } else
        if (V.isAttributeDefined("id"))
        {
            String s3 = (String)V.getAttribute("id");
            if (s3 != null)
                d1 = s1.getStyle("#" + s3);
        }
        return d1;
    }

    protected final D e()
    {
        S s1 = ((ZC)R).getStyleSheet();
        J j1 = C.getContextStyle(V.getName(), this, s1);
        if (j1 != null)
        {
            com.javio.webwindow.B b1 = C.parseStyleAttributes(j1.m_props);
            return B.createInstance(b1);
        } else
        {
            return null;
        }
    }

    protected final void g()
    {
        D d1 = s();
        D d2 = a();
        D d3 = e();
        I(d1, d2, d3);
        B(d1, d2, d3);
        setComponentVisibility(GI());
        A(d1, d2, d3);
    }

    protected final void A(D d1, D d2, D d3)
    {
        int i1 = EI() ? 1 : 0;
        String s1 = I("cursor", d1, d2, d3, null);
        if (s1 != null)
            getContainingBox = QC.getCursorTypeForName(s1, i1);
        else
            getContainingBox = i1;
    }

    protected void B(D d1, D d2, D d3)
    {
    }

    protected void I(D d1, D d2, D d3)
    {
    }

    protected final boolean h()
    {
        D d1 = s();
        D d2 = a();
        D d3 = e();
        String s1 = I("background-repeat", d1, d2, d3, null);
        return s1 == null || !s1.equals("no-repeat") && !s1.equals("repeat-x");
    }

    protected final boolean k()
    {
        D d1 = s();
        D d2 = a();
        D d3 = e();
        String s1 = I("background-repeat", d1, d2, d3, null);
        return s1 == null || !s1.equals("no-repeat") && !s1.equals("repeat-y");
    }

    protected final void I(Color color)
    {
        f.I.m_color = color;
        f.Z.m_color = color;
        f.C.m_color = color;
        f.B.m_color = color;
    }

    protected void C(D d1, D d2, D d3)
    {
        K = 0;
    }

    protected final void addElement(D d1, D d2, D d3)
    {
        get = 0;
        getBorderSideProperties = 0;
        drawDottedRectangle = 0;
        forceLayout = 0;
        String s1 = I("margin", d1, d2, d3, null);
        if (s1 != null)
        {
            Hashtable hashtable = C.parseBlockSideProperty("margin", s1);
            get = Math.max(0, getBorderSideProperties("top", hashtable));
            forceLayout = Math.max(0, getBorderSideProperties("right", hashtable));
            getBorderSideProperties = Math.max(0, getBorderSideProperties("bottom", hashtable));
            drawDottedRectangle = Math.max(0, getBorderSideProperties("left", hashtable));
            return;
        } else
        {
            String s2 = I("margin-top", d1, d2, d3, null);
            get = QC.getPixels(W.getDefaultFont(), V, s2, 0);
            s2 = I("margin-bottom", d1, d2, d3, null);
            getBorderSideProperties = QC.getPixels(W.getDefaultFont(), V, s2, 0);
            s2 = I("margin-left", d1, d2, d3, null);
            drawDottedRectangle = QC.getPixels(W.getDefaultFont(), V, s2, 0);
            s2 = I("margin-right", d1, d2, d3, null);
            forceLayout = QC.getPixels(W.getDefaultFont(), V, s2, 0);
            return;
        }
    }

    protected void D(D d1, D d2, D d3)
    {
        f.I.Z();
        f.Z.Z();
        f.C.Z();
        f.B.Z();
        Hashtable hashtable = new Hashtable();
        Hashtable hashtable1 = append(d1);
        Hashtable hashtable2 = append(d2);
        Hashtable hashtable3 = append(d3);
        black(hashtable, hashtable3);
        black(hashtable, hashtable2);
        black(hashtable, hashtable1);
        String s1 = I("border-style", d1, d2, d3, null);
        if (s1 == null)
            s1 = (String)hashtable.get("border-style");
        if (s1 != null)
            if (s1.equals("solid"))
            {
                f.I.m_style = 1;
                f.Z.m_style = 1;
                f.C.m_style = 1;
                f.B.m_style = 1;
                A(4);
            } else
            if (s1.equals("dashed"))
            {
                f.I.m_style = 0;
                f.Z.m_style = 0;
                f.C.m_style = 0;
                f.B.m_style = 0;
                A(4);
            }
        int i1 = U();
        if (i1 >= 0)
            A(i1);
        String s2 = (String)hashtable.get("border-width");
        if (s2 != null)
            try
            {
                int j1 = QC.getPixels(s2, -1);
                if (j1 >= 0)
                    A(j1);
            }
            catch (NumberFormatException numberformatexception) { }
        int k1 = createInstance("border-top-width", d1, d2, d3, -1);
        if (k1 > -1)
            f.I.m_thickness = k1;
        k1 = createInstance("border-left-width", d1, d2, d3, -1);
        if (k1 > -1)
            f.C.m_thickness = k1;
        k1 = createInstance("border-bottom-width", d1, d2, d3, -1);
        if (k1 > -1)
            f.Z.m_thickness = k1;
        k1 = createInstance("border-right-width", d1, d2, d3, -1);
        if (k1 > -1)
            f.B.m_thickness = k1;
        int l1 = createInstance("border-width", d1, d2, d3, -1);
        if (l1 > -1)
        {
            f.I.m_thickness = l1;
            f.Z.m_thickness = l1;
            f.C.m_thickness = l1;
            f.B.m_thickness = l1;
        }
        String s3 = (String)hashtable.get("border-color");
        if (s3 != null)
        {
            Color color = QC.stringToColor(s3);
            if (color != null)
                I(color);
        }
        F(d1, d2, d3);
        bottom(0, d1, d2, d3);
        bottom(1, d1, d2, d3);
        bottom(2, d1, d2, d3);
        bottom(3, d1, d2, d3);
        I(f.I);
        I(f.Z);
        I(f.C);
        I(f.B);
    }

    private Hashtable append(D d1)
    {
        if (d1 != null)
        {
            String s1 = (String)d1.getAttribute("border");
            if (s1 != null)
                return C.parseBorderProperty(s1);
        }
        return null;
    }

    private void black(Hashtable hashtable, Hashtable hashtable1)
    {
        if (hashtable1 == null)
            return;
        String s1;
        String s2;
        for (Enumeration enumeration = hashtable1.keys(); enumeration.hasMoreElements(); hashtable.put(s1, s2))
        {
            s1 = (String)enumeration.nextElement();
            s2 = (String)hashtable1.get(s1);
        }

    }

    protected int U()
    {
        return -1;
    }

    protected void I(L l1)
    {
        if (l1.m_color == null)
            l1.m_color = Color.black;
        if (l1.m_thickness == -1)
            l1.m_thickness = T();
    }

    protected void F(D d1, D d2, D d3)
    {
        Color color = I("border-color", d1, d2, d3);
        if (color != null)
            I(color);
    }

    protected final void bottom(int i1, D d1, D d2, D d3)
    {
        L l1 = null;
        String s1 = null;
        switch (i1)
        {
        case 0: // '\0'
            s1 = "border-top";
            l1 = f.I;
            break;

        case 1: // '\001'
            s1 = "border-bottom";
            l1 = f.Z;
            break;

        case 2: // '\002'
            s1 = "border-left";
            l1 = f.C;
            break;

        case 3: // '\003'
            s1 = "border-right";
            l1 = f.B;
            break;

        default:
            return;
        }
        if (d1 != null)
        {
            String s2 = (String)d1.getAttribute(s1);
            if (s2 != null)
                contains(l1, C.getBorderSideProperties(s2));
        }
        if (d2 != null)
        {
            String s3 = (String)d2.getAttribute(s1);
            if (s3 != null)
                contains(l1, C.getBorderSideProperties(s3));
        }
        if (d3 != null)
        {
            String s4 = (String)d3.getAttribute(s1);
            if (s4 != null)
                contains(l1, C.getBorderSideProperties(s4));
        }
    }

    private void contains(L l1, L l2)
    {
        if (l1.m_color == null && l2.m_color != null)
            l1.m_color = l2.m_color;
        if (l1.m_thickness == -1 && l2.m_thickness > -1)
            l1.m_thickness = l2.m_thickness;
        if (l1.m_style == 2 && l2.m_style != 2)
            l1.m_style = l2.m_style;
    }

    protected final String containsKey(String s1, String s2)
    {
        D d1 = s();
        if (d1 != null)
        {
            String s3 = (String)d1.getAttribute(s1);
            if (s3 != null)
                return s3;
        }
        D d2 = a();
        if (d2 != null)
        {
            String s4 = (String)d2.getAttribute(s1);
            if (s4 != null)
                return s4;
        }
        D d3 = e();
        if (d3 != null)
        {
            String s5 = (String)d3.getAttribute(s1);
            if (s5 != null)
                return s5;
        }
        return s2;
    }

    protected final String I(String s1, String s2)
    {
        return I(s1, s(), a(), e(), s2);
    }

    protected final String I(String s1, D d1, D d2, D d3, String s2)
    {
        if (d1 != null)
        {
            String s3 = (String)d1.getAttribute(s1);
            if (s3 != null)
                return s3;
        }
        if (d2 != null)
        {
            String s4 = (String)d2.getAttribute(s1);
            if (s4 != null)
                return s4;
        }
        if (d3 != null)
        {
            String s5 = (String)d3.getAttribute(s1);
            if (s5 != null)
                return s5;
        }
        return s2;
    }

    protected final int createInstance(String s1, D d1, D d2, D d3, int i1)
    {
        if (d1 != null)
        {
            String s2 = (String)d1.getAttribute(s1);
            int j1 = QC.getPixels(s2, -1);
            if (j1 > -1)
                return j1;
        }
        if (d2 != null)
        {
            String s3 = (String)d2.getAttribute(s1);
            int k1 = QC.getPixels(s3, -1);
            if (k1 > -1)
                return k1;
        }
        if (d3 != null)
        {
            String s4 = (String)d3.getAttribute(s1);
            int l1 = QC.getPixels(s4, -1);
            if (l1 > -1)
                return l1;
        }
        return i1;
    }

    protected final Color I(String s1, D d1, D d2, D d3)
    {
        Color color = drawDottedRectangle(d1, s1);
        if (color == null)
        {
            color = drawDottedRectangle(d2, s1);
            if (color == null)
                color = drawDottedRectangle(d3, s1);
        }
        return color;
    }

    private Color drawDottedRectangle(D d1, String s1)
    {
        if (d1 != null)
        {
            String s2 = (String)d1.getAttribute(s1);
            if (s2 != null)
            {
                Color color = QC.stringToColor(s2);
                if (color != null)
                    return color;
            }
        }
        return null;
    }

    protected final void equals(D d1, D d2, D d3)
    {
        X = 5;
        String s1 = I("position", d1, d2, d3, null);
        if (s1 != null)
            if (s1.equals("absolute"))
                X = 0;
            else
            if (s1.equals("relative"))
                X = 1;
        forceLayout(d1, d2, d3);
    }

    protected void J(D d1, D d2, D d3)
    {
        j = 0;
    }

    protected final void E(D d1, D d2, D d3)
    {
        z = -1;
        c = -1;
        b = -1F;
        d = -1F;
        equalsIgnoreCase(d1);
        equalsIgnoreCase(d2);
        equalsIgnoreCase(d3);
    }

    protected final void equalsIgnoreCase(D d1)
    {
        if (d1 == null)
            return;
        if (z == -1 && b == -1F)
        {
            String s1 = (String)d1.getAttribute("width");
            z = QC.getPixels(s1, -1);
            if (z == -1)
                b = QC.I(s1, -1F);
        }
        if (c == -1 && d == -1F)
        {
            String s2 = (String)d1.getAttribute("height");
            c = QC.getPixels(s2, -1);
            if (c == -1)
                d = QC.I(s2, -1F);
        }
    }

    protected final void forceLayout(D d1, D d2, D d3)
    {
        Y = 0x80000000;
        i = 0x80000000;
        containsKey = -1F;
        createInstance = -1F;
        get(d1);
        get(d2);
        get(d3);
    }

    private void get(D d1)
    {
        if (d1 != null)
        {
            if (i == 0x80000000)
            {
                String s1 = (String)d1.getAttribute("top");
                i = QC.getPixels(s1, 0x80000000);
                if (i == 0x80000000)
                    containsKey = QC.I(s1, -1F);
            }
            if (Y == 0x80000000)
            {
                String s2 = (String)d1.getAttribute("left");
                Y = QC.getPixels(s2, 0x80000000);
                if (Y == 0x80000000)
                    createInstance = QC.I(s2, -1F);
            }
        }
    }

    protected void Z(D d1, D d2, D d3)
    {
        I(-1, -1, -1, -1);
        getAttributes(d1);
        getAttributes(d2);
        getAttributes(d3);
        getAttribute(d1);
        getAttribute(d2);
        getAttribute(d3);
        if (H.top == -1)
            H.top = K();
        if (H.left == -1)
            H.left = G();
        if (H.right == -1)
            H.right = H();
        if (H.bottom == -1)
            H.bottom = L();
    }

    private void getAttribute(D d1)
    {
        if (d1 != null)
        {
            Object obj = null;
            if (H.top == -1)
            {
                String s1 = (String)d1.getAttribute("padding-top");
                H.top = QC.getPixels(W.getDefaultFont(), V, s1, -1);
            }
            if (H.left == -1)
            {
                String s2 = (String)d1.getAttribute("padding-left");
                H.left = QC.getPixels(W.getDefaultFont(), V, s2, -1);
            }
            if (H.bottom == -1)
            {
                String s3 = (String)d1.getAttribute("padding-bottom");
                H.bottom = QC.getPixels(W.getDefaultFont(), V, s3, -1);
            }
            if (H.right == -1)
            {
                String s4 = (String)d1.getAttribute("padding-right");
                H.right = QC.getPixels(W.getDefaultFont(), V, s4, -1);
            }
        }
    }

    private void getAttributes(D d1)
    {
        if (d1 == null)
            return;
        String s1 = (String)d1.getAttribute("padding");
        if (s1 != null)
        {
            Insets insets = new Insets(-1, -1, -1, -1);
            Hashtable hashtable = C.parseBlockSideProperty("padding", s1);
            insets.top = Math.max(0, getBorderSideProperties("top", hashtable));
            insets.left = Math.max(0, getBorderSideProperties("left", hashtable));
            insets.bottom = Math.max(0, getBorderSideProperties("bottom", hashtable));
            insets.right = Math.max(0, getBorderSideProperties("right", hashtable));
            I(insets.top, insets.left, insets.bottom, insets.right);
        }
    }

    private int getBorderSideProperties(String s1, Hashtable hashtable)
    {
        if (hashtable.containsKey(s1))
        {
            String s2 = (String)hashtable.get(s1);
            return QC.getPixels(W.getDefaultFont(), V, s2, W.m_props.I.getSize());
        } else
        {
            return -1;
        }
    }

    protected final float l()
    {
        return b;
    }

//    protected final int m()
//    {
//        String s1;
//        s1 = V.resolveStyleValue("z-index");
//        if (s1 == null)
//            break MISSING_BLOCK_LABEL_22;
//        return Integer.parseInt(s1);
//        NumberFormatException numberformatexception;
//        numberformatexception;
//        return 0;
//    }

    protected final int m()
    {
        String string = V.resolveStyleValue("z-index");

        do
           {
            if (string != null)
               {
                int i;

                try
                {
                    i = Integer.parseInt(string);
                }
                catch (NumberFormatException numberformatexception)
                {
                    break;
                }
                return i;
            }
        }

        while (false);
        return 0;
    }

    
    
    public final HTMLPane getHTMLPane()
    {
        return W;
    }

    public final String getName()
    {
        return V.getName();
    }

    public void setComponentVisibility(boolean flag)
    {
    }

    protected final TZ n()
    {
        return V.getAttributes();
    }

    protected final Object I(Object obj)
    {
        TZ tz = n();
        if (tz != null)
            return tz.getAttribute(obj);
        else
            return null;
    }

    protected final String o()
    {
        return V.resolveStyleValue("href");
    }

    protected final String p()
    {
        return V.resolveStyleValue("target");
    }

    public final boolean isType(int i1)
    {
        return getElementType() == i1;
    }

    public final EI getElement()
    {
        return V;
    }

    public final int getElementType()
    {
        return V.getType();
    }

    public final UC getParent()
    {
        return T;
    }

    public final G getContainingBox()
    {
        if (E(0))
        {
            for (UC uc = getParent(); uc != null; uc = uc.getParent())
                if (uc.u() && !uc.E(3) && (uc.E(1) || uc.E(0)))
                    return (G)uc;

        } else
        {
            for (UC uc1 = getParent(); uc1 != null; uc1 = uc1.getParent())
                if (uc1.u())
                    return (G)uc1;

        }
        return null;
    }

    protected final void q()
    {
        if (W != null)
            W.forceLayout();
    }

    protected String M()
    {
        return "";
    }

    protected void I(boolean flag)
    {
        L = flag;
    }

    protected final boolean r()
    {
        boolean flag = L || V.resolveStyleValue("nobr") == "true";
        return flag;
    }

    protected final boolean t()
    {
        switch (getElementType())
        {
        case 6: // '\006'
        case 14: // '\016'
        case 15: // '\017'
        case 16: // '\020'
        case 17: // '\021'
        case 18: // '\022'
        case 19: // '\023'
        case 21: // '\025'
        case 29: // '\035'
            return true;

        case 7: // '\007'
        case 8: // '\b'
        case 9: // '\t'
        case 10: // '\n'
        case 11: // '\013'
        case 12: // '\f'
        case 13: // '\r'
        case 20: // '\024'
        case 22: // '\026'
        case 23: // '\027'
        case 24: // '\030'
        case 25: // '\031'
        case 26: // '\032'
        case 27: // '\033'
        case 28: // '\034'
        default:
            return false;
        }
    }

    protected boolean z()
    {
        return false;
    }

    protected final boolean C(int i1, int j1)
    {
        return getBounds().contains(i1, j1);
    }

    public boolean addChild(UC uc)
    {
        return true;
    }

    public int getCursorType()
    {
        String s1 = containsKey("cursor", null);
        if (s1 == null)
            s1 = V.resolveStyleValue("cursor");
        if (s1 != null)
            getContainingBox = QC.getCursorTypeForName(s1, -1);
        else
            getContainingBox = -1;
        return getContainingBox;
    }

    protected boolean A()
    {
        return false;
    }

    protected boolean F()
    {
        return false;
    }

    protected boolean D()
    {
        return false;
    }

    protected int getMaximumWordLength()
    {
        return 0;
    }

    protected boolean c()
    {
        return false;
    }

    protected boolean O()
    {
        return true;
    }

    protected final boolean u()
    {
        return F() && D();
    }

    protected final boolean E(int i1)
    {
        return X == i1;
    }

    protected final int v()
    {
        return get;
    }

    protected final int w()
    {
        return getBorderSideProperties;
    }

    protected final int x()
    {
        return forceLayout;
    }

    public final int getLeftMargin()
    {
        return drawDottedRectangle;
    }

    protected final int y()
    {
        return Math.max(0, f.I.m_thickness);
    }

    protected final int II()
    {
        return Math.max(0, f.Z.m_thickness);
    }

    protected final int ZI()
    {
        return Math.max(0, f.C.m_thickness);
    }

    protected final int CI()
    {
        return Math.max(0, f.B.m_thickness);
    }

    protected boolean R()
    {
        return false;
    }

    protected boolean J()
    {
        return false;
    }

    protected boolean X()
    {
        return false;
    }

    protected final boolean BI()
    {
        return V.resolveStyleValue("nobr") == "true";
    }

    protected boolean d()
    {
        return false;
    }

    protected boolean f()
    {
        return false;
    }

    protected boolean J(int i1)
    {
        return false;
    }

    protected UC[] S(int i1)
    {
        return null;
    }

    protected final boolean DI()
    {
        switch (j)
        {
        case 1: // '\001'
        case 2: // '\002'
            return true;
        }
        return false;
    }

    protected boolean S()
    {
        return false;
    }

    protected final void I(int i1, int j1, int k1, int l1)
    {
        H.top = i1;
        H.left = j1;
        H.bottom = k1;
        H.right = l1;
    }

    protected final int FI()
    {
        return H.left;
    }

    protected final int JI()
    {
        return H.right;
    }

    protected final int SI()
    {
        return H.top;
    }

    protected final int AI()
    {
        return H.bottom;
    }

    protected final boolean EI()
    {
        return o() != null;
    }

    protected boolean P()
    {
        return true;
    }

    protected int G()
    {
        return 0;
    }

    protected int H()
    {
        return 0;
    }

    protected int K()
    {
        return 0;
    }

    protected int L()
    {
        return 0;
    }

    protected int T()
    {
        return 0;
    }

    protected void Q()
    {
        M = -1;
        O = -1;
    }

    protected void C()
    {
    }

    protected synchronized void Z()
    {
    }

    protected boolean W()
    {
        return true;
    }

    protected void N()
    {
        for (int i1 = 0; i1 < U.length; i1++)
            U[i1].N();

        V = null;
        T = null;
        U = new UC[0];
        R = null;
        W = null;
    }

    protected void I(Graphics g1, Rectangle rectangle)
    {
        if (V.getFocus())
        {
            Color color = Color.black;
            if (R != null)
                color = QC.stringToColor(R.getTextColor());
            g1.setColor(color);
            QC.drawDottedRectangle(g1, G.x, G.y, G.width, G.height);
        }
    }

    protected Color E()
    {
        return null;
    }

    protected final void Z(Graphics g1)
    {
        Color color = E();
        if (color != null)
        {
            Color color1 = g1.getColor();
            g1.setColor(color);
            g1.fillRect(G.x, G.y, G.width, G.height);
            g1.setColor(color1);
        }
    }

    protected void I(Graphics g1)
    {
        Rectangle rectangle = new Rectangle(G);
        if (rectangle.width < 0)
            rectangle.width = 0;
        if (rectangle.height < 0)
            rectangle.height = 0;
        if (f.I.I() && f.I.m_color != null)
            QC.paintBorderSide(g1, rectangle, f.I.m_color, y(), 0);
        if (f.Z.I() && f.Z.m_color != null)
            QC.paintBorderSide(g1, rectangle, f.Z.m_color, II(), 1);
        if (f.C.I() && f.C.m_color != null)
            QC.paintBorderSide(g1, rectangle, f.C.m_color, ZI(), 2);
        if (f.B.I() && f.B.m_color != null)
            QC.paintBorderSide(g1, rectangle, f.B.m_color, CI(), 3);
    }

    public void paint(Graphics g1, Rectangle rectangle, int i1)
    {
        // =====================================================================
        // OVERRIDE: Replaced empty method body with antialiasing code that all
        //           superclasses can just call super to inherit smooting of
        //           fonts.
        // System.out.println("UC.paint() called from " + getClass().getName());
        SwingUtil.makeAntiAliased(g1, true);
        // =====================================================================
    }

    protected final boolean GI()
    {
        String s1 = V.getStyleValue("visibility");
        if (s1 != null)
        {
            if (s1.equalsIgnoreCase("hidden"))
                return false;
            if (s1.equalsIgnoreCase("visible"))
                return true;
        }
        if (HI())
            return false;
        UC uc = getParent();
        if (uc != null)
            return uc.GI();
        else
            return true;
    }

    protected final boolean HI()
    {
        String s1 = V.getStyleValue("display");
        return s1 != null && s1.equalsIgnoreCase("none");
    }

    protected int b()
    {
        return 0;
    }

    protected UC I(EI ei)
    {
        if (V == ei)
            return this;
        else
            return null;
    }

    protected void I(int i1, Vector vector)
    {
        if (isType(i1))
            vector.addElement(this);
    }

    protected UC Z(EI ei)
    {
        if (V == ei)
            return this;
        else
            return null;
    }

    protected EI elementFromPoint(int i1, int j1)
    {
        if (G.contains(i1, j1))
            return V;
        else
            return null;
    }

    protected UC Z(int i1)
    {
        if (V.getLength() > 0)
        {
            int j1 = V.getP0();
            int k1 = j1 + V.getLength();
            if (i1 >= j1 && i1 <= k1)
                return this;
        }
        return null;
    }

    protected RZ I(int i1, int j1, int k1)
    {
        if (m() == k1 && GI() && getBounds().contains(i1, j1))
            return new RZ(this);
        else
            return null;
    }

    protected final void B(int i1, int j1)
    {
        G.width += Math.max(0, i1 - G.width);
        G.height += Math.max(0, j1 - G.height);
    }

    protected final void Z(int i1, int j1, int k1, int l1)
    {
        G.setBounds(i1, j1, k1, l1);
    }

    public Rectangle getBounds()
    {
        return G;
    }

    protected int C(int i1)
    {
        return 0;
    }

    protected int I(int i1)
    {
        return 0;
    }

    protected final void D(int i1, int j1)
    {
        G.x = i1;
        G.y = j1;
    }

    protected Rectangle I(int i1, int j1, int k1, LayoutInfo layoutinfo)
    {
        W.I(this);
        G.setBounds(i1, j1, 0, 0);
        return G;
    }

    protected final void KI()
    {
        if (E(1))
        {
            int i1 = 0;
            int k1 = 0;
            G g1 = null;
            if (i != 0x80000000)
                k1 = i;
            else
            if ((double)containsKey >= 0.0D)
            {
                g1 = getContainingBox();
                if (g1 != null)
                {
                    Rectangle rectangle = g1.getBounds();
                    k1 = (int)((float)rectangle.height * containsKey);
                }
            }
            if (Y != 0x80000000)
                i1 = Y;
            else
            if ((double)createInstance >= 0.0D)
            {
                if (g1 == null)
                    g1 = getContainingBox();
                if (g1 != null)
                {
                    Rectangle rectangle1 = g1.getBounds();
                    i1 = (int)((float)rectangle1.width * createInstance);
                }
            }
            if (i1 != 0x80000000 || k1 != 0x80000000)
            {
                if (i1 == 0x80000000)
                    i1 = 0;
                if (k1 == 0x80000000)
                    k1 = 0;
                I(i1, k1, true);
            }
        } else
        if (E(0))
        {
            boolean flag = false;
            boolean flag1 = false;
            int l1 = 0;
            int i2 = 0;
            G g2 = getContainingBox();
            if (i != 0x80000000)
            {
                flag1 = true;
                i2 = i;
            } else
            if ((double)containsKey >= 0.0D)
            {
                flag1 = true;
                if (g2 != null)
                    i2 = (int)((float)g2.getBounds().height * containsKey);
                else
                    i2 = (int)((float)W.getPaneHeight() * containsKey);
            }
            if (Y != 0x80000000)
            {
                flag = true;
                l1 = Y;
            } else
            if ((double)createInstance >= 0.0D)
            {
                flag = true;
                if (g2 != null)
                    l1 = (int)((float)g2.getBounds().width * createInstance);
                else
                    l1 = (int)((float)W.getPaneWidth() * createInstance);
            }
            if (flag || flag1)
            {
                int j2 = 0;
                int k2 = 0;
                if (g2 != null)
                {
                    j2 = g2.getBounds().x;
                    k2 = g2.getBounds().y;
                }
                int l2 = 0;
                int i3 = 0;
                if (flag)
                {
                    int j3 = j2 + l1;
                    l2 = j3 - G.x;
                }
                if (flag1)
                {
                    int k3 = k2 + i2;
                    i3 = k3 - G.y;
                }
                if (l2 != 0 || i3 != 0)
                    I(l2, i3, true);
            }
        }
        for (int j1 = 0; j1 < U.length; j1++)
            U[j1].KI();

    }

    protected void Y()
    {
        M = -1;
        N = -1;
        O = -1;
        P = -1;
        UC uc = T;
        if (uc != null)
            uc.Y();
    }

    protected void I(int i1, int j1, boolean flag)
    {
        G.x += i1;
        G.y += j1;
    }

    protected int I(LayoutInfo layoutinfo)
    {
        return 0;
    }
}
