import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.io.InputStreamReader; 
import java.util.Collections; 
import java.util.Comparator; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class funny_visual_interface_v2 extends PApplet {





FVIDraggableManager manager;
Toolbox toolbox;
boolean typingValue, typingName, printingOutput;
String valueTyped, nfName, programOutput;

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
  if(printingOutput){
    textSize(18);
    text("The output of your program was: " + programOutput, width / 4 + width / 15, 90);
  }
  
  noFill();
  rectMode(CORNER);
  textSize(18);
  
  text("V", 49 * width / 50 +  width / 100 - textWidth("V") / 2, 100 + width / 76);
  rect(49 * width / 50, 100, width / 50, width / 50);
  text("S", width / 4 + width / 100 - textWidth("S") / 2, 100 + width / 76);
  rect(width / 4, 100, width / 50, width / 50);
  text("R", width / 25 + width / 4 + width / 100 - textWidth("R") / 2, 100 + width / 76);
  rect(width / 4 + width / 25, 100, width / 50, width / 50);
  line(width / 4, width / 50 + 101, width, width / 50 + 101);
  manager.draw();
  toolbox.draw();
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
    printingOutput = false;
    nfName = "";
    valueTyped = "";
  }
  if(mouseX <= width / 4 + width / 50 && mouseX >= width / 4 && mouseY >= 100 && mouseY <= 100 + width / 50){
    typingName = !typingName;
    typingValue = false;
    printingOutput = false;
    nfName = "";
    valueTyped = "";
  }
  if(mouseX >= width / 4 + width / 25 && mouseX <= width / 4 + 3 * width / 50 && mouseY >= 100 && mouseY <= 100 + width / 50){
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
        Process p = Runtime.getRuntime().exec(new String[]{"dotnet","run", programText},null, new File(sketchPath().substring(0, sketchPath().indexOf("funny_visual_interface"))));    
        BufferedReader reader = new BufferedReader(new InputStreamReader (p.getInputStream()));
        String line;
        programOutput = "";
        while((line = reader.readLine()) != null){
           programOutput = line;
           System.out.println(line);
        }
        printingOutput = true;
        typingValue = false;
        typingName = false;
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
      Collections.sort(manager.args, new Comparator<Input>(){
        public int compare(Input i1, Input i2){
          return i1.compareTo(i2);
        }
      });
      ArrayList<Input> unique = new ArrayList<Input>();
      for(Input i : manager.args){
        boolean flag = false;
        for(Input j : unique){
          if(i.content.equals(j.content)){
            flag = true;
            break;
          }
        }
        if(!flag)
          unique.add(i);
      }
      for(Input arg : unique){
        dText += " " +arg.content;
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

public void mouseDragged(){
  manager.handleDrag(mouseX, mouseY);
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
  
  public boolean within(float x, float y){
    return dist(x, y, pos.x, pos.y) <= radius;
  }
  
  public void onDrag(float x, float y){
    if(!locked){
      setPos(new PVector(x, y));
    }
  }

  
  abstract public void draw();
}
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
  public void settings() {  fullScreen(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "funny_visual_interface_v2" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
