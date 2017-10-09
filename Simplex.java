import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Formatter;
import java.util.Locale;

public class Simplex{

    public static final byte SOLVED = -5;
    public static final byte IMPOSSIBLE = -4;
    public static final byte MULTIPLE_SOLUTIONS = -3;
    public static final byte UNLIMETED_SOLUTION = -2;
    public static final byte SOLVING = -1;

    private String [] vb;       //mapa das variaveis basicas
    private String [] vnb;      //mapa das variaveis nao basicas
    private Cell [][] table;    //tabela do simplex
    
    private Map<String, Integer> vbMap = new HashMap<String, Integer>();
    private Map<String, Integer> vnbMap = new HashMap<String, Integer>();

    public Simplex (Cell[][] table, String[] vnb){
        this.table = table;
        this.vnb = vnb;

        this.vb = new String[table.length];
        vb[0] = "f(x)";
        for (int i = 1; i < this.vb.length; i++){
            vb[i] = "k" + i;
            vbMap.put(vb[i], i);
        }

        for (int i = 1; i < this.vnb.length; i++){
            vnbMap.put(vnb[i], i);
        }
    }


    //******************************************************************
    //                      METODOS PUBLICOS
    //******************************************************************

    /** 
    * Mostra o estado atual da tabela
    */
    public void showTable(){
        System.out.println("PARTE SUPERIOR\n");
        System.out.printf("%8s ", "");
        for (int i = 0; i < vnb.length; i++){
            System.out.printf("%8s ", vnb[i]);
        }
        System.out.println("");

        for (int i = 0; i < table.length; i++){
            System.out.printf("%8s ", vb[i]);
            for (int j = 0; j < table[0].length; j++){
                System.out.printf ("%8s ", table[i][j].high_frac.getFraction());
            }
            System.out.println("");
        }

        System.out.println("\n\n");
    }

    /**
    * Mostra o estado atual da parte inferior da tabela
    */
    public void showAuxTable(){

        System.out.println("PARTE INFERIOR\n");

        System.out.printf("%8s ", "");
        for (int i = 0; i < vnb.length; i++){
            System.out.printf("%8s ", vnb[i]);
        }
        System.out.println("");

        for (int i = 0; i < table.length; i++){
            System.out.printf("%8s ", vb[i]);
            for (int j = 0; j < table[0].length; j++){
                System.out.printf ("%8s ", table[i][j].low_frac.getFraction());
            }
            System.out.println("");
        }

        System.out.println("\n\n");
    }

    /**
    * Resolve o simplex
    */
    public String solve(){
        int isSolved = SOLVED;
        String answer;
        //se existe um membro negativo, vai para a primeira parte do algoritmo
        if (getNegativeML() != -1){
            isSolved = firstStep();
        }

        if (isSolved == SOLVED){
            isSolved = secondStep();

            if (isSolved == SOLVED){
                answer = getAnswer();
            }
            else if (isSolved == MULTIPLE_SOLUTIONS){
                answer = "Existem multiplas solucoes para o problema";
            }
            else{
                answer = "A soulucao do problema e ilimitada";
            }
        }
        else{
            answer = "Nao existe solucao para o problema informado";
        }

        return answer;
    }



    //******************************************************************
    //                      METODOS PRIVADOS
    //******************************************************************

    /**
    * Primeiro passo do algorimo (Quando existe membro livre negativo)
    */
    private int firstStep(){
        boolean possible = isPossible();
        int pos = -1;

        //para quando nao existir mais membros livres ou quando achar uma solucao
        //impossivel
        while (possible && (pos = getNegativeML()) != -1){

            //pega a linha e coluna permissiva
            int permissive_col = getPermissiveColumn(pos);
            int permissive_line = getPermissiveLine(permissive_col);

            switchMethod(permissive_line, permissive_col);

            possible = isPossible();
            showTable();
        }

        return possible ? SOLVED : IMPOSSIBLE;
    }

