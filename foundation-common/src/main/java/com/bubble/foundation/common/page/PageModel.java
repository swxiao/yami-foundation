package com.bubble.foundation.common.page;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bubble.foundation.common.util.BeanUtil;

/**
 * 分页Model
 * 
 * @author King
 *
 * @param <E>
 */
public class PageModel<E> implements Serializable {

	public PageModel() {
	}

	public PageModel(int pageSize, int pageNo) {
		this.pageSize = pageSize;
		this.pageNo = pageNo;
	}

	public PageModel<E> initData(List<E> list, long total) {
		this.list = list;
		this.total = ((Long) total).intValue();
		return this;
	}

	private Map extension = new HashMap();

	// 结果集
	private List<E> list;

	// 查询记录数
	private int total;

	// 每页多少条数据
	private int pageSize = 10;

	// 第几页
	private int pageNo = 1;

	//
	private int offset = -1;

	//
	private int limit = 0;

	/**
	 * 总页数
	 * 
	 * @return
	 */
	public int getTotalPages() {
		int tPages = (total + pageSize - 1) / pageSize;
		return tPages == 0 ? tPages = 1 : tPages;
	}

	/**
	 * 取得首页
	 * 
	 * @return
	 */
	public int getTopPageNo() {
		return 1;
	}

	public List<E> getList() {
		return list;
	}

	public Map getExtension() {
		return extension;
	}

	public void setExtension(Map extension) {
		this.extension = extension;
	}

	/**
	 * 上一页
	 * 
	 * @return
	 */
	public int getPreviousPageNo() {
		if (pageNo <= 1) {
			return 1;
		}
		return pageNo - 1;
	}

	/**
	 * 下一页
	 * 
	 * @return
	 */
	public int getNextPageNo() {
		if (pageNo >= getBottomPageNo()) {
			return getBottomPageNo();
		}
		return pageNo + 1;
	}

	/**
	 * 取得尾页
	 * 
	 * @return
	 */
	public int getBottomPageNo() {
		return getTotalPages() < 1 ? 1 : getTotalPages();
	}

	public void setList(List<E> list) {
		this.list = list;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getPageSize() {
		if (pageSize == 0) {
			pageSize = limit;
		}
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNo() {
		if (pageNo == 0) {
			this.pageNo = (limit + offset) / getPageSize();
		}
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getOffset() {
		if (this.offset == -1) {
			this.offset = (pageNo - 1) * pageSize;
		}
		return this.offset;
	}

	public int getLimit() {
		if (this.limit == 0) {
			this.limit = pageSize;
		}
		return this.limit;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	@Override
	public String toString() {
		return BeanUtil.bean2JSON(this);
	}

}
