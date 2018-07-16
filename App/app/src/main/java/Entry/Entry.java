package Entry;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Entry {
    private  Long id;
    private String filepath;
    private String title;
    private  String caption;

    //------- Constructors
    public Entry(String filePath, String title, String caption){
        id = null;
        this.filepath = filePath;
        this.title = title;
        this.caption = caption;
    }

    public Entry(Long id, String filePath, String title, String caption){
        this.id = id;
        this.filepath = filePath;
        this.title = title;
        this.caption = caption;
    }

    public Entry(String title, String caption){
        id = null;
        filepath = "";
        this.title = title;
        this.caption = caption;
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
        return BitmapFactory.decodeFile(filepath);
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
                .getTitle()) && caption.equals(((Entry) obj).getCaption()) )
                || id.equals(((Entry) obj).getId());
    }
}
