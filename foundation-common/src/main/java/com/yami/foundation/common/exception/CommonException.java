/**
 * Copyright [2015-2017]
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
package com.yami.foundation.common.exception;

/**
 * common 异常
 * 
 * @author xiaosw<xiaosw@msn.cn>
 * @since 2017年1月13日
 */
public class CommonException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 836785496836557533L;

	/**
	 * 
	 */
	public CommonException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public CommonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CommonException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public CommonException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public CommonException(Throwable cause) {
		super(cause);
	}

}
