package sat;

import java.util.*;

import javax.xml.stream.events.Comment;

import immutable.EmptyImList;
import immutable.ImList;
import sat.env.Environment;
import sat.formula.*;

/**
 * A simple DPLL SAT solver. See http://en.wikipedia.org/wiki/DPLL_algorithm
 */
public class SATSolver {
    /**
     * Solve the problem using a simple version of DPLL with backtracking and
     * unit propagation. The returned environment binds literals of class
     * bool.Variable rather than the special literals used in clausification of
     * class clausal.Literal, so that clients can more readily use it.
     * 
     * @return an environment for which the problem evaluates to Bool.TRUE, or
     *         null if no such environment exists.
     */
    public static Environment solve(Formula formula) {
        ImList<Clause> formulaClauses = formula.getClauses();
        Environment env = new Environment();
        Environment result = solve(formulaClauses, env);

        return result;
    }

    /**
     * Takes a partial assignment of variables to values, and recursively
     * searches for a complete satisfying assignment.
     * 
     * @param clauses
     *            formula in conjunctive normal form
     * @param env
     *            assignment of some or all variables in clauses to true or
     *            false values.
     * @return an environment for which all the clauses evaluate to Bool.TRUE,
     *         or null if no such environment exists.
     */
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



    /**
     * given a clause list and literal, produce a new list resulting from
     * setting that literal to true
     * 
     * @param clauses
     *            , a list of clauses
     * @param l
     *            , a literal to set to true
     * @return a new list of clauses resulting from setting l to true
     */
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
