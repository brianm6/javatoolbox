package toolbox.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;

/**
 * Banner converts a text string into a banner using ASCII characters to form
 * larger versions of the letters.
 */ 
public class Banner
{
    private static final String FONT_STANDARD_DATA = 

    "flf2a$ 6 5 16 15 11 0 24463 229\n" + 
    "Standard by Glenn Chappell & Ian Chai 3/93 -- based on Frank's .sig\n" + 
    "Includes ISO Latin-1\n" + 
    "figlet release 2.1 -- 12 Aug 1994\n" + 
    "Modified for figlet 2.2 by John Cowan <cowan@ccil.org>\n" + 
    "  to add Latin-{2,3,4,5} support (Unicode U+0100-017F).\n" + 
    "Permission is hereby given to modify this font, as long as the\n" + 
    "modifier's name is placed on a comment line.\n" + 
    "\n" + 
    "Modified by Paul Burton <solution@earthlink.net> " +
    "12/96 to include new parameter\n" + 
    "supported by FIGlet and FIGWin." + 
    " May also be slightly modified for better use\n" + 
    "of new full-width/kern/smush alternatives, " +
    "but default output is NOT changed.\n" + 
    " $@\n" + 
    " $@\n" + 
    " $@\n" + 
    " $@\n" + 
    " $@\n" + 
    " $@@\n" + 
    "  _ @\n" + 
    " | |@\n" + 
    " | |@\n" + 
    " |_|@\n" + 
    " (_)@\n" + 
    "    @@\n" + 
    "  _ _ @\n" + 
    " ( | )@\n" + 
    "  V V @\n" + 
    "   $  @\n" + 
    "   $  @\n" + 
    "      @@\n" + 
    "    _  _   @\n" + 
    "  _| || |_ @\n" + 
    " |_  ..  _|@\n" + 
    " |_      _|@\n" + 
    "   |_||_|  @\n" + 
    "           @@\n" + 
    "   _  @\n" + 
    "  | | @\n" + 
    " / __)@\n" + 
    " \\__ \\@\n" + 
    " (   /@\n" + 
    "  |_| @@\n" + 
    "  _  __@\n" + 
    " (_)/ /@\n" + 
    "   / / @\n" + 
    "  / /_ @\n" + 
    " /_/(_)@\n" + 
    "       @@\n" + 
    "   ___   @\n" + 
    "  ( _ )  @\n" + 
    "  / _ \\/\\@\n" + 
    " | (_>  <@\n" + 
    "  \\___/\\/@\n" + 
    "         @@\n" + 
    "  _ @\n" + 
    " ( )@\n" + 
    " |/ @\n" + 
    "  $ @\n" + 
    "  $ @\n" + 
    "    @@\n" + 
    "   __@\n" + 
    "  / /@\n" + 
    " | | @\n" + 
    " | | @\n" + 
    " | | @\n" + 
    "  \\_\\@@\n" + 
    " __  @\n" + 
    " \\ \\ @\n" + 
    "  | |@\n" + 
    "  | |@\n" + 
    "  | |@\n" + 
    " /_/ @@\n" + 
    "       @\n" + 
    " __/\\__@\n" + 
    " \\    /@\n" + 
    " /_  _\\@\n" + 
    "   \\/  @\n" + 
    "       @@\n" + 
    "        @\n" + 
    "    _   @\n" + 
    "  _| |_ @\n" + 
    " |_   _|@\n" + 
    "   |_|  @\n" + 
    "        @@\n" + 
    "    @\n" + 
    "    @\n" + 
    "    @\n" + 
    "  _ @\n" + 
    " ( )@\n" + 
    " |/ @@\n" + 
    "        @\n" + 
    "        @\n" + 
    "  _____ @\n" + 
    " |_____|@\n" + 
    "    $   @\n" + 
    "        @@\n" + 
    "    @\n" + 
    "    @\n" + 
    "    @\n" + 
    "  _ @\n" + 
    " (_)@\n" + 
    "    @@\n" + 
    "     __@\n" + 
    "    / /@\n" + 
    "   / / @\n" + 
    "  / /  @\n" + 
    " /_/   @\n" + 
    "       @@\n" + 
    "   ___  @\n" + 
    "  / _ \\ @\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "  _ @\n" + 
    " / |@\n" + 
    " | |@\n" + 
    " | |@\n" + 
    " |_|@\n" + 
    "    @@\n" + 
    "  ____  @\n" + 
    " |___ \\ @\n" + 
    "   __) |@\n" + 
    "  / __/ @\n" + 
    " |_____|@\n" + 
    "        @@\n" + 
    "  _____ @\n" + 
    " |___ / @\n" + 
    "   |_ \\ @\n" + 
    "  ___) |@\n" + 
    " |____/ @\n" + 
    "        @@\n" + 
    "  _  _   @\n" + 
    " | || |  @\n" + 
    " | || |_ @\n" + 
    " |__   _|@\n" + 
    "    |_|  @\n" + 
    "         @@\n" + 
    "  ____  @\n" + 
    " | ___| @\n" + 
    " |___ \\ @\n" + 
    "  ___) |@\n" + 
    " |____/ @\n" + 
    "        @@\n" + 
    "   __   @\n" + 
    "  / /_  @\n" + 
    " | '_ \\ @\n" + 
    " | (_) |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "  _____ @\n" + 
    " |___  |@\n" + 
    "    / / @\n" + 
    "   / /  @\n" + 
    "  /_/   @\n" + 
    "        @@\n" + 
    "   ___  @\n" + 
    "  ( _ ) @\n" + 
    "  / _ \\ @\n" + 
    " | (_) |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "   ___  @\n" + 
    "  / _ \\ @\n" + 
    " | (_) |@\n" + 
    "  \\__, |@\n" + 
    "    /_/ @\n" + 
    "        @@\n" + 
    "    @\n" + 
    "  _ @\n" + 
    " (_)@\n" + 
    "  _ @\n" + 
    " (_)@\n" + 
    "    @@\n" + 
    "    @\n" + 
    "  _ @\n" + 
    " (_)@\n" + 
    "  _ @\n" + 
    " ( )@\n" + 
    " |/ @@\n" + 
    "   __@\n" + 
    "  / /@\n" + 
    " / / @\n" + 
    " \\ \\ @\n" + 
    "  \\_\\@\n" + 
    "     @@\n" + 
    "        @\n" + 
    "  _____ @\n" + 
    " |_____|@\n" + 
    " |_____|@\n" + 
    "    $   @\n" + 
    "        @@\n" + 
    " __  @\n" + 
    " \\ \\ @\n" + 
    "  \\ \\@\n" + 
    "  / /@\n" + 
    " /_/ @\n" + 
    "     @@\n" + 
    "  ___ @\n" + 
    " |__ \\@\n" + 
    "   / /@\n" + 
    "  |_| @\n" + 
    "  (_) @\n" + 
    "      @@\n" + 
    "    ____  @\n" + 
    "   / __ \\ @\n" + 
    "  / / _` |@\n" + 
    " | | (_| |@\n" + 
    "  \\ \\__,_|@\n" + 
    "   \\____/ @@\n" + 
    "     _    @\n" + 
    "    / \\   @\n" + 
    "   / _ \\  @\n" + 
    "  / ___ \\ @\n" + 
    " /_/   \\_\\@\n" + 
    "          @@\n" + 
    "  ____  @\n" + 
    " | __ ) @\n" + 
    " |  _ \\ @\n" + 
    " | |_) |@\n" + 
    " |____/ @\n" + 
    "        @@\n" + 
    "   ____ @\n" + 
    "  / ___|@\n" + 
    " | |    @\n" + 
    " | |___ @\n" + 
    "  \\____|@\n" + 
    "        @@\n" + 
    "  ____  @\n" + 
    " |  _ \\ @\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    " |____/ @\n" + 
    "        @@\n" + 
    "  _____ @\n" + 
    " | ____|@\n" + 
    " |  _|  @\n" + 
    " | |___ @\n" + 
    " |_____|@\n" + 
    "        @@\n" + 
    "  _____ @\n" + 
    " |  ___|@\n" + 
    " | |_   @\n" + 
    " |  _|  @\n" + 
    " |_|    @\n" + 
    "        @@\n" + 
    "   ____ @\n" + 
    "  / ___|@\n" + 
    " | |  _ @\n" + 
    " | |_| |@\n" + 
    "  \\____|@\n" + 
    "        @@\n" + 
    "  _   _ @\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    " |  _  |@\n" + 
    " |_| |_|@\n" + 
    "        @@\n" + 
    "  ___ @\n" + 
    " |_ _|@\n" + 
    "  | | @\n" + 
    "  | | @\n" + 
    " |___|@\n" + 
    "      @@\n" + 
    "      _ @\n" + 
    "     | |@\n" + 
    "  _  | |@\n" + 
    " | |_| |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "  _  __@\n" + 
    " | |/ /@\n" + 
    " | ' / @\n" + 
    " | . \\ @\n" + 
    " |_|\\_\\@\n" + 
    "       @@\n" + 
    "  _     @\n" + 
    " | |    @\n" + 
    " | |    @\n" + 
    " | |___ @\n" + 
    " |_____|@\n" + 
    "        @@\n" + 
    "  __  __ @\n" + 
    " |  \\/  |@\n" + 
    " | |\\/| |@\n" + 
    " | |  | |@\n" + 
    " |_|  |_|@\n" + 
    "         @@\n" + 
    "  _   _ @\n" + 
    " | \\ | |@\n" + 
    " |  \\| |@\n" + 
    " | |\\  |@\n" + 
    " |_| \\_|@\n" + 
    "        @@\n" + 
    "   ___  @\n" + 
    "  / _ \\ @\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "  ____  @\n" + 
    " |  _ \\ @\n" + 
    " | |_) |@\n" + 
    " |  __/ @\n" + 
    " |_|    @\n" + 
    "        @@\n" + 
    "   ___  @\n" + 
    "  / _ \\ @\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\__\\_\\@\n" + 
    "        @@\n" + 
    "  ____  @\n" + 
    " |  _ \\ @\n" + 
    " | |_) |@\n" + 
    " |  _ < @\n" + 
    " |_| \\_\\@\n" + 
    "        @@\n" + 
    "  ____  @\n" + 
    " / ___| @\n" + 
    " \\___ \\ @\n" + 
    "  ___) |@\n" + 
    " |____/ @\n" + 
    "        @@\n" + 
    "  _____ @\n" + 
    " |_   _|@\n" + 
    "   | |  @\n" + 
    "   | |  @\n" + 
    "   |_|  @\n" + 
    "        @@\n" + 
    "  _   _ @\n" + 
    " | | | |@\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    " __     __@\n" + 
    " \\ \\   / /@\n" + 
    "  \\ \\ / / @\n" + 
    "   \\ V /  @\n" + 
    "    \\_/   @\n" + 
    "          @@\n" + 
    " __        __@\n" + 
    " \\ \\      / /@\n" + 
    "  \\ \\ /\\ / / @\n" + 
    "   \\ V  V /  @\n" + 
    "    \\_/\\_/   @\n" + 
    "             @@\n" + 
    " __  __@\n" + 
    " \\ \\/ /@\n" + 
    "  \\  / @\n" + 
    "  /  \\ @\n" + 
    " /_/\\_\\@\n" + 
    "       @@\n" + 
    " __   __@\n" + 
    " \\ \\ / /@\n" + 
    "  \\ V / @\n" + 
    "   | |  @\n" + 
    "   |_|  @\n" + 
    "        @@\n" + 
    "  _____@\n" + 
    " |__  /@\n" + 
    "   / / @\n" + 
    "  / /_ @\n" + 
    " /____|@\n" + 
    "       @@\n" + 
    "  __ @\n" + 
    " | _|@\n" + 
    " | | @\n" + 
    " | | @\n" + 
    " | | @\n" + 
    " |__|@@\n" + 
    " __    @\n" + 
    " \\ \\   @\n" + 
    "  \\ \\  @\n" + 
    "   \\ \\ @\n" + 
    "    \\_\\@\n" + 
    "       @@\n" + 
    "  __ @\n" + 
    " |_ |@\n" + 
    "  | |@\n" + 
    "  | |@\n" + 
    "  | |@\n" + 
    " |__|@@\n" + 
    "  /\\ @\n" + 
    " |/\\|@\n" + 
    "   $ @\n" + 
    "   $ @\n" + 
    "   $ @\n" + 
    "     @@\n" + 
    "        @\n" + 
    "        @\n" + 
    "        @\n" + 
    "        @\n" + 
    "  _____ @\n" + 
    " |_____|@@\n" + 
    "  _ @\n" + 
    " ( )@\n" + 
    "  \\|@\n" + 
    "  $ @\n" + 
    "  $ @\n" + 
    "    @@\n" + 
    "        @\n" + 
    "   __ _ @\n" + 
    "  / _` |@\n" + 
    " | (_| |@\n" + 
    "  \\__,_|@\n" + 
    "        @@\n" + 
    "  _     @\n" + 
    " | |__  @\n" + 
    " | '_ \\ @\n" + 
    " | |_) |@\n" + 
    " |_.__/ @\n" + 
    "        @@\n" + 
    "       @\n" + 
    "   ___ @\n" + 
    "  / __|@\n" + 
    " | (__ @\n" + 
    "  \\___|@\n" + 
    "       @@\n" + 
    "      _ @\n" + 
    "   __| |@\n" + 
    "  / _` |@\n" + 
    " | (_| |@\n" + 
    "  \\__,_|@\n" + 
    "        @@\n" + 
    "       @\n" + 
    "   ___ @\n" + 
    "  / _ \\@\n" + 
    " |  __/@\n" + 
    "  \\___|@\n" + 
    "       @@\n" + 
    "   __ @\n" + 
    "  / _|@\n" + 
    " | |_ @\n" + 
    " |  _|@\n" + 
    " |_|  @\n" + 
    "      @@\n" + 
    "        @\n" + 
    "   __ _ @\n" + 
    "  / _` |@\n" + 
    " | (_| |@\n" + 
    "  \\__, |@\n" + 
    "  |___/ @@\n" + 
    "  _     @\n" + 
    " | |__  @\n" + 
    " | '_ \\ @\n" + 
    " | | | |@\n" + 
    " |_| |_|@\n" + 
    "        @@\n" + 
    "  _ @\n" + 
    " (_)@\n" + 
    " | |@\n" + 
    " | |@\n" + 
    " |_|@\n" + 
    "    @@\n" + 
    "    _ @\n" + 
    "   (_)@\n" + 
    "   | |@\n" + 
    "   | |@\n" + 
    "  _/ |@\n" + 
    " |__/ @@\n" + 
    "  _    @\n" + 
    " | | __@\n" + 
    " | |/ /@\n" + 
    " |   < @\n" + 
    " |_|\\_\\@\n" + 
    "       @@\n" + 
    "  _ @\n" + 
    " | |@\n" + 
    " | |@\n" + 
    " | |@\n" + 
    " |_|@\n" + 
    "    @@\n" + 
    "            @\n" + 
    "  _ __ ___  @\n" + 
    " | '_ ` _ \\ @\n" + 
    " | | | | | |@\n" + 
    " |_| |_| |_|@\n" + 
    "            @@\n" + 
    "        @\n" + 
    "  _ __  @\n" + 
    " | '_ \\ @\n" + 
    " | | | |@\n" + 
    " |_| |_|@\n" + 
    "        @@\n" + 
    "        @\n" + 
    "   ___  @\n" + 
    "  / _ \\ @\n" + 
    " | (_) |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "        @\n" + 
    "  _ __  @\n" + 
    " | '_ \\ @\n" + 
    " | |_) |@\n" + 
    " | .__/ @\n" + 
    " |_|    @@\n" + 
    "        @\n" + 
    "   __ _ @\n" + 
    "  / _` |@\n" + 
    " | (_| |@\n" + 
    "  \\__, |@\n" + 
    "     |_|@@\n" + 
    "       @\n" + 
    "  _ __ @\n" + 
    " | '__|@\n" + 
    " | |   @\n" + 
    " |_|   @\n" + 
    "       @@\n" + 
    "      @\n" + 
    "  ___ @\n" + 
    " / __|@\n" + 
    " \\__ \\@\n" + 
    " |___/@\n" + 
    "      @@\n" + 
    "  _   @\n" + 
    " | |_ @\n" + 
    " | __|@\n" + 
    " | |_ @\n" + 
    "  \\__|@\n" + 
    "      @@\n" + 
    "        @\n" + 
    "  _   _ @\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\__,_|@\n" + 
    "        @@\n" + 
    "        @\n" + 
    " __   __@\n" + 
    " \\ \\ / /@\n" + 
    "  \\ V / @\n" + 
    "   \\_/  @\n" + 
    "        @@\n" + 
    "           @\n" + 
    " __      __@\n" + 
    " \\ \\ /\\ / /@\n" + 
    "  \\ V  V / @\n" + 
    "   \\_/\\_/  @\n" + 
    "           @@\n" + 
    "       @\n" + 
    " __  __@\n" + 
    " \\ \\/ /@\n" + 
    "  >  < @\n" + 
    " /_/\\_\\@\n" + 
    "       @@\n" + 
    "        @\n" + 
    "  _   _ @\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\__, |@\n" + 
    "  |___/ @@\n" + 
    "      @\n" + 
    "  ____@\n" + 
    " |_  /@\n" + 
    "  / / @\n" + 
    " /___|@\n" + 
    "      @@\n" + 
    "    __@\n" + 
    "   / /@\n" + 
    "  | | @\n" + 
    " < <  @\n" + 
    "  | | @\n" + 
    "   \\_\\@@\n" + 
    "  _ @\n" + 
    " | |@\n" + 
    " | |@\n" + 
    " | |@\n" + 
    " | |@\n" + 
    " |_|@@\n" + 
    " __   @\n" + 
    " \\ \\  @\n" + 
    "  | | @\n" + 
    "   > >@\n" + 
    "  | | @\n" + 
    " /_/  @@\n" + 
    "  /\\/|@\n" + 
    " |/\\/ @\n" + 
    "   $  @\n" + 
    "   $  @\n" + 
    "   $  @\n" + 
    "      @@\n" + 
    "  _   _ @\n" + 
    " (_)_(_)@\n" + 
    "   /_\\  @\n" + 
    "  / _ \\ @\n" + 
    " /_/ \\_\\@\n" + 
    "        @@\n" + 
    "  _   _ @\n" + 
    " (_)_(_)@\n" + 
    "  / _ \\ @\n" + 
    " | |_| |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "  _   _ @\n" + 
    " (_) (_)@\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "  _   _ @\n" + 
    " (_)_(_)@\n" + 
    "  / _` |@\n" + 
    " | (_| |@\n" + 
    "  \\__,_|@\n" + 
    "        @@\n" + 
    "  _   _ @\n" + 
    " (_)_(_)@\n" + 
    "  / _ \\ @\n" + 
    " | (_) |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "  _   _ @\n" + 
    " (_) (_)@\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\__,_|@\n" + 
    "        @@\n" + 
    "   ___ @\n" + 
    "  / _ \\@\n" + 
    " | |/ /@\n" + 
    " | |\\ \\@\n" + 
    " | ||_/@\n" + 
    " |_|   @@\n" + 
    "160  NO-BREAK SPACE\n" + 
    " $@\n" + 
    " $@\n" + 
    " $@\n" + 
    " $@\n" + 
    " $@\n" + 
    " $@@\n" + 
    "161  INVERTED EXCLAMATION MARK\n" + 
    "  _ @\n" + 
    " (_)@\n" + 
    " | |@\n" + 
    " | |@\n" + 
    " |_|@\n" + 
    "    @@\n" + 
    "162  CENT SIGN\n" + 
    "    _  @\n" + 
    "   | | @\n" + 
    "  / __)@\n" + 
    " | (__ @\n" + 
    "  \\   )@\n" + 
    "   |_| @@\n" + 
    "163  POUND SIGN\n" + 
    "    ___  @\n" + 
    "   / ,_\\ @\n" + 
    " _| |_   @\n" + 
    "  | |___ @\n" + 
    " (_,____|@\n" + 
    "         @@\n" + 
    "164  CURRENCY SIGN\n" + 
    " /\\___/\\@\n" + 
    " \\  _  /@\n" + 
    " | (_) |@\n" + 
    " / ___ \\@\n" + 
    " \\/   \\/@\n" + 
    "        @@\n" + 
    "165  YEN SIGN\n" + 
    "  __ __ @\n" + 
    "  \\ V / @\n" + 
    " |__ __|@\n" + 
    " |__ __|@\n" + 
    "   |_|  @\n" + 
    "        @@\n" + 
    "166  BROKEN BAR\n" + 
    "  _ @\n" + 
    " | |@\n" + 
    " |_|@\n" + 
    "  _ @\n" + 
    " | |@\n" + 
    " |_|@@\n" + 
    "167  SECTION SIGN\n" + 
    "    __ @\n" + 
    "  _/ _)@\n" + 
    " / \\ \\ @\n" + 
    " \\ \\\\ \\@\n" + 
    "  \\ \\_/@\n" + 
    " (__/  @@\n" + 
    "168  DIAERESIS\n" + 
    "  _   _ @\n" + 
    " (_) (_)@\n" + 
    "  $   $ @\n" + 
    "  $   $ @\n" + 
    "  $   $ @\n" + 
    "        @@\n" + 
    "169  COPYRIGHT SIGN\n" + 
    "    _____   @\n" + 
    "   / ___ \\  @\n" + 
    "  / / __| \\ @\n" + 
    " | | (__   |@\n" + 
    "  \\ \\___| / @\n" + 
    "   \\_____/  @@\n" + 
    "170  FEMININE ORDINAL INDICATOR\n" + 
    "  __ _ @\n" + 
    " / _` |@\n" + 
    " \\__,_|@\n" + 
    " |____|@\n" + 
    "    $  @\n" + 
    "       @@\n" + 
    "171  LEFT-POINTING DOUBLE ANGLE QUOTATION MARK\n" + 
    "   ____@\n" + 
    "  / / /@\n" + 
    " / / / @\n" + 
    " \\ \\ \\ @\n" + 
    "  \\_\\_\\@\n" + 
    "       @@\n" + 
    "172  NOT SIGN\n" + 
    "        @\n" + 
    "  _____ @\n" + 
    " |___  |@\n" + 
    "     |_|@\n" + 
    "    $   @\n" + 
    "        @@\n" + 
    "173  SOFT HYPHEN\n" + 
    "       @\n" + 
    "       @\n" + 
    "  ____ @\n" + 
    " |____|@\n" + 
    "    $  @\n" + 
    "       @@\n" + 
    "174  REGISTERED SIGN\n" + 
    "    _____   @\n" + 
    "   / ___ \\  @\n" + 
    "  / | _ \\ \\ @\n" + 
    " |  |   /  |@\n" + 
    "  \\ |_|_\\ / @\n" + 
    "   \\_____/  @@\n" + 
    "175  MACRON\n" + 
    "  _____ @\n" + 
    " |_____|@\n" + 
    "    $   @\n" + 
    "    $   @\n" + 
    "    $   @\n" + 
    "        @@\n" + 
    "176  DEGREE SIGN\n" + 
    "   __  @\n" + 
    "  /  \\ @\n" + 
    " | () |@\n" + 
    "  \\__/ @\n" + 
    "    $  @\n" + 
    "       @@\n" + 
    "177  PLUS-MINUS SIGN\n" + 
    "    _   @\n" + 
    "  _| |_ @\n" + 
    " |_   _|@\n" + 
    "  _|_|_ @\n" + 
    " |_____|@\n" + 
    "        @@\n" + 
    "178  SUPERSCRIPT TWO\n" + 
    "  ___ @\n" + 
    " |_  )@\n" + 
    "  / / @\n" + 
    " /___|@\n" + 
    "   $  @\n" + 
    "      @@\n" + 
    "179  SUPERSCRIPT THREE\n" + 
    "  ____@\n" + 
    " |__ /@\n" + 
    "  |_ \\@\n" + 
    " |___/@\n" + 
    "   $  @\n" + 
    "      @@\n" + 
    "180  ACUTE ACCENT\n" + 
    "  __@\n" + 
    " /_/@\n" + 
    "  $ @\n" + 
    "  $ @\n" + 
    "  $ @\n" + 
    "    @@\n" + 
    "181  MICRO SIGN\n" + 
    "        @\n" + 
    "  _   _ @\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    " | ._,_|@\n" + 
    " |_|    @@\n" + 
    "182  PILCROW SIGN\n" + 
    "   _____ @\n" + 
    "  /     |@\n" + 
    " | (| | |@\n" + 
    "  \\__ | |@\n" + 
    "    |_|_|@\n" + 
    "         @@\n" + 
    "183  MIDDLE DOT\n" + 
    "    @\n" + 
    "  _ @\n" + 
    " (_)@\n" + 
    "  $ @\n" + 
    "  $ @\n" + 
    "    @@\n" + 
    "184  CEDILLA\n" + 
    "    @\n" + 
    "    @\n" + 
    "    @\n" + 
    "    @\n" + 
    "  _ @\n" + 
    " )_)@@\n" + 
    "185  SUPERSCRIPT ONE\n" + 
    "  _ @\n" + 
    " / |@\n" + 
    " | |@\n" + 
    " |_|@\n" + 
    "  $ @\n" + 
    "    @@\n" + 
    "186  MASCULINE ORDINAL INDICATOR\n" + 
    "  ___ @\n" + 
    " / _ \\@\n" + 
    " \\___/@\n" + 
    " |___|@\n" + 
    "   $  @\n" + 
    "      @@\n" + 
    "187  RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK\n" + 
    " ____  @\n" + 
    " \\ \\ \\ @\n" + 
    "  \\ \\ \\@\n" + 
    "  / / /@\n" + 
    " /_/_/ @\n" + 
    "       @@\n" + 
    "188  VULGAR FRACTION ONE QUARTER\n" + 
    "  _   __    @\n" + 
    " / | / / _  @\n" + 
    " | |/ / | | @\n" + 
    " |_/ /|_  _|@\n" + 
    "  /_/   |_| @\n" + 
    "            @@\n" + 
    "189  VULGAR FRACTION ONE HALF\n" + 
    "  _   __   @\n" + 
    " / | / /__ @\n" + 
    " | |/ /_  )@\n" + 
    " |_/ / / / @\n" + 
    "  /_/ /___|@\n" + 
    "           @@\n" + 
    "190  VULGAR FRACTION THREE QUARTERS\n" + 
    "  ____  __    @\n" + 
    " |__ / / / _  @\n" + 
    "  |_ \\/ / | | @\n" + 
    " |___/ /|_  _|@\n" + 
    "    /_/   |_| @\n" + 
    "              @@\n" + 
    "191  INVERTED QUESTION MARK\n" + 
    "   _  @\n" + 
    "  (_) @\n" + 
    "  | | @\n" + 
    " / /_ @\n" + 
    " \\___|@\n" + 
    "      @@\n" + 
    "192  LATIN CAPITAL LETTER A WITH GRAVE\n" + 
    "   __   @\n" + 
    "   \\_\\  @\n" + 
    "   /_\\  @\n" + 
    "  / _ \\ @\n" + 
    " /_/ \\_\\@\n" + 
    "        @@\n" + 
    "193  LATIN CAPITAL LETTER A WITH ACUTE\n" + 
    "    __  @\n" + 
    "   /_/  @\n" + 
    "   /_\\  @\n" + 
    "  / _ \\ @\n" + 
    " /_/ \\_\\@\n" + 
    "        @@\n" + 
    "194  LATIN CAPITAL LETTER A WITH CIRCUMFLEX\n" + 
    "   //\\  @\n" + 
    "  |/_\\| @\n" + 
    "   /_\\  @\n" + 
    "  / _ \\ @\n" + 
    " /_/ \\_\\@\n" + 
    "        @@\n" + 
    "195  LATIN CAPITAL LETTER A WITH TILDE\n" + 
    "   /\\/| @\n" + 
    "  |/\\/  @\n" + 
    "   /_\\  @\n" + 
    "  / _ \\ @\n" + 
    " /_/ \\_\\@\n" + 
    "        @@\n" + 
    "196  LATIN CAPITAL LETTER A WITH DIAERESIS\n" + 
    "  _   _ @\n" + 
    " (_)_(_)@\n" + 
    "   /_\\  @\n" + 
    "  / _ \\ @\n" + 
    " /_/ \\_\\@\n" + 
    "        @@\n" + 
    "197  LATIN CAPITAL LETTER A WITH RING ABOVE\n" + 
    "    _   @\n" + 
    "   (o)  @\n" + 
    "   /_\\  @\n" + 
    "  / _ \\ @\n" + 
    " /_/ \\_\\@\n" + 
    "        @@\n" + 
    "198  LATIN CAPITAL LETTER AE\n" + 
    "     ______ @\n" + 
    "    /  ____|@\n" + 
    "   / _  _|  @\n" + 
    "  / __ |___ @\n" + 
    " /_/ |_____|@\n" + 
    "            @@\n" + 
    "199  LATIN CAPITAL LETTER C WITH CEDILLA\n" + 
    "   ____ @\n" + 
    "  / ___|@\n" + 
    " | |    @\n" + 
    " | |___ @\n" + 
    "  \\____|@\n" + 
    "    )_) @@\n" + 
    "200  LATIN CAPITAL LETTER E WITH GRAVE\n" + 
    "   __   @\n" + 
    "  _\\_\\_ @\n" + 
    " | ____|@\n" + 
    " |  _|_ @\n" + 
    " |_____|@\n" + 
    "        @@\n" + 
    "201  LATIN CAPITAL LETTER E WITH ACUTE\n" + 
    "    __  @\n" + 
    "  _/_/_ @\n" + 
    " | ____|@\n" + 
    " |  _|_ @\n" + 
    " |_____|@\n" + 
    "        @@\n" + 
    "202  LATIN CAPITAL LETTER E WITH CIRCUMFLEX\n" + 
    "   //\\  @\n" + 
    "  |/_\\| @\n" + 
    " | ____|@\n" + 
    " |  _|_ @\n" + 
    " |_____|@\n" + 
    "        @@\n" + 
    "203  LATIN CAPITAL LETTER E WITH DIAERESIS\n" + 
    "  _   _ @\n" + 
    " (_)_(_)@\n" + 
    " | ____|@\n" + 
    " |  _|_ @\n" + 
    " |_____|@\n" + 
    "        @@\n" + 
    "204  LATIN CAPITAL LETTER I WITH GRAVE\n" + 
    "  __  @\n" + 
    "  \\_\\ @\n" + 
    " |_ _|@\n" + 
    "  | | @\n" + 
    " |___|@\n" + 
    "      @@\n" + 
    "205  LATIN CAPITAL LETTER I WITH ACUTE\n" + 
    "   __ @\n" + 
    "  /_/ @\n" + 
    " |_ _|@\n" + 
    "  | | @\n" + 
    " |___|@\n" + 
    "      @@\n" + 
    "206  LATIN CAPITAL LETTER I WITH CIRCUMFLEX\n" + 
    "  //\\ @\n" + 
    " |/_\\|@\n" + 
    " |_ _|@\n" + 
    "  | | @\n" + 
    " |___|@\n" + 
    "      @@\n" + 
    "207  LATIN CAPITAL LETTER I WITH DIAERESIS\n" + 
    "  _   _ @\n" + 
    " (_)_(_)@\n" + 
    "  |_ _| @\n" + 
    "   | |  @\n" + 
    "  |___| @\n" + 
    "        @@\n" + 
    "208  LATIN CAPITAL LETTER ETH\n" + 
    "    ____  @\n" + 
    "   |  _ \\ @\n" + 
    "  _| |_| |@\n" + 
    " |__ __| |@\n" + 
    "   |____/ @\n" + 
    "          @@\n" + 
    "209  LATIN CAPITAL LETTER N WITH TILDE\n" + 
    "   /\\/|@\n" + 
    "  |/\\/ @\n" + 
    " | \\| |@\n" + 
    " | .` |@\n" + 
    " |_|\\_|@\n" + 
    "       @@\n" + 
    "210  LATIN CAPITAL LETTER O WITH GRAVE\n" + 
    "   __   @\n" + 
    "   \\_\\  @\n" + 
    "  / _ \\ @\n" + 
    " | |_| |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "211  LATIN CAPITAL LETTER O WITH ACUTE\n" + 
    "    __  @\n" + 
    "   /_/  @\n" + 
    "  / _ \\ @\n" + 
    " | |_| |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "212  LATIN CAPITAL LETTER O WITH CIRCUMFLEX\n" + 
    "   //\\  @\n" + 
    "  |/_\\| @\n" + 
    "  / _ \\ @\n" + 
    " | |_| |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "213  LATIN CAPITAL LETTER O WITH TILDE\n" + 
    "   /\\/| @\n" + 
    "  |/\\/  @\n" + 
    "  / _ \\ @\n" + 
    " | |_| |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "214  LATIN CAPITAL LETTER O WITH DIAERESIS\n" + 
    "  _   _ @\n" + 
    " (_)_(_)@\n" + 
    "  / _ \\ @\n" + 
    " | |_| |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "215  MULTIPLICATION SIGN\n" + 
    "     @\n" + 
    "     @\n" + 
    " /\\/\\@\n" + 
    " >  <@\n" + 
    " \\/\\/@\n" + 
    "     @@\n" + 
    "216  LATIN CAPITAL LETTER O WITH STROKE\n" + 
    "   ____ @\n" + 
    "  / _// @\n" + 
    " | |// |@\n" + 
    " | //| |@\n" + 
    "  //__/ @\n" + 
    "        @@\n" + 
    "217  LATIN CAPITAL LETTER U WITH GRAVE\n" + 
    "   __   @\n" + 
    "  _\\_\\_ @\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "218  LATIN CAPITAL LETTER U WITH ACUTE\n" + 
    "    __  @\n" + 
    "  _/_/_ @\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "219  LATIN CAPITAL LETTER U WITH CIRCUMFLEX\n" + 
    "   //\\  @\n" + 
    "  |/ \\| @\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "220  LATIN CAPITAL LETTER U WITH DIAERESIS\n" + 
    "  _   _ @\n" + 
    " (_) (_)@\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "221  LATIN CAPITAL LETTER Y WITH ACUTE\n" + 
    "    __  @\n" + 
    " __/_/__@\n" + 
    " \\ \\ / /@\n" + 
    "  \\ V / @\n" + 
    "   |_|  @\n" + 
    "        @@\n" + 
    "222  LATIN CAPITAL LETTER THORN\n" + 
    "  _     @\n" + 
    " | |___ @\n" + 
    " |  __ \\@\n" + 
    " |  ___/@\n" + 
    " |_|    @\n" + 
    "        @@\n" + 
    "223  LATIN SMALL LETTER SHARP S\n" + 
    "   ___ @\n" + 
    "  / _ \\@\n" + 
    " | |/ /@\n" + 
    " | |\\ \\@\n" + 
    " | ||_/@\n" + 
    " |_|   @@\n" + 
    "224  LATIN SMALL LETTER A WITH GRAVE\n" + 
    "   __   @\n" + 
    "   \\_\\_ @\n" + 
    "  / _` |@\n" + 
    " | (_| |@\n" + 
    "  \\__,_|@\n" + 
    "        @@\n" + 
    "225  LATIN SMALL LETTER A WITH ACUTE\n" + 
    "    __  @\n" + 
    "   /_/_ @\n" + 
    "  / _` |@\n" + 
    " | (_| |@\n" + 
    "  \\__,_|@\n" + 
    "        @@\n" + 
    "226  LATIN SMALL LETTER A WITH CIRCUMFLEX\n" + 
    "   //\\  @\n" + 
    "  |/_\\| @\n" + 
    "  / _` |@\n" + 
    " | (_| |@\n" + 
    "  \\__,_|@\n" + 
    "        @@\n" + 
    "227  LATIN SMALL LETTER A WITH TILDE\n" + 
    "   /\\/| @\n" + 
    "  |/\\/_ @\n" + 
    "  / _` |@\n" + 
    " | (_| |@\n" + 
    "  \\__,_|@\n" + 
    "        @@\n" + 
    "228  LATIN SMALL LETTER A WITH DIAERESIS\n" + 
    "  _   _ @\n" + 
    " (_)_(_)@\n" + 
    "  / _` |@\n" + 
    " | (_| |@\n" + 
    "  \\__,_|@\n" + 
    "        @@\n" + 
    "229  LATIN SMALL LETTER A WITH RING ABOVE\n" + 
    "    __  @\n" + 
    "   (()) @\n" + 
    "  / _ '|@\n" + 
    " | (_| |@\n" + 
    "  \\__,_|@\n" + 
    "        @@\n" + 
    "230  LATIN SMALL LETTER AE\n" + 
    "           @\n" + 
    "   __ ____ @\n" + 
    "  / _`  _ \\@\n" + 
    " | (_|  __/@\n" + 
    "  \\__,____|@\n" + 
    "           @@\n" + 
    "231  LATIN SMALL LETTER C WITH CEDILLA\n" + 
    "       @\n" + 
    "   ___ @\n" + 
    "  / __|@\n" + 
    " | (__ @\n" + 
    "  \\___|@\n" + 
    "   )_) @@\n" + 
    "232  LATIN SMALL LETTER E WITH GRAVE\n" + 
    "   __  @\n" + 
    "   \\_\\ @\n" + 
    "  / _ \\@\n" + 
    " |  __/@\n" + 
    "  \\___|@\n" + 
    "       @@\n" + 
    "233  LATIN SMALL LETTER E WITH ACUTE\n" + 
    "    __ @\n" + 
    "   /_/ @\n" + 
    "  / _ \\@\n" + 
    " |  __/@\n" + 
    "  \\___|@\n" + 
    "       @@\n" + 
    "234  LATIN SMALL LETTER E WITH CIRCUMFLEX\n" + 
    "   //\\ @\n" + 
    "  |/_\\|@\n" + 
    "  / _ \\@\n" + 
    " |  __/@\n" + 
    "  \\___|@\n" + 
    "       @@\n" + 
    "235  LATIN SMALL LETTER E WITH DIAERESIS\n" + 
    "  _   _ @\n" + 
    " (_)_(_)@\n" + 
    "  / _ \\ @\n" + 
    " |  __/ @\n" + 
    "  \\___| @\n" + 
    "        @@\n" + 
    "236  LATIN SMALL LETTER I WITH GRAVE\n" + 
    " __ @\n" + 
    " \\_\\@\n" + 
    " | |@\n" + 
    " | |@\n" + 
    " |_|@\n" + 
    "    @@\n" + 
    "237  LATIN SMALL LETTER I WITH ACUTE\n" + 
    "  __@\n" + 
    " /_/@\n" + 
    " | |@\n" + 
    " | |@\n" + 
    " |_|@\n" + 
    "    @@\n" + 
    "238  LATIN SMALL LETTER I WITH CIRCUMFLEX\n" + 
    "  //\\ @\n" + 
    " |/_\\|@\n" + 
    "  | | @\n" + 
    "  | | @\n" + 
    "  |_| @\n" + 
    "      @@\n" + 
    "239  LATIN SMALL LETTER I WITH DIAERESIS\n" + 
    "  _   _ @\n" + 
    " (_)_(_)@\n" + 
    "   | |  @\n" + 
    "   | |  @\n" + 
    "   |_|  @\n" + 
    "        @@\n" + 
    "240  LATIN SMALL LETTER ETH\n" + 
    "   /\\/\\ @\n" + 
    "   >  < @\n" + 
    "  _\\/\\ |@\n" + 
    " / __` |@\n" + 
    " \\____/ @\n" + 
    "        @@\n" + 
    "241  LATIN SMALL LETTER N WITH TILDE\n" + 
    "   /\\/| @\n" + 
    "  |/\\/  @\n" + 
    " | '_ \\ @\n" + 
    " | | | |@\n" + 
    " |_| |_|@\n" + 
    "        @@\n" + 
    "242  LATIN SMALL LETTER O WITH GRAVE\n" + 
    "   __   @\n" + 
    "   \\_\\  @\n" + 
    "  / _ \\ @\n" + 
    " | (_) |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "243  LATIN SMALL LETTER O WITH ACUTE\n" + 
    "    __  @\n" + 
    "   /_/  @\n" + 
    "  / _ \\ @\n" + 
    " | (_) |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "244  LATIN SMALL LETTER O WITH CIRCUMFLEX\n" + 
    "   //\\  @\n" + 
    "  |/_\\| @\n" + 
    "  / _ \\ @\n" + 
    " | (_) |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "245  LATIN SMALL LETTER O WITH TILDE\n" + 
    "   /\\/| @\n" + 
    "  |/\\/  @\n" + 
    "  / _ \\ @\n" + 
    " | (_) |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "246  LATIN SMALL LETTER O WITH DIAERESIS\n" + 
    "  _   _ @\n" + 
    " (_)_(_)@\n" + 
    "  / _ \\ @\n" + 
    " | (_) |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "247  DIVISION SIGN\n" + 
    "        @\n" + 
    "    _   @\n" + 
    "  _(_)_ @\n" + 
    " |_____|@\n" + 
    "   (_)  @\n" + 
    "        @@\n" + 
    "248  LATIN SMALL LETTER O WITH STROKE\n" + 
    "         @\n" + 
    "   ____  @\n" + 
    "  / _//\\ @\n" + 
    " | (//) |@\n" + 
    "  \\//__/ @\n" + 
    "         @@\n" + 
    "249  LATIN SMALL LETTER U WITH GRAVE\n" + 
    "   __   @\n" + 
    "  _\\_\\_ @\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\__,_|@\n" + 
    "        @@\n" + 
    "250  LATIN SMALL LETTER U WITH ACUTE\n" + 
    "    __  @\n" + 
    "  _/_/_ @\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\__,_|@\n" + 
    "        @@\n" + 
    "251  LATIN SMALL LETTER U WITH CIRCUMFLEX\n" + 
    "   //\\  @\n" + 
    "  |/ \\| @\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\__,_|@\n" + 
    "        @@\n" + 
    "252  LATIN SMALL LETTER U WITH DIAERESIS\n" + 
    "  _   _ @\n" + 
    " (_) (_)@\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\__,_|@\n" + 
    "        @@\n" + 
    "253  LATIN SMALL LETTER Y WITH ACUTE\n" + 
    "    __  @\n" + 
    "  _/_/_ @\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\__, |@\n" + 
    "  |___/ @@\n" + 
    "254  LATIN SMALL LETTER THORN\n" + 
    "  _     @\n" + 
    " | |__  @\n" + 
    " | '_ \\ @\n" + 
    " | |_) |@\n" + 
    " | .__/ @\n" + 
    " |_|    @@\n" + 
    "255  LATIN SMALL LETTER Y WITH DIAERESIS\n" + 
    "  _   _ @\n" + 
    " (_) (_)@\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\__, |@\n" + 
    "  |___/ @@\n" + 
    "0x0100  LATIN CAPITAL LETTER A WITH MACRON\n" + 
    "   ____ @\n" + 
    "  /___/ @\n" + 
    "   /_\\  @\n" + 
    "  / _ \\ @\n" + 
    " /_/ \\_\\@\n" + 
    "        @@\n" + 
    "0x0101  LATIN SMALL LETTER A WITH MACRON\n" + 
    "    ___ @\n" + 
    "   /_ _/@\n" + 
    "  / _` |@\n" + 
    " | (_| |@\n" + 
    "  \\__,_|@\n" + 
    "        @@\n" + 
    "0x0102  LATIN CAPITAL LETTER A WITH BREVE\n" + 
    "  _   _ @\n" + 
    "  \\\\_// @\n" + 
    "   /_\\  @\n" + 
    "  / _ \\ @\n" + 
    " /_/ \\_\\@\n" + 
    "        @@\n" + 
    "0x0103  LATIN SMALL LETTER A WITH BREVE\n" + 
    "   \\_/  @\n" + 
    "   ___  @\n" + 
    "  / _` |@\n" + 
    " | (_| |@\n" + 
    "  \\__,_|@\n" + 
    "        @@\n" + 
    "0x0104  LATIN CAPITAL LETTER A WITH OGONEK\n" + 
    "        @\n" + 
    "    _   @\n" + 
    "   /_\\  @\n" + 
    "  / _ \\ @\n" + 
    " /_/ \\_\\@\n" + 
    "     (_(@@\n" + 
    "0x0105  LATIN SMALL LETTER A WITH OGONEK\n" + 
    "        @\n" + 
    "   __ _ @\n" + 
    "  / _` |@\n" + 
    " | (_| |@\n" + 
    "  \\__,_|@\n" + 
    "     (_(@@\n" + 
    "0x0106  LATIN CAPITAL LETTER C WITH ACUTE\n" + 
    "     __ @\n" + 
    "   _/_/ @\n" + 
    "  / ___|@\n" + 
    " | |___ @\n" + 
    "  \\____|@\n" + 
    "        @@\n" + 
    "0x0107  LATIN SMALL LETTER C WITH ACUTE\n" + 
    "    __ @\n" + 
    "   /__/@\n" + 
    "  / __|@\n" + 
    " | (__ @\n" + 
    "  \\___|@\n" + 
    "       @@\n" + 
    "0x0108  LATIN CAPITAL LETTER C WITH CIRCUMFLEX\n" + 
    "     /\\ @\n" + 
    "   _//\\\\@\n" + 
    "  / ___|@\n" + 
    " | |___ @\n" + 
    "  \\____|@\n" + 
    "        @@\n" + 
    "0x0109  LATIN SMALL LETTER C WITH CIRCUMFLEX\n" + 
    "    /\\ @\n" + 
    "   /_\\ @\n" + 
    "  / __|@\n" + 
    " | (__ @\n" + 
    "  \\___|@\n" + 
    "       @@\n" + 
    "0x010A  LATIN CAPITAL LETTER C WITH DOT ABOVE\n" + 
    "    []  @\n" + 
    "   ____ @\n" + 
    "  / ___|@\n" + 
    " | |___ @\n" + 
    "  \\____|@\n" + 
    "        @@\n" + 
    "0x010B  LATIN SMALL LETTER C WITH DOT ABOVE\n" + 
    "   []  @\n" + 
    "   ___ @\n" + 
    "  / __|@\n" + 
    " | (__ @\n" + 
    "  \\___|@\n" + 
    "       @@\n" + 
    "0x010C  LATIN CAPITAL LETTER C WITH CARON\n" + 
    "   \\\\// @\n" + 
    "   _\\/_ @\n" + 
    "  / ___|@\n" + 
    " | |___ @\n" + 
    "  \\____|@\n" + 
    "        @@\n" + 
    "0x010D  LATIN SMALL LETTER C WITH CARON\n" + 
    "   \\\\//@\n" + 
    "   _\\/ @\n" + 
    "  / __|@\n" + 
    " | (__ @\n" + 
    "  \\___|@\n" + 
    "       @@\n" + 
    "0x010E  LATIN CAPITAL LETTER D WITH CARON\n" + 
    "   \\\\// @\n" + 
    "  __\\/  @\n" + 
    " |  _ \\ @\n" + 
    " | |_| |@\n" + 
    " |____/ @\n" + 
    "        @@\n" + 
    "0x010F  LATIN SMALL LETTER D WITH CARON\n" + 
    "  \\/  _ @\n" + 
    "   __| |@\n" + 
    "  / _` |@\n" + 
    " | (_| |@\n" + 
    "  \\__,_|@\n" + 
    "        @@\n" + 
    "0x0110  LATIN CAPITAL LETTER D WITH STROKE\n" + 
    "   ____   @\n" + 
    "  |_ __ \\ @\n" + 
    " /| |/ | |@\n" + 
    " /|_|/_| |@\n" + 
    "  |_____/ @\n" + 
    "          @@\n" + 
    "0x0111  LATIN SMALL LETTER D WITH STROKE\n" + 
    "    ---|@\n" + 
    "   __| |@\n" + 
    "  / _` |@\n" + 
    " | (_| |@\n" + 
    "  \\__,_|@\n" + 
    "        @@\n" + 
    "0x0112  LATIN CAPITAL LETTER E WITH MACRON\n" + 
    "   ____ @\n" + 
    "  /___/ @\n" + 
    " | ____|@\n" + 
    " |  _|_ @\n" + 
    " |_____|@\n" + 
    "        @@\n" + 
    "0x0113  LATIN SMALL LETTER E WITH MACRON\n" + 
    "    ____@\n" + 
    "   /_ _/@\n" + 
    "  / _ \\ @\n" + 
    " |  __/ @\n" + 
    "  \\___| @\n" + 
    "        @@\n" + 
    "0x0114  LATIN CAPITAL LETTER E WITH BREVE\n" + 
    "  _   _ @\n" + 
    "  \\\\_// @\n" + 
    " | ____|@\n" + 
    " |  _|_ @\n" + 
    " |_____|@\n" + 
    "        @@\n" + 
    "0x0115  LATIN SMALL LETTER E WITH BREVE\n" + 
    "  \\\\  //@\n" + 
    "    --  @\n" + 
    "  / _ \\ @\n" + 
    " |  __/ @\n" + 
    "  \\___| @\n" + 
    "        @@\n" + 
    "0x0116  LATIN CAPITAL LETTER E WITH DOT ABOVE\n" + 
    "    []  @\n" + 
    "  _____ @\n" + 
    " | ____|@\n" + 
    " |  _|_ @\n" + 
    " |_____|@\n" + 
    "        @@\n" + 
    "0x0117  LATIN SMALL LETTER E WITH DOT ABOVE\n" + 
    "    [] @\n" + 
    "    __ @\n" + 
    "  / _ \\@\n" + 
    " |  __/@\n" + 
    "  \\___|@\n" + 
    "       @@\n" + 
    "0x0118  LATIN CAPITAL LETTER E WITH OGONEK\n" + 
    "        @\n" + 
    "  _____ @\n" + 
    " | ____|@\n" + 
    " |  _|_ @\n" + 
    " |_____|@\n" + 
    "    (__(@@\n" + 
    "0x0119  LATIN SMALL LETTER E WITH OGONEK\n" + 
    "       @\n" + 
    "   ___ @\n" + 
    "  / _ \\@\n" + 
    " |  __/@\n" + 
    "  \\___|@\n" + 
    "    (_(@@\n" + 
    "0x011A  LATIN CAPITAL LETTER E WITH CARON\n" + 
    "   \\\\// @\n" + 
    "  __\\/_ @\n" + 
    " | ____|@\n" + 
    " |  _|_ @\n" + 
    " |_____|@\n" + 
    "        @@\n" + 
    "0x011B  LATIN SMALL LETTER E WITH CARON\n" + 
    "   \\\\//@\n" + 
    "    \\/ @\n" + 
    "  / _ \\@\n" + 
    " |  __/@\n" + 
    "  \\___|@\n" + 
    "       @@\n" + 
    "0x011C  LATIN CAPITAL LETTER G WITH CIRCUMFLEX\n" + 
    "   _/\\_ @\n" + 
    "  / ___|@\n" + 
    " | |  _ @\n" + 
    " | |_| |@\n" + 
    "  \\____|@\n" + 
    "        @@\n" + 
    "0x011D  LATIN SMALL LETTER G WITH CIRCUMFLEX\n" + 
    "     /\\ @\n" + 
    "   _/_ \\@\n" + 
    "  / _` |@\n" + 
    " | (_| |@\n" + 
    "  \\__, |@\n" + 
    "  |___/ @@\n" + 
    "0x011E  LATIN CAPITAL LETTER G WITH BREVE\n" + 
    "   _\\/_ @\n" + 
    "  / ___|@\n" + 
    " | |  _ @\n" + 
    " | |_| |@\n" + 
    "  \\____|@\n" + 
    "        @@\n" + 
    "0x011F  LATIN SMALL LETTER G WITH BREVE\n" + 
    "  \\___/ @\n" + 
    "   __ _ @\n" + 
    "  / _` |@\n" + 
    " | (_| |@\n" + 
    "  \\__, |@\n" + 
    "  |___/ @@\n" + 
    "0x0120  LATIN CAPITAL LETTER G WITH DOT ABOVE\n" + 
    "   _[]_ @\n" + 
    "  / ___|@\n" + 
    " | |  _ @\n" + 
    " | |_| |@\n" + 
    "  \\____|@\n" + 
    "        @@\n" + 
    "0x0121  LATIN SMALL LETTER G WITH DOT ABOVE\n" + 
    "   []   @\n" + 
    "   __ _ @\n" + 
    "  / _` |@\n" + 
    " | (_| |@\n" + 
    "  \\__, |@\n" + 
    "  |___/ @@\n" + 
    "0x0122  LATIN CAPITAL LETTER G WITH CEDILLA\n" + 
    "   ____ @\n" + 
    "  / ___|@\n" + 
    " | |  _ @\n" + 
    " | |_| |@\n" + 
    "  \\____|@\n" + 
    "   )__) @@\n" + 
    "0x0123  LATIN SMALL LETTER G WITH CEDILLA\n" + 
    "        @\n" + 
    "   __ _ @\n" + 
    "  / _` |@\n" + 
    " | (_| |@\n" + 
    "  \\__, |@\n" + 
    "  |_))))@@\n" + 
    "0x0124  LATIN CAPITAL LETTER H WITH CIRCUMFLEX\n" + 
    "  _/ \\_ @\n" + 
    " | / \\ |@\n" + 
    " | |_| |@\n" + 
    " |  _  |@\n" + 
    " |_| |_|@\n" + 
    "        @@\n" + 
    "0x0125  LATIN SMALL LETTER H WITH CIRCUMFLEX\n" + 
    "  _  /\\ @\n" + 
    " | |//\\ @\n" + 
    " | '_ \\ @\n" + 
    " | | | |@\n" + 
    " |_| |_|@\n" + 
    "        @@\n" + 
    "0x0126  LATIN CAPITAL LETTER H WITH STROKE\n" + 
    "  _   _ @\n" + 
    " | |=| |@\n" + 
    " | |_| |@\n" + 
    " |  _  |@\n" + 
    " |_| |_|@\n" + 
    "        @@\n" + 
    "0x0127  LATIN SMALL LETTER H WITH STROKE\n" + 
    "  _     @\n" + 
    " |=|__  @\n" + 
    " | '_ \\ @\n" + 
    " | | | |@\n" + 
    " |_| |_|@\n" + 
    "        @@\n" + 
    "0x0128  LATIN CAPITAL LETTER I WITH TILDE\n" + 
    "  /\\//@\n" + 
    " |_ _|@\n" + 
    "  | | @\n" + 
    "  | | @\n" + 
    " |___|@\n" + 
    "      @@\n" + 
    "0x0129  LATIN SMALL LETTER I WITH TILDE\n" + 
    "    @\n" + 
    " /\\/@\n" + 
    " | |@\n" + 
    " | |@\n" + 
    " |_|@\n" + 
    "    @@\n" + 
    "0x012A  LATIN CAPITAL LETTER I WITH MACRON\n" + 
    " /___/@\n" + 
    " |_ _|@\n" + 
    "  | | @\n" + 
    "  | | @\n" + 
    " |___|@\n" + 
    "      @@\n" + 
    "0x012B  LATIN SMALL LETTER I WITH MACRON\n" + 
    "  ____@\n" + 
    " /___/@\n" + 
    "  | | @\n" + 
    "  | | @\n" + 
    "  |_| @\n" + 
    "      @@\n" + 
    "0x012C  LATIN CAPITAL LETTER I WITH BREVE\n" + 
    "  \\__/@\n" + 
    " |_ _|@\n" + 
    "  | | @\n" + 
    "  | | @\n" + 
    " |___|@\n" + 
    "      @@\n" + 
    "0x012D  LATIN SMALL LETTER I WITH BREVE\n" + 
    "    @\n" + 
    " \\_/@\n" + 
    " | |@\n" + 
    " | |@\n" + 
    " |_|@\n" + 
    "    @@\n" + 
    "0x012E  LATIN CAPITAL LETTER I WITH OGONEK\n" + 
    "  ___ @\n" + 
    " |_ _|@\n" + 
    "  | | @\n" + 
    "  | | @\n" + 
    " |___|@\n" + 
    "  (__(@@\n" + 
    "0x012F  LATIN SMALL LETTER I WITH OGONEK\n" + 
    "  _  @\n" + 
    " (_) @\n" + 
    " | | @\n" + 
    " | | @\n" + 
    " |_|_@\n" + 
    "  (_(@@\n" + 
    "0x0130  LATIN CAPITAL LETTER I WITH DOT ABOVE\n" + 
    "  _[] @\n" + 
    " |_ _|@\n" + 
    "  | | @\n" + 
    "  | | @\n" + 
    " |___|@\n" + 
    "      @@\n" + 
    "0x0131  LATIN SMALL LETTER DOTLESS I\n" + 
    "    @\n" + 
    "  _ @\n" + 
    " | |@\n" + 
    " | |@\n" + 
    " |_|@\n" + 
    "    @@\n" + 
    "0x0132  LATIN CAPITAL LIGATURE IJ\n" + 
    "  ___  _ @\n" + 
    " |_ _|| |@\n" + 
    "  | | | |@\n" + 
    "  | |_| |@\n" + 
    " |__|__/ @\n" + 
    "         @@\n" + 
    "0x0133  LATIN SMALL LIGATURE IJ\n" + 
    "  _   _ @\n" + 
    " (_) (_)@\n" + 
    " | | | |@\n" + 
    " | | | |@\n" + 
    " |_|_/ |@\n" + 
    "   |__/ @@\n" + 
    "0x0134  LATIN CAPITAL LETTER J WITH CIRCUMFLEX\n" + 
    "      /\\ @\n" + 
    "     /_\\|@\n" + 
    "  _  | | @\n" + 
    " | |_| | @\n" + 
    "  \\___/  @\n" + 
    "         @@\n" + 
    "0x0135  LATIN SMALL LETTER J WITH CIRCUMFLEX\n" + 
    "    /\\@\n" + 
    "   /_\\@\n" + 
    "   | |@\n" + 
    "   | |@\n" + 
    "  _/ |@\n" + 
    " |__/ @@\n" + 
    "0x0136  LATIN CAPITAL LETTER K WITH CEDILLA\n" + 
    "  _  _  @\n" + 
    " | |/ / @\n" + 
    " | ' /  @\n" + 
    " | . \\  @\n" + 
    " |_|\\_\\ @\n" + 
    "    )__)@@\n" + 
    "0x0137  LATIN SMALL LETTER K WITH CEDILLA\n" + 
    "  _    @\n" + 
    " | | __@\n" + 
    " | |/ /@\n" + 
    " |   < @\n" + 
    " |_|\\_\\@\n" + 
    "    )_)@@\n" + 
    "0x0138  LATIN SMALL LETTER KRA\n" + 
    "       @\n" + 
    "  _ __ @\n" + 
    " | |/ \\@\n" + 
    " |   < @\n" + 
    " |_|\\_\\@\n" + 
    "       @@\n" + 
    "0x0139  LATIN CAPITAL LETTER L WITH ACUTE\n" + 
    "  _   //@\n" + 
    " | | // @\n" + 
    " | |    @\n" + 
    " | |___ @\n" + 
    " |_____|@\n" + 
    "        @@\n" + 
    "0x013A  LATIN SMALL LETTER L WITH ACUTE\n" + 
    "  //@\n" + 
    " | |@\n" + 
    " | |@\n" + 
    " | |@\n" + 
    " |_|@\n" + 
    "    @@\n" + 
    "0x013B  LATIN CAPITAL LETTER L WITH CEDILLA\n" + 
    "  _     @\n" + 
    " | |    @\n" + 
    " | |    @\n" + 
    " | |___ @\n" + 
    " |_____|@\n" + 
    "    )__)@@\n" + 
    "0x013C  LATIN SMALL LETTER L WITH CEDILLA\n" + 
    "  _   @\n" + 
    " | |  @\n" + 
    " | |  @\n" + 
    " | |  @\n" + 
    " |_|  @\n" + 
    "   )_)@@\n" + 
    "0x013D  LATIN CAPITAL LETTER L WITH CARON\n" + 
    "  _ \\\\//@\n" + 
    " | | \\/ @\n" + 
    " | |    @\n" + 
    " | |___ @\n" + 
    " |_____|@\n" + 
    "        @@\n" + 
    "0x013E  LATIN SMALL LETTER L WITH CARON\n" + 
    "  _ \\\\//@\n" + 
    " | | \\/ @\n" + 
    " | |    @\n" + 
    " | |    @\n" + 
    " |_|    @\n" + 
    "        @@\n" + 
    "0x013F  LATIN CAPITAL LETTER L WITH MIDDLE DOT\n" + 
    "  _     @\n" + 
    " | |    @\n" + 
    " | | [] @\n" + 
    " | |___ @\n" + 
    " |_____|@\n" + 
    "        @@\n" + 
    "0x0140  LATIN SMALL LETTER L WITH MIDDLE DOT\n" + 
    "  _    @\n" + 
    " | |   @\n" + 
    " | | []@\n" + 
    " | |   @\n" + 
    " |_|   @\n" + 
    "       @@\n" + 
    "0x0141  LATIN CAPITAL LETTER L WITH STROKE\n" + 
    "  __    @\n" + 
    " | //   @\n" + 
    " |//|   @\n" + 
    " // |__ @\n" + 
    " |_____|@\n" + 
    "        @@\n" + 
    "0x0142  LATIN SMALL LETTER L WITH STROKE\n" + 
    "  _ @\n" + 
    " | |@\n" + 
    " |//@\n" + 
    " //|@\n" + 
    " |_|@\n" + 
    "    @@\n" + 
    "0x0143  LATIN CAPITAL LETTER N WITH ACUTE\n" + 
    "  _/ /_ @\n" + 
    " | \\ | |@\n" + 
    " |  \\| |@\n" + 
    " | |\\  |@\n" + 
    " |_| \\_|@\n" + 
    "        @@\n" + 
    "0x0144  LATIN SMALL LETTER N WITH ACUTE\n" + 
    "     _  @\n" + 
    "  _ /_/ @\n" + 
    " | '_ \\ @\n" + 
    " | | | |@\n" + 
    " |_| |_|@\n" + 
    "        @@\n" + 
    "0x0145  LATIN CAPITAL LETTER N WITH CEDILLA\n" + 
    "  _   _ @\n" + 
    " | \\ | |@\n" + 
    " |  \\| |@\n" + 
    " | |\\  |@\n" + 
    " |_| \\_|@\n" + 
    " )_)    @@\n" + 
    "0x0146  LATIN SMALL LETTER N WITH CEDILLA\n" + 
    "        @\n" + 
    "  _ __  @\n" + 
    " | '_ \\ @\n" + 
    " | | | |@\n" + 
    " |_| |_|@\n" + 
    " )_)    @@\n" + 
    "0x0147  LATIN CAPITAL LETTER N WITH CARON\n" + 
    "  _\\/ _ @\n" + 
    " | \\ | |@\n" + 
    " |  \\| |@\n" + 
    " | |\\  |@\n" + 
    " |_| \\_|@\n" + 
    "        @@\n" + 
    "0x0148  LATIN SMALL LETTER N WITH CARON\n" + 
    "  \\\\//  @\n" + 
    "  _\\/_  @\n" + 
    " | '_ \\ @\n" + 
    " | | | |@\n" + 
    " |_| |_|@\n" + 
    "        @@\n" + 
    "0x0149  LATIN SMALL LETTER N PRECEDED BY APOSTROPHE\n" + 
    "          @\n" + 
    "  _  __   @\n" + 
    " ( )| '_\\ @\n" + 
    " |/| | | |@\n" + 
    "   |_| |_|@\n" + 
    "          @@\n" + 
    "0x014A  LATIN CAPITAL LETTER ENG\n" + 
    "  _   _ @\n" + 
    " | \\ | |@\n" + 
    " |  \\| |@\n" + 
    " | |\\  |@\n" + 
    " |_| \\ |@\n" + 
    "     )_)@@\n" + 
    "0x014B  LATIN SMALL LETTER ENG\n" + 
    "  _ __  @\n" + 
    " | '_ \\ @\n" + 
    " | | | |@\n" + 
    " |_| | |@\n" + 
    "     | |@\n" + 
    "    |__ @@\n" + 
    "0x014C  LATIN CAPITAL LETTER O WITH MACRON\n" + 
    "   ____ @\n" + 
    "  /_ _/ @\n" + 
    "  / _ \\ @\n" + 
    " | (_) |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "0x014D  LATIN SMALL LETTER O WITH MACRON\n" + 
    "   ____ @\n" + 
    "  /_ _/ @\n" + 
    "  / _ \\ @\n" + 
    " | (_) |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "0x014E  LATIN CAPITAL LETTER O WITH BREVE\n" + 
    "  \\   / @\n" + 
    "   _-_  @\n" + 
    "  / _ \\ @\n" + 
    " | |_| |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "0x014F  LATIN SMALL LETTER O WITH BREVE\n" + 
    "  \\   / @\n" + 
    "   _-_  @\n" + 
    "  / _ \\ @\n" + 
    " | |_| |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "0x0150  LATIN CAPITAL LETTER O WITH DOUBLE ACUTE\n" + 
    "    ___ @\n" + 
    "   /_/_/@\n" + 
    "  / _ \\ @\n" + 
    " | |_| |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "0x0151  LATIN SMALL LETTER O WITH DOUBLE ACUTE\n" + 
    "    ___ @\n" + 
    "   /_/_/@\n" + 
    "  / _ \\ @\n" + 
    " | |_| |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "0x0152  LATIN CAPITAL LIGATURE OE\n" + 
    "   ___  ___ @\n" + 
    "  / _ \\| __|@\n" + 
    " | | | |  | @\n" + 
    " | |_| | |__@\n" + 
    "  \\___/|____@\n" + 
    "            @@\n" + 
    "0x0153  LATIN SMALL LIGATURE OE\n" + 
    "             @\n" + 
    "   ___   ___ @\n" + 
    "  / _ \\ / _ \\@\n" + 
    " | (_) |  __/@\n" + 
    "  \\___/ \\___|@\n" + 
    "             @@\n" + 
    "0x0154  LATIN CAPITAL LETTER R WITH ACUTE\n" + 
    "  _/_/  @\n" + 
    " |  _ \\ @\n" + 
    " | |_) |@\n" + 
    " |  _ < @\n" + 
    " |_| \\_\\@\n" + 
    "        @@\n" + 
    "0x0155  LATIN SMALL LETTER R WITH ACUTE\n" + 
    "     __@\n" + 
    "  _ /_/@\n" + 
    " | '__|@\n" + 
    " | |   @\n" + 
    " |_|   @\n" + 
    "       @@\n" + 
    "0x0156  LATIN CAPITAL LETTER R WITH CEDILLA\n" + 
    "  ____  @\n" + 
    " |  _ \\ @\n" + 
    " | |_) |@\n" + 
    " |  _ < @\n" + 
    " |_| \\_\\@\n" + 
    " )_)    @@\n" + 
    "0x0157  LATIN SMALL LETTER R WITH CEDILLA\n" + 
    "       @\n" + 
    "  _ __ @\n" + 
    " | '__|@\n" + 
    " | |   @\n" + 
    " |_|   @\n" + 
    "   )_) @@\n" + 
    "0x0158  LATIN CAPITAL LETTER R WITH CARON\n" + 
    "  _\\_/  @\n" + 
    " |  _ \\ @\n" + 
    " | |_) |@\n" + 
    " |  _ < @\n" + 
    " |_| \\_\\@\n" + 
    "        @@\n" + 
    "0x0159  LATIN SMALL LETTER R WITH CARON\n" + 
    "  \\\\// @\n" + 
    "  _\\/_ @\n" + 
    " | '__|@\n" + 
    " | |   @\n" + 
    " |_|   @\n" + 
    "       @@\n" + 
    "0x015A  LATIN CAPITAL LETTER S WITH ACUTE\n" + 
    "  _/_/  @\n" + 
    " / ___| @\n" + 
    " \\___ \\ @\n" + 
    "  ___) |@\n" + 
    " |____/ @\n" + 
    "        @@\n" + 
    "0x015B  LATIN SMALL LETTER S WITH ACUTE\n" + 
    "    __@\n" + 
    "  _/_/@\n" + 
    " / __|@\n" + 
    " \\__ \\@\n" + 
    " |___/@\n" + 
    "      @@\n" + 
    "0x015C  LATIN CAPITAL LETTER S WITH CIRCUMFLEX\n" + 
    "  _/\\_  @\n" + 
    " / ___| @\n" + 
    " \\___ \\ @\n" + 
    "  ___) |@\n" + 
    " |____/ @\n" + 
    "        @@\n" + 
    "0x015D  LATIN SMALL LETTER S WITH CIRCUMFLEX\n" + 
    "      @\n" + 
    "  /_\\_@\n" + 
    " / __|@\n" + 
    " \\__ \\@\n" + 
    " |___/@\n" + 
    "      @@\n" + 
    "0x015E  LATIN CAPITAL LETTER S WITH CEDILLA\n" + 
    "  ____  @\n" + 
    " / ___| @\n" + 
    " \\___ \\ @\n" + 
    "  ___) |@\n" + 
    " |____/ @\n" + 
    "    )__)@@\n" + 
    "0x015F  LATIN SMALL LETTER S WITH CEDILLA\n" + 
    "      @\n" + 
    "  ___ @\n" + 
    " / __|@\n" + 
    " \\__ \\@\n" + 
    " |___/@\n" + 
    "   )_)@@\n" + 
    "0x0160  LATIN CAPITAL LETTER S WITH CARON\n" + 
    "  _\\_/  @\n" + 
    " / ___| @\n" + 
    " \\___ \\ @\n" + 
    "  ___) |@\n" + 
    " |____/ @\n" + 
    "        @@\n" + 
    "0x0161  LATIN SMALL LETTER S WITH CARON\n" + 
    "  \\\\//@\n" + 
    "  _\\/ @\n" + 
    " / __|@\n" + 
    " \\__ \\@\n" + 
    " |___/@\n" + 
    "      @@\n" + 
    "0x0162  LATIN CAPITAL LETTER T WITH CEDILLA\n" + 
    "  _____ @\n" + 
    " |_   _|@\n" + 
    "   | |  @\n" + 
    "   | |  @\n" + 
    "   |_|  @\n" + 
    "    )__)@@\n" + 
    "0x0163  LATIN SMALL LETTER T WITH CEDILLA\n" + 
    "  _   @\n" + 
    " | |_ @\n" + 
    " | __|@\n" + 
    " | |_ @\n" + 
    "  \\__|@\n" + 
    "   )_)@@\n" + 
    "0x0164  LATIN CAPITAL LETTER T WITH CARON\n" + 
    "  _____ @\n" + 
    " |_   _|@\n" + 
    "   | |  @\n" + 
    "   | |  @\n" + 
    "   |_|  @\n" + 
    "        @@\n" + 
    "0x0165  LATIN SMALL LETTER T WITH CARON\n" + 
    "  \\/  @\n" + 
    " | |_ @\n" + 
    " | __|@\n" + 
    " | |_ @\n" + 
    "  \\__|@\n" + 
    "      @@\n" + 
    "0x0166  LATIN CAPITAL LETTER T WITH STROKE\n" + 
    "  _____ @\n" + 
    " |_   _|@\n" + 
    "   | |  @\n" + 
    "  -|-|- @\n" + 
    "   |_|  @\n" + 
    "        @@\n" + 
    "0x0167  LATIN SMALL LETTER T WITH STROKE\n" + 
    "  _   @\n" + 
    " | |_ @\n" + 
    " | __|@\n" + 
    " |-|_ @\n" + 
    "  \\__|@\n" + 
    "      @@\n" + 
    "0x0168  LATIN CAPITAL LETTER U WITH TILDE\n" + 
    "        @\n" + 
    "  _/\\/_ @\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "0x0169  LATIN SMALL LETTER U WITH TILDE\n" + 
    "        @\n" + 
    "  _/\\/_ @\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\__,_|@\n" + 
    "        @@\n" + 
    "0x016A  LATIN CAPITAL LETTER U WITH MACRON\n" + 
    "   ____ @\n" + 
    "  /__ _/@\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "0x016B  LATIN SMALL LETTER U WITH MACRON\n" + 
    "   ____ @\n" + 
    "  / _  /@\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\__,_|@\n" + 
    "        @@\n" + 
    "0x016C  LATIN CAPITAL LETTER U WITH BREVE\n" + 
    "        @\n" + 
    "   \\_/_ @\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\____|@\n" + 
    "        @@\n" + 
    "0x016D  LATIN SMALL LETTER U WITH BREVE\n" + 
    "        @\n" + 
    "   \\_/_ @\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\__,_|@\n" + 
    "        @@\n" + 
    "0x016E  LATIN CAPITAL LETTER U WITH RING ABOVE\n" + 
    "    O   @\n" + 
    "  __  _ @\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "0x016F  LATIN SMALL LETTER U WITH RING ABOVE\n" + 
    "    O   @\n" + 
    "  __ __ @\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\__,_|@\n" + 
    "        @@\n" + 
    "0x0170  LATIN CAPITAL LETTER U WITH DOUBLE ACUTE\n" + 
    "   -- --@\n" + 
    "  /_//_/@\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\___/ @\n" + 
    "        @@\n" + 
    "0x0171  LATIN SMALL LETTER U WITH DOUBLE ACUTE\n" + 
    "    ____@\n" + 
    "  _/_/_/@\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\__,_|@\n" + 
    "        @@\n" + 
    "0x0172  LATIN CAPITAL LETTER U WITH OGONEK\n" + 
    "  _   _ @\n" + 
    " | | | |@\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\___/ @\n" + 
    "    (__(@@\n" + 
    "0x0173  LATIN SMALL LETTER U WITH OGONEK\n" + 
    "        @\n" + 
    "  _   _ @\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\__,_|@\n" + 
    "     (_(@@\n" + 
    "0x0174  LATIN CAPITAL LETTER W WITH CIRCUMFLEX\n" + 
    " __    /\\  __@\n" + 
    " \\ \\  //\\\\/ /@\n" + 
    "  \\ \\ /\\ / / @\n" + 
    "   \\ V  V /  @\n" + 
    "    \\_/\\_/   @\n" + 
    "             @@\n" + 
    "0x0175  LATIN SMALL LETTER W WITH CIRCUMFLEX\n" + 
    "      /\\   @\n" + 
    " __  //\\\\__@\n" + 
    " \\ \\ /\\ / /@\n" + 
    "  \\ V  V / @\n" + 
    "   \\_/\\_/  @\n" + 
    "           @@\n" + 
    "0x0176  LATIN CAPITAL LETTER Y WITH CIRCUMFLEX\n" + 
    "    /\\  @\n" + 
    " __//\\\\ @\n" + 
    " \\ \\ / /@\n" + 
    "  \\ V / @\n" + 
    "   |_|  @\n" + 
    "        @@\n" + 
    "0x0177  LATIN SMALL LETTER Y WITH CIRCUMFLEX\n" + 
    "    /\\  @\n" + 
    "   //\\\\ @\n" + 
    " | | | |@\n" + 
    " | |_| |@\n" + 
    "  \\__, |@\n" + 
    "  |___/ @@\n" + 
    "0x0178  LATIN CAPITAL LETTER Y WITH DIAERESIS\n" + 
    "  []  []@\n" + 
    " __    _@\n" + 
    " \\ \\ / /@\n" + 
    "  \\ V / @\n" + 
    "   |_|  @\n" + 
    "        @@\n" + 
    "0x0179  LATIN CAPITAL LETTER Z WITH ACUTE\n" + 
    "  __/_/@\n" + 
    " |__  /@\n" + 
    "   / / @\n" + 
    "  / /_ @\n" + 
    " /____|@\n" + 
    "       @@\n" + 
    "0x017A  LATIN SMALL LETTER Z WITH ACUTE\n" + 
    "    _ @\n" + 
    "  _/_/@\n" + 
    " |_  /@\n" + 
    "  / / @\n" + 
    " /___|@\n" + 
    "      @@\n" + 
    "0x017B  LATIN CAPITAL LETTER Z WITH DOT ABOVE\n" + 
    "  __[]_@\n" + 
    " |__  /@\n" + 
    "   / / @\n" + 
    "  / /_ @\n" + 
    " /____|@\n" + 
    "       @@\n" + 
    "0x017C  LATIN SMALL LETTER Z WITH DOT ABOVE\n" + 
    "   [] @\n" + 
    "  ____@\n" + 
    " |_  /@\n" + 
    "  / / @\n" + 
    " /___|@\n" + 
    "      @@\n" + 
    "0x017D  LATIN CAPITAL LETTER Z WITH CARON\n" + 
    "  _\\_/_@\n" + 
    " |__  /@\n" + 
    "   / / @\n" + 
    "  / /_ @\n" + 
    " /____|@\n" + 
    "       @@\n" + 
    "0x017E  LATIN SMALL LETTER Z WITH CARON\n" + 
    "  \\\\//@\n" + 
    "  _\\/_@\n" + 
    " |_  /@\n" + 
    "  / / @\n" + 
    " /___|@\n" + 
    "      @@\n" + 
    "0x017F  LATIN SMALL LETTER LONG S\n" + 
    "     __ @\n" + 
    "    / _|@\n" + 
    " |-| |  @\n" + 
    " |-| |  @\n" + 
    "   |_|  @\n" + 
    "        @@\n" + 
    "0x02C7  CARON\n" + 
    " \\\\//@\n" + 
    "  \\/ @\n" + 
    "    $@\n" + 
    "    $@\n" + 
    "    $@\n" + 
    "    $@@\n" + 
    "0x02D8  BREVE\n" + 
    " \\\\_//@\n" + 
    "  \\\\\\\\\\_/ @\n" + 
    "     $@\n" + 
    "     $@\n" + 
    "     $@\n" + 
    "     $@@\n" + 
    "0x02D9  DOT ABOVE\n" + 
    " []@\n" + 
    "  $@\n" + 
    "  $@\n" + 
    "  $@\n" + 
    "  $@\n" + 
    "  $@@\n" + 
    "0x02DB  OGONEK\n" + 
    "    $@\n" + 
    "    $@\n" + 
    "    $@\n" + 
    "    $@\n" + 
    "    $@\n" + 
    " )_) @@\n" + 
    "0x02DD  DOUBLE ACUTE ACCENT\n" + 
    "  _ _ @\n" + 
    " /_/_/@\n" + 
    "     $@\n" + 
    "     $@\n" + 
    "     $@\n" + 
    "     $@@";
    
