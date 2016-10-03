import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class PeopleWeb {
    static ArrayList<Person> persons = new ArrayList<>();

    public static void main(String[] args) throws Exception { //Exception stems from loadFile().
        Spark.init();
        loadFile();

        Spark.get(
                "/",
                ((request, response) -> {
                    HashMap m = new HashMap<>();
                    ArrayList twentyPeopleList = new ArrayList();
                    String offset = request.queryParams("offset");
                    int offsetNum = 0;
                    Integer nextPage = null;
                    Integer prevPage = null;

                    if (offset != null) {
                        offsetNum = Integer.parseInt(offset);
                    }


                    for (int i = offsetNum; i < offsetNum + 20; i++) {
                        twentyPeopleList.add(persons.get(i));
                    }
                    if (persons.size() - 20 > offsetNum) {
                        nextPage = offsetNum + 20;
                    }
                    if (20 <= offsetNum) {
                        prevPage = offsetNum - 20;
                    }

                    m.put("next", nextPage);
                    m.put("prev", prevPage);
                    m.put("id", twentyPeopleList);
                    return new ModelAndView(m, "people.html");

                }),
                new MustacheTemplateEngine()
        );

        Spark.get(
                "/person",
                ((request, response) -> {
                    String id = request.queryParams("id");
                    int idNum = Integer.parseInt(id);
                    HashMap m = new HashMap();
                    m.put("id", persons.get(idNum - 1));
                    return new ModelAndView(m, "person.html");


                }),
                new MustacheTemplateEngine()
        );

    }

    static void loadFile() throws IOException {
        File f = new File("people.csv");
        Scanner fileScanner = new Scanner(f);
        while (fileScanner.hasNext()) {
            String line = fileScanner.nextLine();
            while (line.startsWith("id,first_name")) {
                line = fileScanner.nextLine();
            }
            String[] columns = line.split(",");
            Person person = new Person(columns[0], columns[1], columns[2], columns[3], columns[4], columns[5]);
            persons.add(person);

        }
        fileScanner.close();

    }
}
