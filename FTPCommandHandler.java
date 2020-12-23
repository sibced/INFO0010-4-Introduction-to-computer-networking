import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.Date;
import java.util.Random;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.ListIterator;
import java.util.List;
import java.util.ArrayList;

/*
 * FTPCommandHandler is the thread class that handles all the requests 
 * that the considered client might send to the server.
 * 
 * It reads the FTP commands that the client sends in the stream then 
 * it sends a customized three-digit FTP response to the client. 
 */ 

public class FTPCommandHandler extends Thread{

    // Initialization

    private Socket client;
    private List<VirtualFile> fileList = new ArrayList<VirtualFile>() ;
    private List<VirtualFile> folderList = new ArrayList<VirtualFile>() ;
    private List<String> currDir = new ArrayList<String>() ;


    public FTPCommandHandler(Socket client, List<VirtualFile> fileList, List<VirtualFile> folderList, List<String> currDir){
        this.client = client;
        this.fileList = fileList;
        this.folderList = folderList;
        this.currDir = currDir;
    }

    // Initialization of the variables which are created inside the class

    private BufferedReader read;

    private PrintWriter controlResponse;
    private boolean validUser = false;
    private String user = "";
    private boolean login = false;

    private ServerSocket dataSocket = null;
    private Socket dataClient = null;
    private boolean dataConnection = false;
    private String dataType = "";

    private String oldName = null;
    private boolean rnfr = false;

    // Booleans and Strings denote properly the current state of the thread
    // For example, a true validUser means that the user entered is valid

