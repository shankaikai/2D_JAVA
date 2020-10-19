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
        if (clauses.isEmpty()) {return env;}
//      no clauses, the formula is trivially satisfiable.

        int smallestClauseSize = clauses.first().size();
        Clause smallestClause = clauses.first();

        for (Clause cl: clauses) {
            if (cl.isEmpty()) {return null;}
//      empty clause, the clause is unsatisfiable.

            if (cl.size() < smallestClauseSize) {
                smallestClauseSize = cl.size();
                smallestClause = cl;
            }
        }

        Literal literal = smallestClause.chooseLiteral();
//      The variable literal is the first Literal in smallestClause.
        if (smallestClause.isUnit()) {
            if (literal instanceof PosLiteral) {
                env = env.putTrue(literal.getVariable());
//      Positive literal will have bool.TRUE
            } else if (literal instanceof NegLiteral){
                env = env.putFalse(literal.getVariable());
//      Negative literal will have bool.FALSE
            }

            return solve(substitute(clauses, literal), env);

        } else {
            env = env.putTrue(literal.getVariable());
//      set literal to TRUE
            Environment testLiteral = solve(substitute(clauses, literal),env);
//      substitute it in all clause
            if (testLiteral == null){
                env = env.putFalse(literal.getVariable());
//      set literal to FALSE as testLiteral == null
                return solve(substitute(clauses,literal.getNegation()),env);
            }
            return testLiteral;
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
    private static ImList<Clause> substitute(ImList<Clause> clauses,
            Literal l) {
        // TODO: implement this.
        ImList<Clause> new_list = new EmptyImList<Clause>();
        for (Clause clause : clauses) {
            if (clause.contains(l) || clause.contains(l.getNegation())) {
                clause.reduce(l);
            }
            if (clause != null) {
                new_list.add(clause);
            }
        }
        return new_list;
    }

}
