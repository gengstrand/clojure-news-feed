package com.dynamicalsoftware.support;

import java.lang.management.ManagementFactory;
import java.util.Timer;
import java.util.TimerTask;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;

import org.apache.log4j.Logger;

public class FeedPerformanceMBeanImpl extends StandardMBean implements FeedPerformanceMBean {

	private final PerformanceInfo currentMetrics = new PerformanceInfo();
	private final PerformanceInfo pendingMetrics = new PerformanceInfo();
	private final Timer timer = new Timer();
	private static final Logger log = Logger.getLogger(FeedPerformanceMBeanImpl.class.getCanonicalName());

	private long tooSlowThreshold = 1000l;
	
	public static void register(String serviceName, FeedPerformanceMBeanImpl bean) {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		try {
			ObjectName oname = new ObjectName(serviceName);
			mbs.registerMBean(bean, oname);
		} catch (MalformedObjectNameException e) {
			log.error("invalid object name", e);
		} catch (NullPointerException e) {
			log.error("missing name", e);
		} catch (InstanceAlreadyExistsException e) {
			log.error("JMX bean has already been registered", e);
		} catch (MBeanRegistrationException e) {
			log.error("error while registering mbean", e);
		} catch (NotCompliantMBeanException e) {
			log.error("problems with mbean being registered", e);
		}
	}
	
	public FeedPerformanceMBeanImpl(long slow, long period) {
		super(FeedPerformanceMBean.class, false);
		tooSlowThreshold = slow;
		timer.schedule(new ResetMetricsTask(), period, period);
	}
	
	public void logRun(long duration) {
		pendingMetrics.incrementDuration(duration);
	}
	
	public int getCount() {
		return currentMetrics.getCount();
	}

	public int getSlowCount() {
		return currentMetrics.getSlowCount();
	}
	
	public long getAvgLatency() {
		return currentMetrics.getAvgDuration();
	}
	
	class PerformanceInfo {
		private int count = 0;
		private int slow = 0;
		private long duration = 0l;
		public int getCount() {
			return count;
		}
		public int getSlowCount() {
			return slow;
		}
		public void incrementDuration(long more) {
			duration += more;
			count++;
			if (more > tooSlowThreshold) {
				slow++;
			}
		}
		public long getDuration() {
			return duration;
		}
		public long getAvgDuration() {
			long retVal = 0l;
			if (count > 0) {
				retVal = duration / count;
			}
			return retVal;
		}
		public void reset() {
			count = 0;
			slow = 0;
			duration = 0l;
		}
		public void reset(PerformanceInfo other) {
			count = other.getCount();
			slow = other.getSlowCount();
			duration = other.getDuration();
		}
	}
	
	class ResetMetricsTask extends TimerTask {

		@Override
		public void run() {
			currentMetrics.reset(pendingMetrics);
			pendingMetrics.reset();
		}
		
	}

}
