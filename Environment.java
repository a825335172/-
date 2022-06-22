import java.util.ArrayList;
import java.util.List;

/**
 * 全局变量
 */
public class Environment {
    /**
     * 种群规模
     */
    public static final int POPULATION_SIZE = 500;
    /**
     * 交叉概率
     */
    public static final double CROSSOVER_PROBABILITY = 0.4;
    /**
     * 变异概率
     */
    public static final double MUTATION_PROBABILITY = 0.04;
    /**
     * 最大迭代数
     */
    public static final int GENERATION_NUM = 200;

    /**
     * 产品种类数
     */
    public static final int PRODUCT_CATEGORY_NUM = 3;
    /**
     * 产品价格
     */
    public static final int[] PROJECT_PRICE = {20, 30, 50};
    /**
     * 产品加工时间，单位为分钟
     */
    public static final int[] PROJECT_PROCESS_TIME = {20, 30, 50};
    /**
     * 产品线数量
     */
    public static final int PRODUCTION_LINE_NUM = 3;
    /**
     * 一天内时间段数
     */
    public static final int PERIOD_NUM = 6;
    /**
     * 每段时间工人工资
     */
    public static final int[] PERIOD_SALARY = {10, 10, 10, 20, 30, 40};

    /**
     * 所有订单
     */
    public static final List<Order> allOrder = new ArrayList<>();
    /**
     * 所有产品数量
     */
    public static int totalProductNum;
}
