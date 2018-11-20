package com.asciily.foundation.mq.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.connection.DefaultMessage;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * @author kakashi
 * @since 2018年10月22日
 */
public class PrdCsmRedisMessageListenerContainer implements InitializingBean, DisposableBean {

	private static final Logger logger = LoggerFactory.getLogger(PrdCsmRedisMessageListenerContainer.class);

	private final List<ConsumeTask> tasks = new ArrayList<ConsumeTask>();

	private final Map<Topic, Collection<MessageListener>> messageListeners = new ConcurrentHashMap<Topic, Collection<MessageListener>>();

	private RedisSerializer<String> keySerializer = new StringRedisSerializer();

	public void setSerializer(RedisSerializer<String> serializer) {
		this.keySerializer = serializer;
	}

	private RedisConnectionFactory connectionFactory;

	public void setConnectionFactory(RedisConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	private Executor subscriptionExecutor;

	private class ConsumeTask implements Runnable {

		private volatile Topic topic;

		private volatile RedisConnection connection;

		public ConsumeTask(Topic topic) {
			super();
			this.topic = topic;
		}

		@Override
		public void run() {
			try {
				connection = connectionFactory.getConnection();
				byte[] channel = keySerializer.serialize(topic.getTopic());
				if (!Thread.currentThread().interrupted()) {
					while (true) {
						List<byte[]> messages = connection.bRPop(0, channel);
						Message message = new DefaultMessage(channel, messages.get(1));
						for (MessageListener listener : messageListeners.get(topic)) {
							listener.onMessage(message, channel);
						}
					}
				}
			} catch (Throwable t) {
				if (logger.isTraceEnabled()) {
					logger.trace("interrupting Redis consuming...");
				}
				Thread.currentThread().interrupt();
			}
		}

		void cancel() {
			try {
				if (logger.isTraceEnabled()) {
					logger.trace("Cancelling Redis consuming...");
				}
				if (connection != null) {
					Thread.currentThread().interrupt();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	public void addMessageListener(MessageListener listener, Topic topic) {
		Assert.notNull(listener, "a valid listener is required");
		Assert.notNull(topic, "a valid topic is required");

		Collection<MessageListener> collection = messageListeners.get(topic);
		if (collection == null) {
			collection = new CopyOnWriteArraySet<MessageListener>();
			messageListeners.put(topic, collection);
		}
		collection.add(listener);

		if (logger.isTraceEnabled()) {
			logger.trace("Adding listener '" + listener + "' on channel '" + topic.getTopic() + "'");
		}
	}

	public void removeListener(MessageListener listener, Topic topic) {
		Assert.notNull(listener, "a valid listener is required");
		Assert.notNull(listener, "a valid topic is required");

		if (!messageListeners.isEmpty()) {
			for (Topic tp : messageListeners.keySet()) {
				if (!tp.equals(topic)) {
					if (logger.isTraceEnabled()) {
						logger.trace("Removing listener '" + listener + "' on channel '" + topic.getTopic() + ",error,not found topic.'");
					}
					return;
				}
				Collection<MessageListener> listeners = messageListeners.get(tp);
				for (Iterator<MessageListener> iterator = listeners.iterator(); iterator.hasNext();) {
					MessageListener listen = iterator.next();
					if (listeners.equals(listen)) {
						iterator.remove();
					}
				}
			}
		}
	}

	/**
	 * 懒加载
	 */
	private void lazyListen() {
		boolean debug = logger.isDebugEnabled();
		boolean started = false;

		if (running) {
			synchronized (monitor) {
				if (messageListeners.size() > 0) {
					for (Topic topic : messageListeners.keySet()) {
						ConsumeTask consumeTask = new ConsumeTask(topic);
						subscriptionExecutor.execute(consumeTask);
						tasks.add(consumeTask);
					}
					started = true;
				}
			}
			if (debug) {
				if (started) {
					logger.debug("Started listening for Redis messages");
				} else {
					logger.debug("Postpone listening for Redis messages until actual listeners are added");
				}
			}
		}
	}

	@Override
	public void destroy() throws Exception {
		stop();
	}

	private volatile boolean running = false;

	private void stop() {
		if (running) {
			running = false;
		}
		for (ConsumeTask task : tasks) {
			task.cancel();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Stopped PrdCsmRedisMessageListenerContainer");
		}
	}

	private final Object monitor = new Object();

	private void start() {
		if (!running) {
			running = true;
			// wait for the subscription to start before returning
			// technically speaking we can only be notified right before the subscription starts
			synchronized (monitor) {
				lazyListen();
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Started PrdCsmRedisMessageListenerContainer");
			}
		}
	}

	private String beanName;

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public static final String DEFAULT_THREAD_NAME_PREFIX = ClassUtils.getShortName(RedisMessageListenerContainer.class) + "-";

	protected TaskExecutor createDefaultTaskExecutor() {
		String threadNamePrefix = (beanName != null ? beanName + "-" : DEFAULT_THREAD_NAME_PREFIX);
		return new SimpleAsyncTaskExecutor(threadNamePrefix);
	}

	@Override
	public void afterPropertiesSet() {
		subscriptionExecutor = createDefaultTaskExecutor();
		if (!running) {
			start();
		}
	}

}
