package toolbox.jsourceview;

public class Machine
    implements MachineConstants {

        public static void scanLine(Line line, LineStatus linestatus) {
            int i = 0;
    label0:
            do
                switch(i) {
                default:
                    break;
    
                case 0: // '\0'
                    linestatus.countLine = false;
                    if(linestatus.inComment)
                        i = 8;
                    else
                        i = 1;
                    continue;
    
                case 9: // '\t'
                    break label0;
    
                case 1: // '\001'
                    switch(line.getNextToken()) {
                    case 1: // '\001'
                    case 2: // '\002'
                        i = 9;
                        break;
    
                    case 0: // '\0'
                    case 4: // '\004'
                        i = 2;
                        break;
    
                    case 3: // '\003'
                        i = 3;
                        break;
    
                    default:
                        throw new IllegalArgumentException("State 1 transition invalid! ");
                    }
                    continue;
    
                case 2: // '\002'
                    linestatus.countLine = true;
                    switch(line.getNextToken()) {
                    case 0: // '\0'
                    case 4: // '\004'
                        i = 2;
                        break;
    
                    case 1: // '\001'
                    case 2: // '\002'
                        i = 9;
                        break;
    
                    case 3: // '\003'
                        i = 6;
                        break;
    
                    default:
                        throw new IllegalArgumentException("State 2 transition invalid!");
                    }
                    continue;
    
                case 3: // '\003'
                    linestatus.inComment = true;
                    switch(line.getNextToken()) {
                    case 0: // '\0'
                    case 2: // '\002'
                    case 3: // '\003'
                        i = 3;
                        break;
    
                    case 1: // '\001'
                        i = 9;
                        break;
    
                    case 4: // '\004'
                        i = 4;
                        break;
    
                    default:
                        throw new IllegalArgumentException("State 3 transition invalid!");
                    }
                    continue;
    
                case 4: // '\004'
                    linestatus.inComment = false;
                    switch(line.getNextToken()) {
                    case 1: // '\001'
                    case 2: // '\002'
                        i = 9;
                        break;
    
                    case 0: // '\0'
                    case 4: // '\004'
                        i = 5;
                        break;
    
                    case 3: // '\003'
                        i = 6;
                        break;
    
                    default:
                        throw new IllegalArgumentException("State 4 transition invalid!");
                    }
                    continue;
    
                case 5: // '\005'
                    linestatus.countLine = true;
                    i = 4;
                    continue;
    
                case 6: // '\006'
                    linestatus.inComment = true;
                    switch(line.getNextToken()) {
                    case 0: // '\0'
                    case 2: // '\002'
                    case 3: // '\003'
                        i = 6;
                        break;
    
                    case 1: // '\001'
                        i = 9;
                        break;
    
                    case 4: // '\004'
                        i = 7;
                        break;
    
                    default:
                        throw new IllegalArgumentException("State 6 transition invalid!");
                    }
                    continue;
    
                case 7: // '\007'
                    linestatus.inComment = false;
                    switch(line.getNextToken()) {
                    case 0: // '\0'
                    case 4: // '\004'
                        i = 7;
                        break;
    
                    case 1: // '\001'
                    case 2: // '\002'
                        i = 9;
                        break;
    
                    case 3: // '\003'
                        i = 6;
                        break;
    
                    default:
                        throw new IllegalArgumentException("State 7 transition invalid!");
                    }
                    continue;
    
                case 8: // '\b'
                    switch(line.getNextToken()) {
                    case 0: // '\0'
                    case 2: // '\002'
                    case 3: // '\003'
                        i = 8;
                        break;
    
                    case 1: // '\001'
                        i = 9;
                        break;
    
                    case 4: // '\004'
                        i = 4;
                        break;
    
                    default:
                        throw new IllegalArgumentException("State 8 transition invalid!");
                    }
                    break;
                }
            while(true);
        }

    public Machine() {
    }
}