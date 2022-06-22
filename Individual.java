import java.util.*;

/**
 * 个体
 */
public class Individual {
    /**
     * 加工产品的时间段数
     */
    private String periodNum;
    /**
     * 每个时间段加工的产品类别
     */
    public int[] periodCategory;
    /**
     * 所有产品加工的顺序
     */
    public int[] sequence;

    private Individual() {

    }

    /**
     * 随机生成一个个体
     *
     * @return 个体
     */
    public static Individual generateIndividual() {
        Random random = new Random();
        Individual individual = new Individual();

        int max = Environment.PRODUCTION_LINE_NUM * Environment.PERIOD_NUM;
        int len = (int) (Math.log(max) / Math.log(2)) + 1;
        individual.periodNum = "";
        for (int i = 0; i < len; i++) {
            individual.periodNum += random.nextInt(2);
        }
        if (Integer.valueOf(individual.periodNum, 2) > max) {
            individual.periodNum = Integer.toBinaryString(max);
        }

        individual.periodCategory = new int[max];
        for (int i = 0; i < max; i++) {
            individual.periodCategory[i] = random.nextInt(Environment.PRODUCT_CATEGORY_NUM);
        }

        individual.sequence = new int[Environment.totalProductNum];
        int index = 0;
        for (int i = 0; i < Environment.allOrder.size(); i++) {
            Order order = Environment.allOrder.get(i);
            for (int j = 0; j < Environment.PRODUCT_CATEGORY_NUM; j++) {
                for (int k = 0; k < order.remainProductNum[j]; k++) {
                    individual.sequence[index] = i * Environment.PRODUCT_CATEGORY_NUM + j;
                    index++;
                }
            }
        }

        int temp;
        int r;
        for (int i = 0; i < Environment.totalProductNum; i++) {
            r = random.nextInt(Environment.totalProductNum);
            temp = individual.sequence[i];
            individual.sequence[i] = individual.sequence[r];
            individual.sequence[r] = temp;
        }

        for(int j=0;j<individual.sequence.length;j++)
        {
            System.out.print(individual.sequence[j]);
        }
        System.out.println("");
        return individual;
    }

    /**
     * 计算成本
     *
     * @return 成本
     */
    private double calculateCost() {
        int salaryCost = 0;
        int period = Integer.valueOf(periodNum, 2);
        for (int i = 1; i <= period; i++) {
            salaryCost += Environment.PERIOD_SALARY[(i - 1) / Environment.PRODUCT_CATEGORY_NUM];
        }

        // 某类产品加工的时间段
        int[] categoryPeriod = new int[Environment.PRODUCT_CATEGORY_NUM];
        Arrays.fill(categoryPeriod, -1);
        // 某类产品的容量
        int[] categoryCapacity = new int[Environment.PRODUCT_CATEGORY_NUM];
        // 未完成订单
        Set<Order> unfinishedOrders = new HashSet<>();
        for (int pro : sequence) {
            if (categoryCapacity[pro % Environment.PRODUCT_CATEGORY_NUM] == 0) {
                // 没有容量了，需要再分配一些
                categoryPeriod[pro % Environment.PRODUCT_CATEGORY_NUM]++;
                while (categoryPeriod[pro % Environment.PRODUCT_CATEGORY_NUM] < period) {
                    if (periodCategory[categoryPeriod[pro % Environment.PRODUCT_CATEGORY_NUM]] == pro % Environment.PRODUCT_CATEGORY_NUM) {
                        categoryCapacity[pro % Environment.PRODUCT_CATEGORY_NUM] = 240 / Environment.PROJECT_PROCESS_TIME[pro % Environment.PRODUCT_CATEGORY_NUM];
                        break;
                    }
                    categoryPeriod[pro % Environment.PRODUCT_CATEGORY_NUM]++;
                }
            }
            if (categoryCapacity[pro % Environment.PRODUCT_CATEGORY_NUM] == 0) {
                // 没有分配到容量，订单未完成
                unfinishedOrders.add(Environment.allOrder.get(pro / Environment.PRODUCT_CATEGORY_NUM));
            } else {
                categoryCapacity[pro % Environment.PRODUCT_CATEGORY_NUM]--;
            }

        }
        int overDueCost = 0;
        for (Order order : unfinishedOrders) {
            overDueCost += order.price * (order.overdueDay + 1) * 1;
        }
        return salaryCost + overDueCost;

    }

