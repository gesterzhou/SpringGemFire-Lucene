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
package com.vmware.demo.sgf.lucene.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.gemstone.gemfire.cache.execute.FunctionException;
import com.gemstone.gemfire.cache.execute.ResultCollector;
import com.gemstone.gemfire.distributed.DistributedMember;
import com.vmware.demo.sgf.lucene.client.StreamResultCallback;

/**
 * @author Lyndon Adams
 *
 */
public class SearchResultCollector implements ResultCollector {

	List results = new ArrayList();

	StreamResultCallback callback;
	CountDownLatch latch = null; 

	public SearchResultCollector(){
		latch = new CountDownLatch( 1 );
	}

	public SearchResultCollector(final StreamResultCallback callback){
		this();
		this.callback = callback;
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.execute.ResultCollector#getResult()
	 */
	@Override
	public Object getResult() throws FunctionException {
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return results;
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.execute.ResultCollector#getResult(long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public Object getResult(long paramLong, TimeUnit timeUnit)
			throws FunctionException, InterruptedException {

		latch.await( paramLong, timeUnit);
		return results;
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.execute.ResultCollector#addResult(com.gemstone.gemfire.distributed.DistributedMember, java.lang.Object)
	 */
	@Override
	public void addResult(DistributedMember paramDistributedMember, Object o) {
		if( o != null) {
			results.add( o );

			// Stream result to callback function
			if(callback != null ) 
				callback.onEvent(o);
		}
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.execute.ResultCollector#endResults()
	 */
	@Override
	public void endResults() {
		latch.countDown();

		// Send completion signal to callback function
		if(callback != null ) 
			callback.onComplete();

	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.execute.ResultCollector#clearResults()
	 */
	@Override
	public void clearResults() {
		// Clear the result set in the event of a function restart
		results.clear();
	}
}
