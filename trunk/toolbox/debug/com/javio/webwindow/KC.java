package com.javio.webwindow;

import D.D;
import D.S;
import F.Z;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.net.URL;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Iterator;

// Referenced classes of package com.javio.webwindow:
//            UC, RZ, ZC, HC, 
//            EI, QC, HTMLPane, Document, 
//            I, FZ, NZ, LayoutInfo

public class KC extends UC
{

    private boolean abs;
    private boolean charWidth;
    private Font charsWidth;
    private int contains;
    private int createElement;
    private boolean drawChars;
    private boolean drawLine;
    private boolean equals;
    private Rectangle fillRect;
    private Color first;
    protected int I;

    public KC(EI ei, UC uc, Document document, HTMLPane htmlpane)
    {
        super(ei, uc, document, htmlpane);
        abs = true;
        I = -1;
        contains = V.getOrigP0();
        createElement = V.getOrigLength();
    }

    public KC(EI ei, UC uc, KC kc, int j, int k, HTMLPane htmlpane)
    {
        super(ei, uc, kc.R, htmlpane);
        abs = true;
        I = -1;
        contains = j;
        createElement = k;
        Q = kc.Q;
        charWidth = kc.charWidth;
        drawLine = kc.drawLine;
        equals = kc.equals;
        drawChars = true;
        f = null;
        H = null;
    }

    protected final void I(EI ei, UC uc, Document document, HTMLPane htmlpane)
    {
        f = null;
        H = null;
        W = htmlpane;
        V = ei;
        if (uc != null)
            V.setParent(uc.getElement());
        T = uc;
        U = new UC[0];
        R = document;
        L = true;
        G = new Rectangle();
        fillRect = new Rectangle();
        K = 0;
        M = -1;
        N = -1;
        O = -1;
        P = -1;
        Q = false;
        first = null;
    }

    protected final void Z(EI ei, UC uc, Document document, HTMLPane htmlpane)
    {
        charsWidth = null;
        W = htmlpane;
        V = ei;
        T = uc;
        U = new UC[0];
        R = document;
        I(true);
        K = 0;
        M = -1;
        N = -1;
        O = -1;
        P = -1;
        contains = V.getOrigP0();
        createElement = V.getOrigLength();
        Q = false;
        I = -1;
        abs = true;
        charWidth = first("preformatted");
        drawChars = false;
        drawLine = fillRect();
        equals = equals();
        f = null;
        H = null;
        first = null;
    }

    public final void init()
    {
        if (!Q)
        {
            contains = V.getOrigP0();
            createElement = V.getOrigLength();
            charWidth = first("preformatted");
            drawLine = fillRect();
            equals = equals();
            if (equals || drawLine)
            {
                Font font = V();
                int j = QC.getFontSize(font.getSize(), "-2");
                charsWidth = new Font(font.getName(), font.getStyle(), j);
            }
            Q = true;
        }
    }

    protected final void Q()
    {
        contains = V.getOrigP0();
        createElement = V.getOrigLength();
        I = -1;
    }

    public final int getCursorType()
    {
        String s = V.resolveStyleValue("cursor");
        if (s != null)
        {
            int j = QC.getCursorTypeForName(s, -1);
            if (j != -1)
                return j;
        }
        return EI() ? 1 : 2;
    }

    protected final boolean c()
    {
        return true;
    }

    protected final boolean O()
    {
        return false;
    }

    protected boolean W()
    {
        if (charWidth)
            return true;
        char ac[] = B();
        boolean flag = true;
        int j = 0;
        int k = -1;
        do
        {
            if (j >= ac.length)
                break;
            if (!Character.isWhitespace(ac[j++]))
            {
                flag = false;
                break;
            }
            k = j;
        } while (true);
        if (flag)
            return false;
        if (k >= 0)
        {
            contains += k;
            createElement--;
            M = -1;
        }
        return createElement > 0;
    }

    protected final int b()
    {
        return i().getDescent();
    }

    public final EI elementFromPoint(int j, int k)
    {
        return null;
    }

    protected final UC Z(int j)
    {
        if (V.getLength() > 0 && j >= V.getOrigP0() && j <= V.getOrigP0() + V.getOrigLength())
            return this;
        else
            return null;
    }

    protected final RZ I(int j, int k, int l)
    {
        if (m() == l && getBounds().contains(j, k))
            return new RZ(this);
        else
            return null;
    }

