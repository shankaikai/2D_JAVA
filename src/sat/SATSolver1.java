package sat;

import immutable.ImList;
import sat.env.Environment;
import sat.formula.Clause;
import sat.formula.Formula;
import sat.formula.Literal;
import sat.formula.PosLiteral;

/**
 * A simple DPLL SAT solver. See http://en.wikipedia.org/wiki/DPLL_algorithm
 */
public class SATSolver1 {
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
        Environment result = solve(formula.getClauses(),new Environment());
        if(!(result== null)) return result;
        else return null;
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
        if(clauses.isEmpty()){
            return env;
//          if there are no clauses, the formula is trivially satisfiable; recursion ends
        }
        int smallest = clauses.first().size();
        Clause smallest_cl = clauses.first();
        for(Clause cl : clauses) {
            if (cl.isEmpty()) {
                return null;
//              empty clause denotes FALSE in which case formula is unsatisfiable; recursion ends
            }
            if (cl.size() < smallest) {
                smallest = cl.size();
                smallest_cl = cl;
            }
        }

        Literal l = smallest_cl.chooseLiteral(); //choose the first literal of the smallest clause
        if(smallest_cl.isUnit()){
            return solve(substitute(clauses , l ), updateEnv(l, env));
        }else{
            Environment temp_env = solve(substitute(clauses , l), updateEnv(l, env));
            if(temp_env == null){
/*              if temp_env is null, the formula is unsatisfiable for l set to TRUE.
                Hence, we must backtrack and set its value to FALSE and substitute again.
                If the Environment is still null, we have to backtrack further until temp_env
                is not null, or the formula is unsatisfiable.
 */
                return solve(substitute(clauses , l.getNegation()), updateEnv(l.getNegation(), env));
            }
            return temp_env;
        }

    }

    private static Environment updateEnv( Literal l, Environment env){
        if(l instanceof PosLiteral ){
            return env.putTrue(l.getVariable());
        }else{
            return env.putFalse(l.getVariable());
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
        for(Clause cl : clauses){
            if(cl.contains(l)){
                clauses = clauses.remove(cl);
            }else if(cl.contains(l.getNegation())){
                clauses = clauses.remove(cl).add(cl.reduce(l));
            }
        }
        return clauses;
    }

}