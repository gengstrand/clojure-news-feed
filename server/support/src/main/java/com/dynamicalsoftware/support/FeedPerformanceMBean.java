package com.dynamicalsoftware.support;

public interface FeedPerformanceMBean {
	public int getCount();
	public int getSlowCount();
	public long getAvgLatency();
}
