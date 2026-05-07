package org.eclipse.jetty.server;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.FieldOrMethod;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LDC;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.base.Predicates;
import com.google.common.reflect.Reflection;

import io.github.toolfactory.narcissus.Narcissus;

class MainTest {

	private static Method METHOD_FOR_NAME, METHOD_CONAINS_KEY, METHOD_TEST_AND_APPLY, METHOD_GET_VALUE,
			METHOD_CAST = null;

	@BeforeAll
	static void beforeAll() throws NoSuchMethodException {
		//
		final Class<?> clz = Main.class;
		//
		(METHOD_FOR_NAME = clz.getDeclaredMethod("forName", String.class)).setAccessible(true);
		//
		(METHOD_CONAINS_KEY = clz.getDeclaredMethod("containsKey", Map.class, Object.class)).setAccessible(true);
		//
		(METHOD_TEST_AND_APPLY = clz.getDeclaredMethod("testAndApply", Predicate.class, Object.class, Function.class,
				Function.class)).setAccessible(true);
		//
		(METHOD_GET_VALUE = clz.getDeclaredMethod("getValue", LDC.class, ConstantPoolGen.class)).setAccessible(true);
		//
		(METHOD_CAST = clz.getDeclaredMethod("cast", Class.class, Object.class)).setAccessible(true);
		//
	}

	private static class IH implements InvocationHandler {

		private Boolean containsKey, test = null;