    protected final int B(int j)
    {
        int k = contains;
        Rectangle rectangle = getBounds();
        FontMetrics fontmetrics = i();
        char ac[] = B();
        if (ac != null && ac.length > 0)
        {
            int l = 0;
            int i1;
            int j1;
            for (i1 = 0; i1 < ac.length && rectangle.x + l <= j; l += j1)
                j1 = fontmetrics.charWidth(ac[i1++]);

            k = contains;
            if (i1 > 0)
                k += i1 - 1;
        }
        return k;
    }

    protected final void I(boolean flag)
    {
        abs = flag;
    }

    protected final boolean d()
    {
        char ac[] = B();
        for (int j = 0; j < ac.length; j++)
            if (Character.isWhitespace(ac[j]))
                return true;

        return false;
    }

    protected final boolean J(int j)
    {
        if (!abs)
        {
            return false;
        } else
        {
            FontMetrics fontmetrics = i();
            char ac[] = B();
            String s = new String(ac);
            java.util.Locale locale = W.getLocale();
            BreakIterator breakiterator = BreakIterator.getLineInstance(locale);
            breakiterator.setText(s);
            int k = breakiterator.first();
            int l = breakiterator.next();
            int i1 = l - k;
            char ac1[] = new char[i1];
            s.getChars(k, l, ac1, 0);
            int j1 = fontmetrics.charsWidth(ac1, 0, i1);
            return j1 < j;
        }
    }

    protected final boolean f()
    {
        return drawChars;
    }

    protected final UC[] S(int j)
    {
        FontMetrics fontmetrics = i();
        char ac[] = B();
        char ac1[] = new char[ac.length];
        String s = new String(ac);
        BreakIterator breakiterator = BreakIterator.getLineInstance();
        breakiterator.setText(s);
        int k = breakiterator.first();
        int l = k;
        int i1 = 0;
        int j1 = breakiterator.next();
        do
        {
            if (j1 == -1)
                break;
            s.getChars(k, j1, ac1, 0);
            int k1 = j1 - k;
            i1 += fontmetrics.charsWidth(ac1, 0, k1);
            if (i1 < j)
            {
                l = j1;
            } else
            {
                if (l == 0)
                    l = j1;
                break;
            }
            k = j1;
            j1 = breakiterator.next();
        } while (true);
        EI ei = R.createElement(9, V.getAttributes(), contains, l, V.getStyle());
        boolean flag = EI();
        I l1 = R.getAnchorObject(V);
        if (flag && l1 != null)
        {
            l1.addElement(ei);
            ei.setFocus(V.getFocus());
        }
        KC kc = new KC(ei, getParent(), this, V.getP0(), l, W);
        if (l >= createElement)
        {
            UC auc[] = new UC[2];
            auc[0] = kc;
            auc[1] = null;
            return auc;
        }
        int i2 = Math.max(0, createElement - l);
        EI ei1 = R.createElement(9, V.getAttributes(), ei.getP0() + ei.getLength(), i2, V.getStyle());
        if (flag && l1 != null)
        {
            l1.addElement(ei1);
            ei1.setFocus(V.getFocus());
        }
        KC kc1 = new KC(ei1, getParent(), this, ei.getP0() + ei.getLength(), i2, W);
        UC auc1[] = new UC[2];
        auc1[0] = kc;
        auc1[1] = kc1;
        return auc1;
    }

    protected final Font V()
    {
        return QC.getFont(W.m_props.I, V);
    }

    private Color abs()
    {
        boolean flag = V.isHovered();
        if (flag)
        {
            S s = ((ZC)R).getStyleSheet();
            D d1 = s.getStyle("a:hover");
            if (d1 != null)
            {
                String s1 = (String)d1.getAttribute("background");
                if (s1 != null)
                    return QC.stringToColor(s1);
            }
        }
        for (EI ei = V; ei != null && !F.D.isBlockElement(ei.getType()); ei = ei.getParent())
        {
            D d2 = ei.getStyle();
            if (d2 == null)
                continue;
            String s2 = (String)d2.getAttribute("background");
            if (s2 == null)
                s2 = (String)d2.getAttribute("background-color");
            if (s2 != null)
                return QC.stringToColor(s2);
        }

        return null;
    }

