package way.service.core.mq.publics;

import way.service.core.mq.bo.MqEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * MQ公共入口,调用时生产者和消费者一起处理
 * 
 * @author tongchuanwei
 * 
 * 
 */
public class MqPublicInfo {

	// 1：指定队列名称生产和消费消息
	public static void processMsg(Object obj, String queueName) {

		// 生产者
		Producer producer = new Producer(queueName, obj);

		// 消费者
		QueueConsumer consumer = new QueueConsumer(queueName);

		// 线程池方式
		ExecutorService service = Executors.newCachedThreadPool();

		service.submit(consumer);
		service.submit(producer);
	}

	// 2：指定队列名称生产消息
	public static void producerMsg(Object obj, String queueName) {

		// 生产者
		Producer producer = new Producer(queueName, obj);
		// 线程池方式
		ExecutorService service = Executors.newCachedThreadPool();
		service.submit(producer);
	}

	// 3：指定队列名称消费消息
	public static void consumerMsg(String queueName) {

		// 消费者
		QueueConsumer consumer = new QueueConsumer(queueName);
		// 线程池方式
		ExecutorService service = Executors.newFixedThreadPool(10);
		service.submit(consumer);
	}
	
	// 4：指定路由key发布消息
	public static void publishMsg(Object obj,String routingKey) {

		// 生产者
		Producer producer = new Producer(obj,routingKey);
		// 线程池方式
		ExecutorService service = Executors.newCachedThreadPool();
		service.submit(producer);
	}
	
	
	public static void main(String args[]) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", "zhangsan");
		MqEntity<Map<String, Object>> entity = new MqEntity<Map<String, Object>>();
		entity.setObject(map);
		MqPublicInfo.publishMsg(entity, "member.user.update");
	}
}