    @Override
    public void run(){

        getCommand();   //TCP Stream
        try{
            controlResponse = new PrintWriter(client.getOutputStream(),true);
            welcomeMessage();   // Welcome message send to the client

            while(true){
                String readCommand = read.readLine();
                System.out.println("Command: "+readCommand);
                System.out.println(" ");

                String[] command = null;

                // readCommand is split in order to generate two distinct strings
                // e.g. "USER Sam" is splitted in "USER" and "Sam"

                try{
                    command = readCommand.split(" ");
                }
                catch(NullPointerException npe){
                    System.out.println("-- Can't read further commands because of client's bad behavior !");
                }

                // Initialization

                String method = command[0]; // Method of the command
                String parameter = "";
                boolean noArgument = true;
                boolean validArgument = true;
                boolean recognized = false;

                

                if(command.length==2)
                    parameter = command[1]; // Parameter of command

                if(!login){                 // USER and PASS are the mandatory first commands
                    switch(method){
                        case "USER":
                        recognized = true;  // If the command is a known command, recognize = true
                        if(parameter.isEmpty()){
                            validArgument = false; // If there is no parameter entered, validArgument = false
                            break;
                        }
                        userHandler(parameter); // Void USER implemented below
                        break;

                        case "PASS":           // Void PASS implemented below
                        recognized = true;
                        if(parameter.isEmpty()){
                            validArgument = false;
                            break;
                        }
                        passHandler(parameter);
                        break;
                        
                        default:
                        controlResponse.println("530 Need to log in first"); // Default message
                        break;
                    }
                }
                if(login){                      // If userName and password are valid
                    switch(method){

                        case "TYPE":            // void TYPE implemented below 
                        recognized = true;
                        if(parameter.isEmpty()){
                            validArgument = false;
                            break;
                        }
                        typeHandler(parameter);
                        break;

                        case "PORT":            // void PORT implemented below
                        recognized = true;
                        if(parameter.isEmpty()){
                            validArgument = false;
                            break;
                        }
                        portHandler(parameter);
                        break;

                        case "LIST":            // void LIST implemented below
                        recognized = true;
                        listHandler(parameter);
                        break;

                        case "MDTM":            // void MDTM implemented below
                        recognized = true;
                        if(parameter.isEmpty()){
                            validArgument = false;
                            break;
                        }
                        mdtmHandler(parameter);
                        break;

                        case "CWD":             // void CWD implemented below
                        recognized = true;
                        if(parameter.isEmpty()){
                            validArgument = false;
                            break;
                        }
                        cwdHandler(parameter);
                        break;

                        case "DELE":            // void DELE implemented below
                        recognized = true;
                        if(parameter.isEmpty()){
                            validArgument = false;
                            break;
                        }
                        deleHandler(parameter);
                        break;

                        case "RNFR":            // void RNFR implemented below
                        recognized = true;
                        if(parameter.isEmpty()){
                            validArgument = false;
                            break;
                        }
                        rnfrHandler(parameter);
                        break;

                        case "RNTO":            // void RNTO implemented below
                        recognized = true;
                        if(parameter.isEmpty()){
                            validArgument = false;
                            break;
                        }
                        rntoHandler(parameter);
                        break;
                    
                        case "SYST":            // void SYST implemented below
                        recognized = true;
                        if(!parameter.isEmpty()){
                            noArgument = false;
                            break;
                        }
                        systHandler();
                        break;

                        case "FEAT":            // void FEAT implemented below
                        recognized = true;
                        if(!parameter.isEmpty()){
                            noArgument = false;
                            break;
                        }
                        featHandler();
                        break;
                        
                        case "PWD":             // void PWD implemented below
                        recognized = true;
                        if(!parameter.isEmpty()){
                            noArgument = false;
                            break;
                        }
                        pwdHandler();
                        break;

                        case "PASV":            // void PASV implemented below
                        recognized = true;
                        if(!parameter.isEmpty()){
                            noArgument = false;
                            break;
                        }
                        pasvHandler();
                        break;

                        case "EPSV":            // void EPSV implemented below
                        recognized = true;
                        if(!parameter.isEmpty()){
                            noArgument = false;
                            break;
                        }
                        epsvHandler();
                        break;

                        case "CDUP":            // void CDUP implemented below
                        recognized = true;
                        if(!parameter.isEmpty()){
                            noArgument = false;
                            break;
                        }
                        cdupHandler();
                        break;

                        case "RETR":               // void RETR implemented below
                        recognized = true;
                        if(parameter.isEmpty()){
                            validArgument = false;
                            break;
                        }
                        retrHandler(parameter);
                        break;

                        case "STOR":                // void STOR implemented below
                        recognized = true;
                        if(parameter.isEmpty()){
                            validArgument = false;
                            break;
                        }
                        storHandler(parameter);
                        break;


                        default:
                        break;
                    }
                                   
                }

                // If the command hasn't been recognized

                if(!recognized)
                    controlResponse.println("502 Command not recognized or not valid");
                    
                // If the command had extra parameter

                if(!noArgument)
                    controlResponse.println("504 Command doesn't take extra parameters");
                    
                // If the command hadn't parameter entered

                if(!validArgument && recognized)
                    controlResponse.println("501 Command needs parameter"); 
                
                    
                
            }


        }
        catch(IOException ie1){
            System.out.println("Can't read commands");
        }
    }

    // Reads commands sent by the client

    public void getCommand(){ 
        try{
            InputStream in = client.getInputStream();
            read = new BufferedReader( new InputStreamReader(in));
        }
        catch(IOException ie2){
            System.out.println("Server cannot read client's command");
        }
    }

    // Gets commands sent by the client

    public void welcomeMessage(){
        controlResponse.println("220 Welcome Hiard-sensei! Please log in.");
    }
    
    //----------------------------------- AUTHENTICATION ------------------------------------|
    /*
     *Handles USER Command
     *Arguments : 'username' = Username sent by client    
     */
    public void userHandler(String username){

        

        if ( (username.equals("Sam") || username.equals("anonymous")) ) {
            validUser = true;
            user = username;
	        controlResponse.println("331 Password required");
	    }

	    else if( !(username.equals("Sam") || username.equals("anonymous")) ){
	    	controlResponse.println("430 Username not allowed");
        }

        
        	    
    }

