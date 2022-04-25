import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

	private static List<Employee> parseToCSV(String[] mapping, String fileName) {
		List<Employee> listCSV = new ArrayList<>();

		try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
			ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
			strategy.setType(Employee.class);
			strategy.setColumnMapping(mapping);

			CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
					.withMappingStrategy(strategy)
					.build();

			listCSV = csv.parse();

			//проверка
			listCSV.forEach(System.out::println);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return listCSV;
	}

	private static <T> String listToJson(List<Employee> list) {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		Type listType = new TypeToken<List<T>>() {
		}.getType();
		String json = gson.toJson(list, listType);
		return json;
	}

	private static void writeString(String json) {
		try (FileWriter writer = new FileWriter("data.json")) {
			writer.write(json);
			writer.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {

		String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
		String fileName = "data.csv";

		//парсер CSV - JSON
		List<Employee> list = parseToCSV(columnMapping, fileName);
		writeString(listToJson(list));
	}
}
