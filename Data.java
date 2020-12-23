import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.Date;
import java.util.Random;
import java.awt.*;
import javax.imageio.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.List;
import java.util.ListIterator;
import java.util.ArrayList;

/* 
 * Data is the class containing the data of the initial virtual files and folders.
 */

public class Data
{
	final static String secretContent = "UPUPDOWNDOWNLEFTRIGHTLEFTRIGHTBASTART";

	final static String mytextContent = "Irasshaimase";
	
	final static byte[] myimageContent = {66,  77,  70,  1,  0,  0,    0,   0,   0,   0,  62,   0,   0,  0,   40,   0,
		0,   0,  34,  0,  0,  0,   33,   0,   0,   0,   1,   0,   1,  0,    0,   0,
		0,   0,   8,  1,  0,  0,    0,   0,   0,   0,   0,   0,   0,  0,    0,   0,
		0,   0,   0,  0,  0,  0,    0,   0,   0,   0,  -1,  -1,  -1,  0,   -1,  -1,
	   -1,  -1, -64,  0,  0,  0,   -1, -32,   0,   1, -64,   0,   0,  0, -128,  31,
	   -1,  -2, -64,  0,  0,  0,  -65, -33,  -1,  -2, -64,   0,   0,  0,  -65, -33,
	   -1,  -2, -64,  0,  0,  0,  -65, -33,  -1,  -8, -64,   0,   0,  0,  -65, -33,
	   -1,  -2, -64,  0,  0,  0,  -65, -33,  -1,  -2, -64,   0,   0,  0,  -65, -33,
	   -1,  -2, -64,  0,  0,  0,  -65, -33,  -1,  -2, -64,   0,   0,  0,  -65, -33,
	   -1,  -2, -64,  0,  0,  0,  -65, -33,  -1,  -8, -64,   0,   0,  0,  -65, -33,
	   -1,  -2, -64,  0,  0,  0,  -65, -33,  -1,  -2, -64,   0,   0,  0,  -65, -33,
	   -1,  -2, -64,  0,  0,  0,  -65, -33,  -1,  -2, -64,   0,   0,  0,  -65, -33,
	   -1,  -2, -64,  0,  0,  0, -128,  31,  -1,  -8, -64,   0,   0,  0,   -1, -17,
	   -1,  -2, -64,  0,  0,  0,   -1,  -9,  -1,  -2, -64,   0,   0,  0,   -1,  -5,
	   -1,  -2, -64,  0,  0,  0,   -1,  -3,  -2,   0, -64,   0,   0,  0,   -1,  -2,
	   -3,  -1, -64,  0,  0,  0,   -1,  -1, 126,  -1, -64,   0,   0,  0,   -1,  -1,
	  127, 127, -64,  0,  0,  0,   -1,  -1, -65, 127, -64,   0,   0,  0,   -1,  -1,
	  -33, -65, -64,  0,  0,  0,   -1,  -1, -17, -65, -64,   0,   0,  0,   -1,  -1,
	  -17, -33, -64,  0,  0,  0,   -1,  -1,  -9, -33, -64,   0,   0,  0,   -1,  -1,
	   -9, -33, -64,  0,  0,  0,   -1,  -1,  -8,  31, -64,   0,   0,  0,   -1,  -1,
	   -1,  -1, -64,  0,  0,  0
	   
	};

		
}