    /** 
     * Logger 
     */
    private static final Logger logger_ = Logger.getLogger(Banner.class);
    
    /** 
     * Default font
     */
    private static final BannerFont FONT_STANDARD = 
        new BannerFont(FONT_STANDARD_DATA);

    /**
     * Executes banner via Command line args
     * 
     * @param args  String to turn into a banner
     */
    public static void main(String args[])
    {
        // Default options
        boolean splitWords  = false;
        boolean leftJustify = false;
        int     lineWidth   = 80;
         
        try
        {
            CommandLineParser parser = new PosixParser();
            Options options = new Options();

            // Valid options
            Option splitWordsOption =
                new Option("s","splitwords", false, "One word per line");

            Option leftJustifyOption =
                new Option("l", "leftJustify", false, "Left justifies banner");

            Option lineWidthOption = 
                new Option("w", "lineWidth", true, "Maximum line width");
                            
            Option helpOption = 
                new Option("h", "help", false, "Print usage");
                
            Option helpOption2 = 
                new Option("?", "/?", false, "Print Usage");

            options.addOption(helpOption2);
            options.addOption(helpOption);
            options.addOption(splitWordsOption);
            options.addOption(leftJustifyOption);
            options.addOption(lineWidthOption);

            // Parse options
            CommandLine cmdLine = parser.parse(options, args, true);
            
            // Handle options
            for (Iterator i = cmdLine.iterator(); i.hasNext(); )
            {
                Option option = (Option) i.next();
                String opt = option.getOpt();

                if (opt.equals(splitWordsOption.getOpt()))
                {
                    splitWords = true;
                }
                else if (opt.equals(leftJustifyOption.getOpt()))
                {
                    leftJustify = true;
                }
                else if (opt.equals(lineWidthOption.getOpt()))
                {
                    try
                    {
                        lineWidth = Integer.parseInt(option.getValue());
                    }
                    catch (NumberFormatException nfe)
                    {
                        printUsage();
                        System.out.println("\nError: '" + option.getValue() + 
                            "' is not a valid line width.");
                        System.exit(0);
                    }
                }
                else if (opt.equals(helpOption.getOpt())  ||
                         opt.equals(helpOption2.getOpt()))
                {
                    printUsage();
                    System.exit(0);
                }
            }

            // Text to convert to a banner
            switch (cmdLine.getArgs().length)
            {
                case  0: 
                
                    printUsage(); 
                    System.exit(0);
                    break;
                
                default:
                 
                    StringBuffer sb = new StringBuffer();
    
                    for (Iterator i=cmdLine.getArgList().iterator(); 
                         i.hasNext();)
                    {
                        sb.append(i.next());
                        
                        if (i.hasNext())
                            sb.append(" ");
                    }
                    
                    String banner = convert(
                        sb.toString(), splitWords, leftJustify, lineWidth);
                    
                    System.out.println(banner);
                    break;
            }
        }
        catch (Throwable t)
        {
            logger_.error("main", t);
        }
    }

    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /**
     * Prevent construction
     */
    private Banner()
    {
    }
    
