package toolbox.util.ui;

import java.awt.Color;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.collections.iterators.ArrayIterator;

/**
 * Additional colors from X11's rgb.txt.
 */
public final class Colors
{
    public static final XColor alice_blue =  new XColor("alice blue", 240, 248, 255); 
    public static final XColor aliceblue =  new XColor("aliceblue", 240, 248, 255);
    public static final XColor antique_white = new XColor("antique white", 250, 235, 215);
    public static final XColor antiquewhite  = new XColor("antiquewhite", 250, 235, 215);
    public static final XColor antiquewhite1 = new XColor("antiquewhite1", 255, 239, 219);
    public static final XColor antiquewhite2 = new XColor("antiquewhite2", 238, 223, 204);
    public static final XColor antiquewhite3 = new XColor("antiquewhite3", 205, 192, 176);
    public static final XColor antiquewhite4 = new XColor("antiquewhite4", 139, 131, 120);
    public static final XColor aquamarine =  new XColor("aquamarine", 127, 255, 212);
    public static final XColor aquamarine1 = new XColor("aquamarine1", 127, 255, 212);
    public static final XColor aquamarine2 = new XColor("aquamarine2", 118, 238, 198);
    public static final XColor aquamarine3 = new XColor("aquamarine3", 102, 205, 170);
    public static final XColor aquamarine4 = new XColor("aquamarine4", 69, 139, 116);
    public static final XColor azure =  new XColor("azure", 240, 255, 255);
    public static final XColor azure1 =  new XColor("azure1", 240, 255, 255);
    public static final XColor azure2 =  new XColor("azure2", 224, 238, 238);
    public static final XColor azure3 =  new XColor("azure3", 193, 205, 205);
    public static final XColor azure4 =  new XColor("azure4", 131, 139, 139);
    public static final XColor beige =  new XColor("beige", 245, 245, 220);
    public static final XColor bisque =  new XColor("bisque", 255, 228, 196);
    public static final XColor bisque1 =  new XColor("bisque1", 255, 228, 196);
    public static final XColor bisque2 =  new XColor("bisque2", 238, 213, 183);
    public static final XColor bisque3 =  new XColor("bisque3", 205, 183, 158);
    public static final XColor bisque4 =  new XColor("bisque4", 139, 125, 107);
    public static final XColor black =  new XColor("black", 0, 0, 0);
    public static final XColor blanched_almond = new XColor("blanched almond", 255, 235, 205);
    public static final XColor blanchedalmond = new XColor("blanchedalmond", 255, 235, 205);
    public static final XColor blue =  new XColor("blue", 0, 0, 255);
    public static final XColor blue_violet = new XColor("blue violet", 138, 43, 226);
    public static final XColor blue1 =  new XColor("blue1", 0, 0, 255);
    public static final XColor blue2 =  new XColor("blue2", 0, 0, 238);
    public static final XColor blue3 =  new XColor("blue3", 0, 0, 205);
    public static final XColor blue4 =  new XColor("blue4", 0, 0, 139);
    public static final XColor blueviolet =  new XColor("blueviolet", 138, 43, 226);
    public static final XColor brown =  new XColor("brown", 165, 42, 42);
    public static final XColor brown1 =  new XColor("brown1", 255, 64, 64);
    public static final XColor brown2 =  new XColor("brown2", 238, 59, 59);
    public static final XColor brown3 =  new XColor("brown3", 205, 51, 51);
    public static final XColor brown4 =  new XColor("brown4", 139, 35, 35);
    public static final XColor burlywood  =  new XColor("burlywood", 222, 184, 135);
    public static final XColor burlywood1  = new XColor("burlywood1", 255, 211, 155);
    public static final XColor burlywood2  = new XColor("burlywood2", 238, 197, 145);
    public static final XColor burlywood3 =  new XColor("burlywood3", 205, 170, 125);
    public static final XColor burlywood4 =  new XColor("burlywood4", 139, 115, 85);
    public static final XColor cadet_blue =  new XColor("cadet blue", 95, 158, 160);
    public static final XColor cadetblue = new XColor("cadetblue", 95, 158, 160);
    public static final XColor cadetblue1 =  new XColor("cadetblue1", 152, 245, 255);
    public static final XColor cadetblue2 =  new XColor("cadetblue2", 142, 229, 238);
    public static final XColor cadetblue3 =  new XColor("cadetblue3", 122, 197, 205);
    public static final XColor cadetblue4 =  new XColor("cadetblue4", 83, 134, 139);
    public static final XColor chartreuse =  new XColor("chartreuse", 127, 255, 0);
    public static final XColor chartreuse1 = new XColor("chartreuse1", 127, 255, 0);
    public static final XColor chartreuse2 = new XColor("chartreuse2", 118, 238, 0);
    public static final XColor chartreuse3 = new XColor("chartreuse3", 102, 205, 0);
    public static final XColor chartreuse4 = new XColor("chartreuse4", 69, 139, 0);
    public static final XColor chocolate =  new XColor("chocolate", 210, 105, 30);
    public static final XColor chocolate1 =  new XColor("chocolate1", 255, 127, 36);
    public static final XColor chocolate2 =  new XColor("chocolate2", 238, 118, 33);
    public static final XColor chocolate3 =  new XColor("chocolate3", 205, 102, 29);
    public static final XColor chocolate4 =  new XColor("chocolate4", 139, 69, 19);
    public static final XColor coral =  new XColor("coral", 255, 127, 80);
    public static final XColor coral1 =  new XColor("coral1", 255, 114, 86);
    public static final XColor coral2 =  new XColor("coral2", 238, 106, 80);
    public static final XColor coral3 =  new XColor("coral3", 205, 91, 69);
    public static final XColor coral4 =  new XColor("coral4", 139, 62, 47);
    public static final XColor cornflower_blue = new XColor("cornflower blue", 100, 149, 237);
    public static final XColor cornflowerblue = new XColor("cornflowerblue", 100, 149, 237);
    public static final XColor cornsilk =  new XColor("cornsilk", 255, 248, 220);
    public static final XColor cornsilk1 =  new XColor("cornsilk1", 255, 248, 220);
    public static final XColor cornsilk2 =  new XColor("cornsilk2", 238, 232, 205);
    public static final XColor cornsilk3 =  new XColor("cornsilk3", 205, 200, 177);
    public static final XColor cornsilk4 =  new XColor("cornsilk4", 139, 136, 120);
    public static final XColor cyan =  new XColor("cyan", 0, 255, 255);
    public static final XColor cyan1 =  new XColor("cyan1", 0, 255, 255);
    public static final XColor cyan2 =  new XColor("cyan2", 0, 238, 238);
    public static final XColor cyan3 =  new XColor("cyan3", 0, 205, 205);
    public static final XColor cyan4 =  new XColor("cyan4", 0, 139, 139);
    public static final XColor dark_blue =  new XColor("dark blue", 0, 0, 139);
    public static final XColor dark_cyan =  new XColor("dark cyan", 0, 139, 139);
    public static final XColor dark_goldenrod = new XColor("dark goldenrod", 184, 134, 11);
    public static final XColor dark_gray =  new XColor("dark gray", 169, 169, 169);
    public static final XColor dark_green =  new XColor("dark green", 0, 100, 0);
    public static final XColor dark_grey =  new XColor("dark grey", 169, 169, 169);
    public static final XColor dark_khaki =  new XColor("dark khaki", 189, 183, 107);
    public static final XColor dark_magenta = new XColor("dark magenta", 139, 0, 139);
    public static final XColor dark_olive_green = new XColor("dark olive green", 85, 107, 47);
    public static final XColor dark_orange = new XColor("dark orange", 255, 140, 0);
    public static final XColor dark_orchid = new XColor("dark orchid", 153, 50, 204);
    public static final XColor dark_red =  new XColor("dark red", 139, 0, 0);
    public static final XColor dark_salmon = new XColor("dark salmon", 233, 150, 122);
    public static final XColor dark_sea_green = new XColor("dark sea green", 143, 188, 143);
    public static final XColor dark_slate_blue = new XColor("dark slate blue", 72, 61, 139);
    public static final XColor dark_slate_gray = new XColor("dark slate gray", 47, 79, 79);
    public static final XColor dark_slate_grey = new XColor("dark slate grey", 47, 79, 79);
    public static final XColor dark_turquoise = new XColor("dark turquoise", 0, 206, 209);
    public static final XColor dark_violet = new XColor("dark violet", 148, 0, 211);
    public static final XColor darkblue =  new XColor("darkblue", 0, 0, 139);
    public static final XColor darkcyan =  new XColor("darkcyan", 0, 139, 139);
    public static final XColor darkgoldenrod = new XColor("darkgoldenrod", 184, 134, 11);
    public static final XColor darkgoldenrod1 = new XColor("darkgoldenrod1", 255, 185, 15);
    public static final XColor darkgoldenrod2 = new XColor("darkgoldenrod2", 238, 173, 14);
    public static final XColor darkgoldenrod3 = new XColor("darkgoldenrod3", 205, 149, 12);
    public static final XColor darkgoldenrod4 = new XColor("darkgoldenrod4", 139, 101, 8);
    public static final XColor darkgray =  new XColor("darkgray", 169, 169, 169);
    public static final XColor darkgreen =  new XColor("darkgreen", 0, 100, 0);
    public static final XColor darkgrey =  new XColor("darkgrey", 169, 169, 169);
    public static final XColor darkkhaki =  new XColor("darkkhaki", 189, 183, 107);
    public static final XColor darkmagenta = new XColor("darkmagenta", 139, 0, 139);
    public static final XColor darkolivegreen = new XColor("darkolivegreen", 85, 107, 47);
    public static final XColor darkolivegreen1 = new XColor("darkolivegreen1", 202, 255, 112);
    public static final XColor darkolivegreen2 = new XColor("darkolivegreen2", 188, 238, 104);
    public static final XColor darkolivegreen3 = new XColor("darkolivegreen3", 162, 205, 90);
    public static final XColor darkolivegreen4 = new XColor("darkolivegreen4", 110, 139, 61);
    public static final XColor darkorange =  new XColor("darkorange", 255, 140, 0);
    public static final XColor darkorange1 = new XColor("darkorange1", 255, 127, 0);
    public static final XColor darkorange2 = new XColor("darkorange2", 238, 118, 0);
    public static final XColor darkorange3 = new XColor("darkorange3", 205, 102, 0);
    public static final XColor darkorange4 = new XColor("darkorange4", 139, 69, 0);
    public static final XColor darkorchid =  new XColor("darkorchid", 153, 50, 204);
    public static final XColor darkorchid1 = new XColor("darkorchid1", 191, 62, 255);
    public static final XColor darkorchid2 = new XColor("darkorchid2", 178, 58, 238);
    public static final XColor darkorchid3 = new XColor("darkorchid3", 154, 50, 205);
    public static final XColor darkorchid4 = new XColor("darkorchid4", 104, 34, 139);
    public static final XColor darkred =  new XColor("darkred", 139, 0, 0);
    public static final XColor darksalmon =  new XColor("darksalmon", 233, 150, 122);
    public static final XColor darkseagreen = new XColor("darkseagreen", 143, 188, 143);
    public static final XColor darkseagreen1 = new XColor("darkseagreen1", 193, 255, 193);
    public static final XColor darkseagreen2 = new XColor("darkseagreen2", 180, 238, 180);
    public static final XColor darkseagreen3 = new XColor("darkseagreen3", 155, 205, 155);
    public static final XColor darkseagreen4 = new XColor("darkseagreen4", 105, 139, 105);
    public static final XColor darkslateblue = new XColor("darkslateblue", 72, 61, 139);
    public static final XColor darkslategray = new XColor("darkslategray", 47, 79, 79);
    public static final XColor darkslategray1 = new XColor("darkslategray1", 151, 255, 255);
    public static final XColor darkslategray2 = new XColor("darkslategray2", 141, 238, 238);
    public static final XColor darkslategray3 = new XColor("darkslategray3", 121, 205, 205);
    public static final XColor darkslategray4 = new XColor("darkslategray4", 82, 139, 139);
    public static final XColor darkslategrey = new XColor("darkslategrey", 47, 79, 79);
    public static final XColor darkturquoise = new XColor("darkturquoise", 0, 206, 209);
    public static final XColor darkviolet =  new XColor("darkviolet", 148, 0, 211);
    public static final XColor deep_pink =  new XColor("deep pink", 255, 20, 147);
    public static final XColor deep_sky_blue = new XColor("deep sky blue", 0, 191, 255);
    public static final XColor deeppink =  new XColor("deeppink", 255, 20, 147);
    public static final XColor deeppink1 =  new XColor("deeppink1", 255, 20, 147);
    public static final XColor deeppink2 =  new XColor("deeppink2", 238, 18, 137);
    public static final XColor deeppink3 =  new XColor("deeppink3", 205, 16, 118);
    public static final XColor deeppink4 =  new XColor("deeppink4", 139, 10, 80);
    public static final XColor deepskyblue = new XColor("deepskyblue", 0, 191, 255);
    public static final XColor deepskyblue1 = new XColor("deepskyblue1", 0, 191, 255);
    public static final XColor deepskyblue2 = new XColor("deepskyblue2", 0, 178, 238);
    public static final XColor deepskyblue3 = new XColor("deepskyblue3", 0, 154, 205);
    public static final XColor deepskyblue4 = new XColor("deepskyblue4", 0, 104, 139);
    public static final XColor dim_gray =  new XColor("dim gray", 105, 105, 105);
    public static final XColor dim_grey =  new XColor("dim grey", 105, 105, 105);
    public static final XColor dimgray =  new XColor("dimgray", 105, 105, 105);
    public static final XColor dimgrey =  new XColor("dimgrey", 105, 105, 105);
    public static final XColor dodger_blue = new XColor("dodger blue", 30, 144, 255);
    public static final XColor dodgerblue =  new XColor("dodgerblue", 30, 144, 255);
    public static final XColor dodgerblue1 = new XColor("dodgerblue1", 30, 144, 255);
    public static final XColor dodgerblue2 = new XColor("dodgerblue2", 28, 134, 238);
    public static final XColor dodgerblue3 = new XColor("dodgerblue3", 24, 116, 205);
    public static final XColor dodgerblue4 = new XColor("dodgerblue4", 16, 78, 139);
    public static final XColor firebrick =  new XColor("firebrick", 178, 34, 34);
    public static final XColor firebrick1 =  new XColor("firebrick1", 255, 48, 48);
    public static final XColor firebrick2 =  new XColor("firebrick2", 238, 44, 44);
    public static final XColor firebrick3 =  new XColor("firebrick3", 205, 38, 38);
    public static final XColor firebrick4 =  new XColor("firebrick4", 139, 26, 26);
    public static final XColor floral_white = new XColor("floral white", 255, 250, 240);
    public static final XColor floralwhite = new XColor("floralwhite", 255, 250, 240);
    public static final XColor forest_green = new XColor("forest green", 34, 139, 34);
    public static final XColor forestgreen = new XColor("forestgreen", 34, 139, 34);
    public static final XColor gainsboro =  new XColor("gainsboro", 220, 220, 220);
    public static final XColor ghost_white = new XColor("ghost white", 248, 248, 255);
    public static final XColor ghostwhite =  new XColor("ghostwhite", 248, 248, 255);
    public static final XColor gold =  new XColor("gold", 255, 215, 0);
    public static final XColor gold1 =  new XColor("gold1", 255, 215, 0);
    public static final XColor gold2 =  new XColor("gold2", 238, 201, 0);
    public static final XColor gold3 =  new XColor("gold3", 205, 173, 0);
    public static final XColor gold4 =  new XColor("gold4", 139, 117, 0);
    public static final XColor goldenrod =  new XColor("goldenrod", 218, 165, 32);
    public static final XColor goldenrod1 =  new XColor("goldenrod1", 255, 193, 37);
    public static final XColor goldenrod2 =  new XColor("goldenrod2", 238, 180, 34);
    public static final XColor goldenrod3 =  new XColor("goldenrod3", 205, 155, 29);
    public static final XColor goldenrod4 =  new XColor("goldenrod4", 139, 105, 20);
    public static final XColor gray =  new XColor("gray", 190, 190, 190);
    public static final XColor gray0 =  new XColor("gray0", 0, 0, 0);
    public static final XColor gray1 =  new XColor("gray1", 3, 3, 3);
    public static final XColor gray10 =  new XColor("gray10", 26, 26, 26);
    public static final XColor gray100 =  new XColor("gray100", 255, 255, 255);
    public static final XColor gray11 =  new XColor("gray11", 28, 28, 28);
    public static final XColor gray12 =  new XColor("gray12", 31, 31, 31);
    public static final XColor gray13 =  new XColor("gray13", 33, 33, 33);
    public static final XColor gray14 =  new XColor("gray14", 36, 36, 36);
    public static final XColor gray15 =  new XColor("gray15", 38, 38, 38);
    public static final XColor gray16 =  new XColor("gray16", 41, 41, 41);
    public static final XColor gray17 =  new XColor("gray17", 43, 43, 43);
    public static final XColor gray18 =  new XColor("gray18", 46, 46, 46);
    public static final XColor gray19 =  new XColor("gray19", 48, 48, 48);
    public static final XColor gray2 =  new XColor("gray2", 5, 5, 5);
    public static final XColor gray20 =  new XColor("gray20", 51, 51, 51);
    public static final XColor gray21 =  new XColor("gray21", 54, 54, 54);
    public static final XColor gray22 =  new XColor("gray22", 56, 56, 56);
    public static final XColor gray23 =  new XColor("gray23", 59, 59, 59);
    public static final XColor gray24 =  new XColor("gray24", 61, 61, 61);
    public static final XColor gray25 =  new XColor("gray25", 64, 64, 64);
    public static final XColor gray26 =  new XColor("gray26", 66, 66, 66);
    public static final XColor gray27 =  new XColor("gray27", 69, 69, 69);
    public static final XColor gray28 =  new XColor("gray28", 71, 71, 71);
    public static final XColor gray29 =  new XColor("gray29", 74, 74, 74);
    public static final XColor gray3 =  new XColor("gray3", 8, 8, 8);
    public static final XColor gray30 =  new XColor("gray30", 77, 77, 77);
    public static final XColor gray31 =  new XColor("gray31", 79, 79, 79);
    public static final XColor gray32 =  new XColor("gray32", 82, 82, 82);
    public static final XColor gray33 =  new XColor("gray33", 84, 84, 84);
    public static final XColor gray34 =  new XColor("gray34", 87, 87, 87);
    public static final XColor gray35 =  new XColor("gray35", 89, 89, 89);
    public static final XColor gray36 =  new XColor("gray36", 92, 92, 92);
    public static final XColor gray37 =  new XColor("gray37", 94, 94, 94);
    public static final XColor gray38 =  new XColor("gray38", 97, 97, 97);
    public static final XColor gray39 =  new XColor("gray39", 99, 99, 99);
    public static final XColor gray4 =  new XColor("gray4", 10, 10, 10);
    public static final XColor gray40 =  new XColor("gray40", 102, 102, 102);
    public static final XColor gray41 =  new XColor("gray41", 105, 105, 105);
    public static final XColor gray42 =  new XColor("gray42", 107, 107, 107);
    public static final XColor gray43 =  new XColor("gray43", 110, 110, 110);
    public static final XColor gray44 =  new XColor("gray44", 112, 112, 112);
    public static final XColor gray45 =  new XColor("gray45", 115, 115, 115);
    public static final XColor gray46 =  new XColor("gray46", 117, 117, 117);
    public static final XColor gray47 =  new XColor("gray47", 120, 120, 120);
    public static final XColor gray48 =  new XColor("gray48", 122, 122, 122);
    public static final XColor gray49 =  new XColor("gray49", 125, 125, 125);
    public static final XColor gray5 =  new XColor("gray5", 13, 13, 13);
    public static final XColor gray50 =  new XColor("gray50", 127, 127, 127);
    public static final XColor gray51 =  new XColor("gray51", 130, 130, 130);
    public static final XColor gray52 =  new XColor("gray52", 133, 133, 133);
    public static final XColor gray53 =  new XColor("gray53", 135, 135, 135);
    public static final XColor gray54 =  new XColor("gray54", 138, 138, 138);
    public static final XColor gray55 =  new XColor("gray55", 140, 140, 140);
    public static final XColor gray56 =  new XColor("gray56", 143, 143, 143);
    public static final XColor gray57 =  new XColor("gray57", 145, 145, 145);
    public static final XColor gray58 =  new XColor("gray58", 148, 148, 148);
    public static final XColor gray59 =  new XColor("gray59", 150, 150, 150);
    public static final XColor gray6 =  new XColor("gray6", 15, 15, 15);
    public static final XColor gray60 =  new XColor("gray60", 153, 153, 153);
    public static final XColor gray61 =  new XColor("gray61", 156, 156, 156);
    public static final XColor gray62 =  new XColor("gray62", 158, 158, 158);
    public static final XColor gray63 =  new XColor("gray63", 161, 161, 161);
    public static final XColor gray64 =  new XColor("gray64", 163, 163, 163);
    public static final XColor gray65 =  new XColor("gray65", 166, 166, 166);
    public static final XColor gray66 =  new XColor("gray66", 168, 168, 168);
    public static final XColor gray67 =  new XColor("gray67", 171, 171, 171);
    public static final XColor gray68 =  new XColor("gray68", 173, 173, 173);
    public static final XColor gray69 =  new XColor("gray69", 176, 176, 176);
    public static final XColor gray7 =  new XColor("gray7", 18, 18, 18);
    public static final XColor gray70 =  new XColor("gray70", 179, 179, 179);
    public static final XColor gray71 =  new XColor("gray71", 181, 181, 181);
    public static final XColor gray72 =  new XColor("gray72", 184, 184, 184);
    public static final XColor gray73 =  new XColor("gray73", 186, 186, 186);
    public static final XColor gray74 =  new XColor("gray74", 189, 189, 189);
    public static final XColor gray75 =  new XColor("gray75", 191, 191, 191);
    public static final XColor gray76 =  new XColor("gray76", 194, 194, 194);
    public static final XColor gray77 =  new XColor("gray77", 196, 196, 196);
    public static final XColor gray78 =  new XColor("gray78", 199, 199, 199);
    public static final XColor gray79 =  new XColor("gray79", 201, 201, 201);
    public static final XColor gray8 =  new XColor("gray8", 20, 20, 20);
    public static final XColor gray80 =  new XColor("gray80", 204, 204, 204);
    public static final XColor gray81 =  new XColor("gray81", 207, 207, 207);
    public static final XColor gray82 =  new XColor("gray82", 209, 209, 209);
    public static final XColor gray83 =  new XColor("gray83", 212, 212, 212);
    public static final XColor gray84 =  new XColor("gray84", 214, 214, 214);
    public static final XColor gray85 =  new XColor("gray85", 217, 217, 217);
    public static final XColor gray86 =  new XColor("gray86", 219, 219, 219);
    public static final XColor gray87 =  new XColor("gray87", 222, 222, 222);
    public static final XColor gray88 =  new XColor("gray88", 224, 224, 224);
    public static final XColor gray89 =  new XColor("gray89", 227, 227, 227);
    public static final XColor gray9 =  new XColor("gray9", 23, 23, 23);
    public static final XColor gray90 =  new XColor("gray90", 229, 229, 229);
    public static final XColor gray91 =  new XColor("gray91", 232, 232, 232);
    public static final XColor gray92 =  new XColor("gray92", 235, 235, 235);
    public static final XColor gray93 =  new XColor("gray93", 237, 237, 237);
    public static final XColor gray94 =  new XColor("gray94", 240, 240, 240);
    public static final XColor gray95 =  new XColor("gray95", 242, 242, 242);
    public static final XColor gray96 =  new XColor("gray96", 245, 245, 245);
    public static final XColor gray97 =  new XColor("gray97", 247, 247, 247);
    public static final XColor gray98 =  new XColor("gray98", 250, 250, 250);
    public static final XColor gray99 =  new XColor("gray99", 252, 252, 252);
    public static final XColor green =  new XColor("green", 0, 255, 0);
    public static final XColor green_yellow = new XColor("green yellow", 173, 255, 47);
    public static final XColor green1 =  new XColor("green1", 0, 255, 0);
    public static final XColor green2 =  new XColor("green2", 0, 238, 0);
    public static final XColor green3 =  new XColor("green3", 0, 205, 0);
    public static final XColor green4 =  new XColor("green4", 0, 139, 0);
    public static final XColor greenyellow = new XColor("greenyellow", 173, 255, 47);
    public static final XColor grey =  new XColor("grey", 190, 190, 190);
    public static final XColor grey0 =  new XColor("grey0", 0, 0, 0);
    public static final XColor grey1 =  new XColor("grey1", 3, 3, 3);
    public static final XColor grey10 =  new XColor("grey10", 26, 26, 26);
    public static final XColor grey100 =  new XColor("grey100", 255, 255, 255);
    public static final XColor grey11 =  new XColor("grey11", 28, 28, 28);
    public static final XColor grey12 =  new XColor("grey12", 31, 31, 31);
    public static final XColor grey13 =  new XColor("grey13", 33, 33, 33);
    public static final XColor grey14 =  new XColor("grey14", 36, 36, 36);
    public static final XColor grey15 =  new XColor("grey15", 38, 38, 38);
    public static final XColor grey16 =  new XColor("grey16", 41, 41, 41);
    public static final XColor grey17 =  new XColor("grey17", 43, 43, 43);
    public static final XColor grey18 =  new XColor("grey18", 46, 46, 46);
    public static final XColor grey19 =  new XColor("grey19", 48, 48, 48);
    public static final XColor grey2 =  new XColor("grey2", 5, 5, 5);
    public static final XColor grey20 =  new XColor("grey20", 51, 51, 51);
    public static final XColor grey21 =  new XColor("grey21", 54, 54, 54);
    public static final XColor grey22 =  new XColor("grey22", 56, 56, 56);
    public static final XColor grey23 =  new XColor("grey23", 59, 59, 59);
    public static final XColor grey24 =  new XColor("grey24", 61, 61, 61);
    public static final XColor grey25 =  new XColor("grey25", 64, 64, 64);
    public static final XColor grey26 =  new XColor("grey26", 66, 66, 66);
    public static final XColor grey27 =  new XColor("grey27", 69, 69, 69);
    public static final XColor grey28 =  new XColor("grey28", 71, 71, 71);
    public static final XColor grey29 =  new XColor("grey29", 74, 74, 74);
    public static final XColor grey3 =  new XColor("grey3", 8, 8, 8);
    public static final XColor grey30 =  new XColor("grey30", 77, 77, 77);
    public static final XColor grey31 =  new XColor("grey31", 79, 79, 79);
    public static final XColor grey32 =  new XColor("grey32", 82, 82, 82);
    public static final XColor grey33 =  new XColor("grey33", 84, 84, 84);
    public static final XColor grey34 =  new XColor("grey34", 87, 87, 87);
    public static final XColor grey35 =  new XColor("grey35", 89, 89, 89);
    public static final XColor grey36 =  new XColor("grey36", 92, 92, 92);
    public static final XColor grey37 =  new XColor("grey37", 94, 94, 94);
    public static final XColor grey38 =  new XColor("grey38", 97, 97, 97);
    public static final XColor grey39 =  new XColor("grey39", 99, 99, 99);
    public static final XColor grey4 =  new XColor("grey4", 10, 10, 10);
    public static final XColor grey40 =  new XColor("grey40", 102, 102, 102);
    public static final XColor grey41 =  new XColor("grey41", 105, 105, 105);
    public static final XColor grey42 =  new XColor("grey42", 107, 107, 107);
    public static final XColor grey43 =  new XColor("grey43", 110, 110, 110);
    public static final XColor grey44 =  new XColor("grey44", 112, 112, 112);
    public static final XColor grey45 =  new XColor("grey45", 115, 115, 115);
    public static final XColor grey46 =  new XColor("grey46", 117, 117, 117);
    public static final XColor grey47 =  new XColor("grey47", 120, 120, 120);
    public static final XColor grey48 =  new XColor("grey48", 122, 122, 122);
    public static final XColor grey49 =  new XColor("grey49", 125, 125, 125);
    public static final XColor grey5 =  new XColor("grey5", 13, 13, 13);
    public static final XColor grey50 =  new XColor("grey50", 127, 127, 127);
    public static final XColor grey51 =  new XColor("grey51", 130, 130, 130);
    public static final XColor grey52 =  new XColor("grey52", 133, 133, 133);
    public static final XColor grey53 =  new XColor("grey53", 135, 135, 135);
    public static final XColor grey54 =  new XColor("grey54", 138, 138, 138);
    public static final XColor grey55 =  new XColor("grey55", 140, 140, 140);
    public static final XColor grey56 =  new XColor("grey56", 143, 143, 143);
    public static final XColor grey57 =  new XColor("grey57", 145, 145, 145);
    public static final XColor grey58 =  new XColor("grey58", 148, 148, 148);
    public static final XColor grey59 =  new XColor("grey59", 150, 150, 150);
    public static final XColor grey6 =  new XColor("grey6", 15, 15, 15);
    public static final XColor grey60 =  new XColor("grey60", 153, 153, 153);
    public static final XColor grey61 =  new XColor("grey61", 156, 156, 156);
    public static final XColor grey62 =  new XColor("grey62", 158, 158, 158);
    public static final XColor grey63 =  new XColor("grey63", 161, 161, 161);
    public static final XColor grey64 =  new XColor("grey64", 163, 163, 163);
    public static final XColor grey65 =  new XColor("grey65", 166, 166, 166);
    public static final XColor grey66 =  new XColor("grey66", 168, 168, 168);
    public static final XColor grey67 =  new XColor("grey67", 171, 171, 171);
    public static final XColor grey68 =  new XColor("grey68", 173, 173, 173);
    public static final XColor grey69 =  new XColor("grey69", 176, 176, 176);
    public static final XColor grey7 =  new XColor("grey7", 18, 18, 18);
    public static final XColor grey70 =  new XColor("grey70", 179, 179, 179);
    public static final XColor grey71 =  new XColor("grey71", 181, 181, 181);
    public static final XColor grey72 =  new XColor("grey72", 184, 184, 184);
    public static final XColor grey73 =  new XColor("grey73", 186, 186, 186);
    public static final XColor grey74 =  new XColor("grey74", 189, 189, 189);
    public static final XColor grey75 =  new XColor("grey75", 191, 191, 191);
    public static final XColor grey76 =  new XColor("grey76", 194, 194, 194);
    public static final XColor grey77 =  new XColor("grey77", 196, 196, 196);
    public static final XColor grey78 =  new XColor("grey78", 199, 199, 199);
    public static final XColor grey79 =  new XColor("grey79", 201, 201, 201);
    public static final XColor grey8 =  new XColor("grey8", 20, 20, 20);
    public static final XColor grey80 =  new XColor("grey80", 204, 204, 204);
    public static final XColor grey81 =  new XColor("grey81", 207, 207, 207);
    public static final XColor grey82 =  new XColor("grey82", 209, 209, 209);
    public static final XColor grey83 =  new XColor("grey83", 212, 212, 212);
    public static final XColor grey84 =  new XColor("grey84", 214, 214, 214);
    public static final XColor grey85 =  new XColor("grey85", 217, 217, 217);
    public static final XColor grey86 =  new XColor("grey86", 219, 219, 219);
    public static final XColor grey87 =  new XColor("grey87", 222, 222, 222);
    public static final XColor grey88 =  new XColor("grey88", 224, 224, 224);
    public static final XColor grey89 =  new XColor("grey89", 227, 227, 227);
    public static final XColor grey9 =  new XColor("grey9", 23, 23, 23);
    public static final XColor grey90 =  new XColor("grey90", 229, 229, 229);
    public static final XColor grey91 =  new XColor("grey91", 232, 232, 232);
    public static final XColor grey92 =  new XColor("grey92", 235, 235, 235);
    public static final XColor grey93 =  new XColor("grey93", 237, 237, 237);
    public static final XColor grey94 =  new XColor("grey94", 240, 240, 240);
    public static final XColor grey95 =  new XColor("grey95", 242, 242, 242);
    public static final XColor grey96 =  new XColor("grey96", 245, 245, 245);
    public static final XColor grey97 =  new XColor("grey97", 247, 247, 247);
    public static final XColor grey98 =  new XColor("grey98", 250, 250, 250);
    public static final XColor grey99 =  new XColor("grey99", 252, 252, 252);
    public static final XColor honeydew =  new XColor("honeydew", 240, 255, 240);
    public static final XColor honeydew1 =  new XColor("honeydew1", 240, 255, 240);
    public static final XColor honeydew2 =  new XColor("honeydew2", 224, 238, 224);
    public static final XColor honeydew3 =  new XColor("honeydew3", 193, 205, 193);
    public static final XColor honeydew4 =  new XColor("honeydew4", 131, 139, 131);
    public static final XColor hot_pink =  new XColor("hot pink", 255, 105, 180);
    public static final XColor hotpink =  new XColor("hotpink", 255, 105, 180);
    public static final XColor hotpink1 =  new XColor("hotpink1", 255, 110, 180);
    public static final XColor hotpink2 =  new XColor("hotpink2", 238, 106, 167);
    public static final XColor hotpink3 =  new XColor("hotpink3", 205, 96, 144);
    public static final XColor hotpink4 =  new XColor("hotpink4", 139, 58, 98);
    public static final XColor indian_red =  new XColor("indian red", 205, 92, 92);
    public static final XColor indianred =  new XColor("indianred", 205, 92, 92);
    public static final XColor indianred1 =  new XColor("indianred1", 255, 106, 106);
    public static final XColor indianred2 =  new XColor("indianred2", 238, 99, 99);
    public static final XColor indianred3 =  new XColor("indianred3", 205, 85, 85);
    public static final XColor indianred4 =  new XColor("indianred4", 139, 58, 58);
    public static final XColor ivory =  new XColor("ivory", 255, 255, 240);
    public static final XColor ivory1 =  new XColor("ivory1", 255, 255, 240);
    public static final XColor ivory2 =  new XColor("ivory2", 238, 238, 224);
    public static final XColor ivory3 =  new XColor("ivory3", 205, 205, 193);
    public static final XColor ivory4 =  new XColor("ivory4", 139, 139, 131);
    public static final XColor khaki =  new XColor("khaki", 240, 230, 140);
    public static final XColor khaki1 =  new XColor("khaki1", 255, 246, 143);
    public static final XColor khaki2 =  new XColor("khaki2", 238, 230, 133);
    public static final XColor khaki3 =  new XColor("khaki3", 205, 198, 115);
    public static final XColor khaki4 =  new XColor("khaki4", 139, 134, 78);
    public static final XColor lavender =  new XColor("lavender", 230, 230, 250);
    public static final XColor lavender_blush = new XColor("lavender blush", 255, 240, 245);
    public static final XColor lavenderblush = new XColor("lavenderblush", 255, 240, 245);
    public static final XColor lavenderblush1 = new XColor("lavenderblush1", 255, 240, 245);
    public static final XColor lavenderblush2 = new XColor("lavenderblush2", 238, 224, 229);
    public static final XColor lavenderblush3 = new XColor("lavenderblush3", 205, 193, 197);
    public static final XColor lavenderblush4 = new XColor("lavenderblush4", 139, 131, 134);
    public static final XColor lawn_green = new XColor("lawn green", 124, 252, 0);
    public static final XColor lawngreen = new XColor("lawngreen", 124, 252, 0);
    public static final XColor lemon_chiffon = new XColor("lemon chiffon", 255, 250, 205);
    public static final XColor lemonchiffon  = new XColor("lemonchiffon", 255, 250, 205);
    public static final XColor lemonchiffon1 = new XColor("lemonchiffon1", 255, 250, 205);
    public static final XColor lemonchiffon2 = new XColor("lemonchiffon2", 238, 233, 191);
    public static final XColor lemonchiffon3 = new XColor("lemonchiffon3", 205, 201, 165);
    public static final XColor lemonchiffon4 = new XColor("lemonchiffon4", 139, 137, 112);
    public static final XColor light_blue =  new XColor("light blue", 173, 216, 230);
    public static final XColor light_coral = new XColor("light coral", 240, 128, 128);
    public static final XColor light_cyan =  new XColor("light cyan", 224, 255, 255);
    public static final XColor light_goldenrod = new XColor("light goldenrod", 238, 221, 130);
    public static final XColor light_goldenrod_yellow = new XColor("light goldenrod yellow", 250, 250, 210);
    public static final XColor light_gray =  new XColor("light gray", 211, 211, 211);
    public static final XColor light_green = new XColor("light green", 144, 238, 144);
    public static final XColor light_grey =  new XColor("light grey", 211, 211, 211);
    public static final XColor light_pink =  new XColor("light pink", 255, 182, 193);
    public static final XColor light_salmon  = new XColor("light salmon", 255, 160, 122);
    public static final XColor light_sea_green = new XColor("light sea green", 32, 178, 170);
    public static final XColor light_sky_blue = new XColor("light sky blue", 135, 206, 250);
    public static final XColor light_slate_blue = new XColor("light slate blue", 132, 112, 255);
    public static final XColor light_slate_gray = new XColor("light slate gray", 119, 136, 153);
    public static final XColor light_slate_grey = new XColor("light slate grey", 119, 136, 153);
    public static final XColor light_steel_blue = new XColor("light steel blue", 176, 196, 222);
    public static final XColor light_yellow = new XColor("light yellow", 255, 255, 224);
    public static final XColor lightblue =  new XColor("lightblue", 173, 216, 230);
    public static final XColor lightblue1 =  new XColor("lightblue1", 191, 239, 255);
    public static final XColor lightblue2 =  new XColor("lightblue2", 178, 223, 238);
    public static final XColor lightblue3 =  new XColor("lightblue3", 154, 192, 205);
    public static final XColor lightblue4 =  new XColor("lightblue4", 104, 131, 139);
    public static final XColor lightcoral =  new XColor("lightcoral", 240, 128, 128);
    public static final XColor lightcyan =  new XColor("lightcyan", 224, 255, 255);
    public static final XColor lightcyan1 =  new XColor("lightcyan1", 224, 255, 255);
    public static final XColor lightcyan2 =  new XColor("lightcyan2", 209, 238, 238);
    public static final XColor lightcyan3 =  new XColor("lightcyan3", 180, 205, 205);
    public static final XColor lightcyan4 =  new XColor("lightcyan4", 122, 139, 139);
    public static final XColor lightgoldenrod = new XColor("lightgoldenrod", 238, 221, 130);
    public static final XColor lightgoldenrod1 = new XColor("lightgoldenrod1", 255, 236, 139);
    public static final XColor lightgoldenrod2 = new XColor("lightgoldenrod2", 238, 220, 130);
    public static final XColor lightgoldenrod3 = new XColor("lightgoldenrod3", 205, 190, 112);
    public static final XColor lightgoldenrod4 = new XColor("lightgoldenrod4", 139, 129, 76);
    public static final XColor lightgoldenrodyellow = new XColor("lightgoldenrodyellow", 250, 250, 210);
    public static final XColor lightgray =  new XColor("lightgray", 211, 211, 211);
    public static final XColor lightgreen =  new XColor("lightgreen", 144, 238, 144);
    public static final XColor lightgrey =  new XColor("lightgrey", 211, 211, 211);
    public static final XColor lightpink =  new XColor("lightpink", 255, 182, 193);
    public static final XColor lightpink1 =  new XColor("lightpink1", 255, 174, 185);
    public static final XColor lightpink2 =  new XColor("lightpink2", 238, 162, 173);
    public static final XColor lightpink3 =  new XColor("lightpink3", 205, 140, 149);
    public static final XColor lightpink4 =  new XColor("lightpink4", 139, 95, 101);
    public static final XColor lightsalmon = new XColor("lightsalmon", 255, 160, 122);
    public static final XColor lightsalmon1  = new XColor("lightsalmon1", 255, 160, 122);
    public static final XColor lightsalmon2  = new XColor("lightsalmon2", 238, 149, 114);
    public static final XColor lightsalmon3  = new XColor("lightsalmon3", 205, 129, 98);
    public static final XColor lightsalmon4  = new XColor("lightsalmon4", 139, 87, 66);
    public static final XColor lightseagreen = new XColor("lightseagreen", 32, 178, 170);
    public static final XColor lightskyblue  = new XColor("lightskyblue", 135, 206, 250);
    public static final XColor lightskyblue1 = new XColor("lightskyblue1", 176, 226, 255);
    public static final XColor lightskyblue2 =new XColor("lightskyblue2", 164, 211, 238);
    public static final XColor lightskyblue3 =new XColor("lightskyblue3", 141, 182, 205);
    public static final XColor lightskyblue4 =new XColor("lightskyblue4", 96, 123, 139);
    public static final XColor lightslateblue= new XColor("lightslateblue", 132, 112, 255);
    public static final XColor lightslategray= new XColor("lightslategray", 119, 136, 153);
    public static final XColor lightslategrey= new XColor("lightslategrey", 119, 136, 153);
    public static final XColor lightsteelblue= new XColor("lightsteelblue", 176, 196, 222);
    public static final XColor lightsteelblue1= new XColor("lightsteelblue1", 202, 225, 255);
    public static final XColor lightsteelblue2= new XColor("lightsteelblue2", 188, 210, 238);
    public static final XColor lightsteelblue3= new XColor("lightsteelblue3", 162, 181, 205);
    public static final XColor lightsteelblue4= new XColor("lightsteelblue4", 110, 123, 139);
    public static final XColor lightyellow = new XColor("lightyellow", 255, 255, 224);
    public static final XColor lightyellow1 = new XColor("lightyellow1", 255, 255, 224);
    public static final XColor lightyellow2 = new XColor("lightyellow2", 238, 238, 209);
    public static final XColor lightyellow3 = new XColor("lightyellow3", 205, 205, 180);
    public static final XColor lightyellow4 = new XColor("lightyellow4", 139, 139, 122);
    public static final XColor lime_green =  new XColor("lime green", 50, 205, 50);
    public static final XColor limegreen =  new XColor("limegreen", 50, 205, 50);
    public static final XColor linen =  new XColor("linen", 250, 240, 230);
    public static final XColor magenta =  new XColor("magenta", 255, 0, 255);
    public static final XColor magenta1 =  new XColor("magenta1", 255, 0, 255);
    public static final XColor magenta2 =  new XColor("magenta2", 238, 0, 238);
    public static final XColor magenta3 =  new XColor("magenta3", 205, 0, 205);
    public static final XColor magenta4 =  new XColor("magenta4", 139, 0, 139);
    public static final XColor maroon =  new XColor("maroon", 176, 48, 96);
    public static final XColor maroon1 =  new XColor("maroon1", 255, 52, 179);
    public static final XColor maroon2 =  new XColor("maroon2", 238, 48, 167);
    public static final XColor maroon3 =  new XColor("maroon3", 205, 41, 144);
    public static final XColor maroon4 =  new XColor("maroon4", 139, 28, 98);
    public static final XColor medium_aquamarine = new XColor("medium aquamarine", 102, 205, 170);
    public static final XColor medium_blue = new XColor("medium blue", 0, 0, 205);
    public static final XColor medium_orchid =new XColor("medium orchid", 186, 85, 211);
    public static final XColor medium_purple =new XColor("medium purple", 147, 112, 219);
    public static final XColor medium_sea_green =new XColor("medium sea green", 60, 179, 113);
    public static final XColor medium_slate_blue = new XColor("medium slate blue", 123, 104, 238);
    public static final XColor medium_spring_green= new XColor("medium spring green", 0, 250, 154);
    public static final XColor medium_turquoise = new XColor("medium turquoise", 72, 209, 204);
    public static final XColor medium_violet_red = new XColor("medium violet red", 199, 21, 133);
    public static final XColor mediumaquamarine = new XColor("mediumaquamarine", 102, 205, 170);
    public static final XColor mediumblue =  new XColor("mediumblue", 0, 0, 205);
    public static final XColor mediumorchid = new XColor("mediumorchid", 186, 85, 211);
    public static final XColor mediumorchid1 =new XColor("mediumorchid1", 224, 102, 255);
    public static final XColor mediumorchid2 =new XColor("mediumorchid2", 209, 95, 238);
    public static final XColor mediumorchid3 =new XColor("mediumorchid3", 180, 82, 205);
    public static final XColor mediumorchid4 =new XColor("mediumorchid4", 122, 55, 139);
    public static final XColor mediumpurple  =new XColor("mediumpurple", 147, 112, 219);
    public static final XColor mediumpurple1 =new XColor("mediumpurple1", 171, 130, 255);
    public static final XColor mediumpurple2 =new XColor("mediumpurple2", 159, 121, 238);
    public static final XColor mediumpurple3 =new XColor("mediumpurple3", 137, 104, 205);
    public static final XColor mediumpurple4 =new XColor("mediumpurple4", 93, 71, 139);
    public static final XColor mediumseagreen =new XColor("mediumseagreen", 60, 179, 113);
    public static final XColor mediumslateblue =new XColor("mediumslateblue", 123, 104, 238);
    public static final XColor mediumspringgreen =new XColor("mediumspringgreen", 0, 250, 154);
    public static final XColor mediumturquoise =new XColor("mediumturquoise", 72, 209, 204);
    public static final XColor mediumvioletred= new XColor("mediumvioletred", 199, 21, 133);
    public static final XColor midnight_blue =new XColor("midnight blue", 25, 25, 112);
    public static final XColor midnightblue  =new XColor("midnightblue", 25, 25, 112);
    public static final XColor mint_cream =  new XColor("mint cream", 245, 255, 250);
    public static final XColor mintcream =  new XColor("mintcream", 245, 255, 250);
    public static final XColor misty_rose =  new XColor("misty rose", 255, 228, 225);
    public static final XColor mistyrose =  new XColor("mistyrose", 255, 228, 225);
    public static final XColor mistyrose1 =  new XColor("mistyrose1", 255, 228, 225);
    public static final XColor mistyrose2 =  new XColor("mistyrose2", 238, 213, 210);
    public static final XColor mistyrose3 =  new XColor("mistyrose3", 205, 183, 181);
    public static final XColor mistyrose4 =  new XColor("mistyrose4", 139, 125, 123);
    public static final XColor moccasin =  new XColor("moccasin", 255, 228, 181);
    public static final XColor navajo_white = new XColor("navajo white", 255, 222, 173);
    public static final XColor navajowhite = new XColor("navajowhite", 255, 222, 173);
    public static final XColor navajowhite1  =new XColor("navajowhite1", 255, 222, 173);
    public static final XColor navajowhite2  =new XColor("navajowhite2", 238, 207, 161);
    public static final XColor navajowhite3  =new XColor("navajowhite3", 205, 179, 139);
    public static final XColor navajowhite4  =new XColor("navajowhite4", 139, 121, 94);
    public static final XColor navy =  new XColor("navy", 0, 0, 128);
    public static final XColor navy_blue =  new XColor("navy blue", 0, 0, 128);
    public static final XColor navyblue =  new XColor("navyblue", 0, 0, 128);
    public static final XColor old_lace =  new XColor("old lace", 253, 245, 230);
    public static final XColor oldlace =  new XColor("oldlace", 253, 245, 230);
    public static final XColor olive_drab =  new XColor("olive drab", 107, 142, 35);
    public static final XColor olivedrab =  new XColor("olivedrab", 107, 142, 35);
    public static final XColor olivedrab1 =  new XColor("olivedrab1", 192, 255, 62);
    public static final XColor olivedrab2 =  new XColor("olivedrab2", 179, 238, 58);
    public static final XColor olivedrab3 =  new XColor("olivedrab3", 154, 205, 50);
    public static final XColor olivedrab4 =  new XColor("olivedrab4", 105, 139, 34);
    public static final XColor orange =  new XColor("orange", 255, 165, 0);
    public static final XColor orange_red =  new XColor("orange red", 255, 69, 0);
    public static final XColor orange1 =  new XColor("orange1", 255, 165, 0);
    public static final XColor orange2 =  new XColor("orange2", 238, 154, 0);
    public static final XColor orange3 =  new XColor("orange3", 205, 133, 0);
    public static final XColor orange4 =  new XColor("orange4", 139, 90, 0);
    public static final XColor orangered =  new XColor("orangered", 255, 69, 0);
    public static final XColor orangered1 =  new XColor("orangered1", 255, 69, 0);
    public static final XColor orangered2 =  new XColor("orangered2", 238, 64, 0);
    public static final XColor orangered3 =  new XColor("orangered3", 205, 55, 0);
    public static final XColor orangered4 =  new XColor("orangered4", 139, 37, 0);
    public static final XColor orchid =  new XColor("orchid", 218, 112, 214);
    public static final XColor orchid1 =  new XColor("orchid1", 255, 131, 250);
    public static final XColor orchid2 =  new XColor("orchid2", 238, 122, 233);
    public static final XColor orchid3 =  new XColor("orchid3", 205, 105, 201);
    public static final XColor orchid4 =  new XColor("orchid4", 139, 71, 137);
    public static final XColor pale_goldenrod= new XColor("pale goldenrod", 238, 232, 170);
    public static final XColor pale_green =  new XColor("pale green", 152, 251, 152);
    public static final XColor pale_turquoise= new XColor("pale turquoise", 175, 238, 238);
    public static final XColor pale_violet_red= new XColor("pale violet red", 219, 112, 147);
    public static final XColor palegoldenrod =new XColor("palegoldenrod", 238, 232, 170);
    public static final XColor palegreen =  new XColor("palegreen", 152, 251, 152);
    public static final XColor palegreen1 =  new XColor("palegreen1", 154, 255, 154);
    public static final XColor palegreen2 =  new XColor("palegreen2", 144, 238, 144);
    public static final XColor palegreen3 =  new XColor("palegreen3", 124, 205, 124);
    public static final XColor palegreen4 =  new XColor("palegreen4", 84, 139, 84);
    public static final XColor paleturquoise= new XColor("paleturquoise", 175, 238, 238);
    public static final XColor paleturquoise1= new XColor("paleturquoise1", 187, 255, 255);
    public static final XColor paleturquoise2= new XColor("paleturquoise2", 174, 238, 238);
    public static final XColor paleturquoise3= new XColor("paleturquoise3", 150, 205, 205);
    public static final XColor paleturquoise4= new XColor("paleturquoise4", 102, 139, 139);
    public static final XColor palevioletred= new XColor("palevioletred", 219, 112, 147);
    public static final XColor palevioletred1= new XColor("palevioletred1", 255, 130, 171);
    public static final XColor palevioletred2= new XColor("palevioletred2", 238, 121, 159);
    public static final XColor palevioletred3= new XColor("palevioletred3", 205, 104, 137);
    public static final XColor palevioletred4= new XColor("palevioletred4", 139, 71, 93);
    public static final XColor papaya_whip = new XColor("papaya whip", 255, 239, 213);
    public static final XColor papayawhip =  new XColor("papayawhip", 255, 239, 213);
    public static final XColor peach_puff =  new XColor("peach puff", 255, 218, 185);
    public static final XColor peachpuff =  new XColor("peachpuff", 255, 218, 185);
    public static final XColor peachpuff1 =  new XColor("peachpuff1", 255, 218, 185);
    public static final XColor peachpuff2 =  new XColor("peachpuff2", 238, 203, 173);
    public static final XColor peachpuff3 =  new XColor("peachpuff3", 205, 175, 149);
    public static final XColor peachpuff4 =  new XColor("peachpuff4", 139, 119, 101);
    public static final XColor peru =  new XColor("peru", 205, 133, 63);
    public static final XColor pink =  new XColor("pink", 255, 192, 203);
    public static final XColor pink1 =  new XColor("pink1", 255, 181, 197);
    public static final XColor pink2 =  new XColor("pink2", 238, 169, 184);
    public static final XColor pink3 =  new XColor("pink3", 205, 145, 158);
    public static final XColor pink4 =  new XColor("pink4", 139, 99, 108);
    public static final XColor plum =  new XColor("plum", 221, 160, 221);
    public static final XColor plum1 =  new XColor("plum1", 255, 187, 255);
    public static final XColor plum2 =  new XColor("plum2", 238, 174, 238);
    public static final XColor plum3 =  new XColor("plum3", 205, 150, 205);
    public static final XColor plum4 =  new XColor("plum4", 139, 102, 139);
    public static final XColor powder_blue = new XColor("powder blue", 176, 224, 230);
    public static final XColor powderblue =  new XColor("powderblue", 176, 224, 230);
    public static final XColor purple =  new XColor("purple", 160, 32, 240);
    public static final XColor purple1 =  new XColor("purple1", 155, 48, 255);
    public static final XColor purple2 =  new XColor("purple2", 145, 44, 238);
    public static final XColor purple3 =  new XColor("purple3", 125, 38, 205);
    public static final XColor purple4 =  new XColor("purple4", 85, 26, 139);
    public static final XColor red =  new XColor("red", 255, 0, 0);
    public static final XColor red1 =  new XColor("red1", 255, 0, 0);
    public static final XColor red2 =  new XColor("red2", 238, 0, 0);
    public static final XColor red3 =  new XColor("red3", 205, 0, 0);
    public static final XColor red4 =  new XColor("red4", 139, 0, 0);
    public static final XColor rosy_brown =  new XColor("rosy brown", 188, 143, 143);
    public static final XColor rosybrown =  new XColor("rosybrown", 188, 143, 143);
    public static final XColor rosybrown1 =  new XColor("rosybrown1", 255, 193, 193);
    public static final XColor rosybrown2 =  new XColor("rosybrown2", 238, 180, 180);
    public static final XColor rosybrown3 =  new XColor("rosybrown3", 205, 155, 155);
    public static final XColor rosybrown4 =  new XColor("rosybrown4", 139, 105, 105);
    public static final XColor royal_blue =  new XColor("royal blue", 65, 105, 225);
    public static final XColor royalblue =  new XColor("royalblue", 65, 105, 225);
    public static final XColor royalblue1 =  new XColor("royalblue1", 72, 118, 255);
    public static final XColor royalblue2 =  new XColor("royalblue2", 67, 110, 238);
    public static final XColor royalblue3 =  new XColor("royalblue3", 58, 95, 205);
    public static final XColor royalblue4 =  new XColor("royalblue4", 39, 64, 139);
    public static final XColor saddle_brown = new XColor("saddle brown", 139, 69, 19);
    public static final XColor saddlebrown = new XColor("saddlebrown", 139, 69, 19);
    public static final XColor salmon =  new XColor("salmon", 250, 128, 114);
    public static final XColor salmon1 =  new XColor("salmon1", 255, 140, 105);
    public static final XColor salmon2 =  new XColor("salmon2", 238, 130, 98);
    public static final XColor salmon3 =  new XColor("salmon3", 205, 112, 84);
    public static final XColor salmon4 =  new XColor("salmon4", 139, 76, 57);
    public static final XColor sandy_brown = new XColor("sandy brown", 244, 164, 96);
    public static final XColor sandybrown =  new XColor("sandybrown", 244, 164, 96);
    public static final XColor sea_green =  new XColor("sea green", 46, 139, 87);
    public static final XColor seagreen =  new XColor("seagreen", 46, 139, 87);
    public static final XColor seagreen1 =  new XColor("seagreen1", 84, 255, 159);
    public static final XColor seagreen2 =  new XColor("seagreen2", 78, 238, 148);
    public static final XColor seagreen3 =  new XColor("seagreen3", 67, 205, 128);
    public static final XColor seagreen4 =  new XColor("seagreen4", 46, 139, 87);
    public static final XColor seashell =  new XColor("seashell", 255, 245, 238);
    public static final XColor seashell1 =  new XColor("seashell1", 255, 245, 238);
    public static final XColor seashell2 =  new XColor("seashell2", 238, 229, 222);
    public static final XColor seashell3 =  new XColor("seashell3", 205, 197, 191);
    public static final XColor seashell4 =  new XColor("seashell4", 139, 134, 130);
    public static final XColor sienna =  new XColor("sienna", 160, 82, 45);
    public static final XColor sienna1 =  new XColor("sienna1", 255, 130, 71);
    public static final XColor sienna2 =  new XColor("sienna2", 238, 121, 66);
    public static final XColor sienna3 =  new XColor("sienna3", 205, 104, 57);
    public static final XColor sienna4 =  new XColor("sienna4", 139, 71, 38);
    public static final XColor sky_blue =  new XColor("sky blue", 135, 206, 235);
    public static final XColor skyblue =  new XColor("skyblue", 135, 206, 235);
    public static final XColor skyblue1 =  new XColor("skyblue1", 135, 206, 255);
    public static final XColor skyblue2 =  new XColor("skyblue2", 126, 192, 238);
    public static final XColor skyblue3 =  new XColor("skyblue3", 108, 166, 205);
    public static final XColor skyblue4 =  new XColor("skyblue4", 74, 112, 139);
    public static final XColor slate_blue =  new XColor("slate blue", 106, 90, 205);
    public static final XColor slate_gray =  new XColor("slate gray", 112, 128, 144);
    public static final XColor slate_grey =  new XColor("slate grey", 112, 128, 144);
    public static final XColor slateblue =  new XColor("slateblue", 106, 90, 205);
    public static final XColor slateblue1 =  new XColor("slateblue1", 131, 111, 255);
    public static final XColor slateblue2 =  new XColor("slateblue2", 122, 103, 238);
    public static final XColor slateblue3 =  new XColor("slateblue3", 105, 89, 205);
    public static final XColor slateblue4 =  new XColor("slateblue4", 71, 60, 139);
    public static final XColor slategray =  new XColor("slategray", 112, 128, 144);
    public static final XColor slategray1 =  new XColor("slategray1", 198, 226, 255);
    public static final XColor slategray2 =  new XColor("slategray2", 185, 211, 238);
    public static final XColor slategray3 =  new XColor("slategray3", 159, 182, 205);
    public static final XColor slategray4 =  new XColor("slategray4", 108, 123, 139);
    public static final XColor slategrey =  new XColor("slategrey", 112, 128, 144);
    public static final XColor snow =  new XColor("snow", 255, 250, 250);
    public static final XColor snow1 =  new XColor("snow1", 255, 250, 250);
    public static final XColor snow2 =  new XColor("snow2", 238, 233, 233);
    public static final XColor snow3 =  new XColor("snow3", 205, 201, 201);
    public static final XColor snow4 =  new XColor("snow4", 139, 137, 137);
    public static final XColor spring_green = new XColor("spring green", 0, 255, 127);
    public static final XColor springgreen = new XColor("springgreen", 0, 255, 127);
    public static final XColor springgreen1 = new XColor("springgreen1", 0, 255, 127);
    public static final XColor springgreen2 = new XColor("springgreen2", 0, 238, 118);
    public static final XColor springgreen3 = new XColor("springgreen3", 0, 205, 102);
    public static final XColor springgreen4 = new XColor("springgreen4", 0, 139, 69);
    public static final XColor steel_blue =  new XColor("steel blue", 70, 130, 180);
    public static final XColor steelblue =  new XColor("steelblue", 70, 130, 180);
    public static final XColor steelblue1 =  new XColor("steelblue1", 99, 184, 255);
    public static final XColor steelblue2 =  new XColor("steelblue2", 92, 172, 238);
    public static final XColor steelblue3 =  new XColor("steelblue3", 79, 148, 205);
    public static final XColor steelblue4 =  new XColor("steelblue4", 54, 100, 139);
    public static final XColor tan =  new XColor("tan", 210, 180, 140);
    public static final XColor tan1 =  new XColor("tan1", 255, 165, 79);
    public static final XColor tan2 =  new XColor("tan2", 238, 154, 73);
    public static final XColor tan3 =  new XColor("tan3", 205, 133, 63);
    public static final XColor tan4 =  new XColor("tan4", 139, 90, 43);
    public static final XColor thistle =  new XColor("thistle", 216, 191, 216);
    public static final XColor thistle1 =  new XColor("thistle1", 255, 225, 255);
    public static final XColor thistle2 =  new XColor("thistle2", 238, 210, 238);
    public static final XColor thistle3 =  new XColor("thistle3", 205, 181, 205);
    public static final XColor thistle4 =  new XColor("thistle4", 139, 123, 139);
    public static final XColor tomato =  new XColor("tomato", 255, 99, 71);
    public static final XColor tomato1 =  new XColor("tomato1", 255, 99, 71);
    public static final XColor tomato2 =  new XColor("tomato2", 238, 92, 66);
    public static final XColor tomato3 =  new XColor("tomato3", 205, 79, 57);
    public static final XColor tomato4 =  new XColor("tomato4", 139, 54, 38);
    public static final XColor turquoise =  new XColor("turquoise", 64, 224, 208);
    public static final XColor turquoise1 =  new XColor("turquoise1", 0, 245, 255);
    public static final XColor turquoise2 =  new XColor("turquoise2", 0, 229, 238);
    public static final XColor turquoise3 =  new XColor("turquoise3", 0, 197, 205);
    public static final XColor turquoise4 =  new XColor("turquoise4", 0, 134, 139);
    public static final XColor violet =  new XColor("violet", 238, 130, 238);
    public static final XColor violet_red =  new XColor("violet red", 208, 32, 144);
    public static final XColor violetred =  new XColor("violetred", 208, 32, 144);
    public static final XColor violetred1 =  new XColor("violetred1", 255, 62, 150);
    public static final XColor violetred2 =  new XColor("violetred2", 238, 58, 140);
    public static final XColor violetred3 =  new XColor("violetred3", 205, 50, 120);
    public static final XColor violetred4 =  new XColor("violetred4", 139, 34, 82);
    public static final XColor wheat =  new XColor("wheat", 245, 222, 179);
    public static final XColor wheat1 =  new XColor("wheat1", 255, 231, 186);
    public static final XColor wheat2 =  new XColor("wheat2", 238, 216, 174);
    public static final XColor wheat3 =  new XColor("wheat3", 205, 186, 150);
    public static final XColor wheat4 =  new XColor("wheat4", 139, 126, 102);
    public static final XColor white =  new XColor("white", 255, 255, 255);
    public static final XColor white_smoke = new XColor("white smoke", 245, 245, 245);
    public static final XColor whitesmoke =  new XColor("whitesmoke", 245, 245, 245);
    public static final XColor yellow =  new XColor("yellow", 255, 255, 0);
    public static final XColor yellow_green = new XColor("yellow green", 154, 205, 50);
    public static final XColor yellow1 =  new XColor("yellow1", 255, 255, 0);
    public static final XColor yellow2 =  new XColor("yellow2", 238, 238, 0);
    public static final XColor yellow3 =  new XColor("yellow3", 205, 205, 0);
    public static final XColor yellow4 =  new XColor("yellow4", 139, 139, 0);
    public static final XColor yellowgreen = new XColor("yellowgreen", 154, 205, 5);