    /*
     *Handles USER Command
     *Arguments : 'password' = Password sent by client    
     */
    public void passHandler(String password){

        

    	if(validUser){              //check if user command with valid username was previously sent

    		if(user.equals("Sam")){ //if username is 'Sam', password must be 123456
    			if(password.equals("123456")){
    				login = true;
    				controlResponse.println("230 Successfully connected");
    			}
    			else{
                    controlResponse.println("430 Wrong password, retype username and password");
                    //231 pour le déconnecter ??
    				user = "";
                	validUser = false;
    			}
    		}

    		else{
    			login = true;       // If user is anonymous, we don't care about password
    			controlResponse.println("230 Successfully connected");
    		}

    	}
    	else
            controlResponse.println("530 Username not valid");
    }

    //----------------------------------- CONNEXION SUCCEEDED -----------------------------------|

    //-------------------------------------- OPERATIONS ---------------------------------------|

    /*
     *Handles SYST command : informs client about system type
     */
    public void systHandler(){

    	controlResponse.println("215 UNIX Type: L8");
    }

    /*
     *Handles FEAT command : Tells client what extra commands the server implements
     */
    public void featHandler(){
        controlResponse.println("211-Features");
        controlResponse.println("MDTM");    //sends MDTM because MDTM isn't in RFC 959
        controlResponse.println("211 END");
    }

    public void pwdHandler(){
        
        controlResponse.println("257 \""+currDir.get(currDir.size()-1)+"\" created");
    }

    /*
     *Handles TYPE command : informs client about data transfert type (ASCII or binary)
     *Arguments : 'type' = data transfert type    
     */
    public void typeHandler(String type){
        

    	if(!type.equals("A") && !type.equals("I") ){        //If type is neither ASCII or binary
        	controlResponse.println("501 Unrecognized parameter ");//=>Error
        }

    	else if(type.equals("I")){                          //If type is binary
            dataType = "Binary";
            controlResponse.println("200 Transfert mode set to "+type);
        }
        else if(type.equals("A")){                          //If type is ASCII
        	dataType = "ASCII";
        	controlResponse.println("200 Transfert mode set to "+type);
        }
        
    }

    /*
     *Handles PASV command : Establishes data connection in passive mode
     */
    public void pasvHandler(){
        

        int x = new Random().nextInt(100-4) + 4;    //Selects x >= 4 so that portData > 1023
        int y = new Random().nextInt(300);
        int portData = x*256 + y;                   //portData = random port > 1023

        try{
            InetAddress inetAddress = InetAddress.getLocalHost(); // IP adress of the server
            String ipAddress = inetAddress.getHostAddress().replace('.',',');//replaces all occurrences of '.' to ','
            controlResponse.println("227 Entering Passive Mode ("+ipAddress+","+x+","+y+")");

            dataSocket = new ServerSocket(portData); //Creates server socket for files data connection
	        dataClient = dataSocket.accept();         //Socket for client in data connection
	        dataConnection = true;
	    }
	    catch(IOException ie3){
	    	System.out.println("Problem with data connection creation in passive mode");
	    }
    }

    /*
     *Handles EPSV command : Establishes data connection in extended passive mode
     *Same as PASV, except that IP adress of the server is not sent
     */
    public void epsvHandler(){

	    int x = new Random().nextInt(100-4) + 4;    //Selects x >= 4 so that portData > 1023
	    int y = new Random().nextInt(300);
    	int portData = x*256 + y;                   //portData = random port > 1023

    	controlResponse.println("229 Entering Extended Passive Mode (|||"+portData+"|)");
        try{
	        dataSocket = new ServerSocket(portData);
	        dataClient = dataSocket.accept();
	        dataConnection = true;
	    }
	    catch(IOException ie4){
	    	System.out.println("Problem with data connection creation in extended passive mode");
	    }
    }