    //--------------------------------------------------------------------------
    // Public
    //--------------------------------------------------------------------------

    /**
     * Converts from ASCII to a banner
     *
     * @param   message         Message
     * @return  Banner as a string
     */
    public static String convert(String message)
    {
        return convert(message, false, true, Integer.MAX_VALUE);
    }

    /**
     * Converts from ASCII to a banner, eventually centering each line,
     * folding after each word, or when the width limit is reached
     *
     * @param   message         Message
     * @param   splitAtWord     True if split at word
     * @param   leftJustify     True to left justify text
     * @param   splitWidth      Width of split
     * @return  Banner as a string
     */
    public static String convert(String message, boolean splitAtWord,
        boolean leftJustify, int splitWidth)
    {
        return convert(message, FONT_STANDARD, splitAtWord, leftJustify,
                    splitWidth);
    }
    
    /**
     * Converts from ASCII to a banner, eventually centering each line,
     * folding after each word, or when the width limit is reached
     * 
     * @param   message         Message
     * @param   figletFont      Font
     * @param   splitAtWord     True if split at word
     * @param   leftJustify     True to left justify text
     * @param   splitWidth      Width of split
     * @return  Banner as a string
     */
    public static String convert(String message, BannerFont figletFont,
        boolean splitAtWord, boolean leftJustify, int splitWidth)
    {
        String result = "";
        StringTokenizer st = new StringTokenizer(message, " ");
        
        if (splitAtWord)
        {
            while (st.hasMoreElements())
            {
                result = addLine(
                    result, convertOneLine(st.nextToken(), figletFont),
                    leftJustify, splitWidth);
            }
        }
        else
        {
            String line = "";
            
            while (st.hasMoreElements())
            {
                String w = st.nextToken(), word;
                
                if (line == "")
                    word = w;
                else
                    word = ' ' + w;
                    
                String newLine = append(line, word, figletFont);
                
                //System.out.println("word:\n" + word + "line:" + 
                //                  line + "new line: newLine);
                
                if ((width(newLine) > splitWidth) && (line != ""))
                {
                    result =
                        addLine(result, line + '\n', leftJustify, splitWidth);
                        
                    line = append("", w, figletFont);
                }
                else
                {
                    line = newLine;
                }
            }
            
            if (line != "")
                result = addLine(result, line + '\n', leftJustify, splitWidth);
        }
        
        return result;
    }
    
