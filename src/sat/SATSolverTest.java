package sat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Collections;

/*
import static org.junit.Assert.*;

import org.junit.Test;
*/

import sat.env.*;
import sat.formula.*;


public class SATSolverTest {
    // Literal a = PosLiteral.make("a");
    // Literal b = PosLiteral.make("b");
    // Literal c = PosLiteral.make("c");
    // Literal na = a.getNegation();
    // Literal nb = b.getNegation();
    // Literal nc = c.getNegation();

    

	
	// TODO: add the main method that reads the .cnf file and calls SATSolver.solve to determine the satisfiability
    public static void main(String[] args) {
        String file_path = "C:/Users/tiong/Documents/GitHub/2D_JAVA/sampleCNF/test_2020.cnf";
        Formula formula = convertCNF(file_path);
        System.out.println("SAT solver starts!!!");
        long started = System.nanoTime();
        Environment env = SATSolver.solve(formula);
        long time = System.nanoTime();
        long timeTaken = time - started;
        System.out.println("Time:" + timeTaken / 1000000.0 + "ms");
        createTxt(env);
    }
    
    public static void createTxt(Environment env) {
        if (env == null) {
            System.out.println("Unstatisfiable bro!");
        } else {
            System.out.println("Satisfiable bro!");

            try {
                File out = new File("BoolAssignment.txt");
                out.createNewFile();
                System.out.println(out.getName() + " created bro!");
                FileWriter writer = new FileWriter(out.getName());
                String sEnv = env.toString().replace("Environment:[", "").replace("]", "");
                String[] rows = sEnv.split(", ");
                // System.out.println(rows.length);
                for (String row : rows) {
                    writer.write(row + "\n");
                }
                writer.close();
                System.out.println("Solution wrote bro!");

            } catch (IOException e) {
                System.out.println("IO Exception bro...");
            }
        }
    }

    public static Formula convertCNF(String file) {
        List<String> rows;
        try {
            rows = Files.readAllLines(Paths.get(file), StandardCharsets.UTF_8);
        } catch (IOException e){
            System.out.println("IO Exception Error Bro!");
            return null;
        }
        
        ArrayList<Literal> lList = new ArrayList<>();
        ArrayList<Clause> cList = new ArrayList<>();
        
        for (String row : rows) {
            
            if (row.length() != 0) {
                
                if (row.charAt(0) == 'c' || row.charAt(0) == 'p') {
                    // Ignore if the string is a comment/problem statement
                    continue;
                } else {
                    
                    // Split each row into an array 
                    String[] lArr = row.trim().split(" ");

                    for (String l : lArr) {
                        
                        if (l.charAt(0) == '-') {
                            Literal lit = NegLiteral.make(l.substring(1));
                            lList.add(lit);
                        } else if (l.charAt(0) != '0') {
                            Literal lit = PosLiteral.make(l);
                            lList.add(lit);
                        } else {
                            // Call makeCl to create a new Clause Object
                            Clause newClause = makeCl(lList);
                            cList.add(newClause);
                            lList.clear();
                        }
                    }
                 }
            }
        }

        return makeFm(cList);
    }
    
//     public void testSATSolver1(){
//     	// (a v b)
//     	Environment e = SATSolver.solve(makeFm(makeCl(a,b))	);
// /*
//     	assertTrue( "one of the literals should be set to true",
//     			Bool.TRUE == e.get(a.getVariable())  
//     			|| Bool.TRUE == e.get(b.getVariable())	);
    	
// */    	
//     }
    
    
//     public void testSATSolver2(){
//     	// (~a)
//     	Environment e = SATSolver.solve(makeFm(makeCl(na)));
// /*
//     	assertEquals( Bool.FALSE, e.get(na.getVariable()));
// */    	
//     }
    
    private static Formula makeFm(ArrayList<Clause> e) {
        Formula f = new Formula();
        for (Clause c : e) {
            f = f.addClause(c);
        }
        return f;
    }
    
    private static Clause makeCl(ArrayList<Literal> e) {
        Clause c = new Clause();
        for (Literal l : e) {
            c = c.add(l);
        }
        return c;
    }
    
    
    
}