package main;

import java.util.Scanner;
import org.jdom2.*;
import de.fhwgt.quiz.application.Catalog;
import de.fhwgt.quiz.application.Question;
import de.fhwgt.quiz.loader.FilesystemLoaderXML;
import de.fhwgt.quiz.loader.LoaderException;

public class Main {
	
	private static FilesystemLoaderXML fslXML;
	
	public static void main(String[] args) {
				
		Scanner scanner = new Scanner(System.in);
		//FilesystemLoaderXML fslXML = new FilesystemLoaderXML(scanner.nextLine());
		
		fslXML = new FilesystemLoaderXML("file:/" + System.getProperty("user.dir")+"/Fragenkatalog/");
		System.out.println("Folgende Kataloge wurden im Pfad file:/" + System.getProperty("user.dir")+"/Fragenkatalog/ gelesen:");
				
		try {
			if(fslXML.getCatalogs() == null){
				System.out.println("Keine Kataloge");
				return;
			}
			for(String key : fslXML.getCatalogs().keySet()){
					System.out.println(key);
			}
		} catch (LoaderException e) {			
			e.printStackTrace();
		}
		System.out.println("welcher katalog soll ausgegeben werden?");
		printCatalog(scanner.nextLine());
		
		
	}
	
	private static void printCatalog(String name){
		
		try {
			Catalog catalog = fslXML.getCatalogByName(name);
			System.out.println(catalog.getName());
			for (Question question : catalog.getQuestions()) {
				System.out.println("\tFrage: " + question.getQuestion());
				System.out.println("\tTimeout: " +question.getTimeout());
				System.out.println("\tAntworten:");
				for (int i = 0; i < question.getAnswerList().size(); i++) {
					if(question.validateAnswer((long)i)){
						System.out.println("\t\t+ " + question.getAnswerList().get(i));
					}else{
						System.out.println("\t\t- " + question.getAnswerList().get(i));
					}
				}
			}
		} catch (LoaderException e) {
			System.out.println("felher beim lesen");
			e.printStackTrace();
		}
	}
	
	
}
