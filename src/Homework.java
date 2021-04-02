import java.util.ArrayList;
import java.util.HashMap;
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
		for(int s=0; s<cmdp.getNumStates(); s++) {
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
	public static void task1() {		
		// Get CMDP model for 1 agent
		CMDP cmdp = UserGenerator.getCMDPChild();
		CMDP[] cmdps = new CMDP[]{cmdp};
		
		// Solve the problem without constraints
		PlanningAlgorithm alg = new PlanningAlgorithm();		
		Solution solution = alg.solveVI(cmdps, 0.95);
		System.out.println("Expected reward: "+solution.getExpectedReward());
		System.out.println("Expected cost: "+solution.getExpectedCost());
		
		// Simulate solution
		System.out.println();
		Simulator sim = new Simulator(rnd);
		sim.simulate(cmdps, solution, 1000);
		
		// Print policy of agent 0
		int agentID = 0;
		double[][] policy = solution.getPolicy(agentID);
		System.out.println();
		for(int s=0; s<cmdps[agentID].getNumStates(); s++) {
			System.out.print("State "+s+": ");
			for(int a=0; a<cmdps[agentID].getNumActions(); a++) {
				System.out.print(policy[s][a]+" ");
			}
			System.out.println();
		}
		
	}
	
	// Solve unconstrained problem for 1 agent with cost
	public static void task2() {		
		// Get CMDP model for 1 agent
		CMDP cmdp = UserGenerator.getCMDPChild();
		CMDP[] cmdps = new CMDP[]{cmdp};
		
		// Assign cost
		for(int s=0; s<cmdp.getNumStates(); s++) {
			for(int a=0; a<cmdp.getNumActions(); a++) {
				cmdp.assignCost(s, a, 2*a);
			}
		}
//		cmdp.assignCost(0, 0, 0); // TODO add costs to the state action pairs
//		System.out.println("Expected reward: "+ cmdp.getNumStates());
		// Solve the problem without constraints
		PlanningAlgorithm alg = new PlanningAlgorithm();
		Solution solution = alg.solveUnconstrained(cmdps);
		System.out.println("Expected reward: "+solution.getExpectedReward());
		System.out.println("Expected cost: "+solution.getExpectedCost());
		
		// Simulate solution
		System.out.println();
		Simulator sim = new Simulator(rnd);
		sim.simulate(cmdps, solution, 1000);
		
		// Print policy of agent 0
		int agentID = 0;
		double[][] policy = solution.getPolicy(agentID);
		System.out.println();
		for(int s=0; s<cmdps[agentID].getNumStates(); s++) {
			System.out.print("State "+s+": ");
			for(int a=0; a<cmdps[agentID].getNumActions(); a++) {
				System.out.print(policy[s][a]+" ");
			}
			System.out.println();
		}
		
	}
	
	// Solve constrained problem for 1 agent
	public static void task3() {		
		// Get CMDP model for 1 agent
		CMDP cmdp = UserGenerator.getCMDPChild();
		CMDP[] cmdps = new CMDP[]{cmdp};

        for (int s = 0; s < cmdp.getNumStates(); s++) {
            for (int a = 0; a < cmdp.getNumActions(); a++) {
                cmdp.assignCost(s, a, 2 * a);
            }
        }

        PlanningAlgorithm alg = new PlanningAlgorithm();


        for (int s = 0; s < 50; s++) {
            Solution solution = alg.solve(cmdps, s);
            double expectedReward = solution.getExpectedReward();
            System.out.println("Expected reward budget " + s + ": " + expectedReward);
        }
    }

    // Solve constrained problem for 2 agents with trivial budget split
    public static void task4() {
        // Get CMDP models
        CMDP cmdpChild = UserGenerator.getCMDPChild();
        CMDP cmdpAdult = UserGenerator.getCMDPAdult();

        // Assign cost to child
        for (int s = 0; s < cmdpChild.getNumStates(); s++) {
            for (int a = 0; a < cmdpChild.getNumActions(); a++) {
                cmdpChild.assignCost(s, a, 2 * a);
            }
        }

        // Assign cost to adult
        for (int s = 0; s < cmdpAdult.getNumStates(); s++) {
            for (int a = 0; a < cmdpAdult.getNumActions(); a++) {
                cmdpAdult.assignCost(s, a, 2 * a);
            }
        }

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
            Solution sol = alg.solve(new CMDP[]{cmdp}, 10);
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
        Solution combinedSolution = alg.solve(new CMDP[]{cmdpChild, cmdpAdult}, 20);
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

    public static double[] task5(double ratio, int users) {

        CMDP[] list = new CMDP[users];

        int c = (int) Math.floor(users * ratio/ (1 +ratio));
//        System.out.println("###### HOW MANY CHILDREN YOU GOT BRO: " + c);
        for (int i = 0; i < users ; i++) {
            if (i < c) {
                list[i] = UserGenerator.getCMDPChild();
            }
            else {
                list[i] = UserGenerator.getCMDPAdult();
            }
        }

        for (CMDP tmp: list){
            for (int s = 0; s < tmp.getNumStates(); s++) {
                for (int a = 0; a < tmp.getNumActions(); a++) {
                    tmp.assignCost(s, a, 2 * a);
                }
            }
        }



        PlanningAlgorithm alg = new PlanningAlgorithm();
        Simulator sim = new Simulator(rnd);


//        System.out.println("###### HOW MANY CHILDREN YOU got extra: " + counter);


        double start_time = System.currentTimeMillis();
        Solution combinedSolution = alg.solve(list, users * 10);
        double end_time = System.currentTimeMillis();
        System.out.println("Expected cost total: " + combinedSolution.getExpectedCost());
        // simulate
        sim.simulate(list, combinedSolution, 10000);

        return new double[]{ratio, end_time - start_time};


    }

    public static void main(String[] args) {
//
//        int[] array = new int[]{10, 20, 30, 40, 50};
//        double[] ratios = {0.125, 0.25, 0.5, 1, 2, 4, 8};

//        task5(0.5, 10);
//        task5(0.5, 10);
//        task5(0.5, 10);
//        task5(0.5, 10);
//        for (int c : array) {
//            for (double i : ratios) {
//                double[] r_time = task5(i,c);
//                System.out.print("(");
//                System.out.print(r_time[0]+", ");
//                System.out.print(r_time[1]);
//                System.out.print("), ");
//            }
//        }

        task4();
    }
}
