package com.cangoonline.risk.common;

/**
 * 自定义异常
 */
public class CodeException extends Exception{

	private static final long serialVersionUID = 6541688797013312879L;

	private int code;
	private String message;
	
	public CodeException() {
		super();
	}

	public CodeException(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public static CodeException throwException(int code,String message) throws CodeException {
		throw new CodeException(code,message);
	}
	
}
