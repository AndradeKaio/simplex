import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
* Classe para montar processar as expressoes,
* encontrando os valores dos membros livres e 
* das variaveis nao basicas
*/
public class Processor{
    public static final byte MIN = -1;
    public static final byte MAX = 1;

    private Fraction[][] table;
    private int rows, columns;
    private List<String> vnb;
    private Map<String, Integer> vnbMap;
    private Parser p;
    private int mapCount;
    private byte type;

    public Processor(List<String> expressions, byte type){
        this.type = type;
        this.mapCount = 0;
        this.rows = expressions.size();
        this.vnbMap = new HashMap<String, Integer>();
        this.vnb = new ArrayList<String>();

        //pega os valores dos termos da funcao objetiva
        //e mapeia as variaveis nao basicas em posicoes da matriz
        this.p = new Parser(expressions.remove(0));
        List<Fraction> objectiveFunctionValues = new ArrayList<Fraction>();
        processObjectiveFunction(objectiveFunctionValues);

        //inicia a tabela de valores com mais duas colunas
        //uma para saber qual o tipo de desigualdade e outra para o termo isolado
        columns = vnb.size() + 2;
        table = new Fraction[rows][columns];

        for (int j = 0; j < columns-2; j++){
            table[0][j] = objectiveFunctionValues.remove(0);
        }

        for (int i = 1; i < rows; i++){
            for (int j = 0; j < columns; j++){
                table[i][j] = new Fraction(0);
            }
        }

        int i = 1;
        //processa e preenche as tabelas com as restricoes
        while(!expressions.isEmpty()){
            processInequality(expressions.remove(0), i);
            i++;
        }
    }

    public Cell[][] getSimplexTable(){
        Cell[][] matrix = new Cell[rows][columns-1];

        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < matrix[0].length; j++){
                matrix[i][j] = new Cell();
                matrix[i][j].high_frac = new Fraction(0);
                matrix[i][j].low_frac = new Fraction(0);
            }
        }

        //copia os dados da funcao objetiva para a matriz
        if (type == MIN){
            for (int j = 0; j < columns -2; j++){
                matrix[0][j+1].high_frac.add(table[0][j]);
                matrix[0][j+1].high_frac.mult(new Fraction(-1));
            }
        }
        else{
            for (int j = 0; j < columns -2; j++){
                matrix[0][j+1].high_frac.add(table[0][j]);
            }
        }

        //copia os valores das inequacoes para a tabela
        for (int i = 1; i < matrix.length; i++){

            if (table[i][columns-1].getValue() > 0){
                for (int j = 0; j < matrix[0].length - 1; j++){
                    matrix[i][j+1].high_frac.add (table[i][j]);
                    matrix[i][j+1].high_frac.mult(new Fraction(-1));
                }

                matrix[i][0].high_frac.add(table[i][columns-2]);
                matrix[i][0].high_frac.mult(new Fraction(-1));
            }

            else{
                for (int j = 0; j < matrix[0].length - 1; j++){
                    matrix[i][j+1].high_frac.add (table[i][j]);
                }
                matrix[i][0].high_frac.add(table[i][columns-2]);
            }

        }

        return matrix;
    }

    public String[] getVnb(){
        this.vnb.add(0, "ML");
        String[] vnb = new String[this.vnb.size()];
        this.vnb.toArray(vnb);
        this.vnb.remove(0);
        return vnb;
    }

    /**
    * Metodo para receber e processar a funcao objetiva
    * mapeando as variaveis em funcoes e extraindo os valores dos termos
    * @param values Lista recebida por referencia para armazenar os valores dos termos
    */
    private void processObjectiveFunction(List<Fraction> values){
        Record r = nextRecord();

        //se o primeiro registro retornado for o de uma
        //variavel insere o valor e a variavel de forma separada
        if (r != null && r.token == Record.VAR){
            values.add(new Fraction(1.0));
            vnb.add(r.lexem);
            vnbMap.put(r.lexem, mapCount);
            mapCount++;
            r = nextRecord();
        }

        //processa o resto da expressao
        while (r != null && r.token != Record.EOF){
            switch (r.token){
                case Record.SIGNAL:
                    values.add(r.lexem.equals("-") ? new Fraction (-1.0) : new Fraction (1.0));
                    break;
                
                case Record.VAL:
                    if (!r.lexem.contains("/")){
                        values.add(new Fraction (Double.parseDouble(r.lexem)));
                    }
                    else{
                        String[] aux = r.lexem.split("/");
                        values.add(new Fraction (Integer.parseInt(aux[0]), Integer.parseInt(aux[1])));
                    }
                    break; 

                case Record.VAR:
                    vnb.add(r.lexem);
                    vnbMap.put(r.lexem, mapCount);
                    mapCount++;
                    break;
            }

            r = nextRecord();
        }
    }

    /**
    * Metodo para processar uma inequacao
    * @param expression Inequacao a ser processada
    * @param i Indice da expressao na tabela
    */
    private void processInequality(String expression, int i){
        p = new Parser(expression);
        Record r = nextRecord();
        Fraction val;

        if (r != null && r.token == Record.VAR){
            table[i][vnbMap.get(r.lexem)] = new Fraction(1);
            r = nextRecord();
        }

        while (r != null && r.token != Record.EOF){
            switch (r.token){
                case Record.SIGNAL:
                    val = new Fraction(r.lexem.equals("-") ? -1.0 : 1.0);
                    r = nextRecord();
                    table[i][vnbMap.get(r.lexem)] = val;
                    break;
                
                case Record.VAL:
                    if (!r.lexem.contains("/")){
                        val = new Fraction(Double.parseDouble(r.lexem));
                    }
                    else{
                        String[] aux = r.lexem.split("/");
                        val = new Fraction (Integer.parseInt(aux[0]), Integer.parseInt(aux[1]));
                    }
                    
                    r = nextRecord();
                    if (r.token != Record.EOF){
                        table[i][vnbMap.get(r.lexem)] = val;
                    }
                    else{
                        table[i][columns - 2] = val;
                    }
                    break;

                case Record.INEQUALITY:
                    if (r.lexem.equals(">=")){
                        table[i][columns - 1] = new Fraction(1);
                    }
                    else{
                        table[i][columns - 1] = new Fraction(-1);
                    }
                    break;
            }

            r = nextRecord();
        }

    }

    /**
    * Pega o proximo registro do parser
    * @return registro encontrado
    */
    private Record nextRecord(){
        Record r = null;
        try{
            r = p.getRecord();
        }catch (Exception e){
            e.printStackTrace();
        }
        return r;
    }

    public void showTable(){
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < columns; j++){
                if (table[i][j] != null)
                    System.out.printf("%8s ", table[i][j].getFraction());
            }
            System.out.println("");
        }
    }
}