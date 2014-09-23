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
package com.vmware.demo.sgf.lucene;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;


/**
 * Marker interface for a lucene search repository
 * 
 * @author Lyndon Adams
 * @param <K>
 *
 */
public interface LuceneGemFireRepository<LuceneKey, ID extends Serializable> extends CrudRepository<LuceneKey, ID> {
	
	/**
	 * Close the repository resources.
	 */
	public void close();
	
	
	Iterable findAll(Serializable id) throws IllegalArgumentException;
}