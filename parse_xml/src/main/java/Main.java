import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Main {

	private static List<Employee> parseXML() {
		List<Employee> listXML = new ArrayList<>();

		long newId = 0;
		String newFirstName = null;
		String newLastName = null;
		String newCountry = null;
		int newAge = 0;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new File("data.xml"));

			Node root = doc.getDocumentElement();

			//получить первое "поколение"
			NodeList rootElements = root.getChildNodes();
			//очередь для второго "поколения"
			Deque<Element> secondGenDeque = new ArrayDeque<>();

			for (int j = 0; j < rootElements.getLength(); j++) {
				//обход первого "поколения"
				Node firstGen = rootElements.item(j);

				//получить второе "поколение"
				NodeList employeeElements = firstGen.getChildNodes();

				for (int k = 0; k < employeeElements.getLength(); k++) {
					//обход второго поколения
					Node secondGen = employeeElements.item(k);

					if (Node.ELEMENT_NODE == secondGen.getNodeType()) {

						//элементы
						Element element = (Element) secondGen;
						//добавить в очередь
						secondGenDeque.add(element);
					}
				}
			}

			//получить значения у элементов и создать объекты Employee
			int sizeDeque = secondGenDeque.size();
			for (int k = 0; k < sizeDeque; k++) {
				Element node_ = secondGenDeque.poll();
				String nodeName = node_.getNodeName();
				String nodeText = node_.getTextContent();

				switch (nodeName) {
					case "id":
						long l = Long.parseLong(nodeText);
						newId = l;
						break;
					case "firstName":
						newFirstName = nodeText;
						break;
					case "lastName":
						newLastName = nodeText;
						break;
					case "country":
						newCountry = nodeText;
						break;
					case "age":
						int ag = Integer.parseInt(nodeText);
						newAge = ag;
						listXML.add(new Employee(newId, newFirstName, newLastName, newCountry, newAge));
						break;
					default:
						System.out.println("Smth goes wrong");
						break;
				}
			}
			//проверка
			listXML.forEach(System.out::println);

		} catch (ParserConfigurationException | IOException | SAXException e) {
			e.printStackTrace();
		}
		return listXML;
	}

	protected static <T> String listToJson(List<Employee> list) {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		Type listType = new TypeToken<List<T>>() {
		}.getType();
		String json = gson.toJson(list, listType);
		return json;
	}

	private static void writeString(String json) {
		try (FileWriter writer = new FileWriter("data2.json")) {
			writer.write(json);
			writer.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {

		//парсер XML - JSON
		writeString(listToJson(parseXML()));
	}
}
