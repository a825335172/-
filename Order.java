/**
 * 订单
 */
public class Order {
    /**
     * 订单号
     */
    public String orderId;
    /**
     * 各类产品的数量
     */
    public int[] remainProductNum = new int[Environment.PRODUCT_CATEGORY_NUM];
    /**
     * 订单价格
     */
    public int price;
    /**
     * 逾期天数
     */
    public int overdueDay;

    public Order(String orderId, int[] productsNum){
        this.orderId = orderId;
        System.arraycopy(productsNum, 0, this.remainProductNum, 0, Environment.PRODUCT_CATEGORY_NUM);
        price = 0;
        for(int i = 0; i < Environment.PRODUCT_CATEGORY_NUM; i++){
            price += productsNum[i] * Environment.PROJECT_PRICE[i];
        }
        overdueDay = 0;
    }
}
