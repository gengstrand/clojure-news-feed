package info.glennengstrand.resources;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.threeten.bp.OffsetDateTime;

public interface DateAware {
	static SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ssXXX");
	default OffsetDateTime convert(long time) {
		return OffsetDateTime.parse(sdf.format(new Date(time)));
	}
}