    /*
     *Handles PORT command : Establishes data connection in active passive mode on port given by client
     *Argument : 'ipPort' = IP adress of the client + port number (given by client) 
     *                       on which server must establish active data connection
     */
    public void portHandler(String ipPort){
    	String ip = "";
        ip = ipPort.replace('(',',');
        ip = ip.replace(')',',');

        String[] portInfo = ip.split(",");
        String clientIP = portInfo[0]+'.'+portInfo[1]+'.'+portInfo[2]+'.'+portInfo[3];  //IP adress of the client
        InetAddress serverIP = null;
        try{
            serverIP = InetAddress.getLocalHost(); // IP adress of the server
        }
        catch(UnknownHostException uhe1){
            System.out.println("Problem when trying to get server's IP for active data connection");
        }
        
        int portX = Integer.parseInt(portInfo[4]);
        int portY = Integer.parseInt(portInfo[5]);
        int portData = portX*256 + portY;

        if(portData<=1023)      // Checking validity of the entered port
            controlResponse.println("504 Port number must be greater than 1023");
        else{
            try{                // Establishing data connections 
                dataClient = new Socket();
                dataClient.setReuseAddress(true);
                dataClient.bind(new InetSocketAddress(serverIP, 2015));
                dataClient.connect(new InetSocketAddress(clientIP, portData));
            }
            catch(IOException ie5){
                System.out.println("Problem with socket creation in active mode");
            }
            dataConnection = true;
            controlResponse.println("200 Active data connection created");
        }
    }

    /*
     *Handles EPRT command : Establishes data connection in extended active passive mode on port given by client
     *Argument : 'ipPort' = IP adress of the client + port number (given by client) 
     *                       on which server must establish extended active data connection
     */
    public void eprtHandler(String ipPort){
    	String[] eprt = ipPort.split("\\|");
        int portData = Integer.parseInt(eprt[2]);
        if(portData<=1023)
            controlResponse.println("504 Port number must be greater than 1023");
        else{
            try{
                InetAddress serverIP = InetAddress.getLocalHost(); // Server's IP adress
                dataClient = new Socket(eprt[1], portData, serverIP, 2015);
            }
            catch(IOException ie6){
                System.out.println("Problem with socket creation in extended active mode");
            }
            dataConnection = true;
            controlResponse.println("200 Extended active data connection created");
        }

    }

    /*
     *Handles LIST command : lists content of given directory
     *Argument : 'directory' = directory which contains what has to be listed
     *             if directory = null => server lists content of current directory 
     */
    public synchronized void listHandler(String directory){

        if(dataConnection){

            PrintWriter dataResponse = null;
            try{
                dataResponse =  new PrintWriter(dataClient.getOutputStream(),true);
            }
            catch(IOException ie6){
                System.out.println("Problem in listHandler method");
                ie6.getStackTrace();
            }
        
            controlResponse.println("125 Listing initation...");
            
            if(directory.isEmpty()){    //If no parameter, must list content of current directory

                if (currDir.get(currDir.size()-1).equals("/")){ // Checking if we are currently in "/"

                    for(VirtualFile currentFile : fileList){
                        if(currentFile.getFileName().equals("private")){
                            if(user.equals("Sam"))  //Only Sam can see private folderList
                                dataResponse.println(currentFile.fileFeatures());
                        }
                        else
                            dataResponse.println(currentFile.fileFeatures());
                    }
                    controlResponse.println("250 Listing ok.");
                }
                else if (currDir.get(currDir.size()-1).equals("/private")){  // Checking if we are currently in "/private"

                    for(VirtualFile currentFile : folderList)
                        dataResponse.println(currentFile.fileFeatures());
                    controlResponse.println("250 Listing ok.");
                }
            }
            else if(directory.equals("/")){ // Checking if the given directory is "/"
                for(VirtualFile currentFile : fileList){
                    if(currentFile.getFileName().equals("private")){
                        if(user.equals("Sam"))  //Only Sam can see private folderList
                            dataResponse.println(currentFile.fileFeatures());
                    }
                    else
                        dataResponse.println(currentFile.fileFeatures());
                }
                controlResponse.println("250 Listing ok.");
            }
            else if(directory.equals("/private")){  /// Checking if the given directory is "/private"
                for(VirtualFile currentFile : folderList)
                        dataResponse.println(currentFile.fileFeatures());
                controlResponse.println("250 Listing ok.");
            }
            else
                controlResponse.println("504 Folder specified doesn't exist");  // In case of an unknown given directory
            
            try{
                dataConnection = false;
                dataClient.close();
                if(dataSocket!=null)
                    dataSocket.close();             // dataSocket was only open in (extended) passive mode
            }
            catch(IOException ie7){
                System.out.println("Problem when closing data connection");
            }
        }
        else
            controlResponse.println("503 Data connection needs to be opened first");
    }
    