    /**
     * Colors accessible via an array.
     */
    private static final XColor[] COLORS =
    {
        alice_blue,
        aliceblue,
        antique_white,
        antiquewhite,
        antiquewhite1,
        antiquewhite2,
        antiquewhite3,
        antiquewhite4,
        aquamarine,
        aquamarine1,
        aquamarine2,
        aquamarine3,
        aquamarine4,
        azure,
        azure1,
        azure2,
        azure3,
        azure4,
        beige,
        bisque,
        bisque1,
        bisque2,
        bisque3,
        bisque4,
        black,
        blanched_almond,
        blanchedalmond,
        blue,
        blue_violet,
        blue1,
        blue2,
        blue3,
        blue4,
        blueviolet,
        brown,
        brown1,
        brown2,
        brown3,
        brown4,
        burlywood ,
        burlywood1 ,
        burlywood2 ,
        burlywood3,
        burlywood4,
        cadet_blue,
        cadetblue,
        cadetblue1,
        cadetblue2,
        cadetblue3,
        cadetblue4,
        chartreuse,
        chartreuse1,
        chartreuse2,
        chartreuse3,
        chartreuse4,
        chocolate,
        chocolate1,
        chocolate2,
        chocolate3,
        chocolate4,
        coral,
        coral1,
        coral2,
        coral3,
        coral4,
        cornflower_blue,
        cornflowerblue,
        cornsilk,
        cornsilk1,
        cornsilk2,
        cornsilk3,
        cornsilk4,
        cyan,
        cyan1,
        cyan2,
        cyan3,
        cyan4,
        dark_blue,
        dark_cyan,
        dark_goldenrod,
        dark_gray,
        dark_green,
        dark_grey,
        dark_khaki,
        dark_magenta,
        dark_olive_green,
        dark_orange,
        dark_orchid,
        dark_red,
        dark_salmon,
        dark_sea_green,
        dark_slate_blue,
        dark_slate_gray,
        dark_slate_grey,
        dark_turquoise,
        dark_violet,
        darkblue,
        darkcyan,
        darkgoldenrod,
        darkgoldenrod1,
        darkgoldenrod2,
        darkgoldenrod3,
        darkgoldenrod4,
        darkgray,
        darkgreen,
        darkgrey,
        darkkhaki,
        darkmagenta,
        darkolivegreen,
        darkolivegreen1,
        darkolivegreen2,
        darkolivegreen3,
        darkolivegreen4,
        darkorange,
        darkorange1,
        darkorange2,
        darkorange3,
        darkorange4,
        darkorchid,
        darkorchid1,
        darkorchid2,
        darkorchid3,
        darkorchid4,
        darkred,
        darksalmon,
        darkseagreen,
        darkseagreen1,
        darkseagreen2,
        darkseagreen3,
        darkseagreen4,
        darkslateblue,
        darkslategray,
        darkslategray1,
        darkslategray2,
        darkslategray3,
        darkslategray4,
        darkslategrey,
        darkturquoise,
        darkviolet,
        deep_pink,
        deep_sky_blue,
        deeppink,
        deeppink1,
        deeppink2,
        deeppink3,
        deeppink4,
        deepskyblue,
        deepskyblue1,
        deepskyblue2,
        deepskyblue3,
        deepskyblue4,
        dim_gray,
        dim_grey,
        dimgray,
        dimgrey,
        dodger_blue,
        dodgerblue,
        dodgerblue1,
        dodgerblue2,
        dodgerblue3,
        dodgerblue4,
        firebrick,
        firebrick1,
        firebrick2,
        firebrick3,
        firebrick4,
        floral_white,
        floralwhite,
        forest_green,
        forestgreen,
        gainsboro,
        ghost_white,
        ghostwhite,
        gold,
        gold1,
        gold2,
        gold3,
        gold4,
        goldenrod,
        goldenrod1,
        goldenrod2,
        goldenrod3,
        goldenrod4,
        gray,
        gray0,
        gray1,
        gray10,
        gray100,
        gray11,
        gray12,
        gray13,
        gray14,
        gray15,
        gray16,
        gray17,
        gray18,
        gray19,
        gray2,
        gray20,
        gray21,
        gray22,
        gray23,
        gray24,
        gray25,
        gray26,
        gray27,
        gray28,
        gray29,
        gray3,
        gray30,
        gray31,
        gray32,
        gray33,
        gray34,
        gray35,
        gray36,
        gray37,
        gray38,
        gray39,
        gray4,
        gray40,
        gray41,
        gray42,
        gray43,
        gray44,
        gray45,
        gray46,
        gray47,
        gray48,
        gray49,
        gray5,
        gray50,
        gray51,
        gray52,
        gray53,
        gray54,
        gray55,
        gray56,
        gray57,
        gray58,
        gray59,
        gray6,
        gray60,
        gray61,
        gray62,
        gray63,
        gray64,
        gray65,
        gray66,
        gray67,
        gray68,
        gray69,
        gray7,
        gray70,
        gray71,
        gray72,
        gray73,
        gray74,
        gray75,
        gray76,
        gray77,
        gray78,
        gray79,
        gray8,
        gray80,
        gray81,
        gray82,
        gray83,
        gray84,
        gray85,
        gray86,
        gray87,
        gray88,
        gray89,
        gray9,
        gray90,
        gray91,
        gray92,
        gray93,
        gray94,
        gray95,
        gray96,
        gray97,
        gray98,
        gray99,
        green,
        green_yellow,
        green1,
        green2,
        green3,
        green4,
        greenyellow,
        grey,
        grey0,
        grey1,
        grey10,
        grey100,
        grey11,
        grey12,
        grey13,
        grey14,
        grey15,
        grey16,
        grey17,
        grey18,
        grey19,
        grey2,
        grey20,
        grey21,
        grey22,
        grey23,
        grey24,
        grey25,
        grey26,
        grey27,
        grey28,
        grey29,
        grey3,
        grey30,
        grey31,
        grey32,
        grey33,
        grey34,
        grey35,
        grey36,
        grey37,
        grey38,
        grey39,
        grey4,
        grey40,
        grey41,
        grey42,
        grey43,
        grey44,
        grey45,
        grey46,
        grey47,
        grey48,
        grey49,
        grey5,
        grey50,
        grey51,
        grey52,
        grey53,
        grey54,
        grey55,
        grey56,
        grey57,
        grey58,
        grey59,
        grey6,
        grey60,
        grey61,
        grey62,
        grey63,
        grey64,
        grey65,
        grey66,
        grey67,
        grey68,
        grey69,
        grey7,
        grey70,
        grey71,
        grey72,
        grey73,
        grey74,
        grey75,
        grey76,
        grey77,
        grey78,
        grey79,
        grey8,
        grey80,
        grey81,
        grey82,
        grey83,
        grey84,
        grey85,
        grey86,
        grey87,
        grey88,
        grey89,
        grey9,
        grey90,
        grey91,
        grey92,
        grey93,
        grey94,
        grey95,
        grey96,
        grey97,
        grey98,
        grey99,
        honeydew,
        honeydew1,
        honeydew2,
        honeydew3,
        honeydew4,
        hot_pink,
        hotpink,
        hotpink1,
        hotpink2,
        hotpink3,
        hotpink4,
        indian_red,
        indianred,
        indianred1,
        indianred2,
        indianred3,
        indianred4,
        ivory,
        ivory1,
        ivory2,
        ivory3,
        ivory4,
        khaki,
        khaki1,
        khaki2,
        khaki3,
        khaki4,
        lavender,
        lavender_blush,
        lavenderblush,
        lavenderblush1,
        lavenderblush2,
        lavenderblush3,
        lavenderblush4,
        lawn_green,
        lawngreen,
        lemon_chiffon,
        lemonchiffon,
        lemonchiffon1,
        lemonchiffon2,
        lemonchiffon3,
        lemonchiffon4,
        light_blue,
        light_coral,
        light_cyan,
        light_goldenrod,
        light_goldenrod,
        light_gray,
        light_green,
        light_grey,
        light_pink,
        light_salmon,
        light_sea_green,
        light_sky_blue,
        light_slate_blue,
        light_slate_gray,
        light_slate_grey,
        light_steel_blue,
        light_yellow,
        lightblue,
        lightblue1,
        lightblue2,
        lightblue3,
        lightblue4,
        lightcoral,
        lightcyan,
        lightcyan1,
        lightcyan2,
        lightcyan3,
        lightcyan4,
        lightgoldenrod,
        lightgoldenrod1,
        lightgoldenrod2,
        lightgoldenrod3,
        lightgoldenrod4,
        lightgoldenrodyellow,
        lightgray,
        lightgreen,
        lightgrey,
        lightpink,
        lightpink1,
        lightpink2,
        lightpink3,
        lightpink4,
        lightsalmon,
        lightsalmon1,
        lightsalmon2,
        lightsalmon3,
        lightsalmon4,
        lightseagreen,
        lightskyblue,
        lightskyblue1,
        lightskyblue2,
        lightskyblue3,
        lightskyblue4,
        lightslateblue,
        lightslategray,
        lightslategrey,
        lightsteelblue,
        lightsteelblue1,
        lightsteelblue2,
        lightsteelblue3,
        lightsteelblue4,
        lightyellow,
        lightyellow1,
        lightyellow2,
        lightyellow3,
        lightyellow4,
        lime_green,
        limegreen,
        linen,
        magenta,
        magenta1,
        magenta2,
        magenta3,
        magenta4,
        maroon,
        maroon1,
        maroon2,
        maroon3,
        maroon4,
        medium_aquamarine,
        medium_blue,
        medium_orchid,
        medium_purple,
        medium_sea_green,
        medium_slate_blue,
        medium_spring_green,
        medium_turquoise,
        medium_violet_red,
        mediumaquamarine,
        mediumblue,
        mediumorchid,
        mediumorchid1,
        mediumorchid2,
        mediumorchid3,
        mediumorchid4,
        mediumpurple,
        mediumpurple1,
        mediumpurple2,
        mediumpurple3,
        mediumpurple4,
        mediumseagreen,
        mediumslateblue,
        mediumspringgreen,
        mediumturquoise,
        mediumvioletred,
        midnight_blue,
        midnightblue,
        mint_cream,
        mintcream,
        misty_rose,
        mistyrose,
        mistyrose1,
        mistyrose2,
        mistyrose3,
        mistyrose4,
        moccasin,
        navajo_white,
        navajowhite,
        navajowhite1,
        navajowhite2,
        navajowhite3,
        navajowhite4,
        navy,
        navy_blue,
        navyblue,
        old_lace,
        oldlace,
        olive_drab,
        olivedrab,
        olivedrab1,
        olivedrab2,
        olivedrab3,
        olivedrab4,
        orange,
        orange_red,
        orange1,
        orange2,
        orange3,
        orange4,
        orangered,
        orangered1,
        orangered2,
        orangered3,
        orangered4,
        orchid,
        orchid1,
        orchid2,
        orchid3,
        orchid4,
        pale_goldenrod,
        pale_green,
        pale_turquoise,
        pale_violet_red,
        palegoldenrod,
        palegreen,
        palegreen1,
        palegreen2,
        palegreen3,
        palegreen4,
        paleturquoise,
        paleturquoise1,
        paleturquoise2,
        paleturquoise3,
        paleturquoise4,
        palevioletred,
        palevioletred1,
        palevioletred2,
        palevioletred3,
        palevioletred4,
        papaya_whip,
        papayawhip,
        peach_puff,
        peachpuff,
        peachpuff1,
        peachpuff2,
        peachpuff3,
        peachpuff4,
        peru,
        pink,
        pink1,
        pink2,
        pink3,
        pink4,
        plum,
        plum1,
        plum2,
        plum3,
        plum4,
        powder_blue,
        powderblue,
        purple,
        purple1,
        purple2,
        purple3,
        purple4,
        red,
        red1,
        red2,
        red3,
        red4,
        rosy_brown,
        rosybrown,
        rosybrown1,
        rosybrown2,
        rosybrown3,
        rosybrown4,
        royal_blue,
        royalblue,
        royalblue1,
        royalblue2,
        royalblue3,
        royalblue4,
        saddle_brown,
        saddlebrown,
        salmon,
        salmon1,
        salmon2,
        salmon3,
        salmon4,
        sandy_brown,
        sandybrown,
        sea_green,
        seagreen,
        seagreen1,
        seagreen2,
        seagreen3,
        seagreen4,
        seashell,
        seashell1,
        seashell2,
        seashell3,
        seashell4,
        sienna,
        sienna1,
        sienna2,
        sienna3,
        sienna4,
        sky_blue,
        skyblue,
        skyblue1,
        skyblue2,
        skyblue3,
        skyblue4,
        slate_blue,
        slate_gray,
        slate_grey,
        slateblue,
        slateblue1,
        slateblue2,
        slateblue3,
        slateblue4,
        slategray,
        slategray1,
        slategray2,
        slategray3,
        slategray4,
        slategrey,
        snow,
        snow1,
        snow2,
        snow3,
        snow4,
        spring_green,
        springgreen,
        springgreen1,
        springgreen2,
        springgreen3,
        springgreen4,
        steel_blue,
        steelblue,
        steelblue1,
        steelblue2,
        steelblue3,
        steelblue4,
        tan,
        tan1,
        tan2,
        tan3,
        tan4,
        thistle,
        thistle1,
        thistle2,
        thistle3,
        thistle4,
        tomato,
        tomato1,
        tomato2,
        tomato3,
        tomato4,
        turquoise,
        turquoise1,
        turquoise2,
        turquoise3,
        turquoise4,
        violet,
        violet_red,
        violetred,
        violetred1,
        violetred2,
        violetred3,
        violetred4,
        wheat,
        wheat1,
        wheat2,
        wheat3,
        wheat4,
        white,
        white_smoke,
        whitesmoke,
        yellow,
        yellow_green,
        yellow1,
        yellow2,
        yellow3,
        yellow4,
        yellowgreen                    
    };
    
