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

import java.util.List;
import java.util.Properties;

import com.gemstone.gemfire.LogWriter;
import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.Function;
import com.gemstone.gemfire.cache.execute.FunctionContext;
import com.gemstone.gemfire.cache.execute.RegionFunctionContext;
import com.gemstone.gemfire.cache.execute.ResultSender;
import com.gemstone.gemfire.cache.partition.PartitionRegionHelper;
import com.gemstone.gemfire.internal.cache.PartitionedRegion;
import com.vmware.demo.sgf.lucene.LuceneGemFireRepository;
import com.vmware.demo.sgf.lucene.LuceneIndexService;
import com.vmware.demo.sgf.lucene.domain.SearcheCriteria;

/**
 * Search function to utilise the Lucene indexes.
 * 
 * @author Lyndon Adams
 *
 */
public class LuceneSearchFunction implements Function, Declarable {

	public static final String ID = LuceneSearchFunction.class.getSimpleName();

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.execute.FunctionAdapter#execute(com.gemstone.gemfire.cache.execute.FunctionContext)
	 */
	@Override
	public void execute(FunctionContext context) {
		RegionFunctionContext ctx = (RegionFunctionContext)context;
		ResultSender rs = context.getResultSender();
		Cache cache = CacheFactory.getAnyInstance();
		LogWriter logger = cache.getLogger();
		
		String regionPath = ctx.getDataSet().getFullPath();
		logger.info("Executing search count function on " + regionPath);
		
		SearcheCriteria sc = (SearcheCriteria) ctx.getArguments();
		
		boolean hasSuceeded = true;
		Object foundValue = null;
		try {
			LuceneGemFireRepository repo = LuceneIndexService.getRegionIndex( regionPath );
			Region efficientReader =  ( ctx.getDataSet() instanceof PartitionedRegion ) ? PartitionRegionHelper.getLocalData( ctx.getDataSet() ) : ctx.getDataSet();			

			// Stream the results back				
			List keys = (List) repo.findAll( sc );
			if( keys != null && keys.size() > 0){

				for(int i=0; i<keys.size()-1 ;i++){
					foundValue = efficientReader.get( keys.get(i) );
					if( foundValue != null ) {	
						rs.sendResult( foundValue );
					}
				}

				// Get last value
				foundValue = efficientReader.get( keys.get( keys.size()-1) );
				rs.lastResult( foundValue );
			} else {
				rs.lastResult( null ); 
			}

		}catch(Exception e){
			e.printStackTrace();
			rs.sendException( e );
			hasSuceeded = false;			
		} finally {
			if( !hasSuceeded ) rs.lastResult( null ); 
		}
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.execute.FunctionAdapter#getId()
	 */
	@Override
	public String getId() {
		return ID;
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.execute.Function#hasResult()
	 */
	@Override
	public boolean hasResult() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.execute.Function#optimizeForWrite()
	 */
	@Override
	public boolean optimizeForWrite() {
		// Ensure this function is executed on all JVM that host primary data.
		return true;
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.execute.Function#isHA()
	 */
	@Override
	public boolean isHA() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.Declarable#init(java.util.Properties)
	 */
	@Override
	public void init(Properties props) {}
}
