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
package com.yami.foundation.common.entity;

import java.io.Serializable;

import com.yami.foundation.common.util.BeanUtil;

/**
 * 
 * @author xiaosw<xiaosw@msn.cn>
 * @since 2017年1月17日
 */
public abstract class AbstractEntity implements Serializable {

	@Override
	public String toString() {
		return BeanUtil.bean2JSON(this);
	}

}
