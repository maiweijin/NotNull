package per.mwj.notnull.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import per.mwj.notnull.annotation.NotNull;
import per.mwj.notnull.annotation.NullCheck;

@Aspect
public class NullCheckAspectj {
	@Around("@annotation(per.mwj.notnull.annotation.NullCheck)")
	public void check(ProceedingJoinPoint pjp) throws Throwable {
		MethodSignature signature = (MethodSignature) pjp.getSignature();
		Object[] args = pjp.getArgs();
		Method method = signature.getMethod();
		NullCheck nullCheck = method.getAnnotation(NullCheck.class);
		String[] groups = nullCheck.groups();
		Map<String, Boolean> resultMap = new HashMap<>();
		for (String group : groups) {
			resultMap.put(group, true);
		}
		Class<?>[] parameterTypes = method.getParameterTypes();
		for (int i = 0; i < args.length; i++) {
			Field[] declaredFields = parameterTypes[i].getDeclaredFields();
			List<Field> fieldList = Arrays.asList(declaredFields);
			fieldList = fieldList.stream().filter(field -> {
				return field.getAnnotation(NotNull.class) != null;
			}).collect(Collectors.toList());
			for (Field field : fieldList) {
				field.setAccessible(true);
				try {
					Object object = field.get(args[i]);
					if (object == null) {
						NotNull notNull = field.getAnnotation(NotNull.class);
						String[] groups2 = notNull.groups();
						for (String group : groups2) {
							resultMap.put(group, false);
						}
					}
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				}
			}
		}
		NotNullCheckResult.setResultMap(resultMap);
		pjp.proceed();
		NotNullCheckResult.clean();
	}
}
