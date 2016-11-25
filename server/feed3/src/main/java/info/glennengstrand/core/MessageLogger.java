package info.glennengstrand.core;

import java.util.Calendar;

/**
 * how to log Message information
 * @author glenn
 *
 * @param <P> type that holds the payload
 */
public abstract class MessageLogger<P> {

	/**
	 * format the Message information to be logged
	 * @param entity represents the main entity abstraction that this Message is about
	 * @param operation is the operation being performed on the entity
	 * @param payload holds the details of the message 
	 * @return what gets sent to the message broker
	 */
	protected String logRecord(String entity, LogOperation operation, P payload) {
		Calendar now = Calendar.getInstance();
		String ts = new Integer(now.get(Calendar.YEAR)).toString() + "|" + new Integer(now.get(Calendar.MONTH)).toString() + "|" + new Integer(now.get(Calendar.DAY_OF_MONTH)).toString() + "|" + new Integer(now.get(Calendar.HOUR_OF_DAY)).toString() + "|" + new Integer(now.get(Calendar.MINUTE)).toString();
		return ts + "|" + entity + "|" + operation.getTokenValue() + "|" + payload.toString();
	}

	/**
	 * log Message information
	 * @param entity represents the main entity abstraction that this Message is about
	 * @param operation is the operation being performed on the entity
	 * @param payload holds the details of the message 
	 */
	public abstract void log(String entity, LogOperation operation, P payload);
	
	/**
	 * indicates the type of operation being performed 
	 * @author glenn
	 *
	 */
	public static enum LogOperation {
		POST, GET, SEARCH;
		public String getTokenValue() {
			return this.name().toLowerCase();
		}
	}
	
	/**
	 * null logger for when the underlying message broker is not available
	 * @author glenn
	 *
	 */
	public static class DoNothingMessageLogger extends MessageLogger<Long> {

		@Override
		public void log(String entity, LogOperation operation, Long duration) {
		}
		
	}
}
