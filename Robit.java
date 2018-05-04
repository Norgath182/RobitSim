/**
 * Purpose: class to simulate robit, used by RobitSim
 * 
 * @author (Eric Voigt)
 * @project start date (08.02.18)
 * @project version (1.2.1)
 * @ToDo: rotation status fix
 * @last edit (03.05.18)
 * @edit changes: non holo nav complete
 */

import objectdraw.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.Math.*;
import javax.swing.*;

public class Robit{
    private double currX,currY,//current X and Y coordinates,
                   theta,//angle at which the robot is at
                   beta;//angle at which sonar is at
    private Location p1,p2,p3,p4,//4 corners of robot,
                     s1,s2;//2 endpoints of sonar "line"
    private Line l12,l23,l34,l41,//4 edges of robot,
                 s12;//1 sonar line
    public DrawingCanvas canvas;//canvas to draw

    /** 
     * Robit constructor: sets up components
     * parameters:
     *      double starting X,Y coordinates
     *      objectdraw drawing canvas
     */
    public Robit(double startX,double startY,DrawingCanvas canvasIn){
        canvas=canvasIn;
        currX=startX;
        currY=startY;
        p1=new Location(currX+5,currY-5);
        p2=new Location(currX-5,currY-5);
        p3=new Location(currX-5,currY+5);
        p4=new Location(currX+5,currY+5);

        s1=new Location(currX,currY);
        s2=new Location(currX,currY-10);

        l12=new Line(p1,p2,canvas);
        l12.setColor(Color.blue);
        l23=new Line(p2,p3,canvas);
        l34=new Line(p3,p4,canvas);
        l41=new Line(p4,p1,canvas);

        s12=new Line(s1,s2,canvas);
        s12.setColor(Color.red);

        theta=Math.PI/2;
        beta=Math.PI/2;
    }

    /** 
     * move method: moves the robot forward or backwards
     * input: int distance
     */
    public void move(int dMove){
        double dx=dMove*Math.cos(theta),
               dy=-dMove*Math.sin(theta);
        currX+=dx;
        currY+=dy;
        
        p1.translate(dx,dy);
        p2.translate(dx,dy);
        p3.translate(dx,dy);
        p4.translate(dx,dy);

        s1.translate(dx,dy);
        s2.translate(dx,dy);

        setLines();
    }

    /** 
     * rotate method: rotates the robot
     * parameters: double angle
     */
    public void rotate(double dTheta){
        double cosTheta=Math.cos(dTheta),sinTheta=Math.sin(dTheta),
               x1=p1.getX()-currX,y1=p1.getY()-currY,
               x2=p2.getX()-currX,y2=p2.getY()-currY,
               x3=p3.getX()-currX,y3=p3.getY()-currY,
               x4=p4.getX()-currX,y4=p4.getY()-currY,
               sX=s2.getX()-currX,sY=s2.getY()-currY,
               
               nx1=x1*cosTheta-y1*sinTheta,ny1=x1*sinTheta+y1*cosTheta,
               nx2=x2*cosTheta-y2*sinTheta,ny2=x2*sinTheta+y2*cosTheta,
               nx3=x3*cosTheta-y3*sinTheta,ny3=x3*sinTheta+y3*cosTheta,
               nx4=x4*cosTheta-y4*sinTheta,ny4=x4*sinTheta+y4*cosTheta,
               nsX=sX*cosTheta-sY*sinTheta,nsY=sX*sinTheta+sY*cosTheta;

        x1=nx1+currX; y1=ny1+currY;
        x2=nx2+currX; y2=ny2+currY;
        x3=nx3+currX; y3=ny3+currY;
        x4=nx4+currX; y4=ny4+currY;
        sX=nsX+currX; sY=nsY+currY;

        p1=new Location(x1,y1);
        p2=new Location(x2,y2);
        p3=new Location(x3,y3);
        p4=new Location(x4,y4);
        s2=new Location(sX,sY);

        setLines();

        theta-=dTheta;
        theta%=(2*Math.PI);
        beta-=dTheta;
        beta%=(2*Math.PI);
    }

    /**
     * sonar method: turns the sonar sensor
     * parameters: double angle
     */
    public void sonar(double dBeta){
        double sX=s2.getX()-currX,sY=s2.getY()-currY,
               nsX=sX*Math.cos(dBeta)-sY*Math.sin(dBeta),nsY=sX*Math.sin(dBeta)+sY*Math.cos(dBeta);
        sX=nsX+currX; sY=nsY+currY;
        s2=new Location(sX,sY);
        s12.setEndPoints(s1,s2);
        beta-=dBeta;
        beta%=(2*Math.PI);
    }

    /**
     * removeFromCanvas method: implementation of removeFromCanvas for each line
     * parameters: none
     */
    public void removeFromCanvas(){
        l12.removeFromCanvas();
        l23.removeFromCanvas();
        l34.removeFromCanvas();
        l41.removeFromCanvas();
        s12.removeFromCanvas();
    }

    /**
     * moveTo method: make the robit auto navigate to point
     * parameters: point to navigate to
     */
    public void moveTo(Location point){
        double dir=-Math.atan2(point.getY()-currY,point.getX()-currX)-theta;
        int dist=(int)Math.sqrt(Math.pow(point.getX()-currX,2)+Math.pow(point.getY()-currY,2));
        rotate(-dir);
        move(dist);
    }
    
    /*
    for(int r=0;r<=dir;r++){
        rotate(Math.PI/180);
        timer(10);
    }
    for(int d=0;d<=dist;d++){
        move(1);
        timer(10);
    }
    */

    /**
     * getState method: relays robot state information
     * parameters: none
     * return: double array of x,y coordinates, rotation, sonar rotation
     */
    public double[] getState(){
        double[] arr=new double[4];
        arr[0]=currX;
        arr[1]=currY;
        arr[2]=theta;
        arr[3]=beta;
        return arr;
    }

    /**
     * timer method: delays for a set amount of time
     * parameters: time to wait in milliseconds
     */
    public void timer(int time){
        double start=System.currentTimeMillis(),
        end=start+(double)time;
        while(System.currentTimeMillis()<=end){}
    }

    /**
     * setLines method: draws the lines according
     *      to respective endpoints
     *      exists to simplify in other places
     * parameters: none
     */
    private void setLines(){
        l12.setEndPoints(p1,p2);
        l23.setEndPoints(p2,p3);
        l34.setEndPoints(p3,p4);
        l41.setEndPoints(p4,p1);
        s12.setEndPoints(s1,s2);
    }

    /**
     * printPoints method: prints out points for testing purposes
     * parameters: none
     */
    private void printPoints(){
        System.out.println("p1: "+p1.getX()+" "+p1.getY()+
                           "\np2: "+p2.getX()+" "+p2.getY()+
                           "\np3: "+p3.getX()+" "+p3.getY()+
                           "\np4: "+p4.getX()+" "+p4.getY()+
                           "\ns1: "+s1.getX()+" "+s1.getY()+
                           "\ns2: "+s2.getX()+" "+s2.getY()+
                           "\ncurrent: "+currX+" "+currY+"\n\n");
    }

    /**
     * printAngles method: prints out angles for testing purposes
     * parameters: none
     */
    private void printAngles(){
        System.out.println("Robot: "+theta+
                           "\nSonar: "+beta+"\n\n");
    }
}