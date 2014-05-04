/*
 * Copyright (C) 2013 frandfeng
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.frand.easyandroid.db.entity;

import java.util.ArrayList;
import java.util.List;

import com.frand.easyandroid.command.FFBaseEntity;

public class FFTableInfoEntity extends FFBaseEntity {
	private static final long serialVersionUID = 488168612576359150L;
	private String tableName = "";
	private String className = "";
	private FFPKProperyEntity pkProperyEntity = null;
	private ArrayList<FFPropertyEntity> propertieArrayList = new ArrayList<FFPropertyEntity>();

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public ArrayList<FFPropertyEntity> getPropertieArrayList() {
		return propertieArrayList;
	}

	public void setPropertieArrayList(List<FFPropertyEntity> propertyList) {
		this.propertieArrayList = (ArrayList<FFPropertyEntity>) propertyList;
	}

	public FFPKProperyEntity getPkProperyEntity() {
		return pkProperyEntity;
	}

	public void setPkProperyEntity(FFPKProperyEntity pkProperyEntity) {
		this.pkProperyEntity = pkProperyEntity;
	}

}