    /**
     * Gimme the maximum width of a converted text
     * 
     * @param   message  Message
     * @return  Width of text
     */
    public static int width(String message)
    {
        int w = 0;
        
        StringTokenizer st = new StringTokenizer(message, "\n");
        
        while (st.hasMoreElements())
            w = Math.max(w, st.nextToken().length());
            
        return w;
    }
    
    //--------------------------------------------------------------------------
    // Private
    //--------------------------------------------------------------------------
    
    /**
     * Move a banner to the right (for centering)
     * 
     * @param  message  Text of message
     * @param  offset   Offset to start
     * @return Banner moved to the right
     */
    private static String scroll(String message, int offset)
    {
        String result = "";
        String shift = "";
        
        for (int i = 0; i < offset; i++)
            shift += ' ';
            
        StringTokenizer st = new StringTokenizer(message, "\n");
        
        while (st.hasMoreElements())
            result += shift + st.nextToken() + '\n';
            
        return result;
    }
    
    /**
     * Append a new banner line (center if needed)
     * 
     * @param   text         Existing text
     * @param   line         New line
     * @param   leftJustify  True to left justify
     * @param   splitWidth   Width at which to split a line
     * @return  Banner with new line appended
     */
    private static String addLine(String text, String line, boolean leftJustify,
        int splitWidth)
    {
        String result = text;
        
        if (leftJustify)
            result += line;
        else
            result += scroll(line, (int) (splitWidth / 2 - width(line) / 2));
            
        return result;
    }
    
    
    /**
     * Create a banner from text
     * 
     * @param  message      Message
     * @param  font         Font to use
     * @return Banner
     */
    private static String convertOneLine(String message, BannerFont font)
    {
        String result = "";
        
        // for each line
        for (int l = 0; l < font.height_; l++)
        { 
            // for each char
            for (int c = 0; c < message.length(); c++) 
                result += font.getCharLineString(
                            (int) message.charAt(c), l);
                    
            result += '\n';
        }
        
        return result;
    }
    
