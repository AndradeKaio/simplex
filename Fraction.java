public class Fraction{
    
    private int numerator, denominator;

    /**
    * Construtor padrao
    * @param numerator Numerador da fracao
    * @param denomitaro Denominador da fracao
    */
    public Fraction(int numerator, int denominator){
        this.numerator = numerator;
        this.denominator = denominator;
    }

    /**
    * Construtor alternativo
    * @param value Valor inteiro a ser transformado em fracao
    */
    public Fraction (int value){
        this.numerator = value;
        this.denominator = 1;
    }

    /**
    * Construtor alternativo
    * @param value Valor real a ser convertido em fracao
    */
    public Fraction(double value){
        String text = Double.toString(value);
        int decimalPlaces = text.length() - text.indexOf('.') - 1;
        this.denominator = (int)Math.pow(10, decimalPlaces);
        value *= this.denominator;
        numerator = (int)value;
        simplify();
    }

    /**
    * Soma uma outra fracao a fracao corrente
    * @param f Fracao a ser somada
    */
    public void add(Fraction f){
        if (this.denominator == f.getDenominator()){
            this.numerator += f.getNumerator();
        }
        else{
            int mdc = getMDC(this.denominator, f.getDenominator());
            int mmc = (this.denominator * f.getDenominator()) / mdc;

            this.numerator = ((mmc / this.denominator) * this.numerator) + ((mmc / f.getDenominator()) * f.getNumerator());
            this.denominator = mmc;
        }

        simplify();
    }

    /**
    * Subtrai da fracao corrente uma fracao f
    * @param f Fracao a ser subtraida
    */
    public void sub(Fraction f){
        if (this.denominator == f.getDenominator()){
            this.numerator -= f.getNumerator();
        }
        else{
            int mdc = getMDC(this.denominator, f.getDenominator());
            int mmc = (this.denominator * f.getDenominator()) / mdc;

            this.numerator = ((mmc / this.denominator) * this.numerator) - ((mmc / f.getDenominator()) * f.getNumerator());
            this.denominator = mmc;
        }

        simplify();
    }

    /**
    * Multiplica a fracao corrente por uma outra fracao
    * @param f Fracao multiplicadora
    */
    public void mult(Fraction f){
        this.numerator *= f.getNumerator();
        this.denominator *= f.getDenominator();
        simplify();
    }

    /**
    * Divide a fracao atual por uma outra fracao
    *  @param f Fracao divisora
    */
    public void div(Fraction f){
        this.numerator *= f.getDenominator();
        this.denominator *= f.getNumerator();
    }

    /**
    * Simplifica a fracao
    */
    public void simplify(){
        int mdc = getMDC(numerator, denominator);
        numerator /= mdc;
        denominator /= mdc;
    }

    /**
    * Calcula o valor real da fracao
    * @return valor da expressao
    */
    public double getValue(){
        return (double)numerator / (double)denominator;
    }

    /**
    *
    *
    */
    public Fraction getInverse(){
        Fraction f = new Fraction(this.numerator < 0 ?  -1 * this.denominator : this.denominator, Math.abs(this.numerator));
        return f;
    }

    public int getNumerator(){
        return this.numerator;
    }

    public int getDenominator(){
        return this.denominator;
    }

    public String getFraction(){
        return Integer.toString(this.numerator) + "/" + Integer.toString(this.denominator);
    }

    /**
        Retorna o MDC de dois numeros inteiros
        @param a Primeiro valor inteiro
        @param b Segundo valor inteiro
        @return MDC entre os dois valores recebidos
    */
    private int getMDC(int a, int b){
        while (a % b != 0){
            int temp = a;
            a = b;
            b = temp % b;
        }

        return Math.abs(b);
    }
}