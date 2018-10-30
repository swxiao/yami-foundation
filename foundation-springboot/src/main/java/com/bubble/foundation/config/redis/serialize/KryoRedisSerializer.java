package com.bubble.foundation.config.redis.serialize;

import java.io.ByteArrayOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import de.javakaffee.kryoserializers.SynchronizedCollectionsSerializer;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;

/**
 * @author kakashi
 * @since 2018年10月19日
 */
public class KryoRedisSerializer<T> implements RedisSerializer<T> {

	private static final Logger logger = LoggerFactory.getLogger(KryoRedisSerializer.class);

	public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

	private final ThreadLocal<Kryo> kryos = new ThreadLocal<Kryo>() {

		{
			Kryo kryo = new Kryo();
			set(kryo);
		}
	};

	private Class<T> clazz;

	public KryoRedisSerializer(Class<T> clazz) {
		super();
		this.clazz = clazz;
	}

	@Override
	public byte[] serialize(T t) throws SerializationException {
		if (t == null) {
			return EMPTY_BYTE_ARRAY;
		}

		Kryo kryo = kryos.get();
		UnmodifiableCollectionsSerializer.registerSerializers(kryo);
		SynchronizedCollectionsSerializer.registerSerializers(kryo);
		kryo.setReferences(false);
		kryo.register(clazz);

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); Output output = new Output(baos)) {
			kryo.writeClassAndObject(output, t);
			output.flush();
			return baos.toByteArray();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return EMPTY_BYTE_ARRAY;
	}

	@Override
	public T deserialize(byte[] bytes) throws SerializationException {
		if (bytes == null || bytes.length <= 0) {
			return null;
		}

		Kryo kryo = kryos.get();
		UnmodifiableCollectionsSerializer.registerSerializers(kryo);
		SynchronizedCollectionsSerializer.registerSerializers(kryo);
		kryo.setReferences(false);
		kryo.register(clazz);

		try (Input input = new Input(bytes)) {
			return (T) kryo.readClassAndObject(input);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

}
