package com.csvtosql.salesforce.dataworks.model;

public class DataType  implements Mapping{

	private String salesforce = "user";
	private String mysql = "user";
	private String mssql = "user";

	public String getSalesforce() {
		return salesforce;
	}

	public void setSalesforce(String salesforce) {
		this.salesforce = salesforce;
	}

	public String getMysql() {
		return mysql;
	}

	public void setMysql(String mysql) {
		this.mysql = mysql;
	}

	public String getMssql() {
		return mssql;
	}

	public void setMssql(String mssql) {
		this.mssql = mssql;
	}

}