    /**
     * Appends a word to a banner
     * 
     * @param  message  Banner to append word to
     * @param  end      Word to append to the banner
     * @param  font     Font to use
     * @return Banner with word appended
     */
    private static String append(String message, String end, 
        BannerFont font)
    {
        String result = "";
        int h = 0;
        
        if (message == "")
            for (int i = 0; i < font.height_; i++)
                message += " \n";
                
        StringTokenizer st = new StringTokenizer(message, "\n");
        
        while (st.hasMoreElements())
        {
            result += st.nextToken();
            
            for (int c = 0; c < end.length(); c++) // for each char
                result += font.getCharLineString((int) end.charAt(c), h);
                
            result += '\n';
            h++;
        }
        
        return result;
    }

    /**
     * Prints program usage and help information
     */
    private static void printUsage()
    {
        StringBuffer sb = new StringBuffer();
        
        sb.append("Banner converts a string of text to a banner using ");
        sb.append("ASCII characters.\n\n");
        sb.append("Usage  : java toolbox.util.Banner [options] text\n");
        sb.append("Options: -h, --help        => Prints this help\n");
        sb.append("         -s, --splitWords  => One word per line\n");
        sb.append("         -l, --leftJustify => Left justify text ");
        sb.append("(default is centered)\n");
        sb.append("         -w  --lineWidth   => Max line width\n");
        sb.append("Args   : text              => Text to convert to a banner");
        sb.append("\n");
        
        System.out.println(sb.toString());
    }