    /**
    * Segunda etapa do algoritmo (Quando nao existe membro negativo)
    */
    private int secondStep(){
        int permissive_col, permissive_line;

        //enquanto for encontrado uma coluna permissiva
        while ((permissive_col = getState()) > 0){
            permissive_line = getPermissiveLine(permissive_col);
            switchMethod(permissive_line, permissive_col);
            showTable();
        } 

        return permissive_col;
    }

    /**
    * Realiza a troca das variaveis basicas e nao basicas
    * @param permissive_line Linha permissiva selecionada
    * @param permissive_column Coluna permissiva selecionada
    */
    private void switchMethod(int permissive_line, int permissive_col){
        //inverte o pivo na parte baixa da celula
        table[permissive_line][permissive_col].low_frac.add(table[permissive_line][permissive_col].high_frac.getInverse());

        //multiplica toda a linha pelo inverso do pivo
        for (int j = 0; j < table[0].length; j++){
            if (j != permissive_col){
                    table[permissive_line][j].low_frac.add(table[permissive_line][j].high_frac);
                    table[permissive_line][j].low_frac.mult(table[permissive_line][permissive_col].low_frac);
            }
        }

        //multiplica toda a coluna por - o inverso do pivo
        Fraction aux = new Fraction(-1);
        aux.mult(table[permissive_line][permissive_col].low_frac);

        for (int i = 0; i < table.length; i++){
            if (i != permissive_line){
                table[i][permissive_col].low_frac.add(table[i][permissive_col].high_frac);
                table[i][permissive_col].low_frac.mult(aux);
            }
        }

        //calcula as celulas em branco
        for (int i = 0; i < table.length; i++){
            for (int j = 0; j < table[0].length; j++){
                if (i != permissive_line && j != permissive_col){
                    table[i][j].low_frac.add(table[i][permissive_col].low_frac);
                    table[i][j].low_frac.mult(table[permissive_line][j].high_frac);
                }
            }
        }
            
        //troca as variaveis basicas e nao basicas
        String tmp = vb[permissive_line];
        vb[permissive_line] = vnb[permissive_col];
        vnb[permissive_col] = tmp;

        //gera a nova matriz
        for (int i = 0; i < table.length; i++){
            for (int j = 0; j < table[0].length; j++){
                if (i != permissive_line && j != permissive_col){
                    table[i][j].high_frac.add(table[i][j].low_frac);
                }
                else{
                    table[i][j].high_frac = table[i][j].low_frac;                        
                }

                table[i][j].low_frac = new Fraction(0);
            }
        }
    }

    //******************************************************************
    //                      METODOS GETTERS
    //******************************************************************
    public String getAnswer(){

        StringBuilder answer = new StringBuilder();

        answer.append(String.format("A solucao otima encontrada foi: %f\n\n", Math.abs(table[0][0].high_frac.getValue())));
        //mostra o valor das variaveis basicas
        for (int i = 1; i < table.length; i++){
            if (vb[i].charAt(0) == 'k'){
                answer.append(String.format("O valor da variavel artificial da restricao %d e: %s\n", vbMap.get(vb[i]), table[i][0].high_frac.getFraction()));
            }
            else{
                answer.append(String.format("Valor de %s: %s\n", vb[i], table[i][0].high_frac.getFraction()));
            }
        }

        //mostra o valor das variaveis nao basicas
        for (int j = 1; j < table[0].length; j++){
            if (vnb[j].charAt(0) == 'k'){
                answer.append(String.format("O valor da variavel artificial da restricao %d e: %s\n", vbMap.get(vnb[j]), "0/1"));
            }
            else{
                answer.append(String.format("Valor de %s: %s\n", vnb[j], "0/1"));
            }
        }

        return answer.toString();
    }
    