    /*
     *Handles MDTM command : informs server on latest modified date of a given file
     *Argument : 'filename' = name of file
     */
    public void mdtmHandler(String filename){

        if ( !existsInDir(filename) )   //Can't get latest modified date of file that doesn't exist
            controlResponse.println("550 File "+filename+" not found in current directory");

        if (currDir.get(currDir.size()-1).equals("/")){

            for(VirtualFile currentFile : fileList){

                if(currentFile.getFileName().equals(filename))  //We go through list to find 'filename'
                    controlResponse.println(currentFile.getlastModifiedMDTM());
        	}   
        }
        
        else if (currDir.get(currDir.size()-1).equals("/private")){

            for(VirtualFile currentFile : folderList){

                if(currentFile.getFileName().equals(filename))
                    controlResponse.println(currentFile.getlastModifiedMDTM());
        	}   
        }
    }

    /*
     *Handles CDUP command : change to parent directory
     */
    public void cdupHandler(){

    	if(currDir.get(currDir.size()-1).equals("/private")){
            currDir.remove(currDir.size()-1);
            controlResponse.println("200 Changed to parent directory.");
        }
        else
            controlResponse.println("500 The directory has no parent.");
    }

    /*
     *Handles CWD command : change working directory to specified directory
     *Argument : 'directory' = directory that clients wants to visit
     */
    public void cwdHandler(String directory){        

    	if(directory.equals("private"))
            directory = "/private";         

        if (directory.equals("/private") || directory.equals("/")){

            // If the directory to add is private and the current directory isn't private

            if (directory.equals("/private") && !currDir.get(currDir.size()-1).equals("/private"))
                currDir.add(directory);

            // Else, we remove "/private" from the directory list
            
            if (directory.equals("/") && currDir.get(currDir.size()-1).equals("/private"))
                currDir.remove(currDir.size()-1);

            controlResponse.println("250 Working directory changed.");
        }
        else if (directory.equals("..")){       // In case of a CDUP .. command
            if (currDir.size() == 1){
                controlResponse.println("500 Impossible to access to this parent directory.");
            }
            else{
                currDir.remove(currDir.size()-1);   // Remove the last directory in the list
                controlResponse.println("250 Working directory changed.");
            }

        }
        else{
            controlResponse.println("500 The directory mentioned doesn't exist.");

        }
    }

    /*
     *Handles DELE command : delete specified file
     *Argument : 'filename' = file to delete
     */
    public synchronized void deleHandler(String filename){

        //boolean deleted = false;
        if ( !existsInDir(filename) )   //Can't delete file that's not in current directory
            controlResponse.println("550 File "+filename+" doesn't exist in current directory");
        else{

            if (currDir.get(currDir.size()-1).equals("/")){
                int i = 0;  //Save index of current file in fileList list
                for(VirtualFile currentFile : fileList){

                    if(currentFile.getFileName().equals(filename)){
                        fileList.remove(i);
                        controlResponse.println("250 File deleted");
                        break;
                    }
                    i = i+1;
                }   
            }
            else if (currDir.get(currDir.size()-1).equals("/private")){
                int i = 0;  //Save index of current file in folderList list
                for(VirtualFile currentFile : folderList){

                    if(currentFile.getFileName().equals(filename)){
                        folderList.remove(i);
                        controlResponse.println("250 File deleted");
                        break;
                    }
                    i = i+1;
                }
            }
        }
    }