    //--------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------
    
    /**
     * Font described by ascii string in figlet format
     */ 
    static class BannerFont
    {
        private char hardblank_;
        private int height_ = -1;
        private int heightWithoutDescenders_ = -1;
        private int maxLine_ = -1;
        private int smushMode_ = -1;
        private char font_[][][] = null;
        private String fontName_ = null;

        //----------------------------------------------------------------------
        //  Constructors
        //----------------------------------------------------------------------

        /**
         * Creates a banner font
         * 
         * @param  fontData  Figlet font data
         */        
        public BannerFont(String fontData)
        {
            readFont(new StringReader(fontData));
        }

        /**
         * Creates a banner font
         *
         * @param  aURL  URL to figlet font data
         */            
        public BannerFont(URL aURL)
        {
            InputStream conn;
            
            try
            {
                conn = aURL.openStream();
                readFont(new InputStreamReader(conn));
            }
            catch (IOException e)
            {
                logger_.error("IO Error", e);
            }
        }

        //----------------------------------------------------------------------
        //  Public
        //----------------------------------------------------------------------
        
        /**
         * @return  Font data
         */
        public char[][][] getFont()
        {
            return font_;
        }
    
        /**
         * @param   c   Char to get
         * @return  Char in font
         */
        public char[][] getChar(int c)
        {
            return font_[c];
        }
    
