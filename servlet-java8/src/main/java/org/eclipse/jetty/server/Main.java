package org.eclipse.jetty.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.servlet.http.MainServlet;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.FieldOrMethod;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.IFLT;
import org.apache.bcel.generic.IF_ICMPLE;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.MethodGen;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Main {

	public static void main(final String[] args) throws Exception {
		//
		final Map<String, String> map = toMap(args);
		//
		final int defaultPort = 8080;
		//
		final ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		//
		servletContextHandler.setContextPath("/");
		//
		final ServletHolder servletHolder = new ServletHolder(new MainServlet());
		//
		forEach(Arrays.asList("/", "/imageFormatNames", "/switchWindow"),
				x -> servletContextHandler.addServlet(servletHolder, x));
		//
		final int port = intValue(testAndApply(x -> containsKey(map, x), "port",
				x -> NumberUtils.toInt(toString(get(map, x)), defaultPort), null), defaultPort);
		//
		final int[] ports = getPorts();
		//
		if (ports != null) {
			//
			if (ports.length > 0 && port < ports[0]) {
				//
				throw new IllegalArgumentException("Port should be equals or greater than " + ports[0]);
				//
			} else if (ports.length > 1 && port > ports[1]) {
				//
				throw new IllegalArgumentException("Port should be equals or less than " + ports[1]);
				//
			} // if
				//
		} // if
			//
		final Server server = new Server(intValue(testAndApply(x -> containsKey(map, x), "port",
				x -> NumberUtils.toInt(toString(get(map, x)), defaultPort), null), defaultPort));
		//
		server.setHandler(servletContextHandler);
		//
		if (!isTestMode()) {
			//
			server.start();
			//
			server.join();
			//
		} // if
			//
	}

	private static Map<String, String> toMap(final String... ss) {
		//
		String s = null;
		//
		Map<String, String> map = null;
		//
		for (int i = 0; i < length(ss); i++) {
			//
			if (Objects.equals(s = ArrayUtils.get(ss, i), "=")) {
				//
				put(map = ObjectUtils.getIfNull(map, LinkedHashMap::new), "", "");
				//
			} else if (s != null && s.length() == 2 && s.charAt(0) == '=') {
				//
				put(map = ObjectUtils.getIfNull(map, LinkedHashMap::new), "", s.substring(1, s.length()));
				//
			} else if (s != null && s.length() == 2 && s.charAt(s.length() - 1) == '=') {
				//
				put(map = ObjectUtils.getIfNull(map, LinkedHashMap::new), s.substring(0, s.length() - 1), "");
				//
			} else if (s != null && s.indexOf('=') >= 0 && s.indexOf('=') == s.lastIndexOf('=')) {
				//
				put(map = ObjectUtils.getIfNull(map, LinkedHashMap::new), StringUtils.substringBefore(s, '='),
						StringUtils.substringAfter(s, '='));
				//
			} else if (s != null && s.length() > 2 && s.indexOf('=') != s.lastIndexOf('=')) {
				//
				put(map = ObjectUtils.getIfNull(map, LinkedHashMap::new), StringUtils.substring(s, 0, s.indexOf('=')),
						StringUtils.substring(s, s.indexOf('=') + 1));
				//
			} // if
				//
		} // for
			//
		return map;
		//
	}

	private static <K, V> void put(final Map<K, V> instance, final K key, final V value) {
		if (instance != null) {
			instance.put(key, value);
		}
	}

	private static int[] getPorts() throws IOException, NoSuchMethodException {
		//
		int[] ints = null;
		//
		final Class<?> clz = InetSocketAddress.class;
		//
		try (final InputStream is = Main.class
				.getResourceAsStream(StringUtils.join("/", replace(Strings.CS, getName(clz), ".", "/"), ".class"))) {
			//
			final JavaClass javaClass = new ClassParser(is, null).parse();
			//
			final Method method = getMethod(javaClass, getDeclaredMethod(clz, "checkPort", Integer.TYPE));
			//
			final ConstantPoolGen cpg = new ConstantPoolGen(getConstantPool(javaClass));
			//
			final Instruction[] ins = getInstructions(getInstructionList(new MethodGen(method, getName(method), cpg)));
			//
			Instruction in = null;
			//
			Integer integer = null;
			//
			for (int i = 0; i < length(ins); i++) {
				//
				if ((in = ArrayUtils.get(ins, i)) instanceof IFLT) {
					//
					ints = ArrayUtils.add(ints, 0);
					//
				} else if (in instanceof IF_ICMPLE && i > 0 && (integer = cast(Integer.class,
						getValue(cast(LDC.class, ArrayUtils.get(ins, i - 1)), cpg))) != null) {
					//
					ints = ArrayUtils.add(ints, integer.intValue());
					//
				} // if
					//
			} // for
				//
		} // try
			//
		return ints;
		//
	}

	private static Object getValue(final LDC instance, final ConstantPoolGen cpg) {
		return instance != null && cpg != null && cpg.getSize() > 0 ? instance.getValue(cpg) : null;
	}

	private static Instruction[] getInstructions(final InstructionList instance) {
		return instance != null ? instance.getInstructions() : null;
	}

	private static InstructionList getInstructionList(final MethodGen instance) {
		return instance != null ? instance.getInstructionList() : null;
	}

	private static String getName(final FieldOrMethod instance) {
		//
		try {
			//
			if (instance == null || FieldUtils.readField(instance, "constant_pool", true) == null) {
				//
				return null;
				//
			} // if
				//
		} catch (final IllegalAccessException e) {
			//
			return null;
			//
		} // try
			//
		return instance.getName();
		//
	}

	private static ConstantPool getConstantPool(final JavaClass instance) {
		return instance != null ? instance.getConstantPool() : null;
	}

	private static Method getMethod(final JavaClass instance, final java.lang.reflect.Method method) {
		//
		try {
			//
			if (instance == null || FieldUtils.readDeclaredField(instance, "methods", true) == null) {
				//
				return null;
				//
			} // if
				//
		} catch (final IllegalAccessException e) {
			//
			return null;
			//
		} // try
			//
		return instance.getMethod(method);
		//
	}

	private static java.lang.reflect.Method getDeclaredMethod(final Class<?> instance, final String name,
			final Class<?>... parameterTypes) throws NoSuchMethodException {
		//
		try {
			//
			if (instance == null || name == null || FieldUtils.readDeclaredField(name, "value", true) == null) {
				//
				return null;
				//
			} // if
				//
		} catch (final IllegalAccessException e) {
			//
			return null;
			//
		} // try
			//
		return instance.getDeclaredMethod(name, parameterTypes);
		//
	}

	private static <T> T cast(final Class<T> clz, final Object instance) {
		return clz != null && clz.isInstance(instance) ? clz.cast(instance) : null;
	}

	private static String replace(final Strings instance, final String text, final String searchString,
			final String replacement) {
		//
		try {
			//
			if (instance == null || text == null || FieldUtils.readDeclaredField(text, "value", true) == null) {
				//
				return null;
				//
			} // if
				//
		} catch (final IllegalAccessException e) {
			//
			return null;
			//
		} // try
			//
		return instance.replace(text, searchString, replacement);
		//
	}

	private static String getName(final Class<?> instance) {
		return instance != null ? instance.getName() : null;
	}

	private static int length(final Object[] instance) {
		return instance != null ? instance.length : 0;
	}

	private static <T> void forEach(final Iterable<T> instance, final Consumer<T> consumer) {
		if (instance != null) {
			instance.forEach(consumer);
		}
	}

	private static boolean isTestMode() {
		return forName("org.junit.jupiter.api.Test") != null;
	}

	private static Class<?> forName(final String className) {
		//
		try {
			//
			if (className == null || FieldUtils.readDeclaredField(className, "value", true) == null) {
				//
				return null;
				//
			} // if
				//
			return StringUtils.isNotBlank(className) ? Class.forName(className) : null;
			//
		} catch (final ClassNotFoundException | IllegalAccessException e) {
			//
			return null;
			//
		} // try
			//
	}

	private static <V> V get(final Map<?, V> instance, final Object key) {
		return instance != null ? instance.get(key) : null;
	}

	private static boolean containsKey(final Map<?, ?> instance, final Object key) {
		return instance != null && instance.containsKey(key);
	}

	private static int intValue(final Number instance, final int defaultValue) {
		return instance != null ? instance.intValue() : defaultValue;
	}

	private static String toString(final Object instance) {
		return instance != null ? instance.toString() : null;
	}

	private static <T, R> R testAndApply(final Predicate<T> predicate, final T value, final Function<T, R> functionTrue,
			final Function<T, R> functionFalse) {
		return predicate != null && predicate.test(value) ? apply(functionTrue, value) : apply(functionFalse, value);
	}

	private static <T, R> R apply(final Function<T, R> instance, final T value) {
		return instance != null ? instance.apply(value) : null;
	}

}