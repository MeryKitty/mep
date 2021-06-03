package algorithm;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class Config {
	public static final ThreadMXBean THREAD_MX_BEAN = ManagementFactory.getThreadMXBean();
	public static final int M = 8;
	public static final float DELTA = 0.5F;
}
