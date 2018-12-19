package thread.pool;

import container.RequestQueueMessageBlock;
import request.Request;
import sun.misc.RequestProcessor;
import thread.processor.ProductInventoryRequestProcessor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductInventoryRequestProcessorPool {

    // 线程池
    private ExecutorService pool = Executors.newFixedThreadPool(10);

    private ProductInventoryRequestProcessorPool(){
        RequestQueueMessageBlock requestQueueMessageBlock = RequestQueueMessageBlock.getInstance();
        for(int i=0; i<requestQueueMessageBlock.getQueues().size(); i++){
            pool.submit(new ProductInventoryRequestProcessor(requestQueueMessageBlock.getQueues().get(i)));
        }
    }

    // 单例类
    private static class Singleton{
        private static ProductInventoryRequestProcessorPool instance;
        static{
            instance = new ProductInventoryRequestProcessorPool();
        }
        public static ProductInventoryRequestProcessorPool getInstance(){
            return instance;
        }
    }

    public static ProductInventoryRequestProcessorPool getInstance(){
        return Singleton.getInstance();
    }

}
