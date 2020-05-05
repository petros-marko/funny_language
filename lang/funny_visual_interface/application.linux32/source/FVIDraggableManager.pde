class FVIDraggableManager{
 
 private ArrayList<FVIDraggable> overseen;
 private FVIDraggable selected1, selected2;
 private ArrayList<String> args;
 
 public FVIDraggableManager(ArrayList<FVIDraggable> overseen){
   this.overseen = overseen;
   args = new ArrayList<String>();
 }
 
 public FVIDraggableManager(){
   this(new ArrayList<FVIDraggable>());
 }
 
 public void addDraggable(FVIDraggable nd){
  overseen.add(nd); 
  if(nd instanceof Input){
    Input in = (Input)nd;
    try{
      int i = Integer.parseInt(in.content);
    }catch(NumberFormatException e){
      if((!in.content.equals("True")) && (!in.content.equals("False")) && (!in.content.equals("Nil")) && !args.contains(in.content))
        args.add(in.content);
    }
  }
 }
 
 public ArrayList<FVIDraggable> getOverseen(){
     return overseen;
 }
 
 public void clear(){
  overseen.clear();
  selected1 = null;
  selected2 = null;
  args.clear();
 }
 
 public void draw(){
  for(FVIDraggable obj : overseen){
    if(obj.getPos().y > 100 + width / 50){
      obj.draw(); 
    }
  }
 }
 
 public void select(PVector clickPos){
  for(FVIDraggable obj : overseen){
    if(dist(clickPos.x, clickPos.y, obj.getPos().x, obj.getPos().y) > obj.getRadius())
      continue;
     if(selected1 == null){
       selected1 = obj;
     }
     else{
       selected2 = obj;
       if(selected2 == selected1){
         overseen.remove(selected2);
         selected2 = selected1 = null;
         break;
       }
       if(selected2 instanceof FunctionMachine){
        FunctionMachine second = (FunctionMachine)selected2;
        if(!second.getInputs().contains(selected1))
          second.addInput(selected1);
        else
          second.removeInput(selected1);
       }
       selected2 = null;
       selected1 = null;
     }
  }
 }
 
 public void scroll(float amount){
      for(FVIDraggable d : overseen){
          d.move(new PVector(0, 10 * amount));
      }
  }
 
 public FVIDraggable getFinal(){
   int counter = 0;
   FVIDraggable res = null;
   for(FVIDraggable f : overseen){
     boolean flag = true;
     for(FVIDraggable other : overseen){
       if(other instanceof FunctionMachine && ((FunctionMachine)other).getInputs().contains(f))
         flag = false;
     }
     if(flag){
       counter ++;
       res = f;
     }
   }
   return (counter == 1)? res:null;
 }
}
