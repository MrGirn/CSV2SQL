
package com.csvtosql.salesforce;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.csvtosql.salesforce.dataworks.controller.SaleForceDataLoadController;

@SpringBootApplication
public class Application {

	public static void main(String[] args) throws FileNotFoundException, IOException {

		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

		SaleForceDataLoadController controller = context.getBean(SaleForceDataLoadController.class);
		controller.ReadDatase();

		context.stop();

	}

}
