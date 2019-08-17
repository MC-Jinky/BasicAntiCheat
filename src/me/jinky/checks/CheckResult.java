package me.jinky.checks;

public class CheckResult {

	private String name;
	private Boolean pf;
	private String desc;

	public CheckResult(String CheckName, Boolean passed, String description) {
		name = CheckName;
		pf = passed;
		this.desc = description;
	}

	public String getDesc() {
		return this.desc;
	}

	public boolean passed() {
		return pf;
	}

	public String getCheckName() {
		return name;
	}
}
