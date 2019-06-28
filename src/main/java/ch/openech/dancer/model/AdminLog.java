package ch.openech.dancer.model;

import java.time.LocalDateTime;

import org.minimalj.model.Keys;
import org.minimalj.model.annotation.NotEmpty;
import org.minimalj.model.annotation.Size;

public class AdminLog {
	public static final AdminLog $ = Keys.of(AdminLog.class);

	public AdminLog() {
		//
	}

	public AdminLog(AdminLogType logType, String msg) {
		this.logType = logType;
		this.msg = msg;
	}

	public Object id;

	@Size(Size.TIME_WITH_SECONDS) @NotEmpty
	public LocalDateTime dateTime = LocalDateTime.now();

	@NotEmpty
	public AdminLogType logType;

	@Size(1000) @NotEmpty
	public String msg;

	public static enum AdminLogType {
		IMPORT, LOGIN, MESSAGE;
	}
}
