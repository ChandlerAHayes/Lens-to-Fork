package Entry;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

public class Entry {
    private  Long id;
    private String filepath = "";
    private int imageDimension = -1;
    private String title = "";
    private String caption = "";
    private String description = "";

    //------- Constructors
    public Entry(String filePath, String title, String caption, String description){
        id = null;
        this.filepath = filePath;
        this.title = title;
        this.caption = caption;
        this.description = description;
    }

    public Entry(String title, String caption, String description){
        id = null;
        this.title = title;
        this.caption = caption;
        this.description = description;
    }

    public Entry(Long id, String filepath, String title, String caption, String description){
        this.id = id;
        this.filepath = filepath;
        this.title = title;
        this.caption = caption;
        this.description = description;
    }

    //-------- Setter & Getters
    // Image File Path
    public String getImageFilePath() {
        return filepath;
    }

    public void setImageFilePath(String filePath) {
        this.filepath = filePath;
    }

    // Bitmap Image
    public Bitmap getImage(){
        if(new File(filepath).exists()){
            return BitmapFactory.decodeFile(filepath);
        }
        else{
            return null;
        }
    }

    // Title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Caption
    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    // Image
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    // Description
    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    //-------- Object Methods

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        if(obj == null){
            return false;
        }
        if(getClass() != obj.getClass()){
            return false;
        }

        //------ Compare attributes
        Entry entry = (Entry) obj;
        return ( filepath.equals(((Entry) obj).getImageFilePath()) && title.equals(((Entry) obj)
                .getTitle()) && caption.equals(((Entry) obj).getCaption()) &&
                description.equals(((Entry) obj).getDescription()) )
                || id.equals(((Entry) obj).getId());
    }
}
