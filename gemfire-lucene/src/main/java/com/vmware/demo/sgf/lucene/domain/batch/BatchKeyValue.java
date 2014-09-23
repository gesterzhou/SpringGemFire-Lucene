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
package com.vmware.demo.sgf.lucene.domain.batch;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.gemstone.gemfire.DataSerializable;
import com.gemstone.gemfire.DataSerializer;
import com.gemstone.gemfire.Instantiator;
import com.vmware.demo.sgf.lucene.domain.SearcheCriteria;

/**
 * @author Lyndon Adams
 *
 */
public final class BatchKeyValue implements DataSerializable {
	
	static {
		Instantiator.register(new Instantiator(BatchKeyValue.class, 80002) {
			 public DataSerializable newInstance() {
			  return new BatchKeyValue();
			 }
		});
	}
	
	
	Object key;
	Object value;
	
	public BatchKeyValue(){}
	
	public BatchKeyValue(Object key, Object value) {
		super();
		this.key = key;
		this.value = value;
	}

	public Object getKey(){
		return key;
	}

	public Object getValue(){
		return value;
	}

	@Override
	public int hashCode() {
		return key.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BatchKeyValue other = (BatchKeyValue) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.DataSerializable#fromData(java.io.DataInput)
	 */
	@Override
	public void fromData(DataInput in) throws IOException,
			ClassNotFoundException {
		key = DataSerializer.readObject(in);
		value = DataSerializer.readObject(in);
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.DataSerializable#toData(java.io.DataOutput)
	 */
	@Override
	public void toData(DataOutput out) throws IOException {
		DataSerializer.writeObject( key, out);
		DataSerializer.writeObject( value, out);
	}
	
	
}
