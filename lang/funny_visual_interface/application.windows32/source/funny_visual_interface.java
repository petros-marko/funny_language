import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.io.InputStreamReader; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class funny_visual_interface extends PApplet {


FVIDraggableManager manager;
Toolbox toolbox;
boolean typingValue, typingName;
String valueTyped, nfName;

public void setup(){
  //size(400,400);
  
  fill(0);
  strokeWeight(2);
  toolbox = new Toolbox(new PVector(0, 64), new PVector(width / 4, height));
  manager = new FVIDraggableManager();
  typingValue = false;
  valueTyped = "";
  nfName = "";
  //println(width / 10 + 30);
}

public void draw(){
  background(255);
  line(width / 4, 0, width / 4, height);
  textSize(32);
  text("Toolbox", width / 15, 32);
  text("Workshop", width / 2 + width / 15, 32);
  if(typingValue){
    textSize(18);
    text("The following input will be inserted when you press enter: " + valueTyped, width / 4 + width / 15, 90);
  }
  if(typingName){
    textSize(18);
    text("Please enter a name for the function you want to save: " + nfName, width / 4 + width / 15, 90);
  }
  noFill();
  rectMode(CORNER);
  rect(49 * width / 50, 100, width / 50, width / 50);
  rect(width / 4, 100, width / 50, width / 50);
  rect(width / 4 + width / 25, 100, width / 50, width / 50);
  line(width / 4, width / 50 + 101, width, width / 50 + 101);
  manager.draw();
  toolbox.draw();
  //println(manager.getFinal());
}

public void mouseClicked(){
  manager.select(new PVector(mouseX, mouseY));
  FunctionMachine possible = toolbox.select(new PVector(mouseX, mouseY));
  if(possible != null){
    manager.addDraggable(possible);
  }
  if(mouseX <= width && mouseX >= 49 * width / 50 && mouseY >= 100 && mouseY <= 100 + width / 50){
    typingValue = !typingValue;
    typingName = false;
    nfName = "";
    valueTyped = "";
  }
  if(mouseX <= width / 4 + width / 50 && mouseX >= width / 4 && mouseY >= 100 && mouseY <= 100 + width / 50){
    typingName = !typingName;
    typingValue = false;
    nfName = "";
    valueTyped = "";
  }
  if(mouseX >= width / 4 + width / 25 && mouseX <= width / 4 + 3 * width / 50 && mouseY >= 100 && mouseY <= 100 + width / 50){
    println("running");
    if(manager.args.size() == 0 && manager.getFinal() != null){
      FunctionMachine f = (FunctionMachine)manager.getFinal();
      String programText = "";
      boolean flag = false;
      for(FunctionMachine fm : toolbox.available){
        if(!fm.getDefinitionText().equals("")){
          flag = true;
         programText += "\n" + fm.getDefinitionText(); 
        } 
      }
      if(flag){
        programText += "\n";
      }
      programText += f;
      println(programText);
      try{
        Process p = Runtime.getRuntime().exec(new String[]{"dotnet","run", programText},null, new File(sketchPath().substring(0, sketchPath().indexOf("/funny_visual_interface"))));    
        BufferedReader reader = new BufferedReader(new InputStreamReader (p.getInputStream()));
        String line;
        while((line = reader.readLine()) != null){
           System.out.println(line);
        }
      }
      catch(Exception e){
        e.printStackTrace(); 
      }
    }
  }
}

public void mouseWheel(MouseEvent event){
  float sb = event.getCount();
  if(mouseX <= width / 4){
    toolbox.scroll(sb);
  }
  if(mouseX > width / 4 && mouseY > 100 + width / 15){
    manager.scroll(sb);
  }
}

public void keyTyped(KeyEvent event){
  if(typingValue){
    //typingName = false;
    if(key != ENTER && key != BACKSPACE){
      valueTyped += key;
    }else if(key == BACKSPACE && !valueTyped.equals("")){
     valueTyped = valueTyped.substring(0, valueTyped.length() - 1); 
    }else if(key == ENTER && !valueTyped.equals("")){
      manager.addDraggable(new Input(new PVector(width / 2, 150 + width / 50),valueTyped));
      typingValue = false; 
      valueTyped = "";
    }
  }
  else if(typingName){
    //typingValue = false;
    if(key == ENTER){
      FunctionMachine f = (FunctionMachine)manager.getFinal();
      FunctionMachine nf = new FunctionMachine(new PVector(), nfName);
    if(f != null){
      String dText = "(fun " + nf.getName();
      for(String arg : manager.args){
        dText += " " +arg;
      }
      dText += " " + f.toString().replace("self",nf.getName()) + ")";
      nf.setDefinitionText(dText);
      //println(nf.getDefinitionText());
      toolbox.add(nf);
      manager.clear();
      typingName = false;
      nfName = "";
    }
    }
    else if(key == BACKSPACE && !nfName.equals("")){
      nfName = nfName.substring(0, nfName.length() - 1);
    }
    else if(key == ESC){
      typingName = false;
      nfName = "";
    }
    else{
      nfName += key;
    }
  }
}
abstract class FVIDraggable{
  protected PVector pos, connector;
  protected float radius;
  protected boolean locked;
  
  public FVIDraggable(PVector pos, float radius){
    this.pos = pos;
    this.radius = radius;
    this.locked = false;
  }
  
  public PVector getPos(){
    return pos;
  }
  
  public float getRadius(){
    return radius;
  }
  
  public PVector getConnector(){
    return connector;
  }
  
  public void setLocked(boolean nv){
    locked = nv;
  }
  
  public boolean getLocked(){
    return locked;
  }
  
  public void setPos(PVector np){
    pos = np;
  }
  
  public void move(PVector dif){
    setPos(pos.add(dif));
  }
  
  public void drag(){
   if(mousePressed){
     if(dist(pos.x, pos.y, mouseX, mouseY) < radius){
       setPos(new PVector(mouseX, mouseY));
     }
   }
  }

  
  public void draw(){
    if(!locked)
      drag();
  }
}
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
   super.draw();
   connector = new PVector(pos.x, pos. y + width / 20);
   rectMode(CENTER);
   fill(0);
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
  
  public String toString(){
   String res = "( " + name;
   for(FVIDraggable i : inputs){
     res += " " + ((i instanceof FunctionMachine)? ((FunctionMachine)i):((Input)i)).toString();
   }
   res += " )";
   return res;
  }
}
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
  public void settings() {  fullScreen(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "funny_visual_interface" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
