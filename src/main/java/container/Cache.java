package container;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*这里仅仅使用内存简单模拟一下而已*/
public class Cache {
    // productId->currentNum
    private static Map<Integer, Integer> productInventoryMap = new ConcurrentHashMap<Integer, Integer>();

    public static Map<Integer, Integer> getProductInventoryMap() {
        return productInventoryMap;
    }
}
