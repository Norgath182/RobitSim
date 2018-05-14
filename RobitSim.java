/**
 * Purpose: to simulate a simple robot with
 * a differential drive and a sonar sensor
 *
 * @author (Eric Voigt)
 * @project start date (08.02.18)
 * @project version (1.2.1)
 * @current features: movement, rotation, sonar rotation,
 *                    GUI control, keybound control,
 *                    nonholonomic autonavigate
 * @ToDo: holonomic autonavigate,
 *      obstacle avoidance,
 *      area mapping,
 *      localization,
 *      SLAM implementation
 * @last edit (20.04.18)
 * @edit changes: non holo completion (in class Robit)
 */

import objectdraw.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.Math.*;
import javax.swing.*;

public class RobitSim extends FrameWindowController implements KeyListener{
    //GUI control buttons for turning, moving, and turning sonar
    private FilledRect rotL,rotR,mvF,mvB,mvSL,mvSR,status,reset;
    //instance of robot class
    private Robit robity;
    //point for auto navigation
    private FilledOval moveLoc;
    
    /** 
     * begin method: sets up the 7 GUI buttons and robot sim
     * parameters: none
     */
    public void begin(){
        rotL=new FilledRect(10,10,20,20,canvas);
        rotL.setColor(Color.green);
        new Text("<",15,10,canvas);
        mvF=new FilledRect(40,10,20,20,canvas);
        mvF.setColor(Color.green);
        new Text("^",45,10,canvas);
        rotR=new FilledRect(70,10,20,20,canvas);
        rotR.setColor(Color.green);
        new Text(">",75,10,canvas);
        mvSL=new FilledRect(10,40,20,20,canvas);
        mvSL.setColor(Color.green);
        new Text("(",15,45,canvas);
        mvB=new FilledRect(40,40,20,20,canvas);
        mvB.setColor(Color.green);
        new Text("v",45,45,canvas);
        mvSR=new FilledRect(70,40,20,20,canvas);
        mvSR.setColor(Color.green);
        new Text(")",75,45,canvas);
        status=new FilledRect(canvas.getWidth()-50,10,20,20,canvas);
        status.setColor(Color.blue);
        new Text("S",canvas.getWidth()-45,10,canvas);
        reset=new FilledRect(canvas.getWidth()-30,10,20,20,canvas);
        reset.setColor(Color.red);
        new Text("R",canvas.getWidth()-25,10,canvas);
        moveLoc=new FilledOval(0,0,5,5,canvas);
        moveLoc.setColor(Color.green);
        moveLoc.hide();
        
        robity=new Robit(canvas.getWidth()/2,canvas.getHeight()/2,canvas);
        
        canvas.addKeyListener(this);
        this.addKeyListener(this);
        requestFocusInWindow();
    }
    
    /**
     * onMouseDrag method: makes the robit follow a dragged mouse pointer
     * parameters: Location point (given by window)
     */
    public void onMouseDrag(Location point){
        robity.moveTo(point);
    }
    
    /** 
     * onMousePress method: manages GUI button pressing
     * parameters: Location (given by window)
     */
    public void onMousePress(Location point){
        //move forward
        if(mvF.contains(point))
            robity.move(10);
        //move backwards
        else if(mvB.contains(point))
            robity.move(-10);
        //turn left
        else if(rotL.contains(point))
            robity.rotate(-Math.PI/4);
        //turn right
        else if(rotR.contains(point))
            robity.rotate(Math.PI/4);
        //turn sonar left
        else if(mvSL.contains(point))
            robity.sonar(-Math.PI/4);
        //turn sonar right
        else if(mvSR.contains(point))
            robity.sonar(Math.PI/4);
        else if(status.contains(point)){
            System.out.println("X: "+robity.getState()[0]+
                               "\nY: "+robity.getState()[1]+
                               "\nTheta: "+robity.getState()[2]+
                               "\nBeta: "+robity.getState()[3]);
        }
        //reset robot to base parameters
        else if(reset.contains(point)){
            robity.removeFromCanvas();
            robity=new Robit(canvas.getWidth()/2,canvas.getHeight()/2,canvas);
        }else{
            moveLoc.moveTo(point);
            moveLoc.show();
            //robity.wait(2000);
            robity.moveTo(point);
            moveLoc.hide();
        }
    }
    
    /**
     * keyTyped method: Required by KeyListener Interface but not used here.
     * parameters: KeyEvent (not used)
     */
    public void keyTyped(KeyEvent e){}

    /**
     * keyReleased method: Required by KeyListener Interface but not used here.
     * parameters: KeyEvent (not used)
     */
    public void keyReleased(KeyEvent e){}
    
    /**
     * keyPressed method: listens to which key is pressed
     *      and acts accordingly:
     * W: move forward
     * S: move backward
     * A: turn left
     * D: turn right
     * Q: turn sonar left
     * E: turn sonar right
     * 
     * parameters: KeyEvent (given by KeyListener input)
     */
    public void keyPressed(KeyEvent e){
        if(e.getKeyCode()==87)//w
            robity.move(1);
        else if(e.getKeyCode()==83)//s
            robity.move(-1);
        if(e.getKeyCode()==65)//a
            robity.rotate(-Math.PI/18);
        else if(e.getKeyCode()==68)//d
            robity.rotate(Math.PI/18);
        if(e.getKeyCode()==81)//q
            robity.sonar(-Math.PI/18);
        else if(e.getKeyCode()==69)//e
            robity.sonar(Math.PI/18);
    }
}