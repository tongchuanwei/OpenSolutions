package way.service.core.mq.publics;

import com.rabbitmq.client.MessageProperties;
import way.service.core.utils.LoggerUtils;
import way.service.core.utils.SerializeUtil;
import way.service.core.utils.StringUtils;

import java.io.IOException;

/**
 * The producer endpoint that writes to the queue.
 * 
 * @author tongchuanwei
 * 
 */
public class Producer extends EndPoint implements Runnable {

	protected Object obj;
	protected String routingKey;

	public Producer(String queueName, Object obj) {

		super(queueName);
		this.obj = obj;
	}
	
	public Producer(Object obj,String routingKey) {

		super("");
		this.routingKey = routingKey;
		this.obj = obj;
	}

	// 生产者发送消息
	public void sendMessage(Object object) throws IOException {
		if (StringUtils.isNotBlank(routingKey)) {
			channel.exchangeDeclare(exchange, exchangeType);
			channel.basicPublish(exchange, routingKey, MessageProperties.PERSISTENT_BASIC, SerializeUtil.serialize(object));
		} 
		else {
			channel.basicPublish("", queueName, MessageProperties.PERSISTENT_BASIC, SerializeUtil.serialize(object));
		}
		LoggerUtils.debug(getClass(), "send.................... '" + object + "'");
	}

	@Override
	public void run() {
		try {
			sendMessage(obj);
		} catch (IOException e) {
			LoggerUtils.error(getClass(), "向生产者发送消息抛出异常",e);
		}
	}
}