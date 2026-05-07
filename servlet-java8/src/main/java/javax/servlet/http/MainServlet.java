package javax.servlet.http;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC;

public class MainServlet extends HttpServlet {

	private static final long serialVersionUID = -6674454082731240928L;

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		//
		try (final OutputStream os = getOutputStream(response);
				final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			//
			final String servletPath = getServletPath(request);
			//
			if (Objects.equals(servletPath, "/imageFormatNames")) {
				//
				write(os, new ObjectMapper().writeValueAsBytes(ImageIO.getWriterFormatNames()));
				//
				return;
				//
			} else if (Objects.equals(servletPath, "/")) {
				//
				final List<HWND> hwnds = findHWNDListByTitle(getParameter(request, "title"));
				//
				HWND hwnd = null;
				//
				final Robot robot = new Robot();
				//
				User32 user32 = null;
				//
				RECT rect = null;
				//
				for (int i = 0; i < Math.min(IterableUtils.size(hwnds), 1); i++) {
					//
					if ((hwnd = IterableUtils.get(hwnds, i)) == null) {
						//
						continue;
						//
					} // if
						//
					if ((user32 = ObjectUtils.getIfNull(user32, () -> User32.INSTANCE)) != null) {
						//
						user32.ShowWindow(hwnd, User32.SW_MINIMIZE);
						//
						Thread.sleep(100);
						//
						user32.ShowWindow(hwnd, User32.SW_RESTORE);
						//
						Thread.sleep(100);
						//
						user32.SetForegroundWindow(hwnd);
						//
						Thread.sleep(100);
						//
					} // if
						//
					rect = new RECT();
					//
					if ((user32 = ObjectUtils.getIfNull(user32, () -> User32.INSTANCE)) != null) {
						//
						user32.GetWindowRect(hwnd, rect);
						//
					} // if
						//
					final Rectangle rectangle = (rect.right - rect.left) > 0 && (rect.bottom - rect.top) > 0
							? new Rectangle(rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top)
							: null;
					//
					BufferedImage bufferedImage = rectangle != null ? robot.createScreenCapture(rectangle) : null;
					//
					if (bufferedImage != null) {
						//
						final Map<?, ?> parameterMap = getParameterMap(request);
						//
						if (allMatch(Stream.of("w", "h"), x -> containsKey(parameterMap, x))) {
							//
							bufferedImage = bufferedImage.getSubimage(NumberUtils.toInt(getParameter(request, "x")),
									NumberUtils.toInt(getParameter(request, "y")),
									NumberUtils.toInt(getParameter(request, "w")),
									NumberUtils.toInt(getParameter(request, "h")));
							//
						} // if
							//
						if (containsKey(parameterMap, "format")) {
							//
							ImageIO.write(bufferedImage, getParameter(request, "format"), baos);
							//
						} else {
							//
							ImageIO.write(bufferedImage, "png", baos);
							//
						} // if
							//
						final byte[] bs = baos.toByteArray();
						//
						setHeader(response, "sha512Hex", DigestUtils.sha512Hex(bs));
						//
						write(os, bs);
						//
					} // if
						//
				} // for
					//
			} else if (Boolean.logicalAnd(Objects.equals(servletPath, "/switchWindow"), !isTestMode())) {
				//
				final Robot robot = new Robot();
				//
				robot.keyPress(KeyEvent.VK_ALT);
				//
				robot.keyPress(KeyEvent.VK_TAB);
				//
				robot.keyRelease(KeyEvent.VK_TAB);
				//
				robot.keyRelease(KeyEvent.VK_ALT);
				//
			} // if
				//
		} catch (final Exception e) {
			//
			throw new ServletException(e);
			//
		} // try
			//
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

	private static void setHeader(final HttpServletResponse instance, final String name, final String value) {
		if (instance != null) {
			instance.setHeader(name, value);
		}
	}

	private static Map<String, String[]> getParameterMap(final ServletRequest instance) {
		return instance != null ? instance.getParameterMap() : null;
	}

	private static String getServletPath(final HttpServletRequest instance) {
		return instance != null ? instance.getServletPath() : null;
	}

	private static ServletOutputStream getOutputStream(final ServletResponse instance) throws IOException {
		return instance != null ? instance.getOutputStream() : null;
	}

	private static <T> boolean allMatch(final Stream<T> instance, final Predicate<T> predicate) {
		return instance != null && instance.allMatch(predicate);
	}

	private static void write(final OutputStream instance, final byte[] bs) throws IOException {
		if (instance != null) {
			instance.write(bs);
		}
	}

	private static String getParameter(final ServletRequest instance, final String parameter) {
		return instance != null ? instance.getParameter(parameter) : null;
	}

	private static boolean containsKey(final Map<?, ?> instance, final Object key) {
		return instance != null && instance.containsKey(key);
	}

	private static List<HWND> findHWNDListByTitle(final String title) {
		//
		final List<HWND> list = new ArrayList<>();
		//
		final User32 user32 = Objects.equals("sun.nio.fs.WindowsFileSystem",
				getName(getClass(FileSystems.getDefault()))) ? User32.INSTANCE : null;
		//
		if (user32 != null) {
			//
			user32.EnumWindows(new WNDENUMPROC() {

				@Override
				public boolean callback(final HWND hWnd, final Pointer arg1) {
					//
					final char[] windowText = new char[512];
					//
					user32.GetWindowText(hWnd, windowText, windowText.length);
					//
					if (Objects.equals(Native.toString(windowText), title)) {
						//
						list.add(hWnd);
						//
					} // if
						//
					return true;
					//
				}

			}, null);
			//
		} // if
			//
		return list;
		//
	}

	private static Class<?> getClass(final Object instance) {
		return instance != null ? instance.getClass() : null;
	}

	private static String getName(final Class<?> instance) {
		return instance != null ? instance.getName() : null;
	}

}