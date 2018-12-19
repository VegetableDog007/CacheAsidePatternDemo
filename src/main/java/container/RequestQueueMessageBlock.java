package container;

import org.apache.log4j.Logger;
import request.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class RequestQueueMessageBlock {
    /*
     * 注意这个RequestQueueMessageBlock是需要被多个线程并发访问的
     * 所以，这里选用的集合类型需要选用线程安全的
     * */
    // 存放的内存队列列表
    private List<ArrayBlockingQueue<Request>> queues;
    // 标识表，用于读去重
    private Map<Integer, Boolean> flagMap = new ConcurrentHashMap<Integer, Boolean>();
    private static Logger logger = Logger.getLogger(RequestQueueMessageBlock.class);

    private RequestQueueMessageBlock(){
        queues = new ArrayList<ArrayBlockingQueue<Request>>();
        for(int i=0; i<8; i++){
            ArrayBlockingQueue<Request> queue = new ArrayBlockingQueue<Request>(100);
            queues.add(queue);
        }
    }

    public static RequestQueueMessageBlock getInstance(){
        return Singleton.getInstance();
    }

    private static class Singleton{
        private static RequestQueueMessageBlock instance;
        static{
            instance = new RequestQueueMessageBlock();
        }
        public static RequestQueueMessageBlock getInstance(){
            return instance;
        }
    }

    public List<ArrayBlockingQueue<Request>> getQueues() {
        return queues;
    }

    public Map<Integer, Boolean> getFlagMap() {
        return flagMap;
    }

    public void addRequest(Request request){
        try{
            int hashIdx = hash(request);
            queues.get(hashIdx).put(request);
            logger.debug("商品id："+request.getProductId()+"；"+"目标内存队列id："+hashIdx);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int hash(Request request){
        String key = String.valueOf(request.getProductId());
        int h;
        if(key==null){
            h=0;
        }
        else{
            h = key.hashCode();
            h = h^(h>>>16);
        }
        int hashIdx = (queues.size()-1)&h;
        return hashIdx;
    }
}
