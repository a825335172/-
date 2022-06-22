import java.util.Random;

/**
 * 遗传算法
 */
public class Genetic {
    /**
     * 种群
     */
    private Individual[] population = new Individual[Environment.POPULATION_SIZE];
    private final Random random = new Random();

    /**
     * 初始化
     */
    private void init() {
        for (int i = 0; i < Environment.POPULATION_SIZE; i++) {
            population[i] = Individual.generateIndividual();
        }
    }

    /**
     * 选择
     */
    private void select() {
        // 生成轮盘
        double[] roulette = new double[Environment.POPULATION_SIZE];
        double rouletteSum = 0;
        for (int i = 0; i < Environment.POPULATION_SIZE; i++) {
            rouletteSum += population[i].calculateFitness();
            roulette[i] = rouletteSum;
        }
        for (int i = 0; i < Environment.POPULATION_SIZE; i++) {
            roulette[i] /= rouletteSum;
        }

        // 使用轮盘选择
        Individual[] parents = new Individual[Environment.POPULATION_SIZE];
        double r;
        int indicator = 0;
        for (int i = 0; i < Environment.POPULATION_SIZE; i++) {
            r = random.nextDouble();
            for (int j = 0; j < Environment.POPULATION_SIZE; j++) {
                if (r < roulette[j]) {
                    indicator = j;
                    break;
                }
            }
            parents[i] = population[indicator].copy();
        }
        population = parents;
    }

    /**
     * 交叉
     */
    private void crossover() {
        for (int i = 0; i < Environment.POPULATION_SIZE - 1; i += 2) {
            if (random.nextDouble() > Environment.CROSSOVER_PROBABILITY) continue;
            population[i].crossover(population[i + 1]);
        }
    }

    /**
     * 变异
     */
    private void mutation() {
        for (int i = 1; i < Environment.POPULATION_SIZE; i++) {
            if (random.nextDouble() > Environment.MUTATION_PROBABILITY) continue;
            population[i].mutation();
        }
    }

    /**
     * 获取最优解
     * @return 最优个体
     */
    private Individual getBestSolution() {
        Individual bestIndividual = null;
        double bestFitness = 0;
        for (Individual individual : population) {
            double fitness = individual.calculateFitness();
            if (fitness > bestFitness) {
                bestFitness = fitness;
                bestIndividual = individual;
            }
        }
        return bestIndividual;
    }

    public void run() {
        init();
        Individual bestIndividual = null;
        for (int i = 0; i < Environment.GENERATION_NUM; i++) {
            System.out.println("Generation  " + (i + 1));
            select();
            crossover();
            mutation();
            bestIndividual = getBestSolution();
            bestIndividual.show();
        }
        bestIndividual.process();
    }

}
