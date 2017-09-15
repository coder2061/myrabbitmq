package com.github.core;

/**
 * 作业结果
 * 
 * @author jiangyf
 * @date 2017年9月13日 下午5:43:32
 */
public class Result {
	private boolean isSuccess;

	private String desc;

	public Result() {
	}

	public Result(boolean isSuccess, String desc) {
		this.isSuccess = isSuccess;
		this.desc = desc;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
