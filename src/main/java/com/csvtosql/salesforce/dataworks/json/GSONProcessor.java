package com.csvtosql.salesforce.dataworks.json;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.csvtosql.salesforce.dataworks.model.DataType;
import com.csvtosql.salesforce.dataworks.model.TableColumsNameMapTODataType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Component
public class GSONProcessor {

	public static Map<String, Map<String, String>> dataTypeMapping = new HashMap<String, Map<String, String>>();
	public static Map<String, Map<String, String>> dataTypeMappingSize = new HashMap<String, Map<String, String>>();
	Map<String, String> dataTypes = new HashMap<String, String>();	

	@Value("classpath:*.json")
	private Resource[] inputResources;

	@PostConstruct
	public void gsonProcessor() throws IOException {

		for (Resource r : inputResources) {
			switch (r.getFile().getName()) {
			case "datatype.mapping.json":
				CatchDataTypeMapping(r);
				break;
			}
		}
		
		for (Resource r : inputResources) {
			if (!r.getFile().getName().equals("datatype.mapping.json")) {
				CatchTableColumeTODataType(r);
				CatchTableColumeTODataTypeSize(r);
			}
		}
	}

	private void CatchDataTypeMapping(Resource r) {
		try {
			Reader reader = Files.newBufferedReader(Paths.get(r.getFile().getPath()));
			List<DataType> users = new Gson().fromJson(reader, new TypeToken<List<DataType>>() {
			}.getType());
			for (DataType us : users) {
				dataTypes.put(us.getSalesforce(), us.getMssql());
			}			
			reader.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	
	private void CatchTableColumeTODataTypeSize(Resource r) {
		try {
			Reader reader = Files.newBufferedReader(Paths.get(r.getFile().getPath()));
			List<TableColumsNameMapTODataType> users = new Gson().fromJson(reader, new TypeToken<List<TableColumsNameMapTODataType>>() {
			}.getType());
			Map<String, String> temp = new HashMap<String, String>();
			for (TableColumsNameMapTODataType us : users) {
				temp.put(us.getFieldname(), us.getSize());
			}
			String key = FilenameUtils.removeExtension(r.getFile().getName());
			dataTypeMappingSize.put(key, temp);
			reader.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private void CatchTableColumeTODataType(Resource r) {
		try {
			Reader reader = Files.newBufferedReader(Paths.get(r.getFile().getPath()));
			List<TableColumsNameMapTODataType> users = new Gson().fromJson(reader, new TypeToken<List<TableColumsNameMapTODataType>>() {
			}.getType());
			Map<String, String> temp = new HashMap<String, String>();
			for (TableColumsNameMapTODataType us : users) {
				temp.put(us.getFieldname(), dataTypes.getOrDefault(us.getFieldtype(), " NVARCHAR"));
			}
			String key = FilenameUtils.removeExtension(r.getFile().getName());
			dataTypeMapping.put(key, temp);
			reader.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
