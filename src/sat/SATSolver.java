package sat;

import immutable.EmptyImList;
import immutable.ImList;
import sat.env.Environment;
import sat.formula.*;

public class SATSolver {

    public static Environment solve(Formula formula) {
        ImList<Clause> formulaClauses = formula.getClauses();
        Environment env = new Environment();
        Environment result = solve(formulaClauses, env);

        return result;
    }


    private static Environment solve(ImList<Clause> clauses, Environment env) {
        
        // no clauses, the formula is trivially satisfiable.
        if (clauses.isEmpty()) {
            return env;
        }

        Clause smallestClause = clauses.first();

        for (Clause cl: clauses) {
            // Empty clause, the clause is unsatisfiable.
            if (cl.isEmpty()) {
                return null;
            }
            
            // Replace the smallestClause variable with the next clause if it has less literals
            if (cl.size() < smallestClause.size()) {
                smallestClause = cl;
            }
        }

        // The variable literal is the first Literal in smallestClause.
        Literal literal = smallestClause.chooseLiteral();
        
        if (smallestClause.isUnit()) {

            if (literal instanceof PosLiteral) {
                return solve(substitute(clauses, literal), env.putTrue(literal.getVariable()));
                // Positive literal will have bool.TRUE
            } else {
                return solve(substitute(clauses, literal), env.putFalse(literal.getVariable()));
                // Negative literal will have bool.FALSE
            }
            

        } else {

            Environment testE = solve(substitute(clauses, literal), env.putTrue(literal.getVariable()));

            // substitute it in all clause
            if (testE == null){
                testE = solve(substitute(clauses, literal.getNegation()), env.putFalse(literal.getVariable()));

            }
            return testE;
        }
    }

    private static ImList<Clause> substitute(ImList<Clause> clauses, Literal l) {

        // Initiate new clause list
        ImList<Clause> out = new EmptyImList<Clause>();

        // Iterate through all the clauses in the clause list
        for (Clause cl : clauses) {
            
            // Check if clause contains the literal l or its negation
            if (cl.contains(l.getNegation())) {
                // Set literal to true if cl contains the negation of l
                out = out.add(cl.reduce(l));
            
            } else if (!cl.contains(l)) {
                // Add the clause to the output list if the clause does not contain l
                out = out.add(cl);
            }           
        }
        return out;
    }
}
