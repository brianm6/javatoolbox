package toolbox.jsourceview;

import java.util.Vector;

public class Queue extends Vector
{

    public synchronized void enqueue(Object obj)
    {
        addElement(obj);
    }

    public synchronized Object dequeue()
    {
        Object obj = null;
        if(size() > 0)
        {
            obj = firstElement();
            removeElementAt(0);
        }
        return obj;
    }

    public synchronized Object peek()
    {
        size();
        return elementAt(0);
    }

    public synchronized boolean empty()
    {
        return size() == 0;
    }

    public synchronized int length()
    {
        return super.size();
    }

    public synchronized int search(Object obj)
    {
        int i = indexOf(obj);
        if(i >= 0)
            return i;
        else
            return -1;
    }

    public Queue()
    {
    }
}