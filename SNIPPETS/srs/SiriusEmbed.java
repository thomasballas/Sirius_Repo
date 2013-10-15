//package srs;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.*;

public class SRSembed {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String msg = "";
		int ctr = 0, Strings = args.length;
		//System.out.println(Strings + "\n");
		if (args.length<2){
			System.out.println("Not enough arguments supplied.  Please provide the following command:\n"
					+ "java SRSembed [input image] [input text file] [output image file (optional)]\n");
			return;
		}

		try {
			BufferedImage srcimg = ImageIO.read(new File(args[0]));
			BufferedImage img = new BufferedImage(srcimg.getWidth(), srcimg.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
			img.getGraphics().drawImage(srcimg, 0, 0, null);
			Scanner scans = new Scanner(new File(args[1]));

			msg = scans.nextLine();
			//System.out.println(msg);

			File outputfile = new File("embeddedImg.png");
			if (args.length>2){
				//CALL UP A FILE FOR THE MESSAGE OUTPUT (SECOND ARGUMENT)
				outputfile = new File(args[2]);
			} 
			// if file doesnt exists, then create it
			if (!outputfile.exists()) {
				outputfile.createNewFile();
			}

			ImageIO.write(embed(msg, img), "png", outputfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	/*
	 * embed() takes in a string "msg" and a BufferedImage "img".  It assesses the input image fro x and y bounds.  
	 * Then, it proceeds to create an eight place array to hold the individual bits of a single character in msg.
	 * The algorithm isolates each bit in the char and resets the array place to 1 if the value is non-zero.
	 * Once it's created this buffer, it begins altering the RGB values of img's pixels.
	 * This is done by first zeroing the LSB and then ORing it with our array value.
	 * The resulting image, once the string has been exhausted, is returned.
	 * */
	static BufferedImage embed(String msg, BufferedImage img)
	{
		int msgLength = msg.length();
		int scaleX = img.getWidth();
		int scaleY = img.getHeight();
		int x = 0, y = 0, RGB = 0, EOL = 0, bitCounter = 0;
		int newChar;
		int curChar = 0;
		int chars[] = new int[8];
		String pixels = "";


		//ITERATE OVER THE Y-AXIS OF THE IMAGE
		while (y < (scaleY-1))
		{
			//ITERATE OVER THE X-AXIS OF THE IMAGE
			x = 0;
			while (x < (scaleX-1))
			{
				//EVERY 8 PIXELS, FILL THE ARRAY WITH NEW LSB VALUES
				if (bitCounter%8 == 0){
					//CHECK TO MAKE SURE WE'VE NOT ALREADY FINISHED EMBEDDING OUR STRING
					if (curChar >= msgLength) {
						//DEBUGGING
						/*
							try{
								File file = new File("pixels.txt");
	
								// if file doesnt exists, then create it
								if (!file.exists()) {
									file.createNewFile();
								}
	
								//SET UP AND WRITE TO OUR MESSAGE TEXT FILE
								FileWriter fw = new FileWriter(file.getAbsoluteFile());
								BufferedWriter bw = new BufferedWriter(fw);
								bw.write(pixels);
								bw.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						*/
						return img;
					}
					//FILL THE ARRAY WITH NEW LSB VALUES -> ASCII VALUE OF MESSAGE TEXT BEGINNING WITH 
					//THE MSB AT CHARS[0] AND ENDING WITH THE LSB AT CHARS[7]
					for (int i = 0; i<8; i++)
					{
						//ISOLATE THE APPROPRIATE ASCII BIT
						chars[i] = (msg.charAt(curChar) & (1<<(7-i)));
						//IF THE RESULTING VALUE IS NOT 0, REASSIGN TO 1
						if (chars[i] != 0) chars[i] = 1;  
					}
					//AFTER DISSECTING A CHAR, INCREMENT THE COUNTER SO WE CAN PROCEED THROUGH THE STRING
					curChar++;
				}
				//RETRIEVE CURRENT PIXEL RGB VALUE
				RGB = img.getRGB(x,y);
				//DEBUGGING
				//int origRGB = RGB;
				//ZERO THE LSB OF THE PIXEL
				RGB = 0xFFFFFFFE & RGB;
				//-OR- THE PIXEL WITH OUR NEW CHAR LSB
				RGB = RGB | chars[bitCounter%8];
				/*DEBUGGING
				pixels += RGB
						+ " = "
						+ origRGB
						+ "\n";
				*/
				//SET THE IMAGE PIXEL
				img.setRGB(x, y, RGB);
				//				FOR DEBUGGING: PRINTS CURRENT MESSAGE BIT AND VALUE OF RGB PIXEL
				//				System.out.println(chars[bitCounter%8]+" = "+ RGB);
				//				RGB = img.getRGB(x,y);
				//				FOR DEBUGGING: PRINTS CURRENT MESSAGE BIT AND VERIFIES VALUE OF SET RGB PIXEL				
				//				System.out.println("check "+chars[bitCounter%8]+" = "+ RGB);
				//INCREMENT THE BIT COUNTER
				bitCounter++;
				//MOVE ONTO THE NEXT PIXEL HORIZONTALLY
				x++;

			}
			//NEXT ROW OF PIXELS
			y++;
		}


		return img;

	}

}
