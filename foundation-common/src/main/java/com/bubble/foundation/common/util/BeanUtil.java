/**
 * Copyright 2015-2017
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package com.bubble.foundation.common.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.bubble.foundation.common.exception.CommonException;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 数据结构转换操作
 * 
 * @author Vincent xiao<xiaosw@msn.cn>
 * @since 2015年2月5日
 */
public class BeanUtil extends org.apache.commons.beanutils.BeanUtils {

	/**
	 * Use jacksonMapper Convert JSON to Bean
	 * 
	 * @param obj
	 *            Source Object
	 * @return Object of Map
	 * @throws CommonException
	 */
	public static Map<String, Object> bean2Map(Object obj) throws CommonException {
		String errorMessage = "The operation of bean2Map has been error!";
		Map<String, Object> map = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			map = objectMapper.readValue(bean2JSON(obj), Map.class);
		} catch (JsonParseException e) {
			throw new CommonException(errorMessage, e);
		} catch (JsonMappingException e) {
			throw new CommonException(errorMessage, e);
		} catch (IOException e) {
			throw new CommonException(errorMessage, e);
		}
		return map;
	}

	/**
	 * Use original method(Introspector) Convert Bean to Map
	 * 
	 * @param map
	 *            Source Map
	 * @param clz
	 *            Target Class Object
	 * @return Target Object
	 * @throws CommonException
	 */
	public static <T> T map2Bean(Map map, Class<T> clz) throws CommonException {
		String errorMessage = "The operation of map2Bean has been error!";
		try {
			Object obj = clz.newInstance(); // 创建 JavaBean 对象
			BeanInfo beanInfo = Introspector.getBeanInfo(clz);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor descriptor : propertyDescriptors) {
				String propertyName = descriptor.getName();
				if (map.containsKey(propertyName)) {
					Object value = map.get(propertyName);
					if (descriptor.getWriteMethod() != null) {
						descriptor.getWriteMethod().invoke(obj, value);
					}
				}
			}
			return (T) obj;
		} catch (IntrospectionException e) {
			throw new CommonException(errorMessage, e);
		} catch (IllegalArgumentException e) {
			throw new CommonException(errorMessage, e);
		} catch (IllegalAccessException e) {
			throw new CommonException(errorMessage, e);
		} catch (InvocationTargetException e) {
			throw new CommonException(errorMessage, e);
		} catch (InstantiationException e) {
			throw new CommonException(errorMessage, e);
		}
	}

	/**
	 * Use jacksonMapper Convert JSON to Bean
	 * 
	 * @param obj
	 *            Source Object
	 * @return JSON String
	 * @throws CommonException
	 */
	public static String bean2JSON(Object obj) throws CommonException {
		String errorMessage = "The operation of bean2Json has been error!";
		ObjectMapper objectMapper = null;
		StringBuffer strBuffer = new StringBuffer("");
		objectMapper = new ObjectMapper();
		try {
			strBuffer.append(objectMapper.writeValueAsString(obj));
		} catch (JsonGenerationException e) {
			throw new CommonException(errorMessage, e);
		} catch (JsonMappingException e) {
			throw new CommonException(errorMessage, e);
		} catch (IOException e) {
			throw new CommonException(errorMessage, e);
		}
		return strBuffer.toString();
	}

	/**
	 * Use jacksonMapper Convert JSON to Bean
	 * 
	 * @param json
	 *            JSON String
	 * @param clz
	 *            Target Class Object
	 * @return Target Object
	 * @throws CommonException
	 */
	public static <T> T json2Bean(String json, Class<T> clz) throws CommonException {
		String errorMessage = "The operation of json2Bean has been error!";
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return (T) objectMapper.readValue(json, clz);
		} catch (JsonParseException e) {
			throw new CommonException(errorMessage, e);
		} catch (JsonMappingException e) {
			throw new CommonException(errorMessage, e);
		} catch (IOException e) {
			throw new CommonException(errorMessage, e);
		}
	}

	/**
	 * Use original method(Introspector) Convert Bean to Map
	 * 
	 * @param obj
	 *            Source Object
	 * @return Object of Map
	 * @throws CommonException
	 */
	public static Map<String, Object> bean2MapByOrigin(Object obj) throws CommonException {
		String errorMessage = "The operation of bean2Map has been error!";
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyDescriptors) {
				String key = property.getName();
				if (!"class".equals(key)) {
					Method getter = property.getReadMethod();
					Object value = getter.invoke(obj);
					map.put(key, value);
				}
			}
			return map;
		} catch (IntrospectionException e) {
			throw new CommonException(errorMessage, e);
		} catch (IllegalArgumentException e) {
			throw new CommonException(errorMessage, e);
		} catch (IllegalAccessException e) {
			throw new CommonException(errorMessage, e);
		} catch (InvocationTargetException e) {
			throw new CommonException(errorMessage, e);
		}
	}

	/**
	 * @param message
	 * @param clz
	 * @return
	 * @throws CommonException
	 */
	public static <T> List<T> json2List(String message, Class<T> clz) throws CommonException {
		String errorMessage = "The operation of jsonToList has been error!";
		try {
			return (List<T>) JSONArray.parseArray(message, clz);
		} catch (Exception e) {
			throw new CommonException(errorMessage, e);
		}
	}
}
