package picture.diary.lenstofork.Diary.Entry;

import java.io.File;

public class Entry {
    private  Long id;
    private String filepath;
    private String title;
    private String caption;
    private String description;
    private CaptionColor captionColor;
    private CaptionPosition captionPosition;

    //------- Constructors
    public Entry(String filePath, String title, String caption, String description){
        id = null;
        this.filepath = filePath;
        this.title = title;
        this.caption = caption;
        this.description = description;
        captionColor = CaptionColor.WHITE;
        captionPosition = CaptionPosition.CENTER;
    }

    public Entry(Long id, String filepath, String title, String caption, String description){
        this.id = id;
        this.filepath = filepath;
        this.title = title;
        this.caption = caption;
        this.description = description;
        captionColor = CaptionColor.WHITE;
        captionPosition = CaptionPosition.CENTER;
    }

    //-------- Setter & Getters
    // Image File Path
    public String getImageFilePath() {
        return filepath;
    }

    public void setImageFilePath(String filePath) {
        if(!filePath.equals("")){
            // delete old file
            new File(this.filepath).delete();
        }
        this.filepath = filePath;
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

    // Caption Color
    public void setCaptionColor(CaptionColor color){
        this.captionColor = color;
    }

    public void setCaptionColor(String colorString){
        this.captionColor = CaptionColor.getCaptionColor(colorString);
    }

    public CaptionColor getCaptionColor() {
        return captionColor;
    }

    // Caption Position

    public void setCaptionPosition(String captionPosition){
        this.captionPosition = CaptionPosition.getCaptionPosition(captionPosition);
    }

    public void setCaptionPosition(CaptionPosition captionPosition) {
        this.captionPosition = captionPosition;
    }

    public CaptionPosition getCaptionPosition() {
        return captionPosition;
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
        // filepath must be equal
        if(!filepath.equals(entry.getImageFilePath())){
            return false;
        }
        // title must be equal
        if(!title.equals(entry.getTitle())){
            return false;
        }
        // caption must be equal
        if(!caption.equals(entry.getCaption())){
            return false;
        }
        // captionColor must be equal
        if(!captionColor.equals(entry.getCaptionColor())){
            return false;
        }
        // description must be equal
        if(!description.equals(entry.getDescription())){
            return false;
        }
        // ID's must be equal
        if(!id.equals(entry.getId())){
            return false;
        }
        return true;
    }
}
