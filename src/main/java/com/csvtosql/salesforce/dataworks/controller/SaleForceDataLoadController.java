package com.csvtosql.salesforce.dataworks.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;

import com.csvtosql.salesforce.dataworks.json.GSONProcessor;
import com.csvtosql.salesforce.dataworks.repository.BookRepository;

@Controller
public class SaleForceDataLoadController {
	private static final Logger log = LoggerFactory.getLogger(SaleForceDataLoadController.class);
	@Autowired
	JdbcTemplate jdbcTemplate;
	@Autowired
	@Qualifier("namedParameterJdbcBookRepository")
	private BookRepository bookRepository;

	@Value("file:C:/files/*.csv")
	private Resource[] inputResourcescsv;

	public void ReadDatase() throws FileNotFoundException, IOException {
		Map<File, List<String>> headerDetail = getHeaderDetail(new File("C:\\files"));

		Map<String, List<Map<String, Object>>> dataa = perpareDatafromSourceFiles();

		perpareCreateTables(dataa);
		// Enable this statement to load the data
		perpareInsertTables(dataa);

		System.out.println("Exiting the Process ");

	}

	private void perpareInsertTables(Map<String, List<Map<String, Object>>> dataa) {
		for (String key : dataa.keySet()) {
			for (Map<String, Object> list : dataa.get(key)) {
				String tableName = "SF_" + key;
				StringBuffer qry = new StringBuffer("INSERT  INTO " + tableName.toLowerCase() + " ( ");
				for (String field : list.keySet()) {
					qry.append(field).append(",");
				}
				String query = qry.toString();
				if (query.endsWith(",")) {
					query = query.substring(0, query.length() - 1);
				}
				query = query.concat(") VALUES ");

				StringBuffer qry2 = new StringBuffer(" ( ");
				for (String field : list.keySet()) {
					String tmp = list.get(field).toString().replaceAll("[^a-zA-Z 0-9]", "");
					qry2.append("'" + tmp + "'").append(",");
				}
				query = query + qry2.toString();
				if (query.endsWith(",")) {
					query = query.substring(0, query.length() - 1);
				}
				query = query.concat(")");

				System.out.println(query);

				try {
					jdbcTemplate.execute(query);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}

			}
		}
	}	

	private void perpareCreateTables(Map<String, List<Map<String, Object>>> dataa) {

		for (String key : dataa.keySet()) {
			int countercteateTable = 0;
			for (Map<String, Object> list : dataa.get(key)) {
				if (countercteateTable++ == 0) {
					String tableName = "SF_" + key;
					StringBuffer qry = new StringBuffer("CREATE TABLE " + tableName + " ( ");
					for (String field : list.keySet()) {
						Map<String, String> accountFieldToDataType = GSONProcessor.dataTypeMapping.get(key);
						Map<String, String> accountFieldToDataTypeSize = GSONProcessor.dataTypeMappingSize.get(key);
						if (accountFieldToDataType.containsKey(field) || true) {

							String datatype =  accountFieldToDataType.get(field);
							if (datatype == null) {
								datatype = "NVARCHAR";
							}
							String size = null;
							if (accountFieldToDataTypeSize.containsKey(field)) {
								if (accountFieldToDataTypeSize.get(field).equals("")) {
									size = " (200) ";
								}
								size = " (" + accountFieldToDataTypeSize.get(field) + ") ";
							} else {
								size = " (200) ";
							}

							datatype = datatype + size;

							qry.append(field).append(" ").append(datatype).append(",");
						}
					}

					String query = qry.toString();
					if (query.endsWith(",")) {
						query = query.substring(0, query.length() - 1);
					}
					query = query.concat(")");
					System.out.println(query);
					try {
					jdbcTemplate.execute("DROP TABLE IF EXISTS " + tableName);
					jdbcTemplate.execute(query);
					}catch (Exception E) {
						
					}

				} else {
					break;
				}

			}
		}
	}

	private Map<String, List<Map<String, Object>>> perpareDatafromSourceFiles() throws IOException, FileNotFoundException {
		Map<String, List<Map<String, Object>>> dataa = new HashMap<String, List<Map<String, Object>>>();
		for (Resource r : inputResourcescsv) {
			if (!r.getFile().getName().equals("datatype.mapping.json")) {
				String key = FilenameUtils.removeExtension(r.getFile().getName());
				List<Map<String, Object>> extracts = new ArrayList<Map<String, Object>>();
				String line;
				int count = 0;
				String[] split = null;
				BufferedReader br = new BufferedReader(new FileReader(r.getFile()));
				while ((line = br.readLine()) != null) {
					Map<String, Object> rowHash = new HashMap<String, Object>();
					if (count == 0) {
						split = line.split(",");
					} else {
						String[] rows = line.split(",");
						for (int i = 0; i < rows.length; i++) {
							try {
								rowHash.put(split[i], rows[i]);
							} catch (ArrayIndexOutOfBoundsException e) {
							}
						}
					}
					if (count != 0)
						extracts.add(rowHash);

					count++;
				}

				dataa.put(key, extracts);

			}

		}
		return dataa;
	}

	public static boolean isAlphaNumeric(String str) {
		String regex = "^(?=.*[a-zA-Z])(?=.*[0-9])[A-Za-z0-9]+$";
		Pattern p = Pattern.compile(regex);
		if (str == null) {
			return false;
		}
		Matcher m = p.matcher(str);
		return m.matches();
	}

	private Map<File, List<String>> getHeaderDetail(File folder) throws IOException, FileNotFoundException {

		File[] listOfFiles = folder.listFiles();
		Map<File, List<String>> data = new HashMap<File, List<String>>();
		for (File file : listOfFiles) {
			if (file.isFile()) {

				try (BufferedReader br = new BufferedReader(new FileReader(file))) {
					String line;
					int count = 0;
					while (count == 0 && (line = br.readLine()) != null) {

						data.put(file, Arrays.asList(line.split(",")));

						count++;
					}
				}
			}
		}
		return data;
	}

}

class Request {

	public Map<String, File> date = new HashMap<String, File>();

	public Map<String, File> getDate() {
		return date;
	}

	public void setDate(Map<String, File> date) {
		this.date = date;
	}

}