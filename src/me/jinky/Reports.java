package me.jinky;

import java.util.List;
import java.util.Random;

public class Reports {

	public static int saveReport(List<String> r) {
		int id = getNextReportID();
		Cenix.getCenix().getConfig().set("Reports." + id, r);
		Cenix.getCenix().saveConfig();
		return id;
	}

	private static int getNextReportID() {
		int id = new Random().nextInt(9000) + 1000;
		if (Cenix.getCenix().getConfig().contains("Reports." + id)) {
			return getNextReportID();
		} else {
			return id;
		}
	}
}
