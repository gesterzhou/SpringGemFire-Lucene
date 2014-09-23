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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.gemstone.gemfire.LogWriter;
import com.gemstone.gemfire.cache.Cache;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.execute.FunctionAdapter;
import com.gemstone.gemfire.cache.execute.FunctionContext;
import com.gemstone.gemfire.cache.execute.RegionFunctionContext;
import com.gemstone.gemfire.cache.execute.ResultSender;
import com.vmware.demo.sgf.lucene.LuceneGemFireRepository;
import com.vmware.demo.sgf.lucene.LuceneIndexRepoFactory;
import com.vmware.demo.sgf.lucene.LuceneIndexService;
import com.vmware.demo.sgf.lucene.domain.SearcheCriteria;

/**
 * Get the number of found items using the <code>SearcheCriteria</code>
 * 
 * @author Lyndon Adams
 *
 */
public class SearchCountItems extends FunctionAdapter {
	
	public static final String ID = SearchCountItems.class.getSimpleName();
		

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
		
		
		List results  = null;
		try {			
			LuceneGemFireRepository repo = LuceneIndexService.getRegionIndex( regionPath );
			if( repo != null ) 
				results = (List)repo.findAll( sc );
		}catch(Exception e){
			e.printStackTrace();
			rs.sendException( e );
		}finally {
			rs.lastResult(  ( results != null ) ? results.size() : 0 );
		}
	}
	
	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.execute.Function#optimizeForWrite()
	 */
	@Override
	public boolean optimizeForWrite() {
		return true;
	}
	

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.execute.FunctionAdapter#getId()
	 */
	@Override
	public String getId() {
		return ID;
	}

}
