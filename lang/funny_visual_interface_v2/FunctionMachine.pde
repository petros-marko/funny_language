class FunctionMachine extends FVIDraggable{
  
  private String name;
  private ArrayList<FVIDraggable> inputs;
  private String definitionText;
  
  public FunctionMachine(PVector pos, String name){
   super(pos, width/10);
   this.connector = new PVector(pos.x, pos.y + width / 20);
   this.name = name;
   this.inputs = new ArrayList<FVIDraggable>();
   definitionText = "";
  }
  
  public void draw(){
   connector = new PVector(pos.x, pos. y + width / 20);
   rectMode(CENTER);
   //fill(0);
   textMode(CORNER);
   textSize(18);
   text(name, pos.x - width / 20, pos.y - width / 20);
   rect(pos.x, pos.y, width / 10, width / 10); 
   for(int i = 0; i < inputs.size(); i++){
     ((inputs.get(i) instanceof FunctionMachine)? ((FunctionMachine)inputs.get(i)):((Input)inputs.get(i))).draw();
     line(inputs.get(i).getConnector().x, inputs.get(i).getConnector().y, pos.x + (width / 20) * i / inputs.size(), pos.y - width / 20);
   }
  }
  
  public void addInput(FVIDraggable f){
   inputs.add(f); 
  }
  
  public void removeInput(FVIDraggable f){
    inputs.remove(f);
  }
  
  public ArrayList<FVIDraggable> getInputs(){
    return inputs;
  }
  
  public String getDefinitionText(){
    return definitionText;
  }
  
  public void setDefinitionText(String ndt){
    definitionText = ndt;
  }
  
  public String getName(){
    return name;
  }
  
  public void setName(String name){
    this.name = name;
  }
  
  public boolean within(float x, float y){
    return x >= this.pos.x - width /20 && x <= this.pos.x + width / 20 && y >= this.pos.y - width / 20 && y <= this.pos.y + width / 20;
  }
  
  public String toString(){
   String res = "( " + name;
   for(FVIDraggable i : inputs){
     res += " " + ((i instanceof FunctionMachine)? ((FunctionMachine)i):((Input)i)).toString();
   }
   res += " )";
   return res;
  }
}