    private Color charWidth()
    {
        if (EI())
        {
            if (first != null)
                return first;
            NZ nz = W.getLinkTracer();
            if (nz != null)
            {
                String s = o();
                URL url = QC.getURL(R.m_baseURL, s);
                if (url != null && nz.isVisitedLink(url.toString()))
                {
                    String s2 = R.getVLinkColor();
                    if (s2 != null)
                    {
                        first = QC.stringToColor(s2);
                        if (first != null)
                            return first;
                    }
                }
            }
        }
        boolean flag = V.isHovered();
        if (flag)
        {
            S s1 = ((ZC)R).getStyleSheet();
            D d1 = s1.getStyle("a:hover");
            if (d1 != null)
            {
                String s3 = (String)d1.getAttribute("color");
                if (s3 != null)
                    return QC.stringToColor(s3);
            }
        }
        for (EI ei = V; ei != null; ei = ei.getParent())
        {
            D d2 = ei.getStyle();
            if (d2 == null)
                continue;
            String s4 = (String)d2.getAttribute("color");
            if (s4 != null)
                return QC.stringToColor(s4);
        }

        return QC.stringToColor(R.m_textColor);
    }

    private String charsWidth()
    {
        return V.resolveStyleValue("text-decoration");
    }

    public final void paint(Graphics g, Rectangle rectangle, int j)
    {
        fillRect.x = G.x;
        fillRect.y = G.y;
        fillRect.width = G.width;
        fillRect.height = G.height + b();
        if (!fillRect.intersects(rectangle) || m() != j || !GI())
            return;
        char ac[] = B();
        if (ac == null || ac.length == 0)
            return;
        Color color = abs();
        if (color != null)
        {
            g.setColor(color);
            g.fillRect(G.x, G.y, G.width, G.height + b());
        }
        g.setFont(V());
        FontMetrics fontmetrics = g.getFontMetrics();
        if (V.getFocus())
        {
            g.setColor(QC.stringToColor(R.m_textColor));
            I(g, rectangle);
        }
        if (R.isTextSelected())
        {
            createElement(g);
        } else
        {
            g.setColor(charWidth());
            drawChars(g, ac, 0, ac.length, G.x);
        }
        contains(g);
    }

    private void contains(Graphics g)
    {
        g.setColor(Color.yellow);
        Iterator iterator = W.m_textSelectionsList.iterator();
        do
        {
            if (!iterator.hasNext())
                break;
            HC hc = (HC)iterator.next();
            int j = hc.getP0();
            int k = j + hc.getLength();
            int l = contains;
            int i1 = l + createElement;
            if (l <= j && j < i1 && k <= i1)
            {
                FontMetrics fontmetrics = g.getFontMetrics();
                char ac[] = B();
                int j1 = j - l;
                int i2 = 0;
                i2 = fontmetrics.charsWidth(ac, j1, k - j);
                int l2 = 0;
                if (j > l)
                    l2 = fontmetrics.charsWidth(ac, 0, j - l);
                g.setColor(hc.getBackground());
                g.fillRect(G.x + l2, G.y, i2, G.height + b());
                g.setColor(hc.getForeground());
                drawChars(g, ac, j1, k - j, G.x + l2);
            } else
            if (j >= l && j <= i1 && i1 < k)
            {
                FontMetrics fontmetrics1 = g.getFontMetrics();
                char ac1[] = B();
                int k1 = j - l;
                int j2 = 0;
                j2 = fontmetrics1.charsWidth(ac1, k1, i1 - j);
                int i3 = 0;
                if (j > l)
                    i3 = fontmetrics1.charsWidth(ac1, 0, j - l);
                g.setColor(hc.getBackground());
                g.fillRect(G.x + i3, G.y, j2, G.height + b());
                g.setColor(hc.getForeground());
                drawChars(g, ac1, k1, i1 - j, G.x + i3);
            } else
            if (l > j && k > l && k <= i1)
            {
                FontMetrics fontmetrics2 = g.getFontMetrics();
                char ac2[] = B();
                int l1 = 0;
                int k2 = 0;
                k2 = fontmetrics2.charsWidth(ac2, 0, k - l);
                g.setColor(hc.getBackground());
                g.fillRect(G.x, G.y, k2, G.height + b());
                g.setColor(hc.getForeground());
                drawChars(g, ac2, l1, k - l, G.x);
            }
        } while (true);
    }

