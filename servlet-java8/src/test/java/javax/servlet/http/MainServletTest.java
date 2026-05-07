package javax.servlet.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.MethodGen;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.base.Predicates;
import com.google.common.reflect.Reflection;

import io.github.toolfactory.narcissus.Narcissus;

class MainServletTest {

	private static Method METHOD_ALL_MATCH, METHOD_CONAINS_KEY, METHOD_FOR_NAME = null;

	@BeforeAll
	static void beforeAll() throws NoSuchMethodException {
		//
		final Class<?> clz = MainServlet.class;
		//
		(METHOD_ALL_MATCH = clz.getDeclaredMethod("allMatch", Stream.class, Predicate.class)).setAccessible(true);
		//
		(METHOD_CONAINS_KEY = clz.getDeclaredMethod("containsKey", Map.class, Object.class)).setAccessible(true);
		//
		(METHOD_FOR_NAME = clz.getDeclaredMethod("forName", String.class)).setAccessible(true);
		//
	}

	private static class IH implements InvocationHandler {

		private Boolean containsKey, allMatch = null;

		private String getServletPath = null;

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
			if (proxy instanceof ServletRequest) {
				//
				if (Objects.equals(name, "getParameter")) {
					//
					return null;
					//
				} else if (Objects.equals(name, "getParameterMap")) {
					//
					return null;
					//
				} // if
					//
			} // if
				//
			if (proxy instanceof Map) {
				//
				if (Objects.equals(name, "containsKey")) {
					//
					return containsKey;
					//
				} // if
					//
			} else if (proxy instanceof Stream) {
				//
				if (Objects.equals(name, "allMatch")) {
					//
					return allMatch;
					//
				} // if
					//
			} else if (proxy instanceof HttpServletRequest) {
				//
				if (Objects.equals(name, "getServletPath")) {
					//
					return getServletPath;
					//
				} // if
					//
			} else if (proxy instanceof ServletResponse) {
				//
				if (Objects.equals(name, "getOutputStream")) {
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

	private MainServlet instance = null;

	private IH ih = null;

	@BeforeEach
	void beforeEach() {
		//
		instance = new MainServlet();
		//
		ih = new IH();
		//
	}

	@Test
	void testNull() throws Throwable {
		//
		final Method[] ms = MainServlet.class.getDeclaredMethods();
		//
		Method m = null;
		//
		Class<?>[] parameterTypes = null;
		//
		Object result = null;
		//
		String toString = null;
		//
		Object[] os = null;
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
			if (!m.isAccessible()) {
				//
				m.setAccessible(true);
				//
			} // if
				//
			os = toArray(Collections.nCopies(parameterTypes.length, null));
			//
			toString = Objects.toString(m);
			//
			if (Modifier.isStatic(m.getModifiers())) {
				//
				result = invoke(m, null, os);
				//
			} else {
				//
				result = invoke(m, instance = ObjectUtils.getIfNull(instance, MainServlet::new), os);
				//
			} // if
				//
			if (contains(Arrays.asList(Boolean.TYPE), getReturnType(m))
					|| Boolean.logicalAnd(Objects.equals(getName(m), "findHWNDListByTitle"),
							Arrays.equals(parameterTypes, new Class<?>[] { String.class }))) {
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

	@Test
	void testNotNull() throws Throwable {
		//
		final Method[] ms = MainServlet.class.getDeclaredMethods();
		//
		Method m = null;
		//
		Class<?>[] parameterTypes = null;
		//
		Class<?> parameterType = null;
		//
		Object result = null;
		//
		String name, toString = null;
		//
		Object[] os = null;
		//
		Collection<Object> collection = null;
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
			if (!m.isAccessible()) {
				//
				m.setAccessible(true);
				//
			} // if
				//
			if ((ih = ObjectUtils.getIfNull(ih, IH::new)) != null) {
				//
				ih.containsKey = ih.allMatch = Boolean.FALSE;
				//
			} // if
				//
			clear(collection = ObjectUtils.getIfNull(collection, ArrayList::new));
			//
			for (int j = 0; j < parameterTypes.length; j++) {
				//
				if (Objects.equals(parameterType = ArrayUtils.get(parameterTypes, j), Class.class)) {
					//
					add(collection, Object.class);
					//
				} else if (Objects.equals(parameterType, OutputStream.class)) {
					//
					add(collection, new ByteArrayOutputStream());
					//
				} else if (Objects.equals(parameterType, byte[].class)) {
					//
					add(collection, new byte[] {});
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
			os = toArray(collection);
			//
			toString = Objects.toString(m);
			//
			if (Modifier.isStatic(m.getModifiers())) {
				//
				result = invoke(m, null, os);
				//
			} else {
				//
				result = invoke(m, instance = ObjectUtils.getIfNull(instance, MainServlet::new), os);
				//
			} // if
				//
			if (contains(Arrays.asList(Boolean.TYPE), getReturnType(m))
					|| Boolean.logicalAnd(Objects.equals(name = getName(m), "findHWNDListByTitle"),
							Arrays.equals(parameterTypes, new Class<?>[] { String.class }))
					|| Boolean.logicalAnd(Objects.equals(name, "getClass"),
							Arrays.equals(parameterTypes, new Class<?>[] { Object.class }))
					|| Boolean.logicalAnd(Objects.equals(name, "getName"),
							Arrays.equals(parameterTypes, new Class<?>[] { Class.class }))) {
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

	private static boolean isInterface(final Class<?> instance) {
		return instance != null && instance.isInterface();
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

	private static String getName(final Member instance) {
		return instance != null ? instance.getName() : null;
	}

	private static boolean contains(final Collection<?> items, final Object item) {
		return items != null && items.contains(item);
	}

	private static Class<?> getReturnType(final Method instance) {
		return instance != null ? instance.getReturnType() : null;
	}

	private static Object[] toArray(final Collection<?> instance) {
		return instance != null ? instance.toArray() : null;
	}

	@Test
	void testAllMatch() throws IllegalAccessException, InvocationTargetException {
		//
		Assertions.assertEquals(Boolean.TRUE, invoke(METHOD_ALL_MATCH, null, Stream.empty(), Predicates.alwaysTrue()));
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
	void testDoGet() throws IOException, NoSuchMethodException, ServletException {
		//
		if (instance == null) {
			//
			return;
			//
		} // if
			//
		try (final InputStream is = MainServlet.class.getResourceAsStream(
				StringUtils.join("/", Strings.CS.replace(MainServlet.class.getName(), ".", "/"), ".class"))) {
			//
			final JavaClass javaClass = new ClassParser(is, null).parse();
			//
			final org.apache.bcel.classfile.Method method = javaClass != null ? javaClass.getMethod(
					MainServlet.class.getDeclaredMethod("doGet", HttpServletRequest.class, HttpServletResponse.class))
					: null;
			//
			final ConstantPoolGen cpg = new ConstantPoolGen(method != null ? method.getConstantPool() : null);
			//
			final MethodGen mg = new MethodGen(method, null, cpg);
			//
			final InstructionList il = mg.getInstructionList();
			//
			final Instruction[] instructions = il != null ? il.getInstructions() : null;
			//
			LDC ldc = null;
			//
			INVOKESTATIC invokestatic = null;
			//
			for (int i = 0; instructions != null && i < instructions.length; i++) {
				//
				if ((ldc = cast(LDC.class, ArrayUtils.get(instructions, i))) != null && i < instructions.length - 2
						&& (invokestatic = cast(INVOKESTATIC.class, ArrayUtils.get(instructions, i + 1))) != null
						&& Objects.equals(invokestatic.getMethodName(cpg), "equals")
						&& Objects.equals(getName(getClass(ArrayUtils.get(instructions, i + 2))),
								"org.apache.bcel.generic.IFEQ")) {
					//
					if ((ih = ObjectUtils.getIfNull(ih, IH::new)) != null) {
						//
						ih.getServletPath = Objects.toString(ldc.getValue(cpg));
						//
					} // if
						//
					Assertions.assertDoesNotThrow(() -> instance.doGet(
							Reflection.newProxy(HttpServletRequest.class, ih = ObjectUtils.getIfNull(ih, IH::new)),
							null));
					//
				} // if
					//
			} // for
				//
		} // try
			//
	}

	private static <T> T cast(final Class<T> clz, final Object instance) {
		return clz != null && clz.isInstance(instance) ? clz.cast(instance) : null;
	}

	private static Class<?> getClass(final Object instance) {
		return instance != null ? instance.getClass() : null;
	}

	private static String getName(final Class<?> instance) {
		return instance != null ? instance.getName() : null;
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

}