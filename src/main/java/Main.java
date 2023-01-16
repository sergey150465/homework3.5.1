import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String csvFileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, csvFileName);
        String json = listToJson(list);
        String jsonFileName = "data.json";
        writeString(json, jsonFileName);

        String xmlFileName = "data.xml";
        List<Employee> list2 = parseXML(xmlFileName);
        String json2 = listToJson(list2);
        String jsonFileName2 = "data2.json";
        writeString(json2, jsonFileName2);
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            return csvToBean.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        return gson.toJson(list);
    }

    private static void writeString(String json, String jsonFilename) {
        try (FileWriter fileWriter = new FileWriter(jsonFilename)) {
            fileWriter.write(json);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Employee> parseXML(String xmlFilename) throws ParserConfigurationException, IOException, SAXException {
        List<String> elements = new ArrayList<>();
        List<Employee> list = new ArrayList<>();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        Document doc = builder.parse(new File(xmlFilename));
        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeName().equals("employee")) {
                NodeList nodeList1 = node.getChildNodes();
                for (int j = 0; j < nodeList1.getLength(); j++) {
                    Node node_ = nodeList1.item(j);
                    if (Node.ELEMENT_NODE == node_.getNodeType()) {
                        elements.add(node_.getTextContent());
                    }
                }
                list.add(new Employee(
                        Long.parseLong(elements.get(0)),
                        elements.get(1),
                        elements.get(2),
                        elements.get(3),
                        Integer.parseInt(elements.get(4))));
                elements.clear();
            }
        }
        return list;
    }
}
