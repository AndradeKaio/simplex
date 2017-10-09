public class Parser{

    private static final int FINAL_STATE = 7;

    private int sP;
    private String equation;

    /**
    * Construtor padrao
    * @param equation Equacao a ser analisada
    */
    public Parser(String equation){
        this.sP = 0;
        this.equation = equation;
    }

    /**
    * Extrai um token da equacao
    * @return Token lido
    */
    public Record getRecord() throws Exception{
        String lexem = "";
        int state = 0;
        Record r = null;

        while(state != FINAL_STATE){
            switch(state){
                case 0:
                    if (sP >= equation.length()){
                        r = new Record(Record.EOF, "");
                        state = FINAL_STATE;
                    }
                    else if (isDigit(equation.charAt(sP))){
                        lexem += equation.charAt(sP);
                        sP++;
                        state = 1;
                    }
                    else if (isSignal(equation.charAt(sP))){
                        lexem += equation.charAt(sP);
                        sP++;
                        state = 4;
                    }
                    else if (isChar(equation.charAt(sP))){
                        lexem += equation.charAt(sP);
                        sP++;
                        state = 5;
                    }
                    else if (isInequality(equation.charAt(sP))){
                        lexem += equation.charAt(sP);
                        sP++;
                        state = 6;
                    }
                    else if (equation.charAt(sP) == ' '){
                        sP++;
                    }
                    else{
                        throw new Exception("Caractere nao esperado: " + equation.charAt(sP));
                    }
                    break;
                
                case 1:
                    if (sP >= equation.length()){
                        r = new Record(Record.VAL, lexem);
                        state = FINAL_STATE;
                    }
                    else if (isDigit(equation.charAt(sP))){
                        lexem += equation.charAt(sP);
                        sP++;
                    }
                    else if (isDoubleSeparator(equation.charAt(sP))){
                        lexem += equation.charAt(sP);
                        sP++;
                        state = 2;
                    }
                    else{
                        r = new Record(Record.VAL, lexem);
                        state = FINAL_STATE;
                    }
                    break;
                
                case 2:
                    if (sP >= equation.length()){
                        throw new Exception("Fim de String nao esperado");
                    }
                    else if (isDigit(equation.charAt(sP))){
                        state = 3;
                    }
                    else{
                        throw new Exception ("Era esperado um digito. Recebido: " + equation.charAt(sP));
                    }
                    break;
                
                case 3:
                    if (sP >= equation.length()){
                        r = new Record(Record.VAL, lexem);
                        state = FINAL_STATE;
                    }
                    else if (isDigit(equation.charAt(sP))){
                        lexem += equation.charAt(sP);
                        sP++;
                    }
                    else{
                        r = new Record(Record.VAL, lexem);
                        state = FINAL_STATE;
                    }
                    break;

                case 4:
                    if (sP >= equation.length()){
                        r = new Record(Record.SIGNAL, lexem);
                        state = FINAL_STATE;
                    }
                    else if (isDigit(equation.charAt(sP))){
                        state = 1;
                    }
                    else if (equation.charAt(sP) == ' '){
                        sP++;
                    }
                    else{
                        r = new Record(Record.SIGNAL, lexem);
                        state = FINAL_STATE;
                    }
                    break;
                
                case 5:
                    if (sP >= equation.length()){
                        r = new Record(Record.VAR, lexem);
                        state = FINAL_STATE;
                    }
                    else if (isDigit(equation.charAt(sP)) || isChar(equation.charAt(sP))){
                        lexem += equation.charAt(sP);
                        sP++;
                    }
                    else{
                        r = new Record(Record.VAR, lexem);
                        state = FINAL_STATE;
                    }
                    break;
                
                case 6:
                    if (sP >= equation.length()){
                        throw new Exception("Fim de String nao esperado");
                    }
                    else if (equation.charAt(sP) == '='){
                        lexem += equation.charAt(sP);
                        r = new Record(Record.INEQUALITY, lexem);
                        state = FINAL_STATE;
                        sP++;
                    }
                    else{
                        throw new Exception ("Era esperado um digito. Recebido: " + equation.charAt(sP));
                    }
                    break;
            }
        }

        return r;
    }

    private boolean isChar(char c){
        return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'));
    }

    private boolean isDigit(char c){
        return (c >= '0' && c <= '9');
    }

    private boolean isDoubleSeparator(char c){
        return c == '.' || c == '/';
    }

    private boolean isInequality(char c){
        return c == '>' || c == '<';
    }

    private boolean isSignal(char c){
        return c == '+' || c == '-';
    }
}