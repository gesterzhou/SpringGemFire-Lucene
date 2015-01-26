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
package com.vmware.example.lucene;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.vmware.demo.sgf.lucene.client.ClientClusterSearch;

/**
 * @author Lyndon Adams
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:app-context.xml"})
public class GetSearchCountTest {
	
	@Test
	public void getSearchCount(){
		long foundItems = ClientClusterSearch.searchCount("description", new String[]{"Acqu*"}, "/Instrument");
		System.out.println(String.format("\n------------- getSearchCount Items found %d -------------", foundItems));
	}

}