    private boolean createElement(Graphics g)
    {
        Color color = W.getTextSelectionBackgroundColor();
        Color color1 = W.getTextSelectionColor();
        Color color2 = charWidth();
        int j;
        int k;
        if (R.m_textSelection.getLength() >= 0)
        {
            j = R.m_textSelection.getP0();
            k = (j + R.m_textSelection.getLength()) - 1;
        } else
        {
            j = R.m_textSelection.getP0() + R.m_textSelection.getLength();
            k = j + Math.abs(R.m_textSelection.getLength());
        }
        if (j > k)
        {
            int l = j;
            j = k;
            k = l;
        }
        int i1 = contains;
        int j1 = (contains + createElement) - 1;
        if (i1 >= j && j1 <= k)
        {
            g.setColor(color);
            g.fillRect(G.x, G.y, G.width, G.height + b());
            g.setColor(color1);
            char ac[] = B();
            drawChars(g, ac, 0, ac.length, G.x);
            return true;
        }
        if (i1 <= j && j1 >= j && j1 >= k)
        {
            Z z = R.getTextBuffer();
            char ac2[] = null;
            char ac5[] = null;
            char ac8[] = null;
            if (i1 < j)
            {
                ac2 = new char[j - i1];
                z.getChars(i1, j, ac2, 0);
            }
            ac5 = new char[(k - j) + 1];
            z.getChars(j, k + 1, ac5, 0);
            if (j1 > k)
            {
                ac8 = new char[j1 - k];
                if (ac8.length > 0)
                    z.getChars(k + 1, j1 + 1, ac8, 0);
            }
            char ac9[] = B();
            FontMetrics fontmetrics2 = g.getFontMetrics();
            int k2 = G.x;
            if (ac2 != null && ac2.length > 0)
            {
                g.setColor(color2);
                k2 += drawChars(g, ac2, 0, ac2.length, k2);
            }
            if (ac5 != null && ac5.length > 0)
            {
                int l2 = fontmetrics2.charsWidth(ac5, 0, ac5.length);
                g.setColor(color);
                g.fillRect(k2, G.y, l2, G.height + b());
                g.setColor(color1);
                k2 += drawChars(g, ac5, 0, ac5.length, k2);
            }
            if (ac8 != null && ac8.length > 0)
            {
                g.setColor(color2);
                drawChars(g, ac8, 0, ac8.length, k2);
            }
            return true;
        }
        if (i1 < j && j1 >= j && j1 < k)
        {
            Z z1 = R.getTextBuffer();
            char ac3[] = null;
            char ac6[] = null;
            ac3 = new char[j - i1];
            z1.getChars(i1, j, ac3, 0);
            ac6 = new char[(j1 - j) + 1];
            z1.getChars(j, j1 + 1, ac6, 0);
            FontMetrics fontmetrics = g.getFontMetrics();
            int k1 = G.x;
            if (ac3 != null && ac3.length > 0)
            {
                g.setColor(color2);
                k1 += drawChars(g, ac3, 0, ac3.length, k1);
            }
            if (ac6 != null && ac6.length > 0)
            {
                int i2 = fontmetrics.charsWidth(ac6, 0, ac6.length);
                g.setColor(color);
                g.fillRect(k1, G.y, i2, G.height + b());
                g.setColor(color1);
                drawChars(g, ac6, 0, ac6.length, k1);
            }
            return true;
        }
        if (i1 > j && i1 <= k && j1 > k)
        {
            Z z2 = R.getTextBuffer();
            char ac4[] = null;
            char ac7[] = null;
            ac7 = new char[(k - i1) + 1];
            z2.getChars(i1, k + 1, ac7, 0);
            ac4 = new char[j1 - k];
            z2.getChars(k + 1, j1 + 1, ac4, 0);
            FontMetrics fontmetrics1 = g.getFontMetrics();
            int l1 = G.x;
            if (ac7 != null && ac7.length > 0)
            {
                int j2 = fontmetrics1.charsWidth(ac7, 0, ac7.length);
                g.setColor(color);
                g.fillRect(l1, G.y, j2, G.height + b());
                g.setColor(color1);
                l1 += drawChars(g, ac7, 0, ac7.length, l1);
            }
            if (ac4 != null && ac4.length > 0)
            {
                g.setColor(color2);
                drawChars(g, ac4, 0, ac4.length, l1);
            }
            return true;
        } else
        {
            g.setColor(color2);
            char ac1[] = B();
            drawChars(g, ac1, 0, ac1.length, G.x);
            return true;
        }
    }