    /*
     *Handles RNFR command : renames specified filename from..
     *Argument : 'filename' = file to rename
     */
    public synchronized void rnfrHandler(String filename){
        
        if ( !existsInDir(filename) )   //Can't rename file that doesn't exist
            controlResponse.println("550 File "+filename+" doesn't exist in current directory");
        else{

            if(filename.equals("private"))  //Server doesn't handle folders renaming
                controlResponse.println("504 Server cannot rename folders.");
            else{
                controlResponse.println("350 New name required (use RNTO).");

                oldName = filename;
                rnfr = true;
            }
        }
    }

    /*
     *Handles RNTO command : renames file to..
     *Argument : 'filename' = new name
     */
    public synchronized void rntoHandler(String filename){
        
        if(!rnfr)
            controlResponse.println("503 You need to specify which file you want to rename (use RNFR)");    //can't do RNTO without RNFR
        else{

            if (currDir.get(currDir.size()-1).equals("/")){

                for(VirtualFile currentFile : fileList){

                    if(currentFile.getFileName().equals(oldName)){      
                        currentFile.rename(filename);                                     //changes name in fileList
                        controlResponse.println("250 File renamed");
                        break;
                    }
                    
                }   
            }
            else if (currDir.get(currDir.size()-1).equals("/private")){

                for(VirtualFile currentFile : folderList){

                    if(currentFile.getFileName().equals(oldName)){

                        currentFile.rename(filename);                                    //changes name in folderList
                        controlResponse.println("250 File renamed");
                        break;
                    }
                }
            }
            
        }

    }
    
    /*
     *Handles RETR command : download file..
     *Argument : 'filename' = name of file to download
     */    
    public synchronized void retrHandler(String filename){
        if(dataConnection){

            if ( !existsInDir(filename) )   //Can't download file that doesn't exist
                    controlResponse.println("550 File "+filename+" doesn't exist in current directory");         
            else{
                try{ 
                    //Streams initalization for downloading

                    BufferedOutputStream binaryData = new BufferedOutputStream(dataClient.getOutputStream()); 
                    PrintWriter asciiData = new PrintWriter(dataClient.getOutputStream());
                    controlResponse.println("150 File downloading");



                    if (currDir.get(currDir.size()-1).equals("/")){     // If the current directory is "/"

                        for(VirtualFile currentFile : fileList){

                            if(currentFile.getFileName().equals(filename)){
                                if(dataType.equals("Binary")){  //dataType = binary
                                    if(currentFile.isImg()){   //check if image
                                        binaryData.write(currentFile.getimgData());
                                        break;
                                    }
                                    else{   
                                       
                                        binaryData.write(currentFile.getData().getBytes("UTF8"));
                                        break;
                                    }
                                }
                                else{   //dataType = ASCII
                                    if(currentFile.isImg()){   //check if image
                                        
                                        asciiData.print(new String(currentFile.getimgData()) ) ;    //Corruption of file
                                        asciiData.flush();
                                        break;
                                    }
                                    else{   
                                        asciiData.print(currentFile.getData());
                                        asciiData.flush();
                                        break;
                                    }

                                }
                            }
                        }
                        controlResponse.println("226 Download successful");   
                    }           
                    else if (currDir.get(currDir.size()-1).equals("/private")){ // If the current directory is "/private"

                        for(VirtualFile currentFile : folderList){

                            if(currentFile.getFileName().equals(filename)){
                                if(dataType.equals("Binary")){  //dataType = binary
                                    if(currentFile.isImg()){   //check if image
                                        
                                        binaryData.write(currentFile.getimgData());
                                        break;
                                    }
                                    else{
                                        
                                        binaryData.write(currentFile.getData().getBytes("UTF8"));
                                        break;
                                    }
                                }
                                else{   //dataType = ASCII
                                    if(currentFile.isImg()){   //check if image
                                        
                                        asciiData.print(new String(currentFile.getimgData()) ) ;
                                        asciiData.flush();
                                        break;
                                    }
                                    else{   
                                        asciiData.print(currentFile.getData());
                                        asciiData.flush();
                                        break;
                                    }

                                }
                            }
                        }
                        controlResponse.println("226 Download successful. Closing data connection");
                    }
                    binaryData.close(); 
                    asciiData.close();
                    dataConnection = false;         //data connection must be closed after RETR operation
                    dataClient.close();
                    if(dataSocket!=null)
                        dataSocket.close();
                }

                catch(IOException ie8){
                    System.out.println("Problem encountered while downloading file");
                }
            }
        }
        else
            controlResponse.println("503 Data connection must be opened first");
        

    }

