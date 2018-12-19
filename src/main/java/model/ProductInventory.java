package model;

public class ProductInventory {
    private int productId;
    private int currentNum;

    public int getProductId() { return productId; }

    public int getCurrentNum() {
        return currentNum;
    }

    public ProductInventory(int productId, int currentNum) {
        this.productId = productId;
        this.currentNum = currentNum;
    }
}