		public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
			//
			if (Objects.equals(Void.TYPE, getReturnType(method))) {
				//
				return null;
				//
			} // if
				//
			final String name = getName(method);
			//
			if (proxy instanceof Map) {
				//
				if (Objects.equals(name, "get")) {
					//
					return null;
					//
				} else if (Objects.equals(name, "containsKey")) {
					//
					return containsKey;
					//
				} // if
					//
			} else if (proxy instanceof Function) {
				//
				if (Objects.equals(name, "apply")) {
					//
					return null;
					//
				} // if
					//
			} else if (proxy instanceof Predicate) {
				//
				if (Objects.equals(name, "test")) {
					//
					return test;
					//
				} // if
					//
			} else if (proxy instanceof Stream) {
				//
				if (contains(Arrays.asList("collect", "filter", "map"), name)) {
					//
					return null;
					//
				} // if
					//
			} // if
				//
			throw new Throwable(name);
			//
		}

	}

	@Test
	void testNull() throws Throwable {
		//
		final Method[] ms = Main.class.getDeclaredMethods();
		//
		Method m = null;
		//
		Collection<Object> collection = null;
		//
		Class<?>[] parameterTypes = null;
		//
		Object result = null;
		//
		String toString = null;
		//
		for (int i = 0; ms != null && i < ms.length; i++) {
			//
			if ((m = ArrayUtils.get(ms, i)) == null || m.isSynthetic()
					|| (parameterTypes = m.getParameterTypes()) == null) {
				//
				continue;
				//
			} // if
				//
			clear(collection = ObjectUtils.getIfNull(collection, ArrayList::new));
			//
			for (int j = 0; j < parameterTypes.length; j++) {
				//
				if (Objects.equals(ArrayUtils.get(parameterTypes, j), Integer.TYPE)) {
					//
					add(collection, Integer.valueOf(0));
					//
				} else {
					//
					add(collection, null);
					//
				} // if
					//
			} // for
				//
			if (!m.isAccessible()) {
				//
				m.setAccessible(true);
				//
			} // if
				//
			result = invoke(m, null, toArray(collection));
			//
			toString = Objects.toString(m);
			//
			if (contains(Arrays.asList(Integer.TYPE, Boolean.TYPE), getReturnType(m)) || Boolean.logicalAnd(
					Objects.equals(getName(m), "getPorts"), Arrays.equals(parameterTypes, new Class<?>[] {}))) {
				//
				Assertions.assertNotNull(result, toString);
				//
			} else {
				//
				Assertions.assertNull(result, toString);
				//
			} // if
				//
		} // for
			//
	}

	@Test
	void testNotNull() throws Throwable {
		//
		final Method[] ms = Main.class.getDeclaredMethods();
		//
		Method m = null;
		//
		Collection<Object> collection = null;
		//
		Class<?>[] parameterTypes = null;
		//
		Class<?> parameterType = null;
		//
		Object result = null;
		//
		String name, toString = null;
		//
		IH ih = null;
		//
		for (int i = 0; ms != null && i < ms.length; i++) {
			//
			if ((m = ArrayUtils.get(ms, i)) == null || m.isSynthetic()
					|| (parameterTypes = m.getParameterTypes()) == null) {
				//
				continue;
				//
			} // if
				//
			if ((ih = ObjectUtils.getIfNull(ih, IH::new)) != null) {
				//
				ih.containsKey = ih.test = Boolean.FALSE;
				//
			} // if
				//
			clear(collection = ObjectUtils.getIfNull(collection, ArrayList::new));
			//
			for (int j = 0; j < parameterTypes.length; j++) {
				//
				if (Objects.equals(parameterType = ArrayUtils.get(parameterTypes, j), Integer.TYPE)
						|| Objects.equals(parameterType, Number.class)) {
					//
					add(collection, Integer.valueOf(0));
					//
				} else if (Objects.equals(parameterType, Class.class)) {
					//
					add(collection, Object.class);
					//
				} else if (Objects.equals(parameterType, Strings.class)) {
					//
					add(collection, Strings.CS);
					//
				} else if (Objects.equals(parameterType, FieldOrMethod.class)) {
					//
					add(collection, Narcissus.allocateInstance(Field.class));
					//
				} else if (isArray(parameterType)) {
					//
					add(collection, Array.newInstance(getComponentType(parameterType), 0));
					//
				} else if (isInterface(parameterType)) {
					//
					add(collection, Reflection.newProxy(parameterType, ih = ObjectUtils.getIfNull(ih, IH::new)));
					//
				} else {
					//
					add(collection, Narcissus.allocateInstance(parameterType));
					//
				} // if
					//
			} // for
				//
			if (!m.isAccessible()) {
				//
				m.setAccessible(true);
				//
			} // if
				//
			result = invoke(m, null, toArray(collection));
			//
			toString = Objects.toString(m);
			//
			if (contains(Arrays.asList(Integer.TYPE, Boolean.TYPE), getReturnType(m))
					|| Boolean.logicalAnd(Objects.equals(name = getName(m), "toString"),
							Arrays.equals(parameterTypes, new Class<?>[] { Object.class }))
					|| Boolean.logicalAnd(Objects.equals(name, "getName"),
							Arrays.equals(parameterTypes, new Class<?>[] { Class.class }))
					|| Boolean.logicalAnd(Objects.equals(name, "cast"),
							Arrays.equals(parameterTypes, new Class<?>[] { Class.class, Object.class }))
					|| Boolean.logicalAnd(Objects.equals(name, "getPorts"),
							Arrays.equals(parameterTypes, new Class<?>[] {}))
					|| Boolean.logicalAnd(Objects.equals(name, "getInstructions"),
							Arrays.equals(parameterTypes, new Class<?>[] { InstructionList.class }))) {
				//
				Assertions.assertNotNull(result, toString);
				//
			} else {
				//
				Assertions.assertNull(result, toString);
				//
			} // if
				//
		} // for
			//
	}

	private static Object invoke(final Method method, final Object instance, final Object... args)
			throws IllegalAccessException, InvocationTargetException {
		return method != null ? method.invoke(instance, args) : null;
	}

	private static Class<?> getReturnType(final Method instance) {
		return instance != null ? instance.getReturnType() : null;
	}

	private static String getName(final Member instance) {
		return instance != null ? instance.getName() : null;
	}

	private static Class<?> getComponentType(final Class<?> instance) {
		return instance != null ? instance.getComponentType() : null;
	}

	private static boolean isArray(final Class<?> instance) {
		return instance != null && instance.isArray();
	}

	private static boolean isInterface(final Class<?> instance) {
		return instance != null && instance.isInterface();
	}

	private static boolean contains(final Collection<?> items, final Object item) {
		return items != null && items.contains(item);
	}

	private static <E> void add(final Collection<E> items, final E item) {
		if (items != null) {
			items.add(item);
		}
	}

	private static void clear(final Collection<?> instance) {
		if (instance != null) {
			instance.clear();
		}
	}

	private static Object[] toArray(final Collection<?> instance) {
		return instance != null ? instance.toArray() : null;
	}

	@Test
	void testForName() throws IllegalAccessException, InvocationTargetException {
		//
		Assertions.assertNull(invoke(METHOD_FOR_NAME, null, ""));
		//
		Assertions.assertNull(invoke(METHOD_FOR_NAME, null, Integer.toString(1)));
		//
		Assertions.assertNotNull(invoke(METHOD_FOR_NAME, null, "java.lang.Object"));
		//
	}

	@Test
	void testContainsKey() throws IllegalAccessException, InvocationTargetException {
		//
		Assertions.assertEquals(Boolean.TRUE,
				invoke(METHOD_CONAINS_KEY, null, Collections.singletonMap(null, null), null));
		//
	}

	@Test
	void testTestAndApply() throws IllegalAccessException, InvocationTargetException {
		//
		Assertions.assertNull(invoke(METHOD_TEST_AND_APPLY, null, Predicates.alwaysTrue(), null, null, null));
		//
	}

	@Test
	void testGetValue() throws IllegalAccessException, InvocationTargetException {
		//
		Assertions.assertNull(invoke(METHOD_GET_VALUE, null, Narcissus.allocateInstance(LDC.class), null));
		//
	}

	@Test
	void testCast() throws Throwable {
		//
		Assertions.assertNull(cast(Object.class, null));
		//
	}

	private static <T> T cast(final Class<T> clz, final Object instance) throws Throwable {
		try {
			return (T) invoke(METHOD_CAST, null, clz, instance);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testMain() throws Throwable {
		//
		final Method getPorts = Main.class.getDeclaredMethod("getPorts");
		//
		if (getPorts != null && !getPorts.isAccessible()) {
			//
			getPorts.setAccessible(true);
			//
		} // if
			//
		final int[] ports = cast(int[].class, invoke(getPorts, null));
		//
		if (ports != null) {
			//
			if (ports.length > 0) {
				//
				Assertions.assertThrows(IllegalArgumentException.class,
						() -> Main.main(new String[] { StringUtils.joinWith("=", "port", ports[0] - 1) }));
				//
			} // if
				//
			if (ports.length > 1) {
				//
				Assertions.assertThrows(IllegalArgumentException.class,
						() -> Main.main(new String[] { StringUtils.joinWith("=", "port", ports[1] + 1) }));
				//
			} // if
				//
		} // if
			//
	}

}