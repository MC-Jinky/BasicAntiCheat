package me.jinky.checks;

public class CheckResult {

	private String name;
	private Boolean pf;

	public CheckResult(String CheckName, Boolean passed) {
		name = CheckName;
		pf = passed;
	}

	public boolean passed() {
		return pf;
	}

	public String getCheckName() {
		return name;
	}
}
