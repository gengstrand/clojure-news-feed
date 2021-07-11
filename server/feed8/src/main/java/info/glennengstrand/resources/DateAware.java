package info.glennengstrand.resources;

import java.text.SimpleDateFormat;
import java.util.Date;

public interface DateAware {
	static SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
	default Date convert(long time) {
		return new Date(time);
	}
}