    //--------------------------------------------------------------------------
    // Static Public
    //--------------------------------------------------------------------------

    /**
     * Returns the color for the given name.
     * 
     * @param name Name of the color to retrieve.
     * @return Color or null if the name is not found.
     */
    public static Color getColor(String name)
    {
        XColor key = new XColor(name.toLowerCase(), 0, 0, 0);
        int pos = Arrays.binarySearch(COLORS, key);
        return pos < 0 ? null : COLORS[pos]; 
    }

    
    /**
     * Returns an iterator for the list of colors.
     * 
     * @return Iterator
     */
    public static Iterator iterator()
    {
        return new ArrayIterator(COLORS);
    }
    
    //--------------------------------------------------------------------------
    // XColor
    //--------------------------------------------------------------------------

    /**
     * Specialization of {@link java.awt.Color} with an assigned name.
     */
    public static class XColor extends Color implements Comparable
    {
        /**
         * Friendly name of this color.
         */
        private String name_;

        //----------------------------------------------------------------------
        // Constructors
        //----------------------------------------------------------------------
        
        /**
         * Creates a XColor.
         * 
         * @param name Color name.
         * @param red Red component.
         * @param green Green component.
         * @param blue Blue component.
         */
        public XColor(String name, int red, int green, int blue)
        {
            super(red, green, blue);
            name_ = name;
        }

