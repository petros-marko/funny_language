import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;

FVIDraggableManager manager;
Toolbox toolbox;
boolean typingValue, typingName, printingOutput;
String valueTyped, nfName, programOutput;

void setup(){
  //size(400,400);
  fullScreen();
  fill(0);
  strokeWeight(2);
  toolbox = new Toolbox(new PVector(0, 64), new PVector(width / 4, height));
  manager = new FVIDraggableManager();
  typingValue = false;
  valueTyped = "";
  nfName = "";
  //println(width / 10 + 30);
}

void draw(){
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

void mouseClicked(){
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

void mouseWheel(MouseEvent event){
  float sb = event.getCount();
  if(mouseX <= width / 4){
    toolbox.scroll(sb);
  }
  if(mouseX > width / 4 && mouseY > 100 + width / 15){
    manager.scroll(sb);
  }
}

void keyTyped(KeyEvent event){
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

void mouseDragged(){
  manager.handleDrag(mouseX, mouseY);
}
