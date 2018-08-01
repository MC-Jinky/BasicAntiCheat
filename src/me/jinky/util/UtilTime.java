package me.jinky.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilTime {
	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT_DAY = "yyyy-MM-dd";

	public static String now() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(cal.getTime());
	}

	public static long parseDateDiff(String time, boolean future) {
		Matcher m = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?"
				+ "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?"
				+ "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?"
				+ "(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE).matcher(time);
		int years = 0;
		int months = 0;
		int weeks = 0;
		int days = 0;
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
		boolean found = false;
		while (m.find()) {
			if (m.group() == null || m.group().isEmpty()) {
				continue;
			}
			for (int i = 0; i < m.groupCount(); i++) {
				if (m.group(i) != null && !m.group(i).isEmpty()) {
					found = true;
					break;
				}
			}
			if (found) {
				if (m.group(1) != null && !m.group(1).isEmpty()) {
					years = Integer.parseInt(m.group(1));
				}
				if (m.group(2) != null && !m.group(2).isEmpty()) {
					months = Integer.parseInt(m.group(2));
				}
				if (m.group(3) != null && !m.group(3).isEmpty()) {
					weeks = Integer.parseInt(m.group(3));
				}
				if (m.group(4) != null && !m.group(4).isEmpty()) {
					days = Integer.parseInt(m.group(4));
				}
				if (m.group(5) != null && !m.group(5).isEmpty()) {
					hours = Integer.parseInt(m.group(5));
				}
				if (m.group(6) != null && !m.group(6).isEmpty()) {
					minutes = Integer.parseInt(m.group(6));
				}
				if (m.group(7) != null && !m.group(7).isEmpty()) {
					seconds = Integer.parseInt(m.group(7));
				}
				break;
			}
		}
		if (!found) {
			return 0;
		}
		Calendar c = new GregorianCalendar();
		if (years > 0) {
			c.add(Calendar.YEAR, years * (future ? 1 : -1));
		}
		if (months > 0) {
			c.add(Calendar.MONTH, months * (future ? 1 : -1));
		}
		if (weeks > 0) {
			c.add(Calendar.WEEK_OF_YEAR, weeks * (future ? 1 : -1));
		}
		if (days > 0) {
			c.add(Calendar.DAY_OF_MONTH, days * (future ? 1 : -1));
		}
		if (hours > 0) {
			c.add(Calendar.HOUR_OF_DAY, hours * (future ? 1 : -1));
		}
		if (minutes > 0) {
			c.add(Calendar.MINUTE, minutes * (future ? 1 : -1));
		}
		if (seconds > 0) {
			c.add(Calendar.SECOND, seconds * (future ? 1 : -1));
		}
		Calendar max = new GregorianCalendar();
		max.add(Calendar.YEAR, 10);
		if (c.after(max)) {
			return max.getTimeInMillis();
		}
		return c.getTimeInMillis();
	}

	public static String when(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(Long.valueOf(time));
	}

	public static String date() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(cal.getTime());
	}

	public static int toms(int seconds) {
		return seconds * 1000;
	}

	public static enum TimeUnit {
		FIT, DAYS, HOURS, MINUTES, SECONDS, MILLISECONDS;
	}

	public static String left(long epoch) {
		return convertString(epoch - System.currentTimeMillis(), 1, TimeUnit.FIT);
	}

	public static String since(long epoch) {
		return convertString(System.currentTimeMillis() - epoch, 1, TimeUnit.FIT);
	}

	public static double convert(long time, int trim, TimeUnit type) {
		if (type == TimeUnit.FIT) {
			if (time < 60000L) {
				type = TimeUnit.SECONDS;
			} else if (time < 3600000L) {
				type = TimeUnit.MINUTES;
			} else if (time < 86400000L)
				type = TimeUnit.HOURS;
			else {
				type = TimeUnit.DAYS;
			}
		}
		if (type == TimeUnit.DAYS)
			return UtilMath.trim(trim, time / 86400000.0D);
		if (type == TimeUnit.HOURS)
			return UtilMath.trim(trim, time / 3600000.0D);
		if (type == TimeUnit.MINUTES)
			return UtilMath.trim(trim, time / 60000.0D);
		if (type == TimeUnit.SECONDS)
			return UtilMath.trim(trim, time / 1000.0D);
		return UtilMath.trim(trim, time);
	}

	public static String MakeStr(long time) {
		return convertString(time, 1, TimeUnit.FIT);
	}

	public static String MakeStr(long time, int trim) {
		return convertString(time, trim, TimeUnit.FIT);
	}

	public static String convertString(long time, int trim, TimeUnit type) {
		if (time == -1L) {
			return "Permanent";
		}
		if (type == TimeUnit.FIT) {
			if (time < 60000L) {
				type = TimeUnit.SECONDS;
			} else if (time < 3600000L) {
				type = TimeUnit.MINUTES;
			} else if (time < 86400000L)
				type = TimeUnit.HOURS;
			else {
				type = TimeUnit.DAYS;
			}
		}
		if (type == TimeUnit.DAYS)
			return UtilMath.trim(trim, time / 86400000.0D) + " Days";
		if (type == TimeUnit.HOURS)
			return UtilMath.trim(trim, time / 3600000.0D) + " Hours";
		if (type == TimeUnit.MINUTES)
			return UtilMath.trim(trim, time / 60000.0D) + " Minutes";
		if (type == TimeUnit.SECONDS)
			return UtilMath.trim(trim, time / 1000.0D) + " Seconds";
		return UtilMath.trim(trim, time) + " Milliseconds";
	}

	public static boolean elapsed(long from, long required) {
		return System.currentTimeMillis() - from > required;
	}
}