        //----------------------------------------------------------------------
        // Public
        //----------------------------------------------------------------------
        
        /**
         * Returns this colors name.
         * 
         * @return String
         */
        public String getName()
        {
            return name_;
        }
        
        //----------------------------------------------------------------------
        // Comparable Interface
        //----------------------------------------------------------------------
        
        /**
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(Object o)
        {
            XColor other = (XColor) o;
            return name_.compareTo(other.getName());
        }
    }
}

/*
 * @(#)Colors.java 1.4 03/01/23
 * 
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved. SUN
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * @author Shannon Hickey
 * 
 * @version 1.4 01/23/03
 */


//private static final XColor[] colors =
//{
//  new XColor("alice blue", 240, 248, 255),
//  new XColor("aliceblue", 240, 248, 255),
//  new XColor("antique white", 250, 235, 215),
//  new XColor("antiquewhite", 250, 235, 215),
//  new XColor("antiquewhite1", 255, 239, 219),
//  new XColor("antiquewhite2", 238, 223, 204),
//  new XColor("antiquewhite3", 205, 192, 176),
//  new XColor("antiquewhite4", 139, 131, 120),
//  new XColor("aquamarine", 127, 255, 212),
//  new XColor("aquamarine1", 127, 255, 212),
//  new XColor("aquamarine2", 118, 238, 198),
//  new XColor("aquamarine3", 102, 205, 170),
//  new XColor("aquamarine4", 69, 139, 116),
//  new XColor("azure", 240, 255, 255),
//  new XColor("azure1", 240, 255, 255),
//  new XColor("azure2", 224, 238, 238),
//  new XColor("azure3", 193, 205, 205),
//  new XColor("azure4", 131, 139, 139),
//  new XColor("beige", 245, 245, 220),
//  new XColor("bisque", 255, 228, 196),
//  new XColor("bisque1", 255, 228, 196),
//  new XColor("bisque2", 238, 213, 183),
//  new XColor("bisque3", 205, 183, 158),
//  new XColor("bisque4", 139, 125, 107),
//  new XColor("black", 0, 0, 0),
//  new XColor("blanched almond", 255, 235, 205),
//  new XColor("blanchedalmond", 255, 235, 205),
//  new XColor("blue", 0, 0, 255),
//  new XColor("blue violet", 138, 43, 226),
//  new XColor("blue1", 0, 0, 255),
//  new XColor("blue2", 0, 0, 238),
//  new XColor("blue3", 0, 0, 205),
//  new XColor("blue4", 0, 0, 139),
//  new XColor("blueviolet", 138, 43, 226),
//  new XColor("brown", 165, 42, 42),
//  new XColor("brown1", 255, 64, 64),
//  new XColor("brown2", 238, 59, 59),
//  new XColor("brown3", 205, 51, 51),
//  new XColor("brown4", 139, 35, 35),
//  new XColor("burlywood", 222, 184, 135),
//  new XColor("burlywood1", 255, 211, 155),
//  new XColor("burlywood2", 238, 197, 145),
//  new XColor("burlywood3", 205, 170, 125),
//  new XColor("burlywood4", 139, 115, 85),
//  new XColor("cadet blue", 95, 158, 160),
//  new XColor("cadetblue", 95, 158, 160),
//  new XColor("cadetblue1", 152, 245, 255),
//  new XColor("cadetblue2", 142, 229, 238),
//  new XColor("cadetblue3", 122, 197, 205),
//  new XColor("cadetblue4", 83, 134, 139),
//  new XColor("chartreuse", 127, 255, 0),
//  new XColor("chartreuse1", 127, 255, 0),
//  new XColor("chartreuse2", 118, 238, 0),
//  new XColor("chartreuse3", 102, 205, 0),
//  new XColor("chartreuse4", 69, 139, 0),
//  new XColor("chocolate", 210, 105, 30),
//  new XColor("chocolate1", 255, 127, 36),
//  new XColor("chocolate2", 238, 118, 33),
//  new XColor("chocolate3", 205, 102, 29),
//  new XColor("chocolate4", 139, 69, 19),
//  new XColor("coral", 255, 127, 80),
//  new XColor("coral1", 255, 114, 86),
//  new XColor("coral2", 238, 106, 80),
//  new XColor("coral3", 205, 91, 69),
//  new XColor("coral4", 139, 62, 47),
//  new XColor("cornflower blue", 100, 149, 237),
//  new XColor("cornflowerblue", 100, 149, 237),
//  new XColor("cornsilk", 255, 248, 220),
//  new XColor("cornsilk1", 255, 248, 220),
//  new XColor("cornsilk2", 238, 232, 205),
//  new XColor("cornsilk3", 205, 200, 177),
//  new XColor("cornsilk4", 139, 136, 120),
//  new XColor("cyan", 0, 255, 255),
//  new XColor("cyan1", 0, 255, 255),
//  new XColor("cyan2", 0, 238, 238),
//  new XColor("cyan3", 0, 205, 205),
//  new XColor("cyan4", 0, 139, 139),
//  new XColor("dark blue", 0, 0, 139),
//  new XColor("dark cyan", 0, 139, 139),
//  new XColor("dark goldenrod", 184, 134, 11),
//  new XColor("dark gray", 169, 169, 169),
//  new XColor("dark green", 0, 100, 0),
//  new XColor("dark grey", 169, 169, 169),
//  new XColor("dark khaki", 189, 183, 107),
//  new XColor("dark magenta", 139, 0, 139),
//  new XColor("dark olive green", 85, 107, 47),
//  new XColor("dark orange", 255, 140, 0),
//  new XColor("dark orchid", 153, 50, 204),
//  new XColor("dark red", 139, 0, 0),
//  new XColor("dark salmon", 233, 150, 122),
//  new XColor("dark sea green", 143, 188, 143),
//  new XColor("dark slate blue", 72, 61, 139),
//  new XColor("dark slate gray", 47, 79, 79),
//  new XColor("dark slate grey", 47, 79, 79),
//  new XColor("dark turquoise", 0, 206, 209),
//  new XColor("dark violet", 148, 0, 211),
//  new XColor("darkblue", 0, 0, 139),
//  new XColor("darkcyan", 0, 139, 139),
//  new XColor("darkgoldenrod", 184, 134, 11),
//  new XColor("darkgoldenrod1", 255, 185, 15),
//  new XColor("darkgoldenrod2", 238, 173, 14),
//  new XColor("darkgoldenrod3", 205, 149, 12),
//  new XColor("darkgoldenrod4", 139, 101, 8),
//  new XColor("darkgray", 169, 169, 169),
//  new XColor("darkgreen", 0, 100, 0),
//  new XColor("darkgrey", 169, 169, 169),
//  new XColor("darkkhaki", 189, 183, 107),
//  new XColor("darkmagenta", 139, 0, 139),
//  new XColor("darkolivegreen", 85, 107, 47),
//  new XColor("darkolivegreen1", 202, 255, 112),
//  new XColor("darkolivegreen2", 188, 238, 104),
//  new XColor("darkolivegreen3", 162, 205, 90),
//  new XColor("darkolivegreen4", 110, 139, 61),
//  new XColor("darkorange", 255, 140, 0),
//  new XColor("darkorange1", 255, 127, 0),
//  new XColor("darkorange2", 238, 118, 0),
//  new XColor("darkorange3", 205, 102, 0),
//  new XColor("darkorange4", 139, 69, 0),
//  new XColor("darkorchid", 153, 50, 204),
//  new XColor("darkorchid1", 191, 62, 255),
//  new XColor("darkorchid2", 178, 58, 238),
//  new XColor("darkorchid3", 154, 50, 205),
//  new XColor("darkorchid4", 104, 34, 139),
//  new XColor("darkred", 139, 0, 0),
//  new XColor("darksalmon", 233, 150, 122),
//  new XColor("darkseagreen", 143, 188, 143),
//  new XColor("darkseagreen1", 193, 255, 193),
//  new XColor("darkseagreen2", 180, 238, 180),
//  new XColor("darkseagreen3", 155, 205, 155),
//  new XColor("darkseagreen4", 105, 139, 105),
//  new XColor("darkslateblue", 72, 61, 139),
//  new XColor("darkslategray", 47, 79, 79),
//  new XColor("darkslategray1", 151, 255, 255),
//  new XColor("darkslategray2", 141, 238, 238),
//  new XColor("darkslategray3", 121, 205, 205),
//  new XColor("darkslategray4", 82, 139, 139),
//  new XColor("darkslategrey", 47, 79, 79),
//  new XColor("darkturquoise", 0, 206, 209),
//  new XColor("darkviolet", 148, 0, 211),
//  new XColor("deep pink", 255, 20, 147),
//  new XColor("deep sky blue", 0, 191, 255),
//  new XColor("deeppink", 255, 20, 147),
//  new XColor("deeppink1", 255, 20, 147),
//  new XColor("deeppink2", 238, 18, 137),
//  new XColor("deeppink3", 205, 16, 118),
//  new XColor("deeppink4", 139, 10, 80),
//  new XColor("deepskyblue", 0, 191, 255),
//  new XColor("deepskyblue1", 0, 191, 255),
//  new XColor("deepskyblue2", 0, 178, 238),
//  new XColor("deepskyblue3", 0, 154, 205),
//  new XColor("deepskyblue4", 0, 104, 139),
//  new XColor("dim gray", 105, 105, 105),
//  new XColor("dim grey", 105, 105, 105),
//  new XColor("dimgray", 105, 105, 105),
//  new XColor("dimgrey", 105, 105, 105),
//  new XColor("dodger blue", 30, 144, 255),
//  new XColor("dodgerblue", 30, 144, 255),
//  new XColor("dodgerblue1", 30, 144, 255),
//  new XColor("dodgerblue2", 28, 134, 238),
//  new XColor("dodgerblue3", 24, 116, 205),
//  new XColor("dodgerblue4", 16, 78, 139),
//  new XColor("firebrick", 178, 34, 34),
//  new XColor("firebrick1", 255, 48, 48),
//  new XColor("firebrick2", 238, 44, 44),
//  new XColor("firebrick3", 205, 38, 38),
//  new XColor("firebrick4", 139, 26, 26),
//  new XColor("floral white", 255, 250, 240),
//  new XColor("floralwhite", 255, 250, 240),
//  new XColor("forest green", 34, 139, 34),
//  new XColor("forestgreen", 34, 139, 34),
//  new XColor("gainsboro", 220, 220, 220),
//  new XColor("ghost white", 248, 248, 255),
//  new XColor("ghostwhite", 248, 248, 255),
//  new XColor("gold", 255, 215, 0),
//  new XColor("gold1", 255, 215, 0),
//  new XColor("gold2", 238, 201, 0),
//  new XColor("gold3", 205, 173, 0),
//  new XColor("gold4", 139, 117, 0),
//  new XColor("goldenrod", 218, 165, 32),
//  new XColor("goldenrod1", 255, 193, 37),
//  new XColor("goldenrod2", 238, 180, 34),
//  new XColor("goldenrod3", 205, 155, 29),
//  new XColor("goldenrod4", 139, 105, 20),
//  new XColor("gray", 190, 190, 190),
//  new XColor("gray0", 0, 0, 0),
//  new XColor("gray1", 3, 3, 3),
//  new XColor("gray10", 26, 26, 26),
//  new XColor("gray100", 255, 255, 255),
//  new XColor("gray11", 28, 28, 28),
//  new XColor("gray12", 31, 31, 31),
//  new XColor("gray13", 33, 33, 33),
//  new XColor("gray14", 36, 36, 36),
//  new XColor("gray15", 38, 38, 38),
//  new XColor("gray16", 41, 41, 41),
//  new XColor("gray17", 43, 43, 43),
//  new XColor("gray18", 46, 46, 46),
//  new XColor("gray19", 48, 48, 48),
//  new XColor("gray2", 5, 5, 5),
//  new XColor("gray20", 51, 51, 51),
//  new XColor("gray21", 54, 54, 54),
//  new XColor("gray22", 56, 56, 56),
//  new XColor("gray23", 59, 59, 59),
//  new XColor("gray24", 61, 61, 61),
//  new XColor("gray25", 64, 64, 64),
//  new XColor("gray26", 66, 66, 66),
//  new XColor("gray27", 69, 69, 69),
//  new XColor("gray28", 71, 71, 71),
//  new XColor("gray29", 74, 74, 74),
//  new XColor("gray3", 8, 8, 8),
//  new XColor("gray30", 77, 77, 77),
//  new XColor("gray31", 79, 79, 79),
//  new XColor("gray32", 82, 82, 82),
//  new XColor("gray33", 84, 84, 84),
//  new XColor("gray34", 87, 87, 87),
//  new XColor("gray35", 89, 89, 89),
//  new XColor("gray36", 92, 92, 92),
//  new XColor("gray37", 94, 94, 94),
//  new XColor("gray38", 97, 97, 97),
//  new XColor("gray39", 99, 99, 99),
//  new XColor("gray4", 10, 10, 10),
//  new XColor("gray40", 102, 102, 102),
//  new XColor("gray41", 105, 105, 105),
//  new XColor("gray42", 107, 107, 107),
//  new XColor("gray43", 110, 110, 110),
//  new XColor("gray44", 112, 112, 112),
//  new XColor("gray45", 115, 115, 115),
//  new XColor("gray46", 117, 117, 117),
//  new XColor("gray47", 120, 120, 120),
//  new XColor("gray48", 122, 122, 122),
//  new XColor("gray49", 125, 125, 125),
//  new XColor("gray5", 13, 13, 13),
//  new XColor("gray50", 127, 127, 127),
//  new XColor("gray51", 130, 130, 130),
//  new XColor("gray52", 133, 133, 133),
//  new XColor("gray53", 135, 135, 135),
//  new XColor("gray54", 138, 138, 138),
//  new XColor("gray55", 140, 140, 140),
//  new XColor("gray56", 143, 143, 143),
//  new XColor("gray57", 145, 145, 145),
//  new XColor("gray58", 148, 148, 148),
//  new XColor("gray59", 150, 150, 150),
//  new XColor("gray6", 15, 15, 15),
//  new XColor("gray60", 153, 153, 153),
//  new XColor("gray61", 156, 156, 156),
//  new XColor("gray62", 158, 158, 158),
//  new XColor("gray63", 161, 161, 161),
//  new XColor("gray64", 163, 163, 163),
//  new XColor("gray65", 166, 166, 166),
//  new XColor("gray66", 168, 168, 168),
//  new XColor("gray67", 171, 171, 171),
//  new XColor("gray68", 173, 173, 173),
//  new XColor("gray69", 176, 176, 176),
//  new XColor("gray7", 18, 18, 18),
//  new XColor("gray70", 179, 179, 179),
//  new XColor("gray71", 181, 181, 181),
//  new XColor("gray72", 184, 184, 184),
//  new XColor("gray73", 186, 186, 186),
//  new XColor("gray74", 189, 189, 189),
//  new XColor("gray75", 191, 191, 191),
//  new XColor("gray76", 194, 194, 194),
//  new XColor("gray77", 196, 196, 196),
//  new XColor("gray78", 199, 199, 199),
//  new XColor("gray79", 201, 201, 201),
//  new XColor("gray8", 20, 20, 20),
//  new XColor("gray80", 204, 204, 204),
//  new XColor("gray81", 207, 207, 207),
//  new XColor("gray82", 209, 209, 209),
//  new XColor("gray83", 212, 212, 212),
//  new XColor("gray84", 214, 214, 214),
//  new XColor("gray85", 217, 217, 217),
//  new XColor("gray86", 219, 219, 219),
//  new XColor("gray87", 222, 222, 222),
//  new XColor("gray88", 224, 224, 224),
//  new XColor("gray89", 227, 227, 227),
//  new XColor("gray9", 23, 23, 23),
//  new XColor("gray90", 229, 229, 229),
//  new XColor("gray91", 232, 232, 232),
//  new XColor("gray92", 235, 235, 235),
//  new XColor("gray93", 237, 237, 237),
//  new XColor("gray94", 240, 240, 240),
//  new XColor("gray95", 242, 242, 242),
//  new XColor("gray96", 245, 245, 245),
//  new XColor("gray97", 247, 247, 247),
//  new XColor("gray98", 250, 250, 250),
//  new XColor("gray99", 252, 252, 252),
//  new XColor("green", 0, 255, 0),
//  new XColor("green yellow", 173, 255, 47),
//  new XColor("green1", 0, 255, 0),
//  new XColor("green2", 0, 238, 0),
//  new XColor("green3", 0, 205, 0),
//  new XColor("green4", 0, 139, 0),
//  new XColor("greenyellow", 173, 255, 47),
//  new XColor("grey", 190, 190, 190),
//  new XColor("grey0", 0, 0, 0),
//  new XColor("grey1", 3, 3, 3),
//  new XColor("grey10", 26, 26, 26),
//  new XColor("grey100", 255, 255, 255),
//  new XColor("grey11", 28, 28, 28),
//  new XColor("grey12", 31, 31, 31),
//  new XColor("grey13", 33, 33, 33),
//  new XColor("grey14", 36, 36, 36),
//  new XColor("grey15", 38, 38, 38),
//  new XColor("grey16", 41, 41, 41),
//  new XColor("grey17", 43, 43, 43),
//  new XColor("grey18", 46, 46, 46),
//  new XColor("grey19", 48, 48, 48),
//  new XColor("grey2", 5, 5, 5),
//  new XColor("grey20", 51, 51, 51),
//  new XColor("grey21", 54, 54, 54),
//  new XColor("grey22", 56, 56, 56),
//  new XColor("grey23", 59, 59, 59),
//  new XColor("grey24", 61, 61, 61),
//  new XColor("grey25", 64, 64, 64),
//  new XColor("grey26", 66, 66, 66),
//  new XColor("grey27", 69, 69, 69),
//  new XColor("grey28", 71, 71, 71),
//  new XColor("grey29", 74, 74, 74),
//  new XColor("grey3", 8, 8, 8),
//  new XColor("grey30", 77, 77, 77),
//  new XColor("grey31", 79, 79, 79),
//  new XColor("grey32", 82, 82, 82),
//  new XColor("grey33", 84, 84, 84),
//  new XColor("grey34", 87, 87, 87),
//  new XColor("grey35", 89, 89, 89),
//  new XColor("grey36", 92, 92, 92),
//  new XColor("grey37", 94, 94, 94),
//  new XColor("grey38", 97, 97, 97),
//  new XColor("grey39", 99, 99, 99),
//  new XColor("grey4", 10, 10, 10),
//  new XColor("grey40", 102, 102, 102),
//  new XColor("grey41", 105, 105, 105),
//  new XColor("grey42", 107, 107, 107),
//  new XColor("grey43", 110, 110, 110),
//  new XColor("grey44", 112, 112, 112),
//  new XColor("grey45", 115, 115, 115),
//  new XColor("grey46", 117, 117, 117),
//  new XColor("grey47", 120, 120, 120),
//  new XColor("grey48", 122, 122, 122),
//  new XColor("grey49", 125, 125, 125),
//  new XColor("grey5", 13, 13, 13),
//  new XColor("grey50", 127, 127, 127),
//  new XColor("grey51", 130, 130, 130),
//  new XColor("grey52", 133, 133, 133),
//  new XColor("grey53", 135, 135, 135),
//  new XColor("grey54", 138, 138, 138),
//  new XColor("grey55", 140, 140, 140),
//  new XColor("grey56", 143, 143, 143),
//  new XColor("grey57", 145, 145, 145),
//  new XColor("grey58", 148, 148, 148),
//  new XColor("grey59", 150, 150, 150),
//  new XColor("grey6", 15, 15, 15),
//  new XColor("grey60", 153, 153, 153),
//  new XColor("grey61", 156, 156, 156),
//  new XColor("grey62", 158, 158, 158),
//  new XColor("grey63", 161, 161, 161),
//  new XColor("grey64", 163, 163, 163),
//  new XColor("grey65", 166, 166, 166),
//  new XColor("grey66", 168, 168, 168),
//  new XColor("grey67", 171, 171, 171),
//  new XColor("grey68", 173, 173, 173),
//  new XColor("grey69", 176, 176, 176),
//  new XColor("grey7", 18, 18, 18),
//  new XColor("grey70", 179, 179, 179),
//  new XColor("grey71", 181, 181, 181),
//  new XColor("grey72", 184, 184, 184),
//  new XColor("grey73", 186, 186, 186),
//  new XColor("grey74", 189, 189, 189),
//  new XColor("grey75", 191, 191, 191),
//  new XColor("grey76", 194, 194, 194),
//  new XColor("grey77", 196, 196, 196),
//  new XColor("grey78", 199, 199, 199),
//  new XColor("grey79", 201, 201, 201),
//  new XColor("grey8", 20, 20, 20),
//  new XColor("grey80", 204, 204, 204),
//  new XColor("grey81", 207, 207, 207),
//  new XColor("grey82", 209, 209, 209),
//  new XColor("grey83", 212, 212, 212),
//  new XColor("grey84", 214, 214, 214),
//  new XColor("grey85", 217, 217, 217),
//  new XColor("grey86", 219, 219, 219),
//  new XColor("grey87", 222, 222, 222),
//  new XColor("grey88", 224, 224, 224),
//  new XColor("grey89", 227, 227, 227),
//  new XColor("grey9", 23, 23, 23),
//  new XColor("grey90", 229, 229, 229),
//  new XColor("grey91", 232, 232, 232),
//  new XColor("grey92", 235, 235, 235),
//  new XColor("grey93", 237, 237, 237),
//  new XColor("grey94", 240, 240, 240),
//  new XColor("grey95", 242, 242, 242),
//  new XColor("grey96", 245, 245, 245),
//  new XColor("grey97", 247, 247, 247),
//  new XColor("grey98", 250, 250, 250),
//  new XColor("grey99", 252, 252, 252),
//  new XColor("honeydew", 240, 255, 240),
//  new XColor("honeydew1", 240, 255, 240),
//  new XColor("honeydew2", 224, 238, 224),
//  new XColor("honeydew3", 193, 205, 193),
//  new XColor("honeydew4", 131, 139, 131),
//  new XColor("hot pink", 255, 105, 180),
//  new XColor("hotpink", 255, 105, 180),
//  new XColor("hotpink1", 255, 110, 180),
//  new XColor("hotpink2", 238, 106, 167),
//  new XColor("hotpink3", 205, 96, 144),
//  new XColor("hotpink4", 139, 58, 98),
//  new XColor("indian red", 205, 92, 92),
//  new XColor("indianred", 205, 92, 92),
//  new XColor("indianred1", 255, 106, 106),
//  new XColor("indianred2", 238, 99, 99),
//  new XColor("indianred3", 205, 85, 85),
//  new XColor("indianred4", 139, 58, 58),
//  new XColor("ivory", 255, 255, 240),
//  new XColor("ivory1", 255, 255, 240),
//  new XColor("ivory2", 238, 238, 224),
//  new XColor("ivory3", 205, 205, 193),
//  new XColor("ivory4", 139, 139, 131),
//  new XColor("khaki", 240, 230, 140),
//  new XColor("khaki1", 255, 246, 143),
//  new XColor("khaki2", 238, 230, 133),
//  new XColor("khaki3", 205, 198, 115),
//  new XColor("khaki4", 139, 134, 78),
//  new XColor("lavender", 230, 230, 250),
//  new XColor("lavender blush", 255, 240, 245),
//  new XColor("lavenderblush", 255, 240, 245),
//  new XColor("lavenderblush1", 255, 240, 245),
//  new XColor("lavenderblush2", 238, 224, 229),
//  new XColor("lavenderblush3", 205, 193, 197),
//  new XColor("lavenderblush4", 139, 131, 134),
//  new XColor("lawn green", 124, 252, 0),
//  new XColor("lawngreen", 124, 252, 0),
//  new XColor("lemon chiffon", 255, 250, 205),
//  new XColor("lemonchiffon", 255, 250, 205),
//  new XColor("lemonchiffon1", 255, 250, 205),
//  new XColor("lemonchiffon2", 238, 233, 191),
//  new XColor("lemonchiffon3", 205, 201, 165),
//  new XColor("lemonchiffon4", 139, 137, 112),
//  new XColor("light blue", 173, 216, 230),
//  new XColor("light coral", 240, 128, 128),
//  new XColor("light cyan", 224, 255, 255),
//  new XColor("light goldenrod", 238, 221, 130),
//  new XColor("light goldenrod yellow", 250, 250, 210),
//  new XColor("light gray", 211, 211, 211),
//  new XColor("light green", 144, 238, 144),
//  new XColor("light grey", 211, 211, 211),
//  new XColor("light pink", 255, 182, 193),
//  new XColor("light salmon", 255, 160, 122),
//  new XColor("light sea green", 32, 178, 170),
//  new XColor("light sky blue", 135, 206, 250),
//  new XColor("light slate blue", 132, 112, 255),
//  new XColor("light slate gray", 119, 136, 153),
//  new XColor("light slate grey", 119, 136, 153),
//  new XColor("light steel blue", 176, 196, 222),
//  new XColor("light yellow", 255, 255, 224),
//  new XColor("lightblue", 173, 216, 230),
//  new XColor("lightblue1", 191, 239, 255),
//  new XColor("lightblue2", 178, 223, 238),
//  new XColor("lightblue3", 154, 192, 205),
//  new XColor("lightblue4", 104, 131, 139),
//  new XColor("lightcoral", 240, 128, 128),
//  new XColor("lightcyan", 224, 255, 255),
//  new XColor("lightcyan1", 224, 255, 255),
//  new XColor("lightcyan2", 209, 238, 238),
//  new XColor("lightcyan3", 180, 205, 205),
//  new XColor("lightcyan4", 122, 139, 139),
//  new XColor("lightgoldenrod", 238, 221, 130),
//  new XColor("lightgoldenrod1", 255, 236, 139),
//  new XColor("lightgoldenrod2", 238, 220, 130),
//  new XColor("lightgoldenrod3", 205, 190, 112),
//  new XColor("lightgoldenrod4", 139, 129, 76),
//  new XColor("lightgoldenrodyellow", 250, 250, 210),
//  new XColor("lightgray", 211, 211, 211),
//  new XColor("lightgreen", 144, 238, 144),
//  new XColor("lightgrey", 211, 211, 211),
//  new XColor("lightpink", 255, 182, 193),
//  new XColor("lightpink1", 255, 174, 185),
//  new XColor("lightpink2", 238, 162, 173),
//  new XColor("lightpink3", 205, 140, 149),
//  new XColor("lightpink4", 139, 95, 101),
//  new XColor("lightsalmon", 255, 160, 122),
//  new XColor("lightsalmon1", 255, 160, 122),
//  new XColor("lightsalmon2", 238, 149, 114),
//  new XColor("lightsalmon3", 205, 129, 98),
//  new XColor("lightsalmon4", 139, 87, 66),
//  new XColor("lightseagreen", 32, 178, 170),
//  new XColor("lightskyblue", 135, 206, 250),
//  new XColor("lightskyblue1", 176, 226, 255),
//  new XColor("lightskyblue2", 164, 211, 238),
//  new XColor("lightskyblue3", 141, 182, 205),
//  new XColor("lightskyblue4", 96, 123, 139),
//  new XColor("lightslateblue", 132, 112, 255),
//  new XColor("lightslategray", 119, 136, 153),
//  new XColor("lightslategrey", 119, 136, 153),
//  new XColor("lightsteelblue", 176, 196, 222),
//  new XColor("lightsteelblue1", 202, 225, 255),
//  new XColor("lightsteelblue2", 188, 210, 238),
//  new XColor("lightsteelblue3", 162, 181, 205),
//  new XColor("lightsteelblue4", 110, 123, 139),
//  new XColor("lightyellow", 255, 255, 224),
//  new XColor("lightyellow1", 255, 255, 224),
//  new XColor("lightyellow2", 238, 238, 209),
//  new XColor("lightyellow3", 205, 205, 180),
//  new XColor("lightyellow4", 139, 139, 122),
//  new XColor("lime green", 50, 205, 50),
//  new XColor("limegreen", 50, 205, 50),
//  new XColor("linen", 250, 240, 230),
//  new XColor("magenta", 255, 0, 255),
//  new XColor("magenta1", 255, 0, 255),
//  new XColor("magenta2", 238, 0, 238),
//  new XColor("magenta3", 205, 0, 205),
//  new XColor("magenta4", 139, 0, 139),
//  new XColor("maroon", 176, 48, 96),
//  new XColor("maroon1", 255, 52, 179),
//  new XColor("maroon2", 238, 48, 167),
//  new XColor("maroon3", 205, 41, 144),
//  new XColor("maroon4", 139, 28, 98),
//  new XColor("medium aquamarine", 102, 205, 170),
//  new XColor("medium blue", 0, 0, 205),
//  new XColor("medium orchid", 186, 85, 211),
//  new XColor("medium purple", 147, 112, 219),
//  new XColor("medium sea green", 60, 179, 113),
//  new XColor("medium slate blue", 123, 104, 238),
//  new XColor("medium spring green", 0, 250, 154),
//  new XColor("medium turquoise", 72, 209, 204),
//  new XColor("medium violet red", 199, 21, 133),
//  new XColor("mediumaquamarine", 102, 205, 170),
//  new XColor("mediumblue", 0, 0, 205),
//  new XColor("mediumorchid", 186, 85, 211),
//  new XColor("mediumorchid1", 224, 102, 255),
//  new XColor("mediumorchid2", 209, 95, 238),
//  new XColor("mediumorchid3", 180, 82, 205),
//  new XColor("mediumorchid4", 122, 55, 139),
//  new XColor("mediumpurple", 147, 112, 219),
//  new XColor("mediumpurple1", 171, 130, 255),
//  new XColor("mediumpurple2", 159, 121, 238),
//  new XColor("mediumpurple3", 137, 104, 205),
//  new XColor("mediumpurple4", 93, 71, 139),
//  new XColor("mediumseagreen", 60, 179, 113),
//  new XColor("mediumslateblue", 123, 104, 238),
//  new XColor("mediumspringgreen", 0, 250, 154),
//  new XColor("mediumturquoise", 72, 209, 204),
//  new XColor("mediumvioletred", 199, 21, 133),
//  new XColor("midnight blue", 25, 25, 112),
//  new XColor("midnightblue", 25, 25, 112),
//  new XColor("mint cream", 245, 255, 250),
//  new XColor("mintcream", 245, 255, 250),
//  new XColor("misty rose", 255, 228, 225),
//  new XColor("mistyrose", 255, 228, 225),
//  new XColor("mistyrose1", 255, 228, 225),
//  new XColor("mistyrose2", 238, 213, 210),
//  new XColor("mistyrose3", 205, 183, 181),
//  new XColor("mistyrose4", 139, 125, 123),
//  new XColor("moccasin", 255, 228, 181),
//  new XColor("navajo white", 255, 222, 173),
//  new XColor("navajowhite", 255, 222, 173),
//  new XColor("navajowhite1", 255, 222, 173),
//  new XColor("navajowhite2", 238, 207, 161),
//  new XColor("navajowhite3", 205, 179, 139),
//  new XColor("navajowhite4", 139, 121, 94),
//  new XColor("navy", 0, 0, 128),
//  new XColor("navy blue", 0, 0, 128),
//  new XColor("navyblue", 0, 0, 128),
//  new XColor("old lace", 253, 245, 230),
//  new XColor("oldlace", 253, 245, 230),
//  new XColor("olive drab", 107, 142, 35),
//  new XColor("olivedrab", 107, 142, 35),
//  new XColor("olivedrab1", 192, 255, 62),
//  new XColor("olivedrab2", 179, 238, 58),
//  new XColor("olivedrab3", 154, 205, 50),
//  new XColor("olivedrab4", 105, 139, 34),
//  new XColor("orange", 255, 165, 0),
//  new XColor("orange red", 255, 69, 0),
//  new XColor("orange1", 255, 165, 0),
//  new XColor("orange2", 238, 154, 0),
//  new XColor("orange3", 205, 133, 0),
//  new XColor("orange4", 139, 90, 0),
//  new XColor("orangered", 255, 69, 0),
//  new XColor("orangered1", 255, 69, 0),
//  new XColor("orangered2", 238, 64, 0),
//  new XColor("orangered3", 205, 55, 0),
//  new XColor("orangered4", 139, 37, 0),
//  new XColor("orchid", 218, 112, 214),
//  new XColor("orchid1", 255, 131, 250),
//  new XColor("orchid2", 238, 122, 233),
//  new XColor("orchid3", 205, 105, 201),
//  new XColor("orchid4", 139, 71, 137),
//  new XColor("pale goldenrod", 238, 232, 170),
//  new XColor("pale green", 152, 251, 152),
//  new XColor("pale turquoise", 175, 238, 238),
//  new XColor("pale violet red", 219, 112, 147),
//  new XColor("palegoldenrod", 238, 232, 170),
//  new XColor("palegreen", 152, 251, 152),
//  new XColor("palegreen1", 154, 255, 154),
//  new XColor("palegreen2", 144, 238, 144),
//  new XColor("palegreen3", 124, 205, 124),
//  new XColor("palegreen4", 84, 139, 84),
//  new XColor("paleturquoise", 175, 238, 238),
//  new XColor("paleturquoise1", 187, 255, 255),
//  new XColor("paleturquoise2", 174, 238, 238),
//  new XColor("paleturquoise3", 150, 205, 205),
//  new XColor("paleturquoise4", 102, 139, 139),
//  new XColor("palevioletred", 219, 112, 147),
//  new XColor("palevioletred1", 255, 130, 171),
//  new XColor("palevioletred2", 238, 121, 159),
//  new XColor("palevioletred3", 205, 104, 137),
//  new XColor("palevioletred4", 139, 71, 93),
//  new XColor("papaya whip", 255, 239, 213),
//  new XColor("papayawhip", 255, 239, 213),
//  new XColor("peach puff", 255, 218, 185),
//  new XColor("peachpuff", 255, 218, 185),
//  new XColor("peachpuff1", 255, 218, 185),
//  new XColor("peachpuff2", 238, 203, 173),
//  new XColor("peachpuff3", 205, 175, 149),
//  new XColor("peachpuff4", 139, 119, 101),
//  new XColor("peru", 205, 133, 63),
//  new XColor("pink", 255, 192, 203),
//  new XColor("pink1", 255, 181, 197),
//  new XColor("pink2", 238, 169, 184),
//  new XColor("pink3", 205, 145, 158),
//  new XColor("pink4", 139, 99, 108),
//  new XColor("plum", 221, 160, 221),
//  new XColor("plum1", 255, 187, 255),
//  new XColor("plum2", 238, 174, 238),
//  new XColor("plum3", 205, 150, 205),
//  new XColor("plum4", 139, 102, 139),
//  new XColor("powder blue", 176, 224, 230),
//  new XColor("powderblue", 176, 224, 230),
//  new XColor("purple", 160, 32, 240),
//  new XColor("purple1", 155, 48, 255),
//  new XColor("purple2", 145, 44, 238),
//  new XColor("purple3", 125, 38, 205),
//  new XColor("purple4", 85, 26, 139),
//  new XColor("red", 255, 0, 0),
//  new XColor("red1", 255, 0, 0),
//  new XColor("red2", 238, 0, 0),
//  new XColor("red3", 205, 0, 0),
//  new XColor("red4", 139, 0, 0),
//  new XColor("rosy brown", 188, 143, 143),
//  new XColor("rosybrown", 188, 143, 143),
//  new XColor("rosybrown1", 255, 193, 193),
//  new XColor("rosybrown2", 238, 180, 180),
//  new XColor("rosybrown3", 205, 155, 155),
//  new XColor("rosybrown4", 139, 105, 105),
//  new XColor("royal blue", 65, 105, 225),
//  new XColor("royalblue", 65, 105, 225),
//  new XColor("royalblue1", 72, 118, 255),
//  new XColor("royalblue2", 67, 110, 238),
//  new XColor("royalblue3", 58, 95, 205),
//  new XColor("royalblue4", 39, 64, 139),
//  new XColor("saddle brown", 139, 69, 19),
//  new XColor("saddlebrown", 139, 69, 19),
//  new XColor("salmon", 250, 128, 114),
//  new XColor("salmon1", 255, 140, 105),
//  new XColor("salmon2", 238, 130, 98),
//  new XColor("salmon3", 205, 112, 84),
//  new XColor("salmon4", 139, 76, 57),
//  new XColor("sandy brown", 244, 164, 96),
//  new XColor("sandybrown", 244, 164, 96),
//  new XColor("sea green", 46, 139, 87),
//  new XColor("seagreen", 46, 139, 87),
//  new XColor("seagreen1", 84, 255, 159),
//  new XColor("seagreen2", 78, 238, 148),
//  new XColor("seagreen3", 67, 205, 128),
//  new XColor("seagreen4", 46, 139, 87),
//  new XColor("seashell", 255, 245, 238),
//  new XColor("seashell1", 255, 245, 238),
//  new XColor("seashell2", 238, 229, 222),
//  new XColor("seashell3", 205, 197, 191),
//  new XColor("seashell4", 139, 134, 130),
//  new XColor("sienna", 160, 82, 45),
//  new XColor("sienna1", 255, 130, 71),
//  new XColor("sienna2", 238, 121, 66),
//  new XColor("sienna3", 205, 104, 57),
//  new XColor("sienna4", 139, 71, 38),
//  new XColor("sky blue", 135, 206, 235),
//  new XColor("skyblue", 135, 206, 235),
//  new XColor("skyblue1", 135, 206, 255),
//  new XColor("skyblue2", 126, 192, 238),
//  new XColor("skyblue3", 108, 166, 205),
//  new XColor("skyblue4", 74, 112, 139),
//  new XColor("slate blue", 106, 90, 205),
//  new XColor("slate gray", 112, 128, 144),
//  new XColor("slate grey", 112, 128, 144),
//  new XColor("slateblue", 106, 90, 205),
//  new XColor("slateblue1", 131, 111, 255),
//  new XColor("slateblue2", 122, 103, 238),
//  new XColor("slateblue3", 105, 89, 205),
//  new XColor("slateblue4", 71, 60, 139),
//  new XColor("slategray", 112, 128, 144),
//  new XColor("slategray1", 198, 226, 255),
//  new XColor("slategray2", 185, 211, 238),
//  new XColor("slategray3", 159, 182, 205),
//  new XColor("slategray4", 108, 123, 139),
//  new XColor("slategrey", 112, 128, 144),
//  new XColor("snow", 255, 250, 250),
//  new XColor("snow1", 255, 250, 250),
//  new XColor("snow2", 238, 233, 233),
//  new XColor("snow3", 205, 201, 201),
//  new XColor("snow4", 139, 137, 137),
//  new XColor("spring green", 0, 255, 127),
//  new XColor("springgreen", 0, 255, 127),
//  new XColor("springgreen1", 0, 255, 127),
//  new XColor("springgreen2", 0, 238, 118),
//  new XColor("springgreen3", 0, 205, 102),
//  new XColor("springgreen4", 0, 139, 69),
//  new XColor("steel blue", 70, 130, 180),
//  new XColor("steelblue", 70, 130, 180),
//  new XColor("steelblue1", 99, 184, 255),
//  new XColor("steelblue2", 92, 172, 238),
//  new XColor("steelblue3", 79, 148, 205),
//  new XColor("steelblue4", 54, 100, 139),
//  new XColor("tan", 210, 180, 140),
//  new XColor("tan1", 255, 165, 79),
//  new XColor("tan2", 238, 154, 73),
//  new XColor("tan3", 205, 133, 63),
//  new XColor("tan4", 139, 90, 43),
//  new XColor("thistle", 216, 191, 216),
//  new XColor("thistle1", 255, 225, 255),
//  new XColor("thistle2", 238, 210, 238),
//  new XColor("thistle3", 205, 181, 205),
//  new XColor("thistle4", 139, 123, 139),
//  new XColor("tomato", 255, 99, 71),
//  new XColor("tomato1", 255, 99, 71),
//  new XColor("tomato2", 238, 92, 66),
//  new XColor("tomato3", 205, 79, 57),
//  new XColor("tomato4", 139, 54, 38),
//  new XColor("turquoise", 64, 224, 208),
//  new XColor("turquoise1", 0, 245, 255),
//  new XColor("turquoise2", 0, 229, 238),
//  new XColor("turquoise3", 0, 197, 205),
//  new XColor("turquoise4", 0, 134, 139),
//  new XColor("violet", 238, 130, 238),
//  new XColor("violet red", 208, 32, 144),
//  new XColor("violetred", 208, 32, 144),
//  new XColor("violetred1", 255, 62, 150),
//  new XColor("violetred2", 238, 58, 140),
//  new XColor("violetred3", 205, 50, 120),
//  new XColor("violetred4", 139, 34, 82),
//  new XColor("wheat", 245, 222, 179),
//  new XColor("wheat1", 255, 231, 186),
//  new XColor("wheat2", 238, 216, 174),
//  new XColor("wheat3", 205, 186, 150),
//  new XColor("wheat4", 139, 126, 102),
//  new XColor("white", 255, 255, 255),
//  new XColor("white smoke", 245, 245, 245),
//  new XColor("whitesmoke", 245, 245, 245),
//  new XColor("yellow", 255, 255, 0),
//  new XColor("yellow green", 154, 205, 50),
//  new XColor("yellow1", 255, 255, 0),
//  new XColor("yellow2", 238, 238, 0),
//  new XColor("yellow3", 205, 205, 0),
//  new XColor("yellow4", 139, 139, 0),
//  new XColor("yellowgreen", 154, 205, 5)
//};
