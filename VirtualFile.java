import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.Date;
import java.awt.*;
import javax.imageio.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/*
 * VirtualFile is the class that allows us to virtually represent 
 * files and folders (by theirnames, their size, ...).
 * 
 * We made a distinction between image files and the other types of file(text..) :
 * image files must enter a byte array as data in their VirtualFile constructor
 * and text fileshave String data.  A few more methods(getFileName(), ...)
 * are implemented in this class to modify the virtual files/folders attributes. 
 */

public class VirtualFile
    {

        private String filename, filesize, permissions, owner, group, data ;
        private Date lastModified;
        private byte[] imgdata;

		DateFormat dateFormatLIST = new SimpleDateFormat("MMM dd HH:mm",Locale.ENGLISH);
        DateFormat dateFormatMDTM = new SimpleDateFormat("yyyyMMddHHmmss");
        
        // VirtualFile which corresponds to a textFile (data in String)

        public VirtualFile(String filename, String filesize, String permissions, String owner, String group, String data ) {
            this.filename = filename;
            this.filesize = filesize;
            lastModified = new Date();
            this.permissions = permissions;
            this.owner = owner;
            this.group = group;
            this.data = data;
        }

        // VirtualFile which corresponds to a textFile (data in byte[])

        public VirtualFile(String filename, String filesize,String permissions, String owner, String group, byte[] imgdata ) {
            this.filename = filename;
            this.filesize = filesize;            
            lastModified = new Date();
            this.permissions = permissions;
            this.owner = owner;
            this.group = group;
            this.imgdata = imgdata;
        }

        // Methods below are implemented in order to provide usefull arguments to FTPCommandHandler

        public String getFileName(){
        	return filename;
        }

        public String getFileSize(){
        	return filesize;
        }

        public Date getlastModified(){
        	return lastModified;
        }

        public String getlastModifiedMDTM(){
        	return dateFormatMDTM.format(getlastModified());
        }

        public String getPermissions(){
        	return permissions;
        }

        public String getOwner(){
        	return owner;
        }

        public String getGroup(){
        	return group;
        }
        public String getData(){
        	return data;
        }

        public byte[] getimgData(){
        	return imgdata;
        }

        public void rename(String newname){
        	filename = newname;
        }

        public boolean isImg(){     // Check if file is an image file (by its extensions)

            String [] txt = filename.split("\\.");
            if (txt.length !=2){
                return false; 
            }
            String extension = txt[txt.length -1].toLowerCase();
            if(extension.equals("bmp") ||extension.equals("jpeg") ||extension.equals("jpg") ||extension.equals("png") || extension.equals("wbmp") || extension.equals("gif") )
                return true;
            return false;
        }

        public String fileFeatures(){

        	String features = getPermissions();
        	if(getFileSize().equals("4096")) 		// Folder Size recognition
        		features += " 3";
        	else
        		features += " 1";

        	features += " " + getOwner();
        	features += " " + getGroup();
        	features += " " + getFileSize();
        	features += " " + dateFormatLIST.format(getlastModified());
        	features += " " + getFileName();

        	return features;

        }

    }