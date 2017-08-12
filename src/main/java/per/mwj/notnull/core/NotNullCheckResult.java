package per.mwj.notnull.core;

import java.util.Map;

public class NotNullCheckResult {
	private static ThreadLocal<Map<String, Boolean>> result = new ThreadLocal<>();

	public static boolean getResult(String group) {
		Boolean boolean1 = result.get().get(group);
		if (boolean1 != null) {
			return boolean1;
		}
		return false;
	}

	public static void setResultMap(Map<String, Boolean> resultMap) {
		result.set(resultMap);
	}

	public static void clean() {
		result.remove();
	}
}
