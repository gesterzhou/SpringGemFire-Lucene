/*
 * Copyright 2012 VMWare.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vmware.example.lucene.loader;

import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;


/**
 * Boiler plate code to execute Spring-Batch job definitions.
 * 
 * @author Lyndon Adams
 *
 */
@Component
public class CacheBatchLoader {

	private static final Logger logger = LoggerFactory.getLogger(CacheBatchLoader.class);

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	@Qualifier("batchLoaderJob")
	private Job job;

	@Autowired
	@Qualifier("batchResources")
	Properties batchResources;
	

	public void start(){
		logger.info("Launching batch loading.....");

		long time = System.currentTimeMillis();
		
		try {
			JobParametersBuilder paramsBuilder = new JobParametersBuilder();
			
			for(Entry<Object, Object> e : batchResources.entrySet()){
				paramsBuilder.addString( (String)e.getKey(), (String) e.getValue());
			}
			
			jobLauncher.run(job, paramsBuilder.toJobParameters());

			System.out.println("All data loaded.");

			
		} catch (JobExecutionAlreadyRunningException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JobRestartException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JobInstanceAlreadyCompleteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JobParametersInvalidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}

		time = System.currentTimeMillis() - time;
		
		System.out.println(String.format("Completed batch loading (%d)", time) );
	}

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("cacheloading-app-context.xml");
		CacheBatchLoader batchLoader = context.getBean(CacheBatchLoader.class);
		batchLoader.start();
	}
}