    /*
     *Handles STOR command : upload file..
     *Argument : 'filename' = name of file to download
     */    
    public synchronized void storHandler(String filename){
        
        if(dataConnection){
            VirtualFile upFile = null;
            controlResponse.println("150 File downloading");
            if(dataType.equals("ASCII")){
             
                // Initalization

                String str = "";
                String data = "";
                int byteCount=0;
                try{
                    BufferedReader fileRead = new BufferedReader( new InputStreamReader(dataClient.getInputStream()) );
                    while(str!=null ){      // Till it reaches the end of the file
                        str=fileRead.readLine();
                        byteCount = byteCount + str.length();
                        if(fileRead.ready())
                            data = data + str +"\r\n";
                        else{
                            data = data + str;  // It doesn't jump a line if this is the last line of the file
                            break;
                        }
                    }
                    fileRead.close();
                }
                catch(IOException ie9){
                    System.out.println("Problem encountered while reading uploaded file in ASCII mode");
                }

                // New file initialization
                
                upFile = new VirtualFile(filename,Integer.toString(byteCount),"-rw-------",user,"group15","");
                if(upFile.isImg())  //Corruption of image file because ASCII transfer
                    upFile = new VirtualFile(filename,Integer.toString(byteCount),"-rw-------",user,"group15",data.getBytes());     //If file is image, data must be in bytes
                else
                    upFile = new VirtualFile(filename,Integer.toString(byteCount),"-rw-------",user,"group15",data);                //If file is text, data must be characters
            }
            else{
                try{
                    BufferedInputStream fileRead = new BufferedInputStream( dataClient.getInputStream() );
                    byte data[] = new byte [fileRead.available()];  //Creates array of approximate number of bytes that can be read from the input stream
                    int fileLength= fileRead.read(data, 0, data.length);
                    
                    fileRead.close();
                    upFile = new VirtualFile(filename, Integer.toString(fileLength), "-rw-------", user,"group15", "");
                    if(upFile.isImg())
                        upFile = new VirtualFile(filename, Integer.toString(fileLength), "-rw-------", user,"group15", data);
                    else
                        upFile = new VirtualFile(filename, Integer.toString(fileLength), "-rw-------", user,"group15", new String(data));
                }
                catch(IOException ie10){
                    System.out.println("Problem encountered while reading uploaded file in binary mode");
                }
            }
            controlResponse.println("226 Upload successful. Closing data connection");
            if (currDir.get(currDir.size()-1).equals("/")){
                fileList.add(upFile);
            }
            else if (currDir.get(currDir.size()-1).equals("/private")){
                folderList.add(upFile);
            }

            dataConnection = false;
            try{
                dataClient.close();         //data connection must be closed after STOR operation
                if(dataSocket!=null)
                    dataSocket.close();
            }
            catch(IOException ie11){
                System.out.println("Problem encountered when closing data connection");
            }
        }
        else
            controlResponse.println("503 Data connection must be opened first");

    }

    /*
     *Returns true if file 'filename' exists in current directory
     *        false if 'filename' doesn't exist in current directory
     *Arguments : 'filename' = name of file
     */
    public boolean existsInDir(String filename){
        boolean exists = false;
        

        if( currDir.get(currDir.size()-1).equals("/") ){
            for(VirtualFile currentFile : fileList){
                
                if(currentFile.getFileName().equals(filename)){
                    exists=true;
                    break;
                }
            }
        }
        else if(currDir.get(currDir.size()-1).equals("/private") ){
            for(VirtualFile currentFile : folderList){
                
                if(currentFile.getFileName().equals(filename)){
                    exists=true;
                }
            }
        }
        return exists;
    }


}
            
