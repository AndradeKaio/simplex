import java.util.List;
import java.util.ArrayList;

public class Main{   
    
    // public static void main (String [] args){
    //     List<String> vnb = new ArrayList<String>();
    //     List<Fraction> values = new ArrayList<Fraction>();

    //     vnb.add("x1");
    //     vnb.add("x2");
    //     vnb.add("x3");
    //     vnb.add("x4");
    //     vnb.add("x5");

    //     Simplex s = new Simplex(5, vnb);

    //     s.insertVB("k1");
    //     s.insertVB("k2");
    //     s.insertVB("k3");
    //     s.insertVB("k4");
    //     s.insertVB("k5");

    //     s.setValue(0, 1, new Fraction(-2));
    //     s.setValue(0, 2, new Fraction(5));
    //     s.setValue(0, 3, new Fraction(-2));
    //     s.setValue(0, 4, new Fraction(-3, 4));
    //     s.setValue(0, 5, new Fraction(-8));

    //     s.setValue(1, 0, new Fraction(-8));
    //     s.setValue(1, 1, new Fraction(-2));
    //     s.setValue(1, 2, new Fraction(3));
    //     s.setValue(2, 0, new Fraction(20));
    //     s.setValue(2, 3, new Fraction(2));

    //     s.setValue(2, 4, new Fraction(2));
    //     s.setValue(3, 0, new Fraction(-10));
    //     s.setValue(3, 1, new Fraction(-1));
    //     s.setValue(3, 5, new Fraction(-2));
    //     s.setValue(4, 0, new Fraction(-8));

    //     s.setValue(4, 2, new Fraction(1));
    //     s.setValue(4, 3, new Fraction(-1));
    //     s.setValue(5, 0, new Fraction(30));
    //     s.setValue(5, 1, new Fraction(-1));
    //     s.setValue(5, 5, new Fraction(2));

    //     s.showTable();
    //     s.solve();
    // }

    // public static void main (String[] args){
    //     Parser p = new Parser("-x1 + 5.0x2 - 2x3 -3/4x4+8x5>=30");
    //     Record r =  null;

    //     try{
    //         r = p.getRecord();
    //     }catch (Exception e){
    //         e.printStackTrace();
    //     }

    //     while (r.token != Record.EOF){
    //         switch(r.token){
    //             case Record.INT:
    //                 System.out.println("Inteiro: " + Integer.parseInt(r.lexem));
    //                 break;

    //             case Record.DOUBLE:
    //                 if (!r.lexem.contains("/")){
    //                     System.out.println("Real: " + Double.parseDouble(r.lexem));
    //                 }
    //                 else{
    //                     String[] values = r.lexem.split("/");
    //                     Fraction f = new Fraction (Integer.parseInt(values[0]), Integer.parseInt(values[1]));
    //                     System.out.println("Real fracionario: " + f.getFraction());
    //                 }
    //                 break;

    //             case Record.SIGNAL:
    //                 System.out.println("Sinal: " + r.lexem);
    //                 break;
                
    //             case Record.VAR:
    //                 System.out.println("Variavel: " + r.lexem);
    //                 break;
                
    //             case Record.INEQUALITY:
    //                 System.out.println("Desigualdade: " + r.lexem);
    //                 break;
    //         }

    //         try{
    //             r = p.getRecord();
    //         }catch (Exception e){
    //             e.printStackTrace();
    //             System.exit(0);
    //         }
    //     }
    // }

    public static void test (List<String> a, List<String> b){
        a.add("Oi");
        b.add("Chun");
    }

    public static void main (String[] args){
        List<String> a = new ArrayList<String>();
        a.add("80x1+60x2");
        a.add("4x1+6x2>=24");
        a.add("4x1+2x2<=16");
        a.add("x2 <= 3");
        Processor p = new Processor(a, Processor.MAX);
        Simplex s = new Simplex(p.getSimplexTable(), p.getVnb());
        s.showTable();
        System.out.println(s.solve());
        
    }
}