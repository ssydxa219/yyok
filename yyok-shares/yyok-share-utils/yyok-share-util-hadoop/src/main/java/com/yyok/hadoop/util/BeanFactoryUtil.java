package com.yyok.hadoop.util;

import java.util.ServiceLoader;

import org.apache.hadoop.hbase.util.Bytes;

import com.yyok.hbase.util.RowKeyGenerator;

public class BeanFactoryUtil {

	private BeanFactoryUtil() {
	}

	public static BeanFactoryUtil getInstance() {
		return BeanFactoryHolder.instance;
	}

	public  <T> T getBeanInstance(Class<T> type) {
		ServiceLoader<T> serviceLoad = ServiceLoader.load(type, getInstance()
				.getClass().getClassLoader());
		if (serviceLoad != null) {
			for (T ele : serviceLoad)
				return ele;
		}
		return null;

	}

	private static class BeanFactoryHolder {
		private static final BeanFactoryUtil instance = new BeanFactoryUtil();
	}

	public static void main(String[] args) {
		System.out.println(Bytes.toStringBinary(BeanFactoryUtil.getInstance().getBeanInstance(RowKeyGenerator.class).nextId()));

	}

}
