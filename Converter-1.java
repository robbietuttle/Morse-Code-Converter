//Robbie Tuttle (tuttlegr)
//Project 2
//12/07/17


import java.io.File;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/*
 * 
 * 	Converter is a class that simply converts from text to morse code and from morse code to text.
 * 	There are two methods you can not modify except for adding the source code: 
 * 			textToMorseCode() 
 * 			morseCodeToText()
 * 
 * 	You will turn in this file (Converter.java) 
 * 
 * 	NOTE: 	For when creating your morse code file you must ONLY save byte that's equal to 1 or 
 * 			leave empty (null).  Meaning, in class I suggested you could randomly place a character
 * 			from 1 to 255 in any byte that is "on". You can still do this but you must create two
 * 			additional methods for doing this.
 * 
 */
public class Converter {

	// Converts a text file into a RandomAccessFile created file. 
	public static void textToMorseCode(String fromFileName, String toFileName) throws Exception {
		(new File(toFileName)).delete();

		System.out.println("\n-------------------------Text To Morse-------------------------");
		//HashMap for conversion
		ConcurrentHashMap<Character, String> convert = new ConcurrentHashMap<Character, String>();
		Scanner inText = new Scanner(new File("conversions.txt"));
		while(inText.hasNextLine()) {
			String[] part = inText.nextLine().split("\t");
			convert.put(part[0].charAt(0), part[1]);
		}
		//added by me for space
		convert.put(' ',	"0000");
		inText.close();


		//used to read and write
		RandomAccessFile raf = new RandomAccessFile(toFileName, "rw");
		Scanner in = new Scanner(new File(fromFileName));

		//string to store 1's and 0's
		String s= "";

		//this is used to add enters at the beginning of the string if needed
		boolean set = false;

		//adds 1's and 0's to string
		while(in.hasNextLine()) {
			//adds enter line after each line
			if(s.length()!=0 && set == false) {
				s=s+"0000000101110101110000000";
			}

			set=false;
			//reads in line and adds to string
			for(String word : in.nextLine().toUpperCase().trim().split("")) {				
				for(char c : word.toCharArray()) {
					s += convert.get(c) + "000";
				}
			}
			//checks if the string needs to start with enters
			if(s.length()==0) {
				s=s+"0000000101110101110000000";
				//ensures no double enters
				set=true; 
			}
		}


		//Removes extra zeros at the end of the string
		s=s.substring(0,s.length()-3);
		System.out.println("text to raf : "+s);
		in.close();


		//Write to RAF
		for(char c : s.toCharArray()) {
			raf.writeByte(c-'0');
		}

		raf.seek(0);

		//used to check that raf stores correct data
		System.out.print("print of raf: ");
		for(int i = 1 ; i<=raf.length(); i++) {
			System.out.print(raf.readByte());
			raf.seek(i);
		}
		raf.close();
	}




	// Converts a RandomAccessFile created file into a text file. 
	public static void morseCodeToText(String fromFileName, String toFileName) throws Exception {
		System.out.println("\n\n-------------------------Morse To Text-------------------------");
		RandomAccessFile raf = new RandomAccessFile(fromFileName, "rw");
		PrintWriter out = new PrintWriter(new File(toFileName));	
		
		//HashMap for conversion
		ConcurrentHashMap<String, Character> convertFromRaf = new ConcurrentHashMap<String, Character>();
		Scanner inText = new Scanner(new File("conversions.txt"));
		while(inText.hasNextLine()) {
			String[] part = inText.nextLine().split("\t");
			convertFromRaf.put(part[1], part[0].charAt(0));
		}
		inText.close();

		//create string to store 1's and 0's 
		String s = "";
		//read in raf and store 1's and 0's in string
		while(raf.getFilePointer()<raf.length()) {
			s+=(char)(raf.readByte()+'0');
		}

		//used to show that s is correct
		System.out.println("Str from raf: "+s);


		String text = "";
		//splitting the string
		for(String line : s.split("0000000101110101110000000")){

			for(String word:  line.split("0000000")) {
				for(String letter : word.split("000")) {
					if(convertFromRaf.containsKey(letter)) {
						text+=convertFromRaf.get(letter);
					}
				}
				text=text+" ";
			}
			text=text+"\n";
		}

		//removes extra enter at end of file
		text = text.substring(0,text.length()-2);

		System.out.println("\n-----------------------Example Text File-----------------------");
		System.out.println(text);
		System.out.println("---------------------------------------------------------------");
		out.print(text);

		raf.close();
		out.close();
	}
}