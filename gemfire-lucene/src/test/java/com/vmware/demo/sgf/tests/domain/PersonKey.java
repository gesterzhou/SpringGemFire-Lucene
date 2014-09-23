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
import java.io.Serializable;

import org.springframework.data.gemfire.mapping.Region;

import com.gemstone.gemfire.DataSerializable;
import com.gemstone.gemfire.Instantiator;

/**
 * Key class must not use PDX as the PDXInstance wrapper may not be the same on each redundant node for partition regions.
 * @author Lyndon Adams
 *
 */
public class PersonKey implements DataSerializable { 
	
	static {
		Instantiator.register(new Instantiator(PersonKey.class, 17) {
			 public DataSerializable newInstance() {
			  return new PersonKey();
			 }
		});
	}

	
	int id;
	String surname;
	
	public PersonKey(){}

	public PersonKey(int id, String surname) {
		super();
		this.id = id;
		this.surname = surname;
	}

	public int getId() {
		return id;
	}

	public String getSurname() {
		return surname;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((surname == null) ? 0 : surname.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PersonKey other = (PersonKey) obj;
		if (id != other.id)
			return false;
		if (surname == null) {
			if (other.surname != null)
				return false;
		} else if (!surname.equals(other.surname))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.DataSerializable#fromData(java.io.DataInput)
	*/
	@Override
	public void fromData(DataInput in) throws IOException,
			ClassNotFoundException {
		id = in.readInt();
		surname = in.readUTF();		
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.DataSerializable#toData(java.io.DataOutput)
	 */
	@Override
	public void toData(DataOutput out) throws IOException {
		out.writeInt( id );
		out.writeUTF( surname );
	}
	
}
