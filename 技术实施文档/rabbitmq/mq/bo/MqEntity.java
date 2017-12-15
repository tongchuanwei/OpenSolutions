package way.service.core.mq.bo;

import java.io.Serializable;
import java.util.Date;

public class MqEntity<T> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6309673004118164786L;
	/**执行次数*/
	private int count = 0;
	/**执行对象*/
	private T object;
	/**创建时间*/
	private Date date = new Date();
	/**类型 */
	private String type;
	/**队列id*/
	private Long mqId;
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getMqId() {
		return mqId;
	}

	public void setMqId(Long mqId) {
		this.mqId = mqId;
	}
	
	
}
