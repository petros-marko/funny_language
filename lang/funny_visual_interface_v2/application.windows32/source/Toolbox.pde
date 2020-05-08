class Toolbox{
  private PVector ul, lr;
  private ArrayList<FunctionMachine> available;
  
  public Toolbox(PVector ul, PVector lr){
    this.ul = ul;
    this.lr = lr;
    available = new ArrayList<FunctionMachine>();
    this.add("id");
    this.add("plus");
    this.add("minus");
    this.add("times");
    this.add("div");
    this.add("mod");
    this.add("min");
    this.available.get(this.available.size() - 1).setDefinitionText("(fun min m n ( if ( le m n ) m n ))");
    this.add("max");
    this.available.get(this.available.size() - 1).setDefinitionText("(fun max m n ( if ( le m n ) n m ))");
    this.add("if");
    this.add("le");
    this.add("gr");
    this.add("eq");
    this.add("leq");
    this.available.get(this.available.size() - 1).setDefinitionText("(fun leq m n ( or ( eq m n ) ( le m n ) ))");
    this.add("geq");
    this.available.get(this.available.size() - 1).setDefinitionText("(fun geq m n ( or ( eq m n ) ( gr m n ) ))");
    this.add("or");
    this.available.get(this.available.size() - 1).setDefinitionText("(fun or p q ( if p True q ))");
    this.add("and");
    this.available.get(this.available.size() - 1).setDefinitionText("(fun and p q ( if p q False ))");
    this.add("not");
    this.available.get(this.available.size() - 1).setDefinitionText("(fun not p ( if p False True ))");
    this.add("pair");
    this.add("fst");
    this.add("snd");
    this.add("length");
    this.available.get(this.available.size() - 1).setDefinitionText("(fun length l ( if ( eq l Nil ) 0 ( plus 1 ( length ( snd l ) ) ) ))");
    this.add("elemAt");
    this.available.get(this.available.size() - 1).setDefinitionText("(fun elemAt l i ( if ( geq i ( length l ) ) Nil ( if ( eq i 0 ) ( fst l ) ( elemAt ( snd l ) ( minus i 1 ) ) ) ))");
    this.add("append");
    this.available.get(this.available.size() - 1).setDefinitionText("(fun append f s ( if ( eq f Nil ) s ( pair ( fst f ) ( append ( snd f ) s ) ) ))");
    this.add("range");
    this.available.get(this.available.size() - 1).setDefinitionText("(fun range s e ( if ( eq s e ) Nil ( pair s ( range ( plus s 1 ) e ) ) ))");
    this.add("repeat");
    this.available.get(this.available.size() - 1).setDefinitionText("(fun repeat v t ( if ( eq t 0 ) Nil ( pair v ( repeat v ( minus t 1 ) ) ) ))");
    this.add("reverse");
    this.available.get(this.available.size() - 1).setDefinitionText("(fun reverse l ( if ( eq l Nil ) Nil ( append ( reverse ( snd l ) ) ( pair ( fst l ) Nil ) ) ))");
    this.add("zip");
    this.available.get(this.available.size() - 1).setDefinitionText("(fun zip f s ( if ( or ( eq f Nil ) ( eq s Nil ) ) Nil ( pair ( pair ( fst f ) ( fst s ) ) ( zip ( snd f ) ( snd s ) ) ) ))");
    this.add("toInteger");
    this.add("toReal");
    this.add("self");
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