    private int drawChars(Graphics g, char ac[], int j, int k, int l)
    {
        boolean flag = false;
        boolean flag1 = false;
        boolean flag2 = false;
        String s = charsWidth();
        if (s != null)
        {
            flag = s.equals("underline");
            if (!flag)
                flag1 = s.equals("line-through");
            boolean flag3;
            if (!flag1)
                flag3 = s.equals("overline");
        }
        if (!flag1)
            flag1 = drawLine();
        if (drawLine && charsWidth != null)
        {
            g.setFont(charsWidth);
            FontMetrics fontmetrics = g.getFontMetrics();
            fontmetrics = g.getFontMetrics();
            int i1 = fontmetrics.charsWidth(ac, j, k);
            g.drawChars(ac, j, k, l, (G.y + fontmetrics.getAscent()) - fontmetrics.getLeading() - fontmetrics.getDescent());
            return i1;
        }
        if (equals && charsWidth != null)
        {
            g.setFont(charsWidth);
            FontMetrics fontmetrics1 = g.getFontMetrics();
            fontmetrics1 = g.getFontMetrics();
            int j1 = fontmetrics1.charsWidth(ac, j, k);
            g.drawChars(ac, j, k, l, G.y + G.height + fontmetrics1.getDescent());
            return j1;
        }
        FontMetrics fontmetrics2 = g.getFontMetrics();
        int k1 = fontmetrics2.charsWidth(ac, j, k);
        g.drawChars(ac, j, k, l, G.y + G.height);
        if (flag && W.m_props.N)
        {
            int l1 = G.y + G.height + b() / 2;
            g.drawLine(l, l1, l + k1, l1);
        } else
        if (flag1)
        {
            int i2 = G.y + G.height / 2 + b() / 2;
            g.drawLine(l, i2, l + k1, i2);
        }
        return k1;
    }

    protected final Rectangle I(int j, int k, int l, LayoutInfo layoutinfo)
    {
        W.I(this);
        l = C(1);
        Z(j, k, l, C(0));
        return G;
    }

    protected final int I(int j)
    {
        return C(j);
    }

    public int getMaximumWordLength()
    {
        if (I > -1)
            return I;
        I = 0;
        FontMetrics fontmetrics = i();
        char ac[] = B();
        int j = ac.length;
        for (int k = j - 1; k >= 0; k--)
        {
            if (!Character.isWhitespace(ac[k]))
                continue;
            int l = Math.min(k + 1, ac.length);
            if (l < ac.length)
            {
                int i1 = j - k - 1;
                int j1 = fontmetrics.charsWidth(ac, l, i1);
                I = Math.max(j1, I);
            }
            j = k;
        }

        if (j == ac.length)
            I = fontmetrics.charsWidth(ac, 0, ac.length);
        else
        if (j > 0)
            I = Math.max(I, fontmetrics.charsWidth(ac, 0, j));
        return I;
    }

    protected final int C(int j)
    {
        if (j == 1 && M != -1)
            return M;
        if (j == 0 && N != -1)
            return N;
        FontMetrics fontmetrics;
        if (j == 1 && charsWidth != null && (equals || drawLine))
            fontmetrics = Toolkit.getDefaultToolkit().getFontMetrics(charsWidth);
        else
            fontmetrics = i();
        switch (j)
        {
        case 1: // '\001'
            char ac[] = B();
            M = fontmetrics.charsWidth(ac, 0, ac.length);
            return M;

        case 0: // '\0'
            N = fontmetrics.getAscent();
            return N;
        }
        return 0;
    }

    final char[] B()
    {
        char ac[] = R.getText(contains, createElement);
        String s = V.resolveStyleValue("text-transform");
        if (s != null)
            if (s.equals("uppercase"))
            {
                for (int j = 0; j < ac.length; j++)
                    ac[j] = Character.toUpperCase(ac[j]);

            } else
            if (s.equals("lowercase"))
            {
                for (int k = 0; k < ac.length; k++)
                    ac[k] = Character.toLowerCase(ac[k]);

            } else
            if (s.equals("capitalize"))
            {
                boolean flag = false;
                for (int l = 0; l < ac.length; l++)
                {
                    char c1 = ac[l];
                    if (Character.isWhitespace(c1))
                    {
                        flag = true;
                        continue;
                    }
                    if (flag)
                    {
                        ac[l] = Character.toUpperCase(c1);
                        flag = false;
                    }
                }

            }
        return ac;
    }

    private final boolean drawLine()
    {
        boolean flag = QC.isString(V.resolveStyleValue("vertical-align"), "line-through");
        return flag;
    }

    private final boolean equals()
    {
        boolean flag = QC.isString(V.resolveStyleValue("vertical-align"), "subscript");
        if (!flag)
            flag = QC.isString(V.resolveStyleValue("vertical-align"), "sub");
        return flag;
    }

    private final boolean fillRect()
    {
        boolean flag = QC.isString(V.resolveStyleValue("vertical-align"), "superscript");
        if (!flag)
            flag = QC.isString(V.resolveStyleValue("vertical-align"), "super");
        return flag;
    }

    private final boolean first(Object obj)
    {
        String s = V.resolveStyleValue((String)obj);
        return s == "true";
    }

    protected final FontMetrics i()
    {
        if (charsWidth != null)
            return Toolkit.getDefaultToolkit().getFontMetrics(charsWidth);
        else
            return Toolkit.getDefaultToolkit().getFontMetrics(V());
    }
}
