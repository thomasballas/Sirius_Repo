//package srs;

import java.awt.image.BufferedImage;
import java.io.*;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SRSextract {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			//READ IN OUR EMBEDDED IMAGE FROM THE COMMAND LINE (FIRST ARGUMENT)
			if (args.length<1){
				System.out.println("Not enough arguments supplied.  Please provide the following command:\n"
						+ "java SRSextract [input image] [output text file (optional)]\n");
				return;
			}
			BufferedImage img = ImageIO.read(new File(args[0]));
			
			File file = new File("stegoOutput.txt");

			if (args.length>1){
			//CALL UP A FILE FOR THE MESSAGE OUTPUT (SECOND ARGUMENT)
				file = new File(args[1]);
			} 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			//SET UP AND WRITE TO OUR MESSAGE TEXT FILE
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(extract(img));
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	static String extract(BufferedImage img)
	{
		String msg = "";		
		int scaleX = img.getWidth();
		int scaleY = img.getHeight();
		int x = 0, y = 0, RGB = 0, EOL = 0, bitCounter = 0, n=0;
		int newChar;
		int chars[] = new int[8];

		//ITERATE OVER THE Y-AXIS OF THE IMAGE
		while (y < (scaleY-1))
		{
			//ITERATE OVER THE X-AXIS OF THE IMAGE
			x = 0;
			while (x < (scaleX-1))
			{
				//RETRIEVE THE CURRENT PIXEL'S RGB VALUE
				RGB = img.getRGB(x,y);
				//STORE THE LSB OF THE PIXEL
				chars[bitCounter%8] = RGB & 0x01;
			
				//INCREMENT OUR X COUNTER
				x++;
				//INCREMENT THE BITCOUNTER
				bitCounter++;
				
				//IF WE'VE STORED A FULL CHAR, PROCEED
				if(bitCounter%8 == 0) 
				{
					//ZERO THE NEWCHAR INT
					newChar = 0;
					//ADD THE BITSHIFTED VALUE OF THE LSB'S ACCORDING TO THEIR ORIGINAL LOCATION IN THE CHAR
					for (int i = 0; i<8; i++){					
						newChar += chars[i]*(1<<(7-i));
					}
					//APPEND THE CHAR VALUE OF NEWCHAR TO OUR RETURN STRING
					msg += (char) newChar;
					
					//END OF MESSAGE DELIMITER ("nnnnn")
					if (newChar == 'n') EOL++;
					else EOL = 0;
					if (EOL > 4) return msg;
				}
			}
			y++;
		}

		return msg;
	}

}
