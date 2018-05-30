package com.company;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;




public class Main
{

    public static void main(String[] args) throws IOException {

        // write your code here
        File ctest = new File("c:\\test");
        if (!ctest.exists()) {
            System.out.println("Not found");
        } else {
            Date date = new Date();
            String datestring = new SimpleDateFormat("yyyyMM").format(date);
            try {
                System.setErr(new PrintStream(new File("c:\\test\\log\\" + datestring + "_testtask.txt")));
            } catch (FileNotFoundException e) {
                File dir = new File("c:\\test\\log\\");
                dir.mkdir();
                File f = new File("c:\\test\\log\\" + datestring + "_testtask.txt");
                f.createNewFile();
                System.setErr(new PrintStream(new File("c:\\test\\log\\" + datestring + "_testtask.txt")));
            }

            String[] filenamelist;
            filenamelist = (ctest.list());
            ArrayList<fileinfo> Mainlist = new ArrayList<>();
            for (int i = 0; i < filenamelist.length; i++) {
                File f = new File(ctest + File.separator + filenamelist[i]);
                File f2 = new File(getxmltxtpath(f));
                if (f.isFile() && f.getName().endsWith(".pdf") && f2.isFile())
                {
                    fileinfo current = new fileinfo();
                    current.setName(f.getName());
                    current.setMainPath(f.getPath());
                    current = Setfileinfo(current.name, current);
                    Mainlist.add(current);

                    fileinfo twin = new fileinfo();
                    twin.setName(f2.getName());
                    twin.setMainPath(f2.getPath());
                    twin = Setfileinfo(twin.name, twin);
                    Mainlist.add(twin);
                }
            }
            for (fileinfo item : Mainlist)
            {
                Path source = Paths.get((item.getMainPath()));
                //System.out.println("main "+source);

                for (String pth : item.getTPlist())
                {
                    Path target = Paths.get(pth+item.name);
                    //System.out.println("target " + target);
                    File path = new File(pth);
                    //System.out.println("in "+target);
                         if(!path.exists())
                            {
                                path.mkdirs();
                                log("создан каталог "+path);
                            }
                            if(item.getNewname()!=null && !item.getNewname().isEmpty())
                            {
                                target = Paths.get(pth+item.newname);
                                //System.out.println("new name" + target);
                            }
                    try {
                        Files.copy(source, target);
                        log(item.getName()+" скопирован в " +target );
                        }catch (java.nio.file.FileAlreadyExistsException c)
                                {
                                Files.copy(source, target,StandardCopyOption.REPLACE_EXISTING);
                                log(target +" уже существует перезаписан");
                                }

                    //Files.copy(source, target , StandardCopyOption.REPLACE_EXISTING);
                    //System.out.println(item.getName());
                    //ArrayList list = new ArrayList(item.getTPlist());
                    //System.out.println(item.getTPlist());
                }
            }
            String Processedname = new SimpleDateFormat("yyyyMMdd").format(date);
            File Processedpath = new File("c:\\test\\Processed\\"+Processedname+"\\" );
                if(!Processedpath.exists())
                    {
                        Processedpath.mkdirs();
                    }
            for (fileinfo item : Mainlist)
            {
                try {
                    Path home = Paths.get(item.getMainPath());
                    //Path tar = Paths.get("c:\\test\\Processed\\"+Processedname+"\\"+item.getName());
                    Files.move(home, Paths.get("c:\\test\\Processed\\"+Processedname+"\\"+item.getName()) );
                    log(item.getName() + "перенесен в " + Processedpath);
                    }catch(java.nio.file.FileAlreadyExistsException c){log(item.getName()+ " небуду копировать он уже скопрован в " +Processedpath);}
            }
        }

    }

    private static void log (String s)
    {
        Date dt = new Date();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dt);
        System.err.println(timestamp + " " + s);
    }
    public static String getxmltxtpath (File pdf)
    {
        String Path = new String();
        if (pdf.getName().toLowerCase().contains("b2b") && pdf.getName().endsWith(".pdf"))
        {
            Path = ("c:\\test\\"+ pdf.getName().replace(".pdf",".xml")) ;
            //System.out.println(Path);
        } else
        {
            Path = ((pdf.getPath()) + ".txt");
        }
        return Path;
    }

    public static void XMLelements (NodeList nodeList, File xml){
        for (int i = 0; i <= nodeList.getLength(); i++) {
            if (nodeList.item(i) instanceof Element) {
                if (((Element) nodeList.item(i)).getTagName().equals("ServiceId"))
                {
                    log(nodeList.item(i).getTextContent() + " в " + xml.getName());
                }
                if (nodeList.item(i).hasChildNodes())
                {
                    XMLelements(nodeList.item(i).getChildNodes(), xml);
                }
            }
        }

    }

    public static fileinfo Setfileinfo (String name, fileinfo filein)
    {
        ArrayList<String> pathlist = new ArrayList<>();
        if (name.contains("ebill"))
        {
            pathlist.add("c:\\test\\ebill\\");
            if (filein.getName().endsWith(".pdf"))
            {
                pathlist.add("c:\\test\\ebill\\earhive\\");
            }
        }
        if (name.contains("print") && !name.contains("_post"))
        {
            pathlist.add("c:\\test\\print\\");
            if (filein.getName().endsWith(".txt"))
            {
                pathlist.add("c:\\test\\printing\\");
            }

        }
        if (name.contains("print") && name.contains("_post"))
        {
            String Oldname = name;
            String newname = Oldname.replace("_post", ("_(LVpost)_post"));
            filein.setName(newname);
            pathlist.add("c:\\test\\print\\" );
            if (filein.getName().endsWith(".txt"))
            {
                pathlist.add("c:\\test\\printing\\" );
            }
        }
        if (name.contains("b2b"))
        {
            if (name.endsWith("xml"))
            {
                log("Шаримся по "+filein.getName());
                File xmlFile = new File(filein.getMainPath());
                DocumentBuilderFactory Fact = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = null;
                try {
                    builder = Fact.newDocumentBuilder();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                }
                Document document = null;
                try {
                    document = builder.parse(xmlFile);
                } catch (SAXException e) {log("some thing wrong");

                } catch (IOException e) {log(String.format("%ssome thing wrong", xmlFile.getName()));

                }
                Element element = document.getDocumentElement();
                NodeList nodeList = element.getChildNodes();
                XMLelements(nodeList, xmlFile);
            }
        pathlist.add("c:\\test\\b2b\\");
        }
        filein.setTPlist(pathlist);
        return filein;
    }
}