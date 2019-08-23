package com.zcwng.demo.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.ToString;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class Result extends HashMap<String, Object> {

	private static ObjectMapper mapper = new ObjectMapper();
	
	private Result() { } //禁止被new调用

	public static Result of() {
		return new Result();
	}
	public static Result of(int code) {
		Result er = new Result();
		er.put("code", code);
		return er;
	}
	public static Result of(int code,String msg) {
		Result er = new Result();
		er.put("code", code);
		er.put("msg", msg);
		return er;
	}
	public static Result of(int code,String msg,Object data) {
		Result er = new Result();
		er.put("code", code);
		er.put("msg", msg);
		er.put("data", data);
		return er;
	}
	
	public Result put(String key,Object value) {
		super.put(key, value);
		return this;
	}

	@Override
	public String toString() {
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
