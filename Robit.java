/**
 * Purpose: class to simulate robit, used by RobitSim
 * 
 * @author (Eric Voigt)
 * @start date (08.02.18, inherited)
 * @version (1.2.1 dev, inherited)
 * @last edit (20.04.18)
 * @edit changes: creation, separation from RobitSim
 */

import objectdraw.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.Math.*;
import javax.swing.*;

class Robit{
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
        l12.setColor(Color.red);
        l23=new Line(p2,p3,canvas);
        l34=new Line(p3,p4,canvas);
        l41=new Line(p4,p1,canvas);

        s12=new Line(s1,s2,canvas);
        s12.setColor(Color.blue);

        theta=0.0;
        beta=0.0;
    }

    /** 
     * move method: moves the robot forward or backwards
     * input: int distance
     */
    public void move(int dMove){
        currX+=dMove*Math.sin(theta);
        currY-=dMove*Math.cos(theta);

        p1.translate(dMove*Math.sin(theta),-dMove*Math.cos(theta));
        p2.translate(dMove*Math.sin(theta),-dMove*Math.cos(theta));
        p3.translate(dMove*Math.sin(theta),-dMove*Math.cos(theta));
        p4.translate(dMove*Math.sin(theta),-dMove*Math.cos(theta));

        s1.translate(dMove*Math.sin(theta),-dMove*Math.cos(theta));
        s2.translate(dMove*Math.sin(theta),-dMove*Math.cos(theta));

        setLines();
    }

    /** 
     * rotate method: rotates the robot
     * parameters: double angle
     */
    public void rotate(double dTheta){
        double
               x1=p1.getX()-currX,y1=p1.getY()-currY,
               x2=p2.getX()-currX,y2=p2.getY()-currY,
               x3=p3.getX()-currX,y3=p3.getY()-currY,
               x4=p4.getX()-currX,y4=p4.getY()-currY,
               sX=s2.getX()-currX,sY=s2.getY()-currY,
               
               nx1=x1*Math.cos(dTheta)-y1*Math.sin(dTheta),ny1=x1*Math.sin(dTheta)+y1*Math.cos(dTheta),
               nx2=x2*Math.cos(dTheta)-y2*Math.sin(dTheta),ny2=x2*Math.sin(dTheta)+y2*Math.cos(dTheta),
               nx3=x3*Math.cos(dTheta)-y3*Math.sin(dTheta),ny3=x3*Math.sin(dTheta)+y3*Math.cos(dTheta),
               nx4=x4*Math.cos(dTheta)-y4*Math.sin(dTheta),ny4=x4*Math.sin(dTheta)+y4*Math.cos(dTheta),
               nsX=sX*Math.cos(dTheta)-sY*Math.sin(dTheta),nsY=sX*Math.sin(dTheta)+sY*Math.cos(dTheta);

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

        theta+=dTheta;
        theta%=(2*Math.PI);
        beta+=dTheta;
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
        beta+=dBeta;
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

    /** CURRENTLY NOT FUNCTIONING CORRECTLY
     * moveTo method: make the robit auto navigate to point
     * parameters: point to navigate to
     */
    public void moveTo(Location point){
        double dir=Math.atan2(point.getY()-currY,point.getX()-currX);
        if(dir<0) dir=Math.abs(dir)+Math.PI;
        System.out.println("dir: "+dir/Math.PI);
        /*for(int r=0;r<=dir;r++){
           rotate(Math.PI/180);
           wait(10);
        }*/
        rotate(theta-dir);
        int dist=(int)Math.sqrt(Math.pow(point.getX()-currX,2)+
                Math.pow(point.getY()-currY,2));
        /*for(int d=0;d<=dist;d++){
            move(1);
            wait(10);
        }*/
        move(dist);
    }

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
     * wait method: delays for a set amount of time
     * parameters: time to wait in milliseconds
     */
    public void wait(int time){
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