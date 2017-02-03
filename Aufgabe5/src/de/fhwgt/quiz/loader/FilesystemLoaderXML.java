package de.fhwgt.quiz.loader;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import de.fhwgt.quiz.application.Catalog;
import de.fhwgt.quiz.application.Question;

public class FilesystemLoaderXML implements CatalogLoader {

	private String location;
	private Map<String, Catalog> catalogs = new HashMap<String, Catalog>();
	private File[] catalogDir;

	public FilesystemLoaderXML(String newLocation) {
		this.location = newLocation;
	}

	public Map<String, Catalog> getCatalogs() throws LoaderException {
		if (!catalogs.isEmpty()) {
			return catalogs;
		}
		URL url = this.getClass().getClassLoader().getResource(location);

		File dir;
		try {
			if (url != null) {
				dir = new File(url.toURI());
			} else {
				dir = new File("/");
			}
		} catch (URISyntaxException e) {
			// Try to load from the root of the classpath
			dir = new File("/");
		}
		if (dir.exists() && dir.isDirectory()) {
			this.catalogDir = dir.listFiles(new CatalogFilter());
			for (File f : catalogDir) {
				catalogs.put(f.getName(), new Catalog(f.getName(), new QuestionFileLoader(f)));
			}
		}
		return catalogs;

	}

	public class CatalogFilter implements FileFilter {
		@Override
		public boolean accept(File pathname) {
			if (pathname.isFile() && pathname.getName().endsWith(".xml"))
				return true;
			else
				return false;
		}

	}

	public Catalog getCatalogByName(String name) throws LoaderException {
		if (catalogs.isEmpty()) {
			getCatalogs();
		}

		return this.catalogs.get(name);
	}

	public class QuestionFileLoader implements QuestionLoader {

		private final File catalogFile;
		private final List<Question> questions = new ArrayList<Question>();

		public QuestionFileLoader(File file) {
			catalogFile = file;
		}

		@Override
		public List<Question> getQuestions(Catalog catalog) throws LoaderException {

			if (!questions.isEmpty()) {
				return questions;
			}
			SAXBuilder reader = new SAXBuilder();
			try {
				Document doc = reader.build(catalogFile);
				Question temp;
				for (Element frage : doc.getRootElement().getChild("Fragenkatalog").getChild("Fragen").getChildren()) {
					temp = new Question(frage.getTextNormalize());
					if (!(frage.getAttributeValue("Timeout").equals("0"))) {
						temp.setTimeout(Long.parseLong(frage.getAttributeValue("timeout")));
					}

					for (Element antwort : frage.getChild("Antworten").getChildren()) {
						if (antwort.getAttributeValue("correct").equals("true")) {
							temp.addAnswer(antwort.getTextNormalize());
						} else {
							temp.addBogusAnswer(antwort.getTextNormalize());
						}
					}
					questions.add(temp);
				}
			} catch (JDOMException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (!questions.isEmpty()) {
				return questions;
			}
			return null;
		}
	}
}