    /**
     * 计算适应度
     *
     * @return 适应度
     */
    public double calculateFitness() {
        return 1000.0 / calculateCost();
    }

    /**
     * 交叉
     *
     * @param individual 与之交叉的个体
     */
    public void crossover(Individual individual) {
        Individual individual1 = this.copy();
        Individual individual2 = individual.copy();
        Random random = new Random();
        int r;

        // 交叉periodNum
        r = random.nextInt(this.periodNum.length());
        this.periodNum = individual1.periodNum.substring(0, r) + individual2.periodNum.substring(r);
        individual.periodNum = individual2.periodNum.substring(0, r) + individual1.periodNum.substring(r);

        int max = Environment.PRODUCTION_LINE_NUM * Environment.PERIOD_NUM;
        if (Integer.valueOf(this.periodNum, 2) > max) {
            this.periodNum = Integer.toBinaryString(max);
        }
        if (Integer.valueOf(individual.periodNum, 2) > max) {
            individual.periodNum = Integer.toBinaryString(max);
        }

        // 交叉periodCategory
        r = random.nextInt(this.periodCategory.length);
        System.arraycopy(individual2.periodCategory, r, this.periodCategory, r, this.periodCategory.length - r);
        System.arraycopy(individual1.periodCategory, r, individual.periodCategory, r, this.periodCategory.length - r);

        // 交叉sequence
        int len = Environment.allOrder.size() * Environment.PRODUCT_CATEGORY_NUM;
        int[] map1 = new int[len];
        int[] map2 = new int[len];
        for (int i = 0; i < len; i++) {
            map1[i] = map2[i] = Environment.allOrder.get(i / Environment.PRODUCT_CATEGORY_NUM).remainProductNum[i % Environment.PRODUCT_CATEGORY_NUM];
        }
        r = random.nextInt(Environment.totalProductNum);
        int point1 = r;
        int point2 = r;
        for (int i = 0; i < r; i++) {
            map1[individual1.sequence[i]]--;
            map2[individual2.sequence[i]]--;
        }
        for (int i = 0; i < Environment.totalProductNum; i++) {
            if (map1[individual2.sequence[i]] != 0) {
                this.sequence[point1] = individual2.sequence[i];
                map1[individual2.sequence[i]]--;
                point1++;
            }
            if (map2[individual1.sequence[i]] != 0) {
                individual.sequence[point2] = individual1.sequence[i];
                map2[individual1.sequence[i]]--;
                point2++;
            }
        }
    }

    /**
     * 变异
     */
    public void mutation() {
        Random random = new Random();
        int r1, r2;

        // 变异periodNum
        r1 = random.nextInt(periodNum.length());
        periodNum = periodNum.substring(0, r1) + ('1' - periodNum.charAt(r1)) + periodNum.substring(r1 + 1);
        int max = Environment.PRODUCTION_LINE_NUM * Environment.PERIOD_NUM;
        if (Integer.valueOf(periodNum, 2) > max) {
            periodNum = Integer.toBinaryString(max);
        }

        // 变异periodCategory
        r1 = random.nextInt(this.periodCategory.length);
        r2 = random.nextInt(Environment.PRODUCT_CATEGORY_NUM);
        periodCategory[r1] = r2;

        // 变异sequence
        r1 = random.nextInt(Environment.totalProductNum);
        r2 = random.nextInt(Environment.totalProductNum);
        int temp = sequence[r1];
        sequence[r1] = sequence[r2];
        sequence[r2] = temp;
    }

    /**
     * 复制
     *
     * @return 复制的个体
     */
    public Individual copy() {
        Individual individual = new Individual();

        individual.periodNum = this.periodNum;

        individual.periodCategory = new int[this.periodCategory.length];
        System.arraycopy(this.periodCategory, 0, individual.periodCategory, 0, this.periodCategory.length);

        individual.sequence = new int[Environment.totalProductNum];
        System.arraycopy(this.sequence, 0, individual.sequence, 0, Environment.totalProductNum);

        return individual;
    }

