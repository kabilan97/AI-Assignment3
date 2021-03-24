import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class Homework {
    private static final Random rnd = new Random(222);

    // Example
    public static void task0() {
        // Get CMDP model for 1 agent
        CMDP cmdp = UserGenerator.getCMDPChild();
        CMDP[] cmdps = new CMDP[]{cmdp};

        // construct dummy policy that always executes action 4
        ArrayList<double[][]> policies = new ArrayList<double[][]>();
        double[][] policy = new double[cmdp.getNumStates()][cmdp.getNumActions()];
        for (int s = 0; s < cmdp.getNumStates(); s++) {
            policy[s][4] = 1.0;
        }
        policies.add(policy);

        // construct dummy solution object with dummy expectations 0.0
        Solution solution = new Solution(policies, 0.0, 0.0, new double[]{0.0}, new double[]{0.0});

        // use the simulator to execute on run
        Simulator sim = new Simulator(rnd);
        sim.printActions();
        sim.simulate(cmdps, solution, 1);
    }

    // Solve unconstrained problem for 1 agent with value iteration
    public static Solution task1(double discountFactor) {
        // Get CMDP model for 1 agent
        CMDP cmdp = UserGenerator.getCMDPChild();
        CMDP[] cmdps = new CMDP[]{cmdp};

        // Solve the problem without constraints
        PlanningAlgorithm alg = new PlanningAlgorithm();
        Solution solution = alg.solveVI(cmdps, discountFactor);
        System.out.println("Expected reward: " + solution.getExpectedReward());
        System.out.println("Expected cost: " + solution.getExpectedCost());

        // Simulate solution
        System.out.println();
        Simulator sim = new Simulator(rnd);
        sim.simulate(cmdps, solution, 1000);

        // Print policy of agent 0
        int agentID = 0;
        double[][] policy = solution.getPolicy(agentID);
        System.out.println();
        for (int s = 0; s < cmdps[agentID].getNumStates(); s++) {
            System.out.print("State " + s + ": ");
            for (int a = 0; a < cmdps[agentID].getNumActions(); a++) {
                System.out.print(policy[s][a] + " ");
            }
            System.out.println();
        }
        return solution;
    }

    // Solve unconstrained problem for 1 agent with cost
    public static void task2() {
        // Get CMDP model for 1 agent
        CMDP cmdp = UserGenerator.getCMDPChild();
        CMDP[] cmdps = new CMDP[]{cmdp};

        // Assign cost
        cmdp.assignCost(0, 0, 0); // TODO add costs to the state action pairs

        // Solve the problem without constraints
        PlanningAlgorithm alg = new PlanningAlgorithm();
        Solution solution = alg.solveUnconstrained(cmdps);
        System.out.println("Expected reward: " + solution.getExpectedReward());
        System.out.println("Expected cost: " + solution.getExpectedCost());

        // Simulate solution
        System.out.println();
        Simulator sim = new Simulator(rnd);
        sim.simulate(cmdps, solution, 1000);

        // Print policy of agent 0
        int agentID = 0;
        double[][] policy = solution.getPolicy(agentID);
        System.out.println();
        for (int s = 0; s < cmdps[agentID].getNumStates(); s++) {
            System.out.print("State " + s + ": ");
            for (int a = 0; a < cmdps[agentID].getNumActions(); a++) {
                System.out.print(policy[s][a] + " ");
            }
            System.out.println();
        }

    }

    // Solve constrained problem for 1 agent
    public static void task3() {
        // Get CMDP model for 1 agent
        CMDP cmdp = UserGenerator.getCMDPChild();
        CMDP[] cmdps = new CMDP[]{cmdp};

        // Assign cost
        cmdp.assignCost(0, 0, 0); // TODO add costs to the state action pairs

        PlanningAlgorithm alg = new PlanningAlgorithm();
        Solution solution = alg.solve(cmdps, 20.0);
        double expectedReward = solution.getExpectedReward();
        System.out.println("Expected reward budget 20: " + expectedReward);

        // TODO print expected reward as function of cost limit L
    }

    // Solve constrained problem for 2 agents with trivial budget split
    public static void task4() {
        // Get CMDP models
        CMDP cmdpChild = UserGenerator.getCMDPChild();
        CMDP cmdpAdult = UserGenerator.getCMDPAdult();

        // Assign cost to child
        cmdpChild.assignCost(0, 0, 0); // TODO add costs to the state action pairs

        // Assign cost to adult
        cmdpAdult.assignCost(0, 0, 0); // TODO add costs to the state action pairs

        PlanningAlgorithm alg = new PlanningAlgorithm();
        Simulator sim = new Simulator(rnd);

        // Solve both problems separately without constraints and print expectations
        System.out.println("=========== UNCONSTRAINED ===========");
        for (int i = 0; i < 2; i++) {
            CMDP cmdp = (i == 0) ? cmdpChild : cmdpAdult;
            Solution sol = alg.solveUnconstrained(new CMDP[]{cmdp});
            double expectedReward0 = sol.getExpectedReward();
            double expectedCost0 = sol.getExpectedCost();
            System.out.println("Expected reward agent " + i + ": " + expectedReward0);
            System.out.println("Expected cost agent " + i + ": " + expectedCost0);
        }

        // trivial budget split: invest 10 in each agent
        System.out.println();
        System.out.println("=========== SEPARATE PLANNING ===========");

        double expectedReward = 0.0;
        double expectedCost = 0.0;
        for (int i = 0; i < 2; i++) {
            CMDP cmdp = (i == 0) ? cmdpChild : cmdpAdult;
            Solution sol = alg.solve(new CMDP[]{cmdp}, 99999.0); // TODO replace the number with the correct limit
            double expectedReward0 = sol.getExpectedReward();
            double expectedCost0 = sol.getExpectedCost();
            System.out.println("Expected reward agent " + i + ": " + expectedReward0);
            System.out.println("Expected cost agent " + i + ": " + expectedCost0);
            expectedReward += expectedReward0;
            expectedCost += expectedCost0;
        }
        System.out.println("Expected reward: " + expectedReward);
        System.out.println("Expected cost: " + expectedCost);

        // multi-agent problem: invest 20 in total
        Solution combinedSolution = alg.solve(new CMDP[]{cmdpChild, cmdpAdult}, 99999.0); // TODO replace the number with the correct limit
        System.out.println();
        System.out.println("=========== MULTI-AGENT PLANNING ===========");
        System.out.println("Expected reward: " + combinedSolution.getExpectedReward());
        System.out.println("Expected reward agent 0: " + combinedSolution.getExpectedReward(0));
        System.out.println("Expected reward agent 1: " + combinedSolution.getExpectedReward(1));
        System.out.println("Expected cost total: " + combinedSolution.getExpectedCost());
        System.out.println("Expected cost agent 0: " + combinedSolution.getExpectedCost(0));
        System.out.println("Expected cost agent 1: " + combinedSolution.getExpectedCost(1));

        // simulate
        sim.simulate(new CMDP[]{cmdpChild, cmdpAdult}, combinedSolution, 10000);
    }

    public static void main(String[] args) {
        double[] discount_factors = {0., 0.02040816, 0.04081633, 0.06122449, 0.08163265,
                0.10204082, 0.12244898, 0.14285714, 0.16326531, 0.18367347,
                0.20408163, 0.2244898, 0.24489796, 0.26530612, 0.28571429,
                0.30612245, 0.32653061, 0.34693878, 0.36734694, 0.3877551,
                0.40816327, 0.42857143, 0.44897959, 0.46938776, 0.48979592,
                0.51020408, 0.53061224, 0.55102041, 0.57142857, 0.59183673,
                0.6122449, 0.63265306, 0.65306122, 0.67346939, 0.69387755,
                0.71428571, 0.73469388, 0.75510204, 0.7755102, 0.79591837,
                0.81632653, 0.83673469, 0.85714286, 0.87755102, 0.89795918,
                0.91836735, 0.93877551, 0.95918367, 0.97959184, 1.};
        HashMap<Double, Double> df_exp_map = new HashMap<>();

        for (double df :
                discount_factors) {
            Solution tmp = task1(df);
            df_exp_map.putIfAbsent(df, tmp.getExpectedReward());
        }

        File of = new File("df_exp_map.txt");
        BufferedWriter bf = null;
        try {
            bf = new BufferedWriter(new FileWriter(of));
            bf.write("[");
            bf.newLine();
            for (Map.Entry<Double, Double> entry :
                    df_exp_map.entrySet()) {
                bf.write("(" + entry.getKey() + ", " + entry.getValue() + "), ");
            }
            bf.write("]");
            bf.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        task1(.95);
    }
}
