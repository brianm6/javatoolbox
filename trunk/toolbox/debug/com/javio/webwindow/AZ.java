package com.javio.webwindow;

import D.C;
import D.D;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.net.URL;

// Referenced classes of package com.javio.webwindow:
//            UC, HTMLPane, ConnectionHandler, EI, 
//            QC, TZ, Document, SZ, 
//            FZ, K, L, G, 
//            LayoutInfo

public class AZ extends UC
    implements ImageObserver, Runnable
{

    private Image F;
    private int I;
    private int Z;
    private int abs;
    private String addImage;
    private boolean black;
    private Color blue;
    private Color bottom;
    private static Font cacheImage;
    private static int containsImage = 15;
    private Rectangle darkGray;
    private boolean darker;
    private String decode;
    private URL drawFocusBorder;
    private Insets drawImage;

    public AZ(EI ei, UC uc, Document document, HTMLPane htmlpane)
    {
        super(ei, uc, document, htmlpane);
        black = false;
        bottom = new Color(0, 130, 0);
        darkGray = new Rectangle();
    }

    protected final int U()
    {
        String s;
        s = (String)I("border");
        if (s == null)
            break MISSING_BLOCK_LABEL_20;
        return Integer.parseInt(s);
        NumberFormatException numberformatexception;
        numberformatexception;
        return !EI() ? -1 : 2;
    }

    private synchronized void F(Image image, int i, int j)
    {
        int k = -1;
        int l = -1;
        if (j > 0 && i > 0)
        {
            l = i;
            k = j;
        } else
        if (j > 0 || i > 0)
        {
            int i1 = image.getWidth(this);
            int j1 = image.getHeight(this);
            if (i1 > 0 && j1 > 0)
            {
                float f = (float)i1 / (float)j1;
                if (i > 0)
                {
                    k = (int)((float)i / f);
                    l = i;
                } else
                {
                    l = (int)((float)j * f);
                    k = j;
                }
            }
        } else
        {
            l = image.getWidth(this);
            k = image.getHeight(this);
        }
        M = l + I;
        N = k + Z;
    }

    protected final void F(D d, D d1, D d2)
    {
        Color color = I("border-color", d, d1, d2);
        if (color != null)
            I(color);
        else
        if (EI())
            I(Color.blue);
    }

    protected final void I(EI ei, UC uc, Document document, HTMLPane htmlpane)
    {
        htmlpane.registerLoadingImageView(this);
        super.I(ei, uc, document, htmlpane);
    }

    protected final void Z(EI ei, UC uc, Document document, HTMLPane htmlpane)
    {
        I(ei, uc, document, htmlpane);
        I();
    }

    protected final void I()
    {
        darker = true;
        D d = s();
        D d1 = a();
        D d2 = e();
        A(d, d1, d2);
        String s = I("clip", d, d1, d2, null);
        if (s != null)
            drawImage = D.C.parseClipStyle(s);
        else
            drawImage = null;
        j();
        TZ tz = V.getAttributes();
        if (DI())
        {
            I(0, 2, 0, 2);
        } else
        {
            int i = QC.I(-1, "hspace", tz);
            if (i > -1)
                H.left = H.right = i;
            int j = QC.I(-1, "vspace", tz);
            if (j > -1)
                H.top = H.bottom = j;
        }
        Color color = null;
        if (blue != null)
            color = blue;
        Color color1 = QC.I(color, "color", tz);
        if (color1 != null)
            blue = color1;
        addImage = (String)tz.getAttribute("alt");
        abs = 0;
        if (cacheImage == null)
            cacheImage = new Font("Dialog", 0, 11);
        if (addImage != null)
            abs = Toolkit.getDefaultToolkit().getFontMetrics(cacheImage).stringWidth(addImage);
        b = QC.I(b, "width", tz);
        z = -1;
        c = -1;
        String s1 = (String)tz.getAttribute("width");
        if (s1 != null)
            z = QC.getPixels(s1, -1);
        s1 = (String)tz.getAttribute("height");
        if (s1 != null)
            c = QC.getPixels(s1, -1);
        I = H.left + H.right + ZI() + CI();
        Z = H.top + H.bottom + y() + II();
        drawFocusBorder = null;
        F = null;
        black = false;
        String s2 = (String)tz.getAttribute("src");
        if (s2 != null && s2.length() > 0)
        {
            drawFocusBorder = QC.getURL(R.m_baseURL, s2);
            if (drawFocusBorder != null)
            {
                decode = drawFocusBorder.toString();
                HTMLPane _tmp = W;
                SZ sz = HTMLPane.I;
                if (sz.containsImage(decode))
                {
                    F = sz.getImage(decode);
                    black = true;
                    F(F, z, c);
                    W.repaint();
                }
            }
        }
    }

    protected final void J(D d, D d1, D d2)
    {
        j = 0;
        String s = I("float", d, d1, d2, null);
        if (s != null)
            if (s.equalsIgnoreCase("right"))
                j = 2;
            else
            if (s.equalsIgnoreCase("left"))
                j = 1;
    }

    protected final void C(D d, D d1, D d2)
    {
        K = QC.I(false, -1, "align", V.getAttributes());
        if (j != 2 && j != 1)
            switch (K)
            {
            case 0: // '\0'
                j = 1;
                break;

            case 2: // '\002'
                j = 2;
                break;

            default:
                j = 0;
                break;
            }
    }

    protected final void C()
    {
        if (!getHTMLPane().getLoadImages() || F != null)
        {
            getHTMLPane().unRegisterLoadingImageView(this);
            darker = false;
            return;
        }
        if (drawFocusBorder != null)
        {
            SZ sz = HTMLPane.I;
            boolean flag = false;
            decode = drawFocusBorder.toString();
            if (flag = sz.containsImage(decode))
            {
                F = sz.getImage(decode);
                black = true;
                W.repaint();
            } else
            {
                F = W.getConnectionHandler().getImage(drawFocusBorder);
            }
            if (F != null && !flag)
                if (W.m_props.H)
                {
                    if (c > 0 && z > 0)
                    {
                        black = Toolkit.getDefaultToolkit().prepareImage(F, z, c, this);
                    } else
                    {
                        black = Toolkit.getDefaultToolkit().prepareImage(F, -1, -1, this);
                        if (black)
                        {
                            F(F, z, c);
                            abs();
                            W.postLayoutEvent();
                        }
                    }
                } else
                {
                    MediaTracker mediatracker = new MediaTracker(W);
                    try
                    {
                        if (c > 0 && z > 0)
                            mediatracker.addImage(F, 0, z, c);
                        else
                            mediatracker.addImage(F, 0);
                        mediatracker.waitForID(0);
                        black = !mediatracker.isErrorAny();
                        if (!black)
                        {
                            F = null;
                        } else
                        {
                            if (z == -1)
                                z = F.getWidth(null);
                            if (c == -1)
                                c = F.getHeight(null);
                            sz.cacheImage(decode, F);
                            abs();
                            W.postLayoutEvent();
                        }
                    }
                    catch (InterruptedException interruptedexception)
                    {
                        F = null;
                    }
                }
        }
        if (black || F == null)
            getHTMLPane().unRegisterLoadingImageView(this);
        darker = false;
    }

    protected final String M()
    {
        return addImage == null ? "" : addImage;
    }

    protected final void N()
    {
        super.N();
        Z();
    }

    protected final void Z()
    {
        if (F != null)
            F = null;
    }

    protected final boolean O()
    {
        return false;
    }

    protected final void I(Graphics g, Rectangle rectangle)
    {
        if (V.getFocus())
            QC.drawFocusBorder(g, G);
    }

    public final void paint(Graphics g, Rectangle rectangle, int i)
    {
        if (!G.intersects(rectangle) || i != m() || !GI())
            return;
        int j = G.x + H.left + f.C.m_thickness;
        int k = G.y + H.top + f.I.m_thickness;
        int l = G.width - H.left - H.right - ZI() - CI();
        int i1 = G.height - H.top - H.bottom - y() - II();
        if (F != null && black)
        {
            if (drawImage != null && E(0))
            {
                Rectangle rectangle1 = g.getClipBounds();
                Rectangle rectangle2 = new Rectangle(j, k, l, i1);
                if (drawImage.left != 0x80000000)
                    rectangle2.x += drawImage.left;
                if (drawImage.top != 0x80000000)
                    rectangle2.y += drawImage.top;
                if (drawImage.right != 0x80000000)
                    rectangle2.width = Math.abs((j + drawImage.right) - j);
                if (drawImage.bottom != 0x80000000)
                    rectangle2.height = Math.max(0, (k + drawImage.bottom) - rectangle2.y);
                Rectangle rectangle3 = rectangle.intersection(rectangle2);
                g.setClip(rectangle3);
                g.drawImage(F, j, k, l, i1, this);
                g.setClip(rectangle1);
            } else
            {
                g.drawImage(F, j, k, l, i1, this);
            }
            I(g);
        } else
        {
            int j1 = 1;
            darkGray.x = G.x + H.left;
            darkGray.y = G.y + H.top;
            darkGray.width = Math.max(0, G.width - H.left - H.right - 1);
            darkGray.height = Math.max(0, G.height - H.top - H.bottom - 1);
            int k1 = 0;
            byte byte0 = 5;
            int l1 = 2 * byte0;
            if (!darker && F == null && G.width > containsImage + l1 && G.height > containsImage + l1)
            {
                QC.I(g, darkGray);
                Z(g, j + byte0, k + byte0, containsImage, containsImage);
                int i2 = l - byte0 - containsImage - byte0;
                if (addImage != null && abs < i2)
                {
                    Font font = g.getFont();
                    g.setColor(Color.black);
                    g.setFont(cacheImage);
                    FontMetrics fontmetrics = Toolkit.getDefaultToolkit().getFontMetrics(cacheImage);
                    g.drawString(addImage, j + byte0 + containsImage + byte0, k + byte0 + fontmetrics.getAscent());
                    g.setFont(font);
                }
            } else
            {
                g.setColor(Color.black);
                g.drawRect(darkGray.x, darkGray.y, darkGray.width, darkGray.height);
                int j2 = G.width - 2 * j1 - H.left - H.right - k1;
                int k2 = G.height - 2 * j1 - H.top - H.bottom;
                if (j2 - byte0 > containsImage && k2 - byte0 > containsImage)
                    I(g, j + byte0, k + byte0, containsImage, containsImage);
            }
        }
        I(g, rectangle);
    }

    private final void I(Graphics g, int i, int j, int k, int l)
    {
        g.setColor(Color.white);
        g.fillRect(i, j, k, l);
        g.setColor(Color.gray);
        g.drawLine(i, j, i + 15, j);
        g.drawLine(i, j, i, j + 13);
        g.setColor(Color.darkGray);
        g.drawLine(i, j + 14, i + 15, j + 14);
        g.drawLine(i + 15, j, i + 15, j + 13);
        g.setColor(Color.black);
        g.drawRect(i + 8, j + 2, 5, 5);
        g.drawRect(i + 2, j + 7, 5, 5);
        g.setColor(bottom);
        g.fillRect(i + 9, j + 3, 4, 4);
        g.setColor(Color.red);
        g.fillRect(i + 3, j + 8, 4, 4);
    }

    private final void Z(Graphics g, int i, int j, int k, int l)
    {
        byte byte0 = 15;
        Color color = Color.decode("#dcdcdc");
        Color color1 = color.darker().darker();
        int i1 = (k - byte0) / 2;
        int j1 = (l - byte0) / 2;
        g.setColor(Color.white);
        g.fillRect(i, j, k, l);
        g.setColor(color);
        g.drawLine(i + i1, j + j1, i + i1 + byte0, j + j1);
        g.drawLine(i + i1, j + j1, i + i1, j + j1 + byte0);
        g.setColor(color1);
        g.drawLine(i + i1, j + j1 + byte0, i + i1 + byte0, j + j1 + byte0);
        g.drawLine(i + i1 + byte0, j + j1, i + i1 + byte0, j + j1 + byte0);
        g.setColor(Color.red);
        int k1 = (byte0 - 5) / 2;
        int l1 = i + i1 + k1;
        int i2 = j + j1 + k1;
        g.drawLine(l1, i2, l1 + 1, i2);
        g.drawLine(l1 + 4, i2, l1 + 5, i2);
        g.drawLine(l1 + 1, i2 + 1, l1 + 4, i2 + 1);
        g.drawLine(l1 + 2, i2 + 2, l1 + 3, i2 + 2);
        g.drawLine(l1 + 1, i2 + 3, l1 + 4, i2 + 3);
        g.drawLine(l1, i2 + 4, l1 + 1, i2 + 4);
        g.drawLine(l1 + 4, i2 + 4, l1 + 5, i2 + 4);
    }

    private void abs()
    {
        UC uc = T;
        if (uc != null)
            uc.Y();
        W.postLayoutEvent();
    }

    public final boolean imageUpdate(Image image, int i, int j, int k, int l, int i1)
    {
        if (F == null)
        {
            HTMLPane htmlpane = getHTMLPane();
            if (htmlpane != null)
                htmlpane.unRegisterLoadingImageView(this);
            return false;
        }
        if ((i & 0xc0) != 0)
        {
            HTMLPane htmlpane1 = getHTMLPane();
            if (htmlpane1 != null)
            {
                htmlpane1.unRegisterLoadingImageView(this);
                htmlpane1.fireStatusEvent(12, V);
                F = null;
                htmlpane1.repaint();
            }
            return false;
        }
        boolean flag = false;
        if ((i & 2) != 0 && c == -1)
            flag = true;
        if ((i & 1) != 0 && z == -1)
            flag = true;
        if (flag)
        {
            if (W.m_props.O)
                abs();
            else
                QC.I(this);
            return true;
        }
        if ((i & 0x38) != 0)
        {
            if (!black)
            {
                boolean flag1 = (i & 0x20) != 0 || (i & 0x10) != 0;
                if (flag1)
                    black = true;
            }
            if (black)
            {
                if (!HTMLPane.I.containsImage(decode))
                    HTMLPane.I.cacheImage(decode, F);
                HTMLPane htmlpane2 = getHTMLPane();
                if (htmlpane2 != null)
                    htmlpane2.unRegisterLoadingImageView(this);
                F(F, z, c);
                abs();
            }
            HTMLPane htmlpane3 = getHTMLPane();
            if (htmlpane3 != null)
                htmlpane3.repaint();
        }
        return true;
    }

    public final void run()
    {
        abs();
    }

    protected final int I(int i)
    {
        return C(i);
    }

    protected final int C(int i)
    {
        if (HI())
            return 0;
        switch (i)
        {
        case 1: // '\001'
            if (M != -1)
                return M;
            if (z >= 0)
            {
                M = z + I;
            } else
            {
                if (F != null)
                    return Math.max(F.getWidth(null), 0) + Z;
                M = containsImage + abs + I;
            }
            return M;

        case 0: // '\0'
            if (N != -1)
                return N;
            if (c >= 0)
            {
                N = c + Z;
            } else
            {
                if (F != null)
                    return Math.max(F.getHeight(null), 0) + Z;
                N = containsImage + Z;
            }
            return N;
        }
        return 30;
    }

    protected final Rectangle I(int i, int j, int k, LayoutInfo layoutinfo)
    {
        W.I(this);
        if (E(0))
        {
            int l = 0;
            int j1 = 0;
            G g = getContainingBox();
            if (g != null)
            {
                Rectangle rectangle = g.getBounds();
                l = rectangle.x;
                j1 = rectangle.y;
            }
            if (Y != 0x80000000)
                i = l + Y;
            if (this.i != 0x80000000)
                j = j1 + this.i;
        } else
        if (E(1))
        {
            if (Y != 0x80000000)
                i += Y;
            if (this.i != 0x80000000)
                j += this.i;
        }
        int i1 = 0;
        if (b > 0.0F && b <= 1.0F)
            i1 = (int)(b * (float)k);
        else
            i1 = C(1);
        Z(i, j, i1, C(0));
        return G;
    }

}
