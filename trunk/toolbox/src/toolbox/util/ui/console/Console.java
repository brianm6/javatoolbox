package toolbox.util.ui.console;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * Basic interface to an application console that supports intput/output via
 * streams.
 */
public interface Console
{
    /**
     * Returns the stream used for reading input from the console.
     * 
     * @return InputStream
     */
    InputStream getInputStream();


    /**
     * Returns the stream used to write output to the console.
     * 
     * @return PrintStream
     */
    PrintStream getOutputStream();


    /**
     * Sends text to the console.
     * 
     * @param text Text to send to the console.
     */
    public void send(String text);


    /**
     * Clears the contents of the console.
     */
    void clear();


    /**
     * Release any resources held by the console.
     */
    void dispose();
}

//This program is free software; you can redistribute it and/or
//modify it under the terms of the GNU Lesser General Public
//License as published by the Free Software Foundation; either
//version 2 of the License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//Lesser General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public
//License along with this library; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA