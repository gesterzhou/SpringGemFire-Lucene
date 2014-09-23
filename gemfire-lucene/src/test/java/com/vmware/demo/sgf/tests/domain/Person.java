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
package com.vmware.demo.sgf.tests.domain;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.gemfire.mapping.Region;

import com.gemstone.gemfire.DataSerializable;
import com.gemstone.gemfire.DataSerializer;
import com.gemstone.gemfire.Instantiator;
import com.vmware.demo.sgf.lucene.annotations.Searchable;


/**
 * @author Lyndon Adams
 *
 */
@Region("Person")
public class Person implements DataSerializable {  

	static {
		Instantiator.register(new Instantiator(Person.class, 18) {
			 public DataSerializable newInstance() {
			  return new Person();
			 }
		});
	}
	 
	@Searchable(isUnqiue=true)
	Integer Id;
	
	String firstname;
	
	@Searchable
	String surname;
	
	@Searchable
	Date dateofBirth;
	
	@Searchable
	String zipcode;
	
	public Person() {
		super();
	}

	//@PersistenceConstructor
	public Person(int id, String firstname, String surname, Date dateofBirth) {
		super();
		this.Id = id;
		this.firstname = firstname;
		this.surname = surname;
		this.dateofBirth = dateofBirth;
	}

	public Integer getId() {
		return Id;
	}
	
	public void setId(Integer id) {
		this.Id = id;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getSurname() {
		return surname;
	}

	public Date getDateofBirth() {
		return dateofBirth;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public void setDateofBirth(Date dateofBirth) {
		this.dateofBirth = dateofBirth;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.DataSerializable#toData(java.io.DataOutput)
	 */
	@Override
	public void toData(DataOutput out) throws IOException {
		out.writeInt( Id );
		out.writeUTF( firstname);
		out.writeUTF( surname );
		out.writeUTF( zipcode );
		DataSerializer.writeDate( dateofBirth, out);
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.DataSerializable#fromData(java.io.DataInput)
	 */
	@Override
	public void fromData(DataInput in) throws IOException,
			ClassNotFoundException {
		Id = in.readInt();
		firstname = in.readUTF();
		surname = in.readUTF();
		zipcode = in.readUTF();
		dateofBirth = DataSerializer.readDate(in);
	}
}
