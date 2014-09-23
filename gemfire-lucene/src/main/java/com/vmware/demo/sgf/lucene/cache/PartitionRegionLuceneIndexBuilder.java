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

import java.util.Map;
import java.util.Set;

import com.gemstone.gemfire.LogWriter;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.partition.PartitionListener;
import com.gemstone.gemfire.internal.cache.BucketRegion;
import com.gemstone.gemfire.internal.cache.PartitionedRegion;
import com.vmware.demo.sgf.lucene.LuceneGemFireRepository;
import com.vmware.demo.sgf.lucene.LuceneIndexService;
import com.vmware.demo.sgf.lucene.domain.SearchableGemFireEntity;

/**
 * @author Lyndon Adams
 *
 */
public class PartitionRegionLuceneIndexBuilder implements PartitionListener {
	
	LogWriter logger;
	PartitionedRegion pr;
	
	public PartitionRegionLuceneIndexBuilder(){}
	
	public PartitionRegionLuceneIndexBuilder( Region region){
		region = (PartitionedRegion) region;
		logger = region.getCache().getLogger();
	}
		
	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.partition.PartitionListener#afterPrimary(int)
	 */
	@Override
	public void afterPrimary(int bucketId) {
		logger.info(String.format("Bucket %d has been allcated to me due to a rebalance operation. Now adding the index entries for those keys.", bucketId));
		LuceneGemFireRepository repo = LuceneIndexService.getRegionIndex( pr.getFullPath() );
		int keysInserted = 0;
		
		if( repo != null){
			BucketRegion bucketRegion = pr.getDataStore().getLocalBucketById(bucketId);
			
			Set keySet = bucketRegion.keySet();
			Map<?,?> data = null;
			
			if( keySet!=null && keySet.size() > 0) 
				data = bucketRegion.getAll( keySet );
			
			if( data != null && data.size() > 0 ) {
				
				for(Map.Entry e :  data.entrySet() ) {
					repo.save( new SearchableGemFireEntity( e.getKey(), e.getValue()) );
				}
				keysInserted = data.size();
			}
			
			logger.info(String.format("Completed bucket %d index key insertion. %d keys inserted in to index.", bucketId, keysInserted ));
		} else {
			logger.info(String.format("Index repo not found for this region (%s).", pr.getFullPath()));
		}
		
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.partition.PartitionListener#afterRegionCreate(com.gemstone.gemfire.cache.Region)
	 */
	@Override
	public void afterRegionCreate(Region<?, ?> region) {
		pr = (PartitionedRegion) region;
		logger = region.getCache().getLogger();
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.partition.PartitionListener#afterBucketRemoved(int, java.lang.Iterable)
	 */
	@Override
	public void afterBucketRemoved(int bucketId, Iterable<?> keys) {
		logger.info(String.format("Bucket %d has been moved from me. Now removing the index entries for those keys.", bucketId));
		
		LuceneGemFireRepository repo = LuceneIndexService.getRegionIndex( pr.getFullPath() );
		if( repo != null){
			
			BucketRegion bucketRegion = pr.getDataStore().getLocalBucketById(bucketId);
			Set keySet = bucketRegion.keySet();
			
			Map<?,?> data = null;
			
			if( keySet!=null && keySet.size() > 0) 
				data = bucketRegion.getAll( keySet );
			
			if( data != null && data.size() > 0 ) {
				// NEED TO CREATE A SearchCriteria.
				for(Map.Entry e :  data.entrySet() ) {
					repo.delete( new SearchableGemFireEntity( e.getKey(), e.getValue()) );
				}
			}
			logger.info(String.format("Completed bucket %d index entries key removal.", bucketId));
		} else {
			logger.info(String.format("Index repo not found for this region (%s).", pr.getFullPath()));
		}
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.partition.PartitionListener#afterBucketCreated(int, java.lang.Iterable)
	 */
	@Override
	public void afterBucketCreated(int bucketId, Iterable<?> keys) {}

}
