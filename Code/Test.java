

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Scanner;

public class Test {
	
	
	public static void main(String[] args) {
		
		try {
			HashTable<String,Integer> myHashTable = new HashTable<String,Integer>();
			FileInputStream fis=new FileInputStream("story.txt");       
			Scanner s=new Scanner(fis);   
			//long startTimeMain =  System.nanoTime(); 
			while(s.hasNext()) {  
				
				String letter = s.next().toLowerCase(Locale.ENGLISH);
				myHashTable.put(letter,1);
			} 
			//long endTimeMain = System.nanoTime(); 
			//System.out.println(endTimeMain - startTimeMain);
			//System.out.println("Collision Count-->" + myHashTable.collisionCount);
			int op =0;
			
			
			Scanner sc = new Scanner (System.in);
			System.out.println("-------WELCOME TO MY HASH TABLE-------" + "\nYou can search for words...");
			System.out.print("Total word count in HASHTABLE: " + myHashTable.totalWordCount() + "\n");
			System.out.println("Length of HASH TABLE: " + myHashTable.lenghtofHashTable()  + "\n--------------------------------------");
			boolean flagExit=true;
			while(flagExit) {
				System.out.print("Search-->");
				String st = sc.nextLine();
				if(st.contains("ö") || st.contains("Ö") || st.contains("Ý") || st.contains("ü") || st.contains("Ü") ||
						st.contains("ð") || st.contains("Ð") || st.contains("þ") || st.contains("Þ") || st.contains("ç") || st.contains("Ç")) {
					System.out.println("The word has unvalid character!! Use only English letters...");
				}
				else { // sorun yoksa output 
					myHashTable.getInfo(st);
				}
				System.out.println("------------------");
				op++;
				if(op%10==0) { // her 10 islemde cikis yapmak ister misin diye soruyorum
					System.out.println("Would you like to exit?" + "\nOption 1: Yes!!" + "\nOption 2: No!!");
					boolean flagQ = true;
					while(flagQ) {
						System.out.print("choice:");
						String option = sc.nextLine();
						if(option.equals("1")) {
							System.out.println("GOODBYE");
							flagExit=false;
							System.out.println("------------------");
							break;
						}
						else if(option.equals("2")) {
							System.out.println("------------------");
							flagQ=false;
						}
						else {
							System.out.println("Enter only 1 or 2!!");
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("A FileNotFoundException was caught!");
			//e.printStackTrace();
		}
	}

}
