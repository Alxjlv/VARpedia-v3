package images;

import javafx.concurrent.Task;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public class ImageDownload extends Task<Void> {
    private HashMap<String,String> urlList;

    public ImageDownload(HashMap<String,String> urls){
        urlList=urls;
    }

    @Override
    protected Void call() throws Exception {
        System.out.println("Starting download");

        for(String s:urlList.keySet()){
            System.out.println("attempted downloading image with id " + s);
            try(InputStream in = new URL(urlList.get(s)).openStream()){
                Files.copy(in, Paths.get(".temp/images/"+s+".jpg"));
                System.out.println("downloaded image with id "+s);
            }
        }
//        for(int i=0;i<urlList.size();i++){
//            int num = i+1;
//            System.out.println("attempted downloading image " + num);
//            try(InputStream in = new URL(urlList.get(i)).openStream()){
//                Files.copy(in, Paths.get(".temp/images/"+num+".jpg"));//currently downloading images and placing them in root folder
//                System.out.println("downloaded image number"+num);
//            }
//        }
        return null;
    }
}