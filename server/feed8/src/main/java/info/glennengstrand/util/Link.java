package info.glennengstrand.util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public abstract class Link {
	private static final Pattern participant = Pattern.compile("/participant/([0-9]+)");
	public static Long extractId(String value) {
		Matcher m = participant.matcher(value);
		if (m.matches()) {
			return Long.parseLong(m.group(1));
		} else {
			return Long.parseLong(value);
		}
	}
	public static String toLink(Long id) {
		return "/participant/".concat(String.valueOf(id));
	}
}
