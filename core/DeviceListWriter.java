/*
 * DeviceListWriter.java
 *
 */
package core;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.prefs.Preferences;


/**
 *
 * @author  Gerrit Gehnen
 * @version $Id$
 * @see DevicesConfig
 */
public final class DeviceListWriter {
    private boolean verbose = false;

	List<DeviceDescriptor> devs = new ArrayList<DeviceDescriptor>();
    

	public DeviceListWriter() {
    }

    /**
     * Build and append {@link DeviceDescriptor}s from a set of Java classes.
     * 
     * @param dir
     */
    public void addClasses(File codeDir, String pakkage) {
    	// Clean package references.
    	String pakkDir = pakkage.replace('.', File.separatorChar);
    	pakkage = pakkDir.replace('/', '.');
    	pakkage = pakkage.replace('\\', '.');
    	
    	// select only directories
    	File dir = new File(codeDir, pakkDir);
        File[] synthDirs = dir.listFiles(new SynthDirsFilter());

        if(verbose)
            System.out.println("In dir " + codeDir + ":");
        
    	// for all subdirectories = synthesizer models
        for (int i = 0; i < synthDirs.length; i++) {
    	    // select *Device.java
    	    File actSynthDir = new File(pakkDir, synthDirs[i].getName());
    	    String[] synthDevices = actSynthDir.list(new SynthFileFilter());
	        try {
		        MyClassLoader loader = new MyClassLoader(actSynthDir.getPath());
    	    	// for each Device class
	    	    for( int j = 0; j < synthDevices.length; j++) {
	        	    // get Device class name by removing the ".java" from the list of files
	    	        String devName = synthDevices[j].substring(0, synthDevices[j].indexOf('.'));
	        	    try {
	    	            if(verbose)
	    	                System.out.println("  Checking " + actSynthDir.getPath());
	        	    	
			    	    Class deviceclass = loader.loadClass(devName, true);
			            Device dev = (Device) deviceclass.newInstance();
			            devs.add(describe(dev));
			            
						if (verbose)
							System.out.println("    Found " + deviceclass.getName());
        		    } catch (Exception e) {
            			ErrorMsg.reportStatus(e);
    	        		ErrorMsg.reportStatus("Exception with " + devName);
        		    }
				}
			} catch (Exception e) {
				ErrorMsg.reportStatus(e);
			}
		}
    }
    
    public void writeProps(File outFile) throws FileNotFoundException {
    	Properties props = new Properties();
    	
    	for(DeviceDescriptor d : devs) {
    		setProperty(props, d);
    	}
    	
		// save into synthdrivers.properties
		FileOutputStream out = new FileOutputStream(outFile);
		try {
			props.store(out, "Generated devicesfile");
			out.close();
			System.out.println("Done!");
		} catch (Exception e) {
			ErrorMsg.reportStatus(e);
		}
    }

