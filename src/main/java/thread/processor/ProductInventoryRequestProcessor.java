package thread.processor;

import org.apache.log4j.Logger;
import request.Request;

import java.util.concurrent.ArrayBlockingQueue;

public class ProductInventoryRequestProcessor implements Runnable {

    // 库存请求处理线程需要监督的自带的一个内存队列
    private ArrayBlockingQueue<Request> queue;
    private Logger logger = Logger.getLogger(ProductInventoryRequestProcessor.class);

    public ProductInventoryRequestProcessor(ArrayBlockingQueue<Request> queue) {
        this.queue = queue;
    }

    public void run() {
        try {
            // 不断地从内存队列中取出请求并处理
            while(true){
                Request request = queue.take();
                logger.debug("请求被工作线程从队列中取出并开始处理, 商品id："+request.getProductId());
                request.process();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
