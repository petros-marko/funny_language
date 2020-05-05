class Toolbox{
  private PVector ul, lr;
  private ArrayList<FunctionMachine> available;
  
  public Toolbox(PVector ul, PVector lr){
    this.ul = ul;
    this.lr = lr;
    available = new ArrayList<FunctionMachine>();
    this.add("plus");
    this.add("minus");
    this.add("times");
    this.add("div");
    this.add("mod");
    this.add("if");
    this.add("le");
    this.add("gr");
    this.add("eq");
    //this.add("or");
    //this.add("and");
    this.add("pair");
    this.add("fst");
    this.add("snd");
    this.add("self");
    //available.add(new FunctionMachine(new PVector(width / 2, height / 2), "test"));
  }
  
  public void draw(){
    for(FunctionMachine machine : available){
      if(machine.getPos().y >= ul.y && machine.getPos().y <= lr.y)
        machine.draw();
    }
  }
  
  public void scroll(float amount){
    if((amount > 0 && available.get(0).getPos().y - width / 10 <= ul.x) || (amount < 0 && available.get(available.size() - 1).getPos().y + width / 12 >= lr.y)){
      for(FunctionMachine machine : available){
          machine.move(new PVector(0, 10 * amount));
      }
    }
  }
  
  private FunctionMachine select(PVector mousePos){
    for(FunctionMachine machine : available){
      if(dist(mousePos.x, mousePos.y, machine.getPos().x, machine.getPos().y) < machine.getRadius()){
        return new FunctionMachine(new PVector(width / 4 + width / 10, 100 + width / 50 + width / 15), machine.name);
      }
    }
    return null;
  }
  
  private void add(String mName){
    FunctionMachine nMachine = new FunctionMachine(new PVector(width / 8, 110 + available.size() * (width / 10 + 30)), mName);
    nMachine.setLocked(true);
    available.add(nMachine);
  }
  
 public void add(FunctionMachine f){
  f.setPos(new PVector(width / 8, available.get(available.size() - 2).getPos().y + 30 + width / 10));
  f.setLocked(true);
  FunctionMachine self = available.get(available.size() - 1);
  available.remove(self);
  available.add(f);
  self.setPos(new PVector(width / 8, available.get(available.size() - 1).getPos().y + 30 + width / 10));
  available.add(self);
 }
}
