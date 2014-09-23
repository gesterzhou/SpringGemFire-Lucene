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
package com.vmware.demo.sgf.lucene.impl;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.SimpleFSLockFactory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

import com.gopivotal.gemfire.utils.utils.serializers.ObjectSerializer;
import com.vmware.demo.sgf.lucene.LuceneGemFireRepository;
import com.vmware.demo.sgf.lucene.annotations.Searchable;
import com.vmware.demo.sgf.lucene.domain.SearchableGemFireEntity;
import com.vmware.demo.sgf.lucene.domain.SearcheCriteria;

/**
 * Implementation of the LuceneGemFire repository.
 * 
 * @author Lyndon Adams
 * @param <T>
 * @param <T>
 * 
 */
public class LuceneGemFireRepositoryImpl implements LuceneGemFireRepository {

	final static String GEMFIRE_KEY = "gemfireKey".intern();
	final static String savedField = "saved".intern();
	final static String savedFieldValue = "yes".intern();
	
	String defaultFeild;

	FSDirectory directory;
	Analyzer analyzer;
	KeywordAnalyzer keyWordAnalyser;
	IndexWriter indexWriter;

	private boolean hasInitialized = false;

	// Annotated search fields
	List<java.lang.reflect.Field> searchableFields;

	/**
	 * Searching API's
	 */
	SearcherManager searchManager;
	boolean applyAllDeletes = true;

	String filepath;

	// Default memory size for in memory index.
	// Increase this if large indexes are required and ensure JVM setting are
	// amended.
	double ramsize = 64.0;

	public LuceneGemFireRepositoryImpl() {
		super();
	}

	public LuceneGemFireRepositoryImpl(final String filepath) {
		this();
		this.filepath = filepath;
		initialize();
	}

	public LuceneGemFireRepositoryImpl(final String filepath,
			final double ramsize) {
		this.filepath = filepath;
		this.ramsize = ramsize;
		initialize();
	}

	/**
	 * Set the file path for a memory mapped directory.
	 * 
	 * @param filepath
	 */
	public void setFilePath(final String filepath) {
		this.filepath = filepath;
	}

	public void setRamSize(double ramsize) {
		this.ramsize = ramsize;
	}

