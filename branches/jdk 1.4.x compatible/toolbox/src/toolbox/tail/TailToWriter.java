package toolbox.tail;

import java.io.IOException;
import java.io.Writer;

class TailToWriter extends TailAdapter
{
    private Writer sink;
    
    public TailToWriter(Writer sink)
    {
        this.sink = sink;
    }
    
    public void nextLine(Tail tail, String line) 
    {
        try 
        {
            sink.write(line);
            sink.write("\n");
            sink.flush();
        }
        catch (IOException e) 
        {
            Tail.logger_.error(e);
        }
    }

    public Writer getWriter() 
    {
        return sink;
    }
}