    /**
     * Experiment to write synths.html.
     * Not very good; there's no source for the short comments currently in synths.html,
     * and infoText is too large to use in its place.
     * Also no good way to test for editor (editPatch throws and/or hangs).
     * 
     * @param outFile
     * @throws FileNotFoundException
     */
    public void writeDocs(File outFile) throws FileNotFoundException {
    	Object[] empty = new Object[] {null};

		PrintStream out = new PrintStream(new FileOutputStream(outFile));
    	out.println("<html><head>");
    	out.println("<meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\">");
    	out.println("  <title>JSynthLib- Synths supported</title>");

    	out.println("</head><body leftmargin=\"0\" topmargin=\"0\" alink=\"Red\" bgcolor=\"#F4F4F4\" link=\"#3D75B4\" text=\"Black\" vlink=\"#315480\">");
    	out.println("<br><div align=\"center\">");

    	out.println("<table border=\"0\" width=\"600\"><tbody><tr>");
    	out.println("<tr><td><h2>Synths Supported</h2></td></tr>");
    	out.println("<tr><td>The current version of JSynthLib Supports the following Synthesizers:<br><br></td></tr>");
    	
    	out.println("<table border=\"1\" cellpadding=\"5\" cellspacing=\"0\" style=\"empty-cells:show\">");
    	
		out.println("<tr>");
		out.format("  <th>%s</td>\n", "Synth");
		out.format("  <th>%s</td>\n", "Patch<br>Librarian");
		out.format("  <th>%s</td>\n", "Bank<br>Librarian");
		out.format("  <th>%s</td>\n", "Patch<br>Editing");
		out.format("  <th>%s</td>\n", "Comments");
		out.println("</tr>\n");

		String YES = "x";
		String NO = "";
		String MAYBE = "?";
		
    	for(DeviceDescriptor dd : devs) {
    		String synth = dd.getDeviceName();
    		String patchLib = MAYBE, bankLib = MAYBE, patchEd = MAYBE;
    		
    		try {
	    		Class<?> dc = Class.forName(dd.getDeviceClass());
	    		// contruct(prefs) gives access to librarians and editors
	    		Device dev1 = (Device) dc.newInstance();
	    		
	    		synth = dev1.getManufacturerName() + " " + dev1.getModelName();
	    		
//	    		String comment = dev1.getInfoText();
//	    		comment = comment.replace("&", "&amp;");
//	    		comment = comment.replace("<", "&lt;");
//	    		comment = comment.replace(">", "&gt;");
//	    		comment = comment.replace("\r\n", "<br>");
//	    		comment = comment.replace("\n\r", "<br>");
//	    		comment = comment.replace("\n", "<br>");
//	    		comment = comment.replace("\r", "<br>");
	    		
	    		try {
		    		Device dev2 = (Device) dc.getConstructor(Preferences.class).newInstance(empty);
		    		
		    		for(int i=0; i<dev2.driverCount(); i++) {
		    			IDriver dr = dev2.getDriver(i);
		    			if(dr.isBankDriver())
		    				bankLib = YES;
		    			if(dr.isSingleDriver())
		    				patchLib = YES;
	//	    			if(dr instanceof Driver) {
	//	    				Driver dr2 = (Driver)dr;
	//	    				JSLFrame ed = dr2.editPatch(dr2.createNewPatch());
	//	    				if(ed != null)
	//	    					patchEd = YES;
	//	    				ed.dispose();
	//	    			}
		    		}
		    		
		    		if(MAYBE.equals(patchLib))
		    			patchLib = NO;
		    		if(MAYBE.equals(bankLib))
		    			bankLib = NO;
//		    		if(MAYBE.equals(patchEd))
//		    			patchEd = NO;
				} catch (Exception e) {
					ErrorMsg.reportStatus(synth);
					ErrorMsg.reportStatus(e);
				}

	    		out.println("<tr>");
	    		out.format("  <td>%s</td>\n", synth);
	    		out.format("  <td align=\"center\">%s</td>\n", patchLib);
	    		out.format("  <td align=\"center\">%s</td>\n", bankLib);
	    		out.format("  <td align=\"center\">%s</td>\n", patchEd);
	    		//infoText is far too large for this table
//	    		out.format("  <td>%s</td>\n", comment);
	    		out.println("</tr>\n");
	    		
			} catch (Exception e) {
				ErrorMsg.reportStatus(synth);
				ErrorMsg.reportStatus(e);
			}
    	}

    	out.println("</table>");
    	out.println("</tr></tbody></table></div>");
    	out.println("</body></html>");
    	out.close();
    }

    /**
     * Build a {@link DeviceDescriptor} from a {@link Device}.
     * 
     * @param dev
     * @return
     */
    public static DeviceDescriptor describe(Device dev) {
        String shortname = dev.getClass().getSimpleName();
        shortname = shortname.substring(0, shortname.lastIndexOf("Device"));
    	
        DeviceDescriptor d = new DeviceDescriptor();
        d.setDeviceClass(dev.getClass().getName());
        d.setDeviceName(dev.getManufacturerName() + " " + dev.getModelName() + " Driver");
        d.setIDString(dev.getInquiryID());
        d.setManufacturer(dev.getManufacturerName());
        d.setShortName(shortname);
        // Since Devices don't have types yet, just use the first letter of the manufacturer
        // so that we can test it.
        d.setType(d.getManufacturer().substring(0,1));

        return d;
    }
    
