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
