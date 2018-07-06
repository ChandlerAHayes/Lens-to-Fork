package Entry;

import android.graphics.drawable.Drawable;
import android.net.Uri;

public class Entry {
    private  Long id;
    private Uri filepath;
    private Drawable img;
    private String title;
    private  String caption;

    //------- Constructors
    public Entry(Drawable image, String title, String caption){
        this.img = image;
        this.title = title;
        this.caption = caption;
        id = null;
    }

    public Entry(Uri image, String title, String caption){
        this.filepath = image;
        this.title = title;
        this.caption = caption;
        id = null;
    }

    public Entry(Long id, Uri image, String title, String caption){
        this.id = id;
        this.filepath = image;
        this.title = title;
        this.caption = caption;
    }

    //-------- Setter & Getters

    public Uri getImageFilePath() {
        return filepath;
    }

    public void setImageFilePath(Uri image) {
        this.filepath = image;
    }

    public Drawable getImage(){
        return img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

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