    /**
     * 个体展示
     */
    public void show() {
        int period = Integer.valueOf(periodNum, 2);
        // 某类产品加工的时间段
        int[] categoryPeriod = new int[Environment.PRODUCT_CATEGORY_NUM];
        Arrays.fill(categoryPeriod, -1);
        // 某类产品的容量
        int[] categoryCapacity = new int[Environment.PRODUCT_CATEGORY_NUM];
        // 某个时间段加工的各个订单的产品数量
        int[][] periodProductNum = new int[period][Environment.allOrder.size()];

        for (int pro : sequence) {
            if (categoryCapacity[pro % Environment.PRODUCT_CATEGORY_NUM] == 0) {
                // 没有容量了，需要再分配一些
                categoryPeriod[pro % Environment.PRODUCT_CATEGORY_NUM]++;
                while (categoryPeriod[pro % Environment.PRODUCT_CATEGORY_NUM] < period) {
                    if (periodCategory[categoryPeriod[pro % Environment.PRODUCT_CATEGORY_NUM]] == pro % Environment.PRODUCT_CATEGORY_NUM) {
                        categoryCapacity[pro % Environment.PRODUCT_CATEGORY_NUM] = 240 / Environment.PROJECT_PROCESS_TIME[pro % Environment.PRODUCT_CATEGORY_NUM];
                        break;
                    }
                    categoryPeriod[pro % Environment.PRODUCT_CATEGORY_NUM]++;
                }
            }
            if (categoryCapacity[pro % Environment.PRODUCT_CATEGORY_NUM] != 0) {
                // 如果分配到容量
                categoryCapacity[pro % Environment.PRODUCT_CATEGORY_NUM]--;
                periodProductNum[categoryPeriod[pro % Environment.PRODUCT_CATEGORY_NUM]][pro / Environment.PRODUCT_CATEGORY_NUM]++;
            }
        }

        // 打印结果
        for (int i = 0; i < periodProductNum.length; i++) {
            System.out.format("第 %d 条生产线, 第 %d 时间段, 生产第 %d 类产品: ", i % Environment.PRODUCTION_LINE_NUM + 1, i / Environment.PRODUCTION_LINE_NUM + 1, periodCategory[i] + 1);
            StringJoiner joiner = new StringJoiner(", ", "{ ", " }");
            for (int j = 0; j < Environment.allOrder.size(); j++) {
                if (periodProductNum[i][j] != 0)
                    joiner.add(Environment.allOrder.get(j).orderId + ":" + periodProductNum[i][j]);
            }
            System.out.println(joiner);
        }

    }

    /**
     * 加工产品
     */
    public void process() {
        int period = Integer.valueOf(periodNum, 2);
        // 某类产品加工的时间段
        int[] categoryPeriod = new int[Environment.PRODUCT_CATEGORY_NUM];
        Arrays.fill(categoryPeriod, -1);
        // 某类产品的容量
        int[] categoryCapacity = new int[Environment.PRODUCT_CATEGORY_NUM];
        for (int pro : sequence) {
            if (categoryCapacity[pro % Environment.PRODUCT_CATEGORY_NUM] == 0) {
                // 没有容量了，需要再分配一些
                categoryPeriod[pro % Environment.PRODUCT_CATEGORY_NUM]++;
                while (categoryPeriod[pro % Environment.PRODUCT_CATEGORY_NUM] < period) {
                    if (periodCategory[categoryPeriod[pro % Environment.PRODUCT_CATEGORY_NUM]] == pro % Environment.PRODUCT_CATEGORY_NUM) {
                        categoryCapacity[pro % Environment.PRODUCT_CATEGORY_NUM] = 240 / Environment.PROJECT_PROCESS_TIME[pro % Environment.PRODUCT_CATEGORY_NUM];
                        break;
                    }
                    categoryPeriod[pro % Environment.PRODUCT_CATEGORY_NUM]++;
                }
            }
            if (categoryCapacity[pro % Environment.PRODUCT_CATEGORY_NUM] != 0) {
                categoryCapacity[pro % Environment.PRODUCT_CATEGORY_NUM]--;
                Environment.allOrder.get(pro / Environment.PRODUCT_CATEGORY_NUM).remainProductNum[pro % Environment.PRODUCT_CATEGORY_NUM]--;
            }

        }
        for (int i = Environment.allOrder.size() - 1; i >= 0; i--) {
            boolean finish = true;
            for(int remain : Environment.allOrder.get(i).remainProductNum){
                if(remain != 0){
                    finish = false;
                    break;
                }
            }
            if(finish){
                Environment.allOrder.remove(i);
            }
        }
    }
}
