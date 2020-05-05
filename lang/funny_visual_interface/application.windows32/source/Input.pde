class Input extends FVIDraggable{
 
  private String content;
 
  public Input(PVector pos, String content){
    super(pos, 32);
    this.connector = pos;
    this.content = content;
  }
  
  public void draw(){
   super.draw();
   connector = pos;
   textMode(CENTER);
   textSize(32);
   text(content, pos.x,pos.y);
  }
  
  public String toString(){
    return content;
  }
}
