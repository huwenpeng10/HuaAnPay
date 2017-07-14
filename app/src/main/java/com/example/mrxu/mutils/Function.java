package com.example.mrxu.mutils;

import java.util.ArrayList;

public class Function {


	private static Function FUNCTION;
	private ArrayList<F> fs;

	private Function() {

        fs = new ArrayList<F>();
		fs.add(new F(33, "F10002", "刷卡收款"));
		fs.add(new F(55, "F20027", "话费充值"));
		fs.add(new F(66, "F20021", "扫码收款"));
		fs.add(new F(44, "04", "转账"));
		fs.add(new F(64, "06", "转账"));
	}

	public String queryNameByCode(String code) {
		for (F f : fs) {
			if (f.code.equals(code)) {
				return f.name;
			}
		}
		return null;
	}

	public static final Function getInstance() {
		if (FUNCTION == null) {
			FUNCTION = new Function();
		}
		return FUNCTION;
	}


	class F {
		int id;
		String code, name;

		/**
		 * @param id
		 * @param code
		 * @param name
		 */
		public F(int id, String code, String name) {
			this.id = id;
			this.code = code;
			this.name = name;
		}

	}

}