    private static void setProperty(Properties props, DeviceDescriptor dev) {
        String shortname = dev.getShortName();

        props.setProperty(Constants.PROP_PREFIX_DEVICE_CLASS + shortname,
        		dev.getDeviceClass());
        props.setProperty(Constants.PROP_PREFIX_MANUFACTURER + shortname,
        		dev.getManufacturer());
        props.setProperty(Constants.PROP_PREFIX_ID_STRING + shortname,
        		dev.getIDString());
        // saving only model name is better.
        props.setProperty(Constants.PROP_PREFIX_DEVICE_NAME + shortname,
        		dev.getDeviceName());
    }

    /** FilenameFilter which select <code>*Device.class</code>. */
    private static class SynthFileFilter implements FilenameFilter {
    	public SynthFileFilter() {
	    }

    	public boolean accept (File dir, String name) {
// 	        return ((name.endsWith ("Driver.class")||name.endsWith("Converter.class"))
// 		        && name.indexOf ('$')==-1);
            return ((name.indexOf('$') == -1) &&
                    (name.endsWith("Device.java") || name.endsWith("Device.class")));
	    }
    } // SynthFileFilter

    /** FileFilter which select a directory. */
    private static class SynthDirsFilter implements FileFilter {
    	public SynthDirsFilter() {
    	}

    	public boolean accept(File dir) {
	        return (dir.isDirectory());
	    }
    } // class SyntDirsFilter

    /**
     * This class loader uses an alternate directory for loading
     * classes.  When a class is resolved, its class loader is
     * expected to be able to load any additional classes, but this
     * loader doesn't want to have to figure out where to find
     * java.lang.Object, for instance, so it uses Class.forName to
     * locate classes that the system already knows about.<p>
     *
     * Created on 12. September 1999, 00:30
     */
    private static class MyClassLoader extends ClassLoader {
        private String classDir; // root dir to load classes from
        private Hashtable loadedClasses; // Classes that have been loaded

        public MyClassLoader(String classDir) {
            this.classDir = classDir;
            loadedClasses = new Hashtable();
        }

        public synchronized Class loadClass(String className,
                            boolean resolve) throws ClassNotFoundException {
            //System.out.println("loadClass: "+className);
            Class newClass = findLoadedClass(className);
            if (newClass != null)
                return newClass;

            // If the class was in the loadedClasses table, we don't
            // have to load it again, but we better resolve it, just
            // in case.
            newClass = (Class) loadedClasses.get(className);

            if (newClass != null) {
                if (resolve) {  // Should we resolve?
                    resolveClass(newClass);
                }
                return newClass;
            }

            try {
                // Read in the class file
                byte[] classData = getClassData(className);
                // Define the new class
                newClass = defineClass(null, classData, 0, classData.length);
            } catch (IOException readError) {
                // Before we throw an exception, see if the system
                // already knows about this class
                try {
                    newClass = findSystemClass(className);
                    return newClass;
                } catch (Exception any) {
                    throw new ClassNotFoundException(className);
                }
            }

            // Store the class in the table of loaded classes
            loadedClasses.put(className, newClass);

            // If we are supposed to resolve this class, do it
            if (resolve) {
            resolveClass(newClass);
            }

            return newClass;
        }

        // This version of loadClass uses classDir as the root directory
        // for where to look for classes, it then opens up a read stream
        // and reads in the class file as-is.

        protected byte[] getClassData(String className)
            throws IOException {
            // Rather than opening up a FileInputStream directly, we create
            // a File instance first so we can use the length method to
            // determine how big a buffer to allocate for the class

            File classFile = new File(classDir, className + ".class");

            byte[] classData = new byte[(int) classFile.length()];

            // Now open up the input stream
            FileInputStream inFile = new FileInputStream(classFile);

            // Read in the class
            int length = inFile.read(classData);

            inFile.close();

            return classData;
        }
    } // class MyClassLoader

    /**
     * @param args the command line arguments
     * @throws FileNotFoundException if unable to create output file
     */
    public static void main(String[] args) throws FileNotFoundException {
    	DeviceListWriter d = new DeviceListWriter();
    	
        if(args.length>=1 && args[0].equals("-v")) {
            d.verbose  = true;
            ErrorMsg.setDebugLevel(ErrorMsg.DEBUG_MSG);
        }

        d.addClasses(new File("."), "synthdrivers");
        d.writeProps(new File(".", Constants.RESOURCE_NAME_DEVICES_CONFIG));
//        d.writeDocs(new File(".", "synths.html"));
        
        System.exit(0);
    }
}
