package ch.flottesohle.dev;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GodsCrawler {

	private static final String URL = "https://www.gods-and-monsters.com/list-of-greek-gods-goddesses.html";
	// https://florgeous.com/types-of-flowers/
	
	public static void main(String[] args) throws IOException {
		Document doc = Jsoup.connect(URL).get();

		List<String> males = new ArrayList<>();
		List<String> females = new ArrayList<>();
		
		// <li><b>Momus</b> - (a.k.a. Momos) God of satire, writers, and poets.</li><br>
		Elements elements = doc.getElementsByTag("li");
		for (Element element : elements) {
			Elements nameElements = element.getElementsByTag("b");
			if (!nameElements.isEmpty()) {
				Element nameElement = nameElements.get(0);
				
				// <li><b>Nyx</b> - (a.k.a. Nox) Goddess of night.</li><br>
				String text = element.text();
				// daughter, wife 
				boolean female = text.contains("Goddess") || text.contains("goddess") || text.contains(" wife") || text.contains(" Muse");
				//  He 
				boolean male = text.contains("God ") || text.contains("god ");
				if (!female && !male) {
					System.out.println(nameElement.text() + (female ? "(f)" : ""));
				} else if (male) {
					males.add(nameElement.text());
				} else {
					females.add(nameElement.text());
				}
				
			}
		}
		System.out.println("Males: " + males.size() + " / females: " + females.size());
	}

}
