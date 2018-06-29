package picture.diary.lenstofork.Diary;

public class Entry {
    private  Long id;
    private String filepath;
    private String title;
    private  String note;

    //------- Constructors
    public Entry(String image, String title, String note){
        this.filepath = image;
        this.title = title;
        this.note = note;
        id = null;
    }

    public Entry(Long id, String image, String title, String note){
        this.id = id;
        this.filepath = image;
        this.title = title;
        this.note = note;
    }

    public String getImageFilePath() {
        return filepath;
    }

    //-------- Setter & Getters
    public void setImageFilePath(String image) {
        this.filepath = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
                .getTitle()) && note.equals(((Entry) obj).getNote()) )
                || id.equals(((Entry) obj).getId());
    }
}
