package way.service.core.mq.publics;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import way.service.core.mq.bo.MqEntity;
import way.service.core.utils.LoggerUtils;
import way.service.core.utils.SerializeUtil;
import way.service.core.utils.SystemLogUitls;
import way.web.common.mq.MQExecute;

import java.io.IOException;

/**
 * 读取队列的程序端，实现了Runnable接口。
 * 
 * @author tongchuanwei
 * 
 */
public class QueueConsumer extends EndPoint implements Runnable, Consumer {

	public QueueConsumer(String queueName) {
		super(queueName);
	}

	public void run() {
			try {
				channel.basicConsume(queueName, true, this);
				//channel.basicQos(1);
				//channel.basicConsume(queueName, false, this);
			} catch (IOException e) {
				e.printStackTrace();
				LoggerUtils.error(getClass(), queueName + ":执行异常！",e);
			}
	}

	/**
	 * Called when consumer is registered.
	 */
	public void     handleConsumeOk(String consumerTag) {

		SystemLogUitls.debug(getClass(), "Consumer " + consumerTag + " registered");
	}

	/**
	 * Called when new message is available.
	 */
	@SuppressWarnings("unchecked")
	public void handleDelivery(String consumerTag, Envelope env,
			BasicProperties props, byte[] body) throws IOException {
		MqEntity<?> entity = SerializeUtil.deserialize(body,MqEntity.class);
		MQExecute.excMqManager(entity,queueName);
		//channel.basicAck(env.getDeliveryTag(), false);
	}

	public void handleCancel(String consumerTag) {
		
		SystemLogUitls.debug(getClass(), "Consumer " + consumerTag + "handleCancel");
	}

	public void handleCancelOk(String consumerTag) {
		
		SystemLogUitls.debug(getClass(), "Consumer " + consumerTag + "handleCancelOk");
	}

	public void handleRecoverOk(String consumerTag) {
		
		SystemLogUitls.debug(getClass(), "Consumer " + consumerTag + "handleRecoverOk");
	}

	public void handleShutdownSignal(String consumerTag, ShutdownSignalException arg1) {
		
		SystemLogUitls.debug(getClass(), "Consumer " + consumerTag + "handleShutdownSignal");
		//MQExecute.recoverconsumer(queueName);
	}

	
	
}
