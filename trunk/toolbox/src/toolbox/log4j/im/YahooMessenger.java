package toolbox.log4j.im;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import messenger.DataBuffer;
import messenger.Protocols;
import messenger.Yahoo.YahooProtocol;

public class YahooMessenger extends Thread
{
    private Vector v = null;
    private YahooProtocol yahoo = null;
    private Protocols mess = null;
    private int fDumpFactor = 0;
    private DataBuffer fYahooData;
    private boolean fDump = false;
    private UpdateUser fUpdate;

    public YahooMessenger()
    {
        fYahooData = new DataBuffer();
        v = new Vector();

        yahoo = new YahooProtocol(fYahooData, "scsa.yahoo.com", 5050);
        // scsa.yahoo.com
        // cs.yahoo.com
        // scs.yahoo.com
        // protocollen aan vector meegeven

        v.addElement(yahoo);

        mess = new Protocols(v);

        fUpdate =
            new UpdateUser(new DataBuffer(), new DataBuffer(), fYahooData);
        fUpdate.start();

        start();
    }

    public void run()
    {
        BufferedReader console =
            new BufferedReader(new InputStreamReader(System.in));
        String r = "";
        boolean quit = false;

        while (!quit)
        {
            try
            {
                System.out.println("Command: ");
                r = console.readLine();

                if (r.equals("YLOGIN"))
                {
                    yahoo.setLoginName("supahfuzz");
                    yahoo.setEncPasswd("techno");
                    yahoo.login();

                    String message = "howdy doody";
                    String contact = "analogue";

                    yahoo.sendMessage(contact, message);

                }
                else if (r.equals("YMSG"))
                {
                    String message = "howdy doody";
                    String contact = "analogue";

                    yahoo.sendMessage(contact, message);
                }
                else if (r.equals("DUMP"))
                {
                    fDump = true;
                    System.out.println("dump on");

                }
                else if (r.equals("NODUMP"))
                {
                    fDump = false;
                    System.out.println("dump off");

                }
                else if (r.equals("HELP"))
                {
                    System.out.println("COMMANDS SUPPORTED:");
                    System.out.println(" - YLOGIN");
                    System.out.println(" - YMSG");
                    System.out.println(" - DUMP");
                    System.out.println(" - NODUMP");
                    System.out.println(" - QUIT");
                }
                else if (r.equals("QUIT"))
                {
                    //icq.logout();
                    //icq.shutdown();
                    //msn.shutdown();
                    //fUpdate.interrupt();
                    //this.interrupt();
                    quit = true;
                }
                else
                    System.out.println("Command not understood: " + r);
            }
            catch (IOException e)
            {
                System.out.println(e.getMessage());
            }
            catch (Exception e)
            {
                System.out.println(e.getMessage());
            }
        }
        System.exit(0);
    }

    /**
     * Method main.
     * @param args
     */
    public static void main(String[] args)
    {
        YahooMessenger yt = new YahooMessenger();
    }

    class UpdateUser extends Thread
    {
        private DataBuffer fICQData;
        //private Buffer fMSNData;
        private DataBuffer fMSNData;
        private DataBuffer fYahooData;

        public UpdateUser(DataBuffer icq, /*Buffer*/
        DataBuffer msn, DataBuffer yahoo)
        {
            fICQData = icq;
            fMSNData = msn;
            fYahooData = yahoo;
        }

        public void run()
        {
            while (true)
            {
                if (fICQData.ExistsUserData())
                {
                    System.out.print("ICQ: ");
                    System.out.println(fICQData.getToUser());
                }
                if (fICQData.ExistsDump())
                {
                    String output = fICQData.getDump();
                    if (fDump)
                    {
                        System.out.println(output);
                    }
                }
                if (fYahooData.ExistsUserData())
                {
                    System.out.print("YAHOO: ");
                    System.out.println(fYahooData.getToUser());
                }
                if (fYahooData.ExistsDump())
                {
                    String output = fYahooData.getDump();
                    if (fDump)
                    {
                        System.out.println(output);
                    }
                }
                if (fMSNData.ExistsUserData())
                {
                    String s = fMSNData.getToUser();
                    if (!s.equals(""))
                    {
                        System.out.println("MSN: " + s);
                    }
                }
                if (fMSNData.ExistsDump())
                {
                    String s = fMSNData.getDump();
                    if (fDump)
                    {
                        if (!s.equals(""))
                        {
                            System.out.println(s);
                        }
                    }
                }
            }
        }
    }

}