    /**
    * Verifica se a solucao e possivel
    * @return False se o problema for impossivel. True, caso contrario
    */
    private boolean isPossible(){
        boolean possible = false;

        int negativeML = getNegativeML();

        //se o membro livre negativo nao existe, existe uma solucao possivel (solucao otima,
        //solucao ilimitada ou solucao irrestrita)
        if (negativeML == -1){
            possible = true;
        }

        //se existe um membro livre negativo verifica se existe uma regiao permissiva
        else{
            //olhar os membros livres
            for (int i = negativeML; i < table.length && !possible; i++){

                //se um membro livre e negativo        
                if (table[i][0].high_frac.getValue() < 0){

                    //olha a linha
                    for (int j = 1; j < table[0].length; j++){

                        //se existe um elemento negativo na linha, a solucao e possivel
                        if (table[i][j].high_frac.getValue() < 0){
                            possible = true;
                        }
                    }
                }
            }
        }

        return possible;
    }

    /**
    * Pega a posicao do membro livre negativo
    * @return Posicao do membro livre negativo ou -1 caso ele nao exista
    */
    private int getNegativeML(){
        boolean negativeML = false;
        int pos = -1;

        for (int i = 1; i < table.length && !negativeML; i++){
            if (table[i][0].high_frac.getValue() < 0){
                negativeML = true;
                pos = i;
            }
        }

        return pos;
    }

    /**
    *   Metodo para encontrar o estado do algorimo
    *   @retorna -5: se solucao otima foi encontrada.
    *            -3: se existem multiplas solucoes
    *            -2: se a solucao e ilimitada
    *            indice da coluna permissiva se nao se enquadrou em nenhuma dos casos anteriores
    */
    private int getState(){
        int state = SOLVED;

        //verifica se ja encontrou a solucao otima
        for (int j = 1; j < table[0].length; j++){
            if (table[0][j].high_frac.getValue() >= 0){
                state = SOLVING;
                break;
            }
        }

        //se nao foi encontrada uma solucao otima, verifica se existem multiplas solucoes
        if (state == SOLVING){
            state = MULTIPLE_SOLUTIONS;

            //verifica se existe algum numero positivo
            for (int j = 1; j < table[0].length; j++){
                if (table[0][j].high_frac.getValue() > 0){
                    state = SOLVING;
                    break;
                }
            }
        }

        //se nao e nem solucao otima e nem multiplas solucoes olha se e solucao ilimitada
        if (state == SOLVING){
            state = UNLIMETED_SOLUTION;

            //verifica se existe uma variavel nao basica positiva
            for (int j = 1; j < table[0].length && state == UNLIMETED_SOLUTION; j++){
                
                //se existir verifica se existe algum elemento positivo na coluna
                if (table[0][j].high_frac.getValue() > 0){
                    for (int i = 1; i < table.length; i++){

                        //se existir retorna a coluna permissiva
                        if (table[i][j].high_frac.getValue() > 0){
                            state = j;
                            break;
                        }
                    }
                }
            }
        }

        return state;
    }

    /**
    * Seleciona a coluna permissiva de uma linha para a primeira linha
    * @param line Linha a ser analizada
    * @return Coluna permissiva selecionada
    */
    private int getPermissiveColumn(int line){
        int permissive_col = -1;

        for (int j = 1; j < table[0].length; j++){
            if (table[line][j].high_frac.getValue() < 0){
                permissive_col = j;
                break;
            }
        }

        return permissive_col;
    }

    /**
    * Busca a linha permissiva dado uma coluna permissiva
    * @param column Coluna permissiva
    * @return indice da linha permissiva
    */
    private int getPermissiveLine(int column){
        int permissive_line = -1;
        double quota = Double.MAX_VALUE;

        for (int i = 1; i < table.length; i++){

            //se o numero da coluna permissiva nao e 0
            //se ambas as fracoes possuem mesmo sinal
            //se o quociente for menor que o ja encontrado
            if (    table[i][column].high_frac.getNumerator() != 0 &&
                    ((table[i][0].high_frac.getNumerator() < 0 && table[i][column].high_frac.getNumerator() < 0) || (table[i][column].high_frac.getNumerator() >= 0 && table[i][column].high_frac.getNumerator() >= 0)) &&
                    table[i][0].high_frac.getValue() / table[i][column].high_frac.getValue() < quota){

                        quota = table[i][0].high_frac.getValue() / table[i][column].high_frac.getValue();
                        permissive_line = i;
            }
        }

        return permissive_line;
    }
}