package toolbox.ip2hostname;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.regexp.RE;

// DELETE ME

public class JNGrep {

    public static void main(String[] args) {
        JNGrep jngrep = new JNGrep();
        
        
        jngrep.start(System.in, System.out);
        
//        StringInputStream sis = new StringInputStream(
//            "U 172.18.95.136:1900 -> 172.18.95.19:1900\n" +
//            "? 172.18.92.1 -> 224.0.0.10\n");
//        
//        jngrep.start(sis, System.out);
    }
    
    public void start(InputStream is, OutputStream os) {

        
        try {
            for (LineIterator i = IOUtils.lineIterator(is, "UTF8"); i.hasNext();) {
                String line = i.nextLine();
                String fixed = replaceIPAddress(line) + "\n";
                os.write(fixed.getBytes());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String replaceIPAddress(String line) {

        //RE r = new RE("\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b");
        
        String ipAddrRE =             
              "[:digit:]{1,3}"  // 1-3 digits
            + "\\."             // .
            + "[:digit:]{1,3}"  // 1-3 digits
            + "\\."             // .      
            + "[:digit:]{1,3}"  // 1-3 digits
            + "\\."             // .      
            + "[:digit:]{1,3}"; // 1-3 digits

        String portRE = "(\\:[:digit:]{1,5})?"; // colon + 1-5 digits
        
        //        Once you have done this, you can call either of the RE.match methods to perform matching on a String. For example:
        //        will cause the boolean matched to be set to true because the pattern "a*b" matches the string "aaaab".
        //
        //        If you were interested in the number of a's which matched the first part of our example expression, you could change the expression to "(a*)b". Then when you compiled the expression and matched it against something like "xaaaab", you would get results like this:
        
//        RE ipr = new RE("^" + ipAddrRE + "$");
//        System.out.println(ipr.match("1.1.1.1"));
//        System.out.println(ipr.match("0.0.0.0"));
//        System.out.println(ipr.match("129.129.129.129"));
//        
//        System.out.println("");
//        
//        System.out.println(ipr.match("1.1.1"));
//        System.out.println(ipr.match(".0.0.0"));
//        System.out.println(ipr.match("1.2.9"));
//        
//        System.out.println("");
//        
//        RE portr = new RE("^" + portRE + "$");
//        System.out.println(portr.match(":1234"));
//        System.out.println(portr.match(":12345"));
//        System.out.println(portr.match(":1"));
//        System.out.println(portr.match(":00"));
//
//        System.out.println("");
//        
//        System.out.println(portr.match(":"));
//        System.out.println(portr.match(": "));
//        System.out.println(portr.match(":ab"));
//        System.out.println(portr.match(":1a"));
//
//        System.out.println("");

        String rRE =
            "(U|T|I|\\?) "            // U or T or I or ? + space
            + "(" + ipAddrRE + ")"  // 1: ip address
            + portRE                // port
            + " -> "                // arrow
            + "(" + ipAddrRE + ")"  // 2: ip address
            + portRE                // port
            + ".*";                 // any num chars

        RE r = new RE(
            "^"                   // begin of line
          +  rRE
          + "$");                 // end of line
        
        boolean match = r.match(line);
        
//        System.out.println(rRE);
//        System.out.println(r.match("U 172.18.95.136:1900 -> 239.255.255.250:1900"));
//
        
//        for (int i = 0; i < r.getParenCount(); i++) {
//            System.out.println("paren " + i + ": " + r.getParen(i));
//        }
        
        //System.out.println("all: " + r.getParen(0));
        //System.out.println("mush: " + r.getParen(1));
        //System.out.println("ip1: " + r.getParen(2));
        //System.out.println("ip2: " + r.getParen(3));

        if (match) {
            String ip1 = r.getParen(2);
            String ip2 = r.getParen(4);
            
            String host1 = getCachedHostname(ip1);
            String host2 = getCachedHostname(ip2);
    
    //        System.out.println("host1: " + host1);
    //        System.out.println("host2: " + host2);
            
            line = StringUtils.replace(line, ip1, "[" + host1 + "]");
            line = StringUtils.replace(line, ip2, "[" + host2 + "]");
            
            //dumpIP(r.getParen(2));
            //dumpIP(r.getParen(3));
        }
        
        return line;
        
        // U 172.18.95.136:1900 -> 239.255.255.250:1900
        
//        String wholeExpr = r.getParen(0); // wholeExpr will be 'aaaab'
//        String insideParens = r.getParen(1); // insideParens will be 'aaaa'
//
//        int startWholeExpr = r.getParenStart(0); // startWholeExpr will be index 1
//        int endWholeExpr = r.getParenEnd(0); // endWholeExpr will be index 6
//        int lenWholeExpr = r.getParenLength(0); // lenWholeExpr will be 5
//
//        int startInside = r.getParenStart(1); // startInside will be index 1
//        int endInside = r.getParenEnd(1); // endInside will be index 5
//        int lenInside = r.getParenLength(1); // lenInside will be 4
//        return null;
        
        //return "end";
    }

    private Map hosts = new HashMap();
    
    private String getCachedHostname(String ip) {
        
        String hostname = (String) hosts.get(ip);
        
        if (hostname == null) {
            hostname = getHostname(ip);
            hosts.put(ip, hostname);
            System.out.println("Host cache size = " + hosts.size());
        }
        
        return hostname;
    }
    
    private String getHostname(String ip) {
        String hostname = null;
        
        try {
            InetAddress[] ips = InetAddress.getAllByName(ip);
            
            switch (ips.length) {
                case 0: hostname = ip; break;
                default: hostname = ips[0].getCanonicalHostName();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return hostname;
    }
    
    private void dumpIP(String ipAddr) {
        try {
            InetAddress[] ips = InetAddress.getAllByName(ipAddr);
            
            for (int i = 0; i < ips.length; i++) {
                InetAddress ip = ips[i];
                System.out.println(
                    "IP: " + ip.getHostAddress() + 
                    "  Hostname: " + ip.getHostName() +
                    "  Canonical: " + ip.getCanonicalHostName());
            }
            //System.out.println(ArrayUtil.toString(ips2, true));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
