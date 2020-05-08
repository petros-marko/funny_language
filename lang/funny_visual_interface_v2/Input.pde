class Input extends FVIDraggable implements Comparable<Input>{
 
  private String content;
 
  public Input(PVector pos, String content){
    super(pos, 32);
    this.connector = pos;
    this.content = content;
  }
  
  public void draw(){
   connector = pos;
   textMode(CENTER);
   textSize(32);
   text(content, pos.x,pos.y);
  }
  
  public int compareTo(Input other){
    if(this.pos.x < other.pos.x)return -1;
    if(this.pos.x == other.pos.x)return 0;
    return 1;
  }
  
  public String toString(){
    return content;
  }
  
  public boolean within(float x, float y){
    return x >= pos.x - width / 80 && x <= width / 80 + pos.x + textWidth(content) && y >= pos.y - textAscent() && y <= pos.y + textDescent();
  }
}
