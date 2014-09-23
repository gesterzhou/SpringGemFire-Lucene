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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.gemstone.gemfire.LogWriter;
import com.gemstone.gemfire.cache.CacheFactory;
import com.gemstone.gemfire.cache.Operation;
import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.execute.Function;
import com.gemstone.gemfire.cache.execute.FunctionContext;
import com.gemstone.gemfire.cache.execute.RegionFunctionContext;
import com.gemstone.gemfire.cache.execute.ResultSender;
import com.gemstone.gemfire.cache.partition.PartitionRegionHelper;
import com.vmware.demo.sgf.lucene.LuceneGemFireRepository;
import com.vmware.demo.sgf.lucene.LuceneIndexService;
import com.vmware.demo.sgf.lucene.domain.SearchableGemFireEntity;
import com.vmware.demo.sgf.lucene.domain.batch.BatchKeyValue;
import com.vmware.demo.sgf.lucene.domain.batch.BatchOperation;

/**
 * Batch load/delete function for lucene gemfire index system.
 * 
 * @author Lyndon Adams
 *
 */
public class LuceneBatchOperator implements Function, Declarable {
	
	public static final String ID = "LuceneBatchOperator";
	
	LogWriter logger;
	
	
	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.execute.Function#execute(com.gemstone.gemfire.cache.execute.FunctionContext)
	 */
	@Override
	public void execute(FunctionContext context) {
		if( logger == null ) logger = CacheFactory.getAnyInstance().getLogger();
		
		logger.info("Executing LuceneBatchOperator server function.");
		
		boolean isSuccess = true;
		
		RegionFunctionContext ctx = (RegionFunctionContext)context;
		ResultSender rs = ctx.getResultSender();
		
		// Target region to apply keys to.
		Region targetRegion = ctx.getDataSet();
		
		BatchOperation operation = (BatchOperation)ctx.getArguments();
		
		// Set of key/values to be applied to local JVM.
		Set<BatchKeyValue> keyValues = (Set<BatchKeyValue>) ctx.getFilter();
		
		// Get the local index for this given region
		LuceneGemFireRepository repo = LuceneIndexService.getRegionIndex( targetRegion.getFullPath() );
		
		if( PartitionRegionHelper.isPartitionedRegion(ctx.getDataSet() ))
			targetRegion = PartitionRegionHelper.getLocalData( ctx.getDataSet() );
		else 
			targetRegion = ctx.getDataSet();
				
		try {
			Map map = new HashMap();
			Map<Object,SearchableGemFireEntity> repoValues = new HashMap<Object,SearchableGemFireEntity>();
			
			// Now build maps
			for(BatchKeyValue e : keyValues){
				map.put( e.getKey(), e.getValue() );
				repoValues.put( e.getKey(), new SearchableGemFireEntity( e.getKey(), e.getValue() ));
			}
			
			Operation op = operation.getOperation();
		
			if( op.isClear() ){
				repo.delete( repoValues.values() );
				targetRegion.clear();
				
			} else if( op.isCreate() || op.isUpdate() ){			
				repo.save( repoValues.values() );
				targetRegion.putAll(map);
				
			}else if( op.isDestroy() ){
				repo.delete( repoValues.values() );
				for(Object key : map.keySet()){
					targetRegion.destroy(key);
				}
				
			} else if( op.isInvalidate() ){
				repo.delete( repoValues.values() );
				for(Object key : map.keySet()){
					targetRegion.invalidate(key);
				}
			}		
		}catch(Exception e){
			e.printStackTrace();
			isSuccess = false;
		} finally {
			rs.lastResult( isSuccess );
		}
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.execute.Function#getId()
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
	 * @see com.gemstone.gemfire.cache.execute.Function#isHA()
	 */
	@Override
	public boolean isHA() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.execute.Function#optimizeForWrite()
	 */
	@Override
	public boolean optimizeForWrite() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.Declarable#init(java.util.Properties)
	 */
	@Override
	public void init(Properties props) {
		logger = CacheFactory.getAnyInstance().getLogger();
	}
}