        /**
         * @param  c  Char
         * @param  l  Line
         * @return Character line string
         */
        public String getCharLineString(int c, int l)
        {
            if (font_[c][l] == null)
                return null;
            else
                return new String(font_[c][l]);
        }

        //----------------------------------------------------------------------
        //  Private
        //----------------------------------------------------------------------
        
        /**
         * Reads figlet font_ data from an inputstream
         * 
         * @param  is  Inputstream to read font data from
         */
        protected void readFont(Reader reader)
        {
            font_ = new char[256][][];
            BufferedReader data = null;
            String dummyS;
            char dummyC;
            int dummyI;
            
            try
            {
                data = new BufferedReader(reader);
    
                dummyS = data.readLine();
                StringTokenizer st = new StringTokenizer(dummyS, " ");
                String s = st.nextToken();
                hardblank_ = s.charAt(s.length() - 1);
                height_ = Integer.parseInt(st.nextToken());
                heightWithoutDescenders_ = Integer.parseInt(st.nextToken());
                maxLine_ = Integer.parseInt(st.nextToken());
                smushMode_ = Integer.parseInt(st.nextToken());
                dummyI = Integer.parseInt(st.nextToken());
    
                // try to read the font_ name as the first word of
                // the first comment line, but this is not standardized !
                st = new StringTokenizer(data.readLine(), " ");
                if (st.hasMoreElements())
                    fontName_ = st.nextToken();
                else
                    fontName_ = "";
    
                // skip the comments
                for (int i = 0; i < dummyI - 1; i++) 
                    dummyS = data.readLine();
                
                // for all the characters    
                for (int i = 32; i < 256; i++)
                { 
                    //System.out.print(i+":");
                    for (int h = 0; h < height_; h++)
                    {
                        dummyS = data.readLine();
                        
                        if (dummyS == null)
                            i = 256;
                        else
                        {
                            //System.out.println(dummyS);
                            int iNormal = i;
                            boolean abnormal = true;
                            
                            if (h == 0)
                            {
                                try
                                {
                                    i = Integer.parseInt(dummyS);
                                }
                                catch (NumberFormatException e) 
                                {
                                    abnormal = false;
                                }
                                
                                if (abnormal)
                                    dummyS = data.readLine();
                                else
                                    i = iNormal;
                            }
                            
                            if (h == 0)
                                font_[i] = new char[height_][];
                                
                            int t =
                                dummyS.length()-1-((h == height_ - 1) ? 1 : 0);
                                
                            if (height_ == 1)
                                t++;
                                
                            font_[i][h] = new char[t];
                            
                            for (int l = 0; l < t; l++)
                            {
                                char a = dummyS.charAt(l);
                                font_[i][h][l] = (a == hardblank_) ? ' ' : a;
                            }
                        }
                    }
                }
            }
            catch (IOException e)
            {
                logger_.error("IO Error", e);
            }
        }

    }        
}

/**
 * Banner
 *
 * @author Benoit Rigaut CERN July 96
 * www.rigaut.com benoit@rigaut.com
 * released with GPL the 13th of november 2000 (my birthday!)
 *
 * 12/2002 Modifications made to remove resource dependency
 */

