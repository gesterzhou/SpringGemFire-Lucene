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

import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.scheduling.annotation.Async;

import com.gemstone.gemfire.cache.CacheListener;
import com.gemstone.gemfire.cache.Declarable;
import com.gemstone.gemfire.cache.EntryEvent;
import com.gemstone.gemfire.cache.Operation;
import com.gemstone.gemfire.cache.RegionEvent;
import com.vmware.demo.sgf.lucene.LuceneGemFireRepository;
import com.vmware.demo.sgf.lucene.LuceneIndexService;
import com.vmware.demo.sgf.lucene.domain.SearchableGemFireEntity;

/**
 * Lucene index update adapter. Index is updated in a asynch thread.
 *
 * @author Lyndon Adams
 * @param <K>
 * @param <V>
 *
 */
public class LuceneIndexWriter<K, V> implements CacheListener<K, V>, Declarable{
	
	final static String INDEX_PROPERTY_KEY = "lucence-dir";
	String regionPath;
	String indexDirectory= "./luceneIndex";
	
	LuceneGemFireRepository repo;
	
	ConcurrentLinkedQueue<SearchableGemFireEntity> eventList = new ConcurrentLinkedQueue<SearchableGemFireEntity>();
	
	public void setRepo(final LuceneGemFireRepository repo){
		this.repo = repo;		
	}
	
	
	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.Declarable#init(java.util.Properties)
	 */
	@Override
	public void init(Properties props) {}
	
	@Async
	private void processEvent(EntryEvent<K, V> event){
		
		// Register lucene indexes to central service.
		if( regionPath == null ) {
			LuceneIndexService.registerIndex( event.getRegion().getFullPath(), repo);
		}

		if( !event.isCallbackArgumentAvailable() ){
			Operation op = event.getOperation();
			if( op.isClear() ){
				
			} else if( op.isCreate() || op.isUpdate() ){
				repo.save( new SearchableGemFireEntity( event.getKey(), event.getNewValue()) );
			}else if( op.isDestroy() ){
				repo.delete( new SearchableGemFireEntity( event.getKey(), event.getNewValue()) );
			} else if( op.isInvalidate() ){
				repo.delete( new SearchableGemFireEntity( event.getKey(), event.getNewValue()) );
			}
		} 
	}


	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.CacheCallback#close()
	 */
	@Override
	public void close() {
		repo.close();
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.CacheListener#afterCreate(com.gemstone.gemfire.cache.EntryEvent)
	 */
	@Override
	public void afterCreate(EntryEvent<K, V> event) {
		processEvent( event );
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.CacheListener#afterUpdate(com.gemstone.gemfire.cache.EntryEvent)
	 */
	@Override
	public void afterUpdate(EntryEvent<K, V> event) {
		processEvent( event );
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.CacheListener#afterInvalidate(com.gemstone.gemfire.cache.EntryEvent)
	 */
	@Override
	public void afterInvalidate(EntryEvent<K, V> event) {
		processEvent( event );
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.CacheListener#afterDestroy(com.gemstone.gemfire.cache.EntryEvent)
	 */
	@Override
	public void afterDestroy(EntryEvent<K, V> event) {
		processEvent( event );
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.CacheListener#afterRegionInvalidate(com.gemstone.gemfire.cache.RegionEvent)
	 */
	@Override
	public void afterRegionInvalidate(RegionEvent<K, V> event) {
		repo.deleteAll();
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.CacheListener#afterRegionDestroy(com.gemstone.gemfire.cache.RegionEvent)
	 */
	@Override
	public void afterRegionDestroy(RegionEvent<K, V> event) {
		repo.deleteAll();
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.CacheListener#afterRegionClear(com.gemstone.gemfire.cache.RegionEvent)
	 */
	@Override
	public void afterRegionClear(RegionEvent<K, V> event) {
		repo.deleteAll();		
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.CacheListener#afterRegionCreate(com.gemstone.gemfire.cache.RegionEvent)
	 */
	@Override
	public void afterRegionCreate(RegionEvent<K, V> event) {
		LuceneIndexService.registerIndex( event.getRegion().getFullPath() , repo);
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.CacheListener#afterRegionLive(com.gemstone.gemfire.cache.RegionEvent)
	 */
	@Override
	public void afterRegionLive(RegionEvent<K, V> event) {}
	
}