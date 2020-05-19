class FVIDraggableManager{
 
 private ArrayList<FVIDraggable> overseen;
 private FVIDraggable selected1, selected2, beingDragged;
 private ArrayList<Input> args;
 
 public FVIDraggableManager(ArrayList<FVIDraggable> overseen){
   this.overseen = overseen;
   args = new ArrayList<Input>();
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
    }catch(NumberFormatException e1){
      try{
        float x = Float.parseFloat(in.content);
      }
      catch(NumberFormatException e2){
        if((!in.content.equals("True")) && (!in.content.equals("False")) && (!in.content.equals("Nil")))
          args.add(in);
      }
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
  fill(0);
  for(FVIDraggable obj : overseen){
    if(obj.getPos().y > 100 + width / 50){
      obj.draw(); 
    }
  }
  fill(150);
  if(selected1 != null)
    selected1.draw();
  if(selected2 != null)
    selected2.draw();
  fill(0);
 }
 
 public void select(PVector clickPos){
  boolean flag = false;
  for(FVIDraggable obj : overseen){
    if(!obj.within(clickPos.x, clickPos.y))
      continue;
    flag = true;
     if(selected1 == null){
       selected1 = obj;
     }
     else{
       selected2 = obj;
       if(selected2 == selected1){
         overseen.remove(selected2);
         args.remove(selected2);
         for(FVIDraggable d : overseen){
           if(!(d instanceof FunctionMachine))continue;
             FunctionMachine c = (FunctionMachine)d;
             c.getInputs().remove(selected2);
           
         }
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
  if(!flag){
    selected1 = selected2 = null;
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
 
 public void handleDrag(float x, float y){
   if(beingDragged != null){
    if(beingDragged.within(x, y)){
     beingDragged.onDrag(x, y);
     return;
    }
   }
   beingDragged = null;
   for(FVIDraggable d : overseen){
     //println(d.within(x, y) + " " + d);
     if(d.within(x, y)){
       d.onDrag(x, y);
       beingDragged = d;
       return;
     }
   }
 }
}
