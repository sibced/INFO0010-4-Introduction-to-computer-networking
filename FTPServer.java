import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.awt.*;
import javax.imageio.*;
import java.util.List;
import java.util.ListIterator;
import java.util.ArrayList;

/* 
 * The FTP Server class implements the main void
 * which creates the FTP server socket that accepts the client sockets.
 * 
 * Once a client is connected to the server he is redirected FTPCommandHandler thread. 
 * This class initializes two VirtualFile list structures
 * in which the initial files (mytext.txt, ...) are added.
 * 
 */

public class FTPServer {

	public static void main ( String args [ ] ){

		int maxThreads = 0;
		try {
            maxThreads = Integer.parseInt(args[0]); // The maximum number of thread
        }
        catch (NumberFormatException e1) {
            System.out.println(args[0] + " is not a number");
            System.exit(1);
		}

		// Files and folders initialization

		List<VirtualFile> fileList = new ArrayList<VirtualFile>() ;
		List<VirtualFile> folderList = new ArrayList<VirtualFile>() ;
		List<String> currDir = new ArrayList<String>() ;

        fileList.add(new VirtualFile("mytext.txt","12","-rw-------","Sam","group15",Data.mytextContent));
        fileList.add(new VirtualFile("myimage.bmp","326","-rw-------","Sam","group15",Data.myimageContent));
        fileList.add(new VirtualFile("private","4096","drw-------","Sam","group15",""));
        
        folderList.add (new VirtualFile("secret.txt","37","-rw-------","Sam","group15",Data.secretContent));
		
		// Current directory initialization
		
		currDir.add("/");

		try{
			ServerSocket server = new ServerSocket(2115);
			ExecutorService threadPool = null;
			try{
				threadPool = Executors.newFixedThreadPool(maxThreads);
			}
			catch(IllegalArgumentException i){
				System.out.println("Maximum number of threads cannot be negative ! " +args[0] + " is not valid");
				System.exit(1);
			}

			while (true) {
				Socket client = server.accept();
				client.setSoTimeout(15*60000);	// Time-out of 15 minutes
				FTPCommandHandler r = new FTPCommandHandler(client,fileList,folderList,currDir);
				threadPool.execute(r);
			}
		}
		catch(IOException e){
			System.out.println("SocketTimeoutException : Server has awaited for too long ");
		}
	}
}