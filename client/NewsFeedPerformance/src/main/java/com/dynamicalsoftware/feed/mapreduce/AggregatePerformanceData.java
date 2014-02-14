package com.dynamicalsoftware.feed.mapreduce;

/*
Copyright 2013 Dynamical Software, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;

public class AggregatePerformanceData {

	private static Logger log = Logger.getLogger(AggregatePerformanceData.class.getCanonicalName());

	public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {

		public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
			String line = value.toString();
			String[] lp = line.split("\\|");
			if (lp.length == 8) {
				Text tk = new Text();
				tk.set(MessageFormat.format("{0}-{1}-{2} {3}:{4}", new Object[]{lp[0], lp[1], lp[2], lp[3], lp[4]}));
				Text tv = new Text();
				tv.set(MessageFormat.format("{0}|{1}|{2}", new Object[]{lp[5], lp[6], lp[7]}));
				output.collect(tk, tv);
			} else {
				log.warning("invalid format: ".concat(line));
			}
		}
		
	}

	public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text> {
		
		
		public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
			java.util.Map<String, List<Long>> durations = new HashMap<String, List<Long>>();
			while (values.hasNext()) {
				Text t = values.next();
				if (t != null) {
					String d = t.toString();
					if (d != null) {
						String[] dp = d.split("\\|");
						if (dp.length == 3) {
							String dk = MessageFormat.format("{0}|{1}", new Object[]{dp[0], dp[1]});
							try {
								if (durations.containsKey(dk)) {
									durations.get(dk).add(Long.parseLong(dp[2]));
								} else {
									List<Long> duration = new ArrayList<Long>();
									duration.add(Long.parseLong(dp[2]));
									durations.put(dk, duration);
								}
							} catch (NumberFormatException nfe) {
								log.log(Level.WARNING, MessageFormat.format("Expected {0} to be a number.\n", new Object[]{dp[2]}), nfe);
							}
						} else {
							log.warning("invalid format: ".concat(d));
						}
					}
				}
			}
			StringBuffer rpt = new StringBuffer();
			boolean first = true;
			for (String dk : durations.keySet()) {
				List<Long> duration = durations.get(dk);
				Collections.sort(duration);
				if (first) {
					first = false;
				} else {
					rpt.append(":");
				}
				rpt.append(dk);
				rpt.append("=");
				rpt.append(new Integer(duration.size()).toString());
				rpt.append(",");
				rpt.append(duration.get(duration.size() / 2).toString());
				rpt.append(",");
				rpt.append(duration.get(duration.size() * 95 / 100).toString());				
			}
			Text tv = new Text();
			tv.set(rpt.toString());
			output.collect(key, tv);
		}
		
	}

	public static void main(String[] args) throws Exception {
		if (args.length >= 2) {
			JobConf conf = new JobConf(AggregatePerformanceData.class);
			conf.setJobName("aggregate news feed performance data");
			conf.setOutputKeyClass(Text.class);
			conf.setOutputValueClass(Text.class);
			conf.setMapperClass(AggregatePerformanceData.Map.class);
			conf.setReducerClass(AggregatePerformanceData.Reduce.class);
			conf.setInputFormat(TextInputFormat.class);
			conf.setOutputFormat(TextOutputFormat.class);
			FileInputFormat.setInputPaths(conf, new Path(args[0]));
			FileOutputFormat.setOutputPath(conf, new Path(args[1]));
			JobClient.runJob(conf);
		} else {
			System.err.println("\nusage: AggregatePerformanceData input_directory output_directory\n");
		}
	}
	
}