	/**
	 * Set up stores for indexes
	 */
	public void initialize() {
		if (!hasInitialized) {
			try {
				directory = new NIOFSDirectory(new File(filepath),
						new SimpleFSLockFactory());
				analyzer = new StandardAnalyzer(Version.LUCENE_40);

				IndexWriterConfig conf = new IndexWriterConfig(
						Version.LUCENE_40, analyzer);
				conf.setRAMBufferSizeMB(ramsize);
				conf.setOpenMode(OpenMode.CREATE_OR_APPEND);
				conf.setWriteLockTimeout(2000);
				indexWriter = new IndexWriter(directory, conf);

				// Open a reader/searcher
				searchManager = new SearcherManager(indexWriter,
						applyAllDeletes, new SearcherFactory());

				hasInitialized = true;

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * Set up reflection template for searchable fields.
	 * 
	 * @param o - Value object to work from.
	 */
	private boolean setupAttributeReflection(Object o) {
		Class clzz = o.getClass();

		List<java.lang.reflect.Field> foundFields = new ArrayList<java.lang.reflect.Field>();

		for (java.lang.reflect.Field f : clzz.getDeclaredFields()) {
			Searchable sf = f.getAnnotation(Searchable.class);
			if (sf != null && f.getType().equals(String.class)) {
				f.setAccessible(true);
				foundFields.add(f);
			}
		}
		searchableFields = ((foundFields.size() > 0) ? foundFields : null);
		return (searchableFields != null) ? true : false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.repository.CrudRepository#save(java.lang.Object)
	 */
	@Override
	public Object save(Object entity) {
		if (!(entity instanceof SearchableGemFireEntity))
			throw new IllegalArgumentException("Can only accept SearchableGemFireEntity data type for this repository.");
		SearchableGemFireEntity gfEntity = (SearchableGemFireEntity) entity;

		if (searchableFields == null)
			setupAttributeReflection(gfEntity.getValue());

		try {
			Document doc = new Document();

			// Add reflected fields to document iff search value is not null
			for (java.lang.reflect.Field f : searchableFields) {
				String fieldName = f.getName().intern();
				String searchText = (String) f.get(gfEntity.getValue());

				if (searchText != null)
					doc.add(new Field(fieldName, searchText,
							TextField.TYPE_STORED));
			}

			doc.add( new Field(savedField, savedFieldValue, TextField.TYPE_STORED));
			doc.add(new StoredField(GEMFIRE_KEY, new BytesRef( ObjectSerializer.serialize(gfEntity.getKey())))); 
			indexWriter.addDocument(doc);
			searchManager.maybeRefresh();
			indexWriter.commit();
		} catch (IOException e) {
			gfEntity = null;
			e.printStackTrace();
		} catch (Exception e) {
			gfEntity = null;
			e.printStackTrace();
		}
		return gfEntity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.repository.CrudRepository#save(java.lang.Iterable
	 * )
	 */
	@Override
	public Iterable save(Iterable entities) {
		boolean failed = false;

		try {
			for (Object entity : entities) {

				if (!(entity instanceof SearchableGemFireEntity))
					throw new IllegalArgumentException("Can only accept SearchableGemFireEntity data type for this repository.");
				SearchableGemFireEntity gfEntity = (SearchableGemFireEntity) entity;

				if (searchableFields == null)
					setupAttributeReflection(gfEntity.getValue());

				Document doc = new Document();

				// Add reflected fields to document iff search value is not null
				for (java.lang.reflect.Field f : searchableFields) {
					String fieldName = f.getName().intern();
					String searchText = (String) f.get(gfEntity.getValue());

					if (searchText != null)
						doc.add(new Field(fieldName, searchText,
								TextField.TYPE_STORED));
				}

				doc.add( new Field(savedField, savedFieldValue,TextField.TYPE_STORED));
				doc.add(new StoredField(GEMFIRE_KEY, new BytesRef(
						ObjectSerializer.serialize(gfEntity.getKey())))); // StraightBytesDocValuesField
				indexWriter.addDocument(doc);
			}
			searchManager.maybeRefresh();
			indexWriter.commit();

		} catch (IOException e) {
			e.printStackTrace();
			try {
				indexWriter.rollback();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			failed = true;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				indexWriter.rollback();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			failed = true;
		}
		return (failed) ? null : entities;
	}

	private Iterable searchStoreForMultipleItems(Serializable id,
			boolean singleRecordRequired) {
		ArrayList results = new ArrayList();

		IndexSearcher searcher = searchManager.acquire();
		try {
			// Extract search criteria
			String field = defaultFeild;
			List<String> searchItems = null;
			if (id instanceof SearcheCriteria) {
				SearcheCriteria key = (SearcheCriteria) id;
				field = key.getSearchField();
				searchItems = key.getSearchItems();
			}

			QueryParser parser = new QueryParser(Version.LUCENE_40, field,
					analyzer);

			for (String searchText : searchItems) {
				Query query = parser.parse(searchText);
				TopDocs docs = searcher.search(query, Integer.MAX_VALUE);

				if (docs.totalHits > 0) {
					for (int i = 0; i < docs.totalHits; i++) {
						ScoreDoc hit = docs.scoreDocs[i];
						Document doc = searcher.doc(hit.doc);
						Object gfKey = ObjectSerializer.deserialize(doc.getBinaryValue(GEMFIRE_KEY).bytes);

						results.add(gfKey);
						if (singleRecordRequired)
							break;
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				searchManager.release( searcher );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		searcher = null;

		return results;
	}

	private Iterable searchStore(Serializable id, boolean singleRecordRequired) {
		ArrayList results = new ArrayList();

		IndexSearcher searcher = searchManager.acquire();
		try {
			// Extract search criteria
			String field = defaultFeild;
			String searchText = null;
			if (id instanceof SearcheCriteria) {
				SearcheCriteria key = (SearcheCriteria) id;
				field = key.getSearchField();
				searchText = key.getSearchText();
			} else if (id instanceof String) {
				searchText = (String) id;
			}

			QueryParser parser = new QueryParser(Version.LUCENE_40, field, analyzer);
			Query query = parser.parse(searchText);
			TopDocs docs = searcher.search(query, Integer.MAX_VALUE);

			if (docs.totalHits > 0) {
				for (int i = 0; i < docs.totalHits; i++) {
					ScoreDoc hit = docs.scoreDocs[i];

					Document doc = searcher.doc(hit.doc);
					Object gfKey = ObjectSerializer.deserialize(doc.getBinaryValue(GEMFIRE_KEY).bytes);

					results.add(gfKey);

					if (singleRecordRequired)
						break;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				searchManager.release( searcher );
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		searcher = null;
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.repository.CrudRepository#findOne(java.io.
	 * Serializable)
	 */
	@Override
	public Object findOne(Serializable id) {
		List lst = (List) searchStore(id, true);
		return (lst.size() > 0) ? lst.get(0) : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.repository.CrudRepository#exists(java.io.
	 * Serializable)
	 */
	@Override
	public boolean exists(Serializable id) {
		List lst = (List) searchStore(id, true);
		return (lst.size() > 0) ? true : false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.repository.CrudRepository#findAll()
	 */
	@Override
	public Iterable findAll() {
		ArrayList results = new ArrayList();
		
		IndexSearcher searcher = searchManager.acquire();
		try {			
			QueryParser parser = new QueryParser(Version.LUCENE_40, savedField, analyzer);
			Query query = parser.parse( savedFieldValue);
			TopDocs docs = searcher.search(query, Integer.MAX_VALUE);

			if (docs.totalHits > 0) {
				for (int i = 0; i < docs.totalHits; i++) {
					ScoreDoc hit = docs.scoreDocs[i];

					Document doc = searcher.doc(hit.doc);
					Object gfKey = ObjectSerializer.deserialize(doc.getBinaryValue(GEMFIRE_KEY).bytes);

					results.add(gfKey);
				}				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			try {
				searchManager.release( searcher );
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		searcher = null;
		return results;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.repository.CrudRepository#findAll(java.lang.
	 * Iterable)
	 */
	@Override
	public Iterable findAll(Iterable ids) {
		HashSet set = new HashSet();

		for (Object id : ids) {
			
			if (!(id instanceof SearcheCriteria)) throw new IllegalArgumentException("Require SearcheCriteria parameter.");
			List r = (List) searchStoreForMultipleItems((SearcheCriteria) id,false);

			// Concatenate results in to a unique set
			if (!r.isEmpty()) set.addAll(r);
		}
		return Arrays.asList(set);
	}

	@Override
	public Iterable findAll(Serializable id) throws IllegalArgumentException {
		if (!(id instanceof SearcheCriteria))
			throw new IllegalArgumentException( "Require SearcheCriteria parameter.");
		List r = (List) searchStoreForMultipleItems((SearcheCriteria) id, false);
		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.repository.CrudRepository#count()
	 */
	@Override
	public long count() {
		return (indexWriter != null) ? indexWriter.numDocs() : 0;
	}

	/**
	 * Helper method to encapsulate the deletion of a document.
	 * 
	 * @param entity
	 */
	private void deleteDocument(Object entity) {
		if (entity == null)
			throw new IllegalArgumentException("Null parameter is not allowed.");
		try {
			if (entity instanceof SearcheCriteria) {
				SearcheCriteria sc = (SearcheCriteria) entity;
				indexWriter.deleteDocuments(new Term(sc.getSearchField(), sc.getSearchText()));
				searchManager.maybeRefresh();
				indexWriter.commit();
			} else
				deleteDomainObject(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.repository.CrudRepository#delete(java.io.
	 * Serializable)
	 */
	@Override
	public void delete(Serializable id) {
		deleteDocument(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.repository.CrudRepository#delete(java.lang.Object
	 * )
	 */
	@Override
	public void delete(Object entity) {
		deleteDocument(entity);
	}

	/**
	 * Delete an index document using the raw entity used to create the
	 * document.
	 * 
	 * @param entity
	 */
	private void deleteDomainObject(Object entity) throws Exception {
		if (searchableFields == null)
			setupAttributeReflection(entity);
		for (java.lang.reflect.Field f : searchableFields) {
			Searchable s = f.getAnnotation(Searchable.class);
			if (s != null && s.isUnqiue()) {
				String fieldName = f.getName().intern();
				String searchText = (String) f.get(entity);

				if (searchText != null) {
					indexWriter.deleteDocuments(new Term(fieldName, searchText));
					searchManager.maybeRefresh();
					indexWriter.commit();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.data.repository.CrudRepository#delete(java.lang.Iterable
	 * )
	 */
	@Override
	public void delete(Iterable entities) {
		try {
			for (Object entity : entities) {
				if (entity == null)
					throw new IllegalArgumentException("Null parameter is not allowed.");

				if (entity instanceof SearcheCriteria) {
					SearcheCriteria sc = (SearcheCriteria) entity;
					indexWriter.deleteDocuments(new Term(sc.getSearchField(),sc.getSearchText()));
				} else
					deleteDomainObject(entity);
			}
			searchManager.maybeRefresh();
			indexWriter.commit();
		} catch (Exception e) {
			try {
				indexWriter.rollback();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.repository.CrudRepository#deleteAll()
	 */
	@Override
	public void deleteAll() {
		try {
			indexWriter.deleteAll();
			searchManager.maybeRefresh();
			indexWriter.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gopivotal.gemfire.utils.lucene.LuceneGemFireRepository#close()
	 */
	@Override
	public void close() {
		try {
			searchManager.close();
			indexWriter.close();
			FileUtils.deleteDirectory( new File(filepath) );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
