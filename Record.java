public class Record{

    public static final byte VAL = 0;
    public static final byte SIGNAL = 6;
    public static final byte VAR = 7;
    public static final byte INEQUALITY = 10;
    public static final byte EOF = 15;

    public int token;
    public String lexem;

    public Record(int token, String lexem){
        this.token = token;
        this.lexem = lexem;
    }
}