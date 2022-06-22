import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 生产调度问题求解
 */
public class Schedule {
    /**
     * 输入订单
     */
    private void inputOrder() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("输入订单:");
        while (true) {
            String input = "";
            try {
                input = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (input.equals("over")) break;
            String[] inputSplit = input.split("\\s");
            int[] productsNum = new int[Environment.PRODUCT_CATEGORY_NUM];
            for (int i = 0; i < Environment.PRODUCT_CATEGORY_NUM; i++) {
                productsNum[i] = Integer.parseInt(inputSplit[i + 1]);
            }
            Environment.allOrder.add(new Order(inputSplit[0], productsNum));
        }
    }

    /**
     * 初始化全局变量
     */
    private void init() {
        Environment.totalProductNum = 0;
        for (Order order : Environment.allOrder) {
            for (int remain : order.remainProductNum) {
                Environment.totalProductNum += remain;
            }
        }
    }

    public void start() {
        while (true) {
            inputOrder();
            init();
            new Genetic().run();
        }
    }

    public static void main(String[] args) {
        new Schedule().start();
    }
}
