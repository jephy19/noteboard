import java.util.*;

public class Board {

    private final int width;
    private final int height;
    private final int noteWidth;
    private final int noteHeight;
    private final List<String> colors;

    private List<Note> notes = new ArrayList<>();
    private Map<String,Integer> pins = new HashMap<>();

    public Board(int width, int height, int noteWidth, int noteHeight, List<String> colors) {
        this.width = width;
        this.height = height;
        this.noteWidth = noteWidth;
        this.noteHeight = noteHeight;
        this.colors = colors;
    }

    public enum ErrorCode {
        COLOR_NOT_SUPPORTED("ERROR COLOR_NOT_SUPPORTED Input color not supported"),
        OUT_OF_BOUNDS("ERROR OUT_OF_BOUNDS Coordinate outside board boundaries"),
        COMPLETE_OVERLAP("ERROR COMPLETE_OVERLAP Note cannot be added_overlap with existing note"),
        NO_NOTE_AT_COORDINATE("ERROR NO_NOTE_AT_COORDINATE No note exists at given coordinate"),
        PIN_NOT_FOUND("ERROR PIN_NOT_FOUND No pin exists at given coordinate"),
        INVALID_FORMAT("ERROR INVALID_FORMAT");

        private final String message;

        ErrorCode(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return message;
        }
    }


    public String getBoardDim() {
        return this.width+" "+this.height;
    }

    public String getNoteDim() {
        return this.noteWidth+" "+this.noteHeight;
    }

    public List<String> getColors() {
        return this.colors;
    }

    
    public synchronized String post(int x,int y,String color,String msg){


        if(x<0 || y<0 || x+noteWidth>width || y+noteHeight>height)
            return ErrorCode.OUT_OF_BOUNDS.getMessage();

        
        if(!colors.contains(color.toLowerCase()))
            return ErrorCode.COLOR_NOT_SUPPORTED.getMessage();

        for(Note n: notes)
            if(n.sameRect(x,y,noteWidth,noteHeight))
                return ErrorCode.COMPLETE_OVERLAP.getMessage();
            
        notes.add(new Note(x,y,color,msg,noteWidth,noteHeight,false));
        return "OK";
    }

    /* ---------- PIN ---------- */
    public synchronized String pin(int x,int y){

        boolean hit = false;
        String key = x + "," + y;

        for (Note n : notes) {
            if (n.contains(x, y)) {
                hit = true;
                n.pinCount++;
            }
        }

        if (!hit) return ErrorCode.NO_NOTE_AT_COORDINATE.getMessage();

        pins.put(key, pins.getOrDefault(key, 0) + 1);
        return "OK";
    }

    public synchronized String unpin(int x,int y){

        String key=x+","+y;
        if(!pins.containsKey(key))
            return ErrorCode.PIN_NOT_FOUND.getMessage();

        pins.put(key,pins.get(key)-1); // decrement the pin count
        if(pins.get(key)<=0) pins.remove(key);

        for(Note n: notes)
            if(n.contains(x,y) && n.pinCount>0) 
                n.pinCount--;
        return "OK";
    }

    /* ---------- GET ---------- */
    public synchronized String get(String line){

        if(line.equals("GET PINS")){
            StringBuilder sb=new StringBuilder();
            for(String k:pins.keySet())
                sb.append(k).append("\n");
            return sb.length()==0 ? "DATA \n" : "DATA"+" "+sb.toString();
        }

        String color=null;
        String refers=null;
        Integer x_coordinate=null,y_coordinate=null;

        String[] tokens=line.split(" ");

        int i=1;
        while(i<tokens.length){
            String t=tokens[i];
            if(t.startsWith("color=")){
                String value=t.substring(6).trim();
                if(value.isEmpty()) return ErrorCode.INVALID_FORMAT.getMessage()+" "+"color must be specified for 'color='";
                color=value;
                i++;
            }else if(t.startsWith("refersTo=")){
                String value=t.substring(9).trim();
                if(value.isEmpty()) return ErrorCode.INVALID_FORMAT.getMessage()+" "+"reference must be specified after 'refersTo='";
                refers=value;
                i++;
            }else if(t.startsWith("contains=")){
                String rest=t.substring(9).trim();
                if(rest.isEmpty()){
                    if(i+2>=tokens.length) return ErrorCode.INVALID_FORMAT.getMessage()+" "+"coordinates must be specified after 'contains='";
                    try{
                        x_coordinate=Integer.parseInt(tokens[i+1]);
                        y_coordinate=Integer.parseInt(tokens[i+2]);
                    }catch(NumberFormatException e){
                        return ErrorCode.INVALID_FORMAT.getMessage()+" "+"coordinates must be integers";
                    }
                    i+=3;
                }else{
                    if(i+1>=tokens.length) return ErrorCode.INVALID_FORMAT.getMessage();
                    try{
                        x_coordinate=Integer.parseInt(rest);
                        y_coordinate=Integer.parseInt(tokens[i+1]);
                    }catch(NumberFormatException e){
                        return ErrorCode.INVALID_FORMAT.getMessage();
                    }
                    i+=2;
                }
            }else
                return ErrorCode.INVALID_FORMAT.getMessage();
        }

        if(color==null && refers==null && x_coordinate==null)
            return ErrorCode.INVALID_FORMAT.getMessage()+" "+"GET must have at least one parameter";

        StringBuilder out=new StringBuilder();

        for(Note n: notes){

            if(color!=null && !n.color.equalsIgnoreCase(color)) continue;
            if(refers!=null && !n.msg.contains(refers)) continue;
            if(x_coordinate!=null && !n.contains(x_coordinate,y_coordinate)) continue;

            out.append(n.toString()).append("\n");
        }

        return out.length()==0 ? "OK EMPTY" : out.toString();
    }

    /* ---------- SHAKE ---------- */
    public synchronized String shake(){
        notes.removeIf(n -> n.pinCount==0);
        return "OK";
    }

    /* ---------- CLEAR ---------- */
    public synchronized String clear(){
        notes.clear();
        pins.clear();
        return "OK";
    }

    
}
