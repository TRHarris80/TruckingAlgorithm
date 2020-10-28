
import java.io.*;
import java.util.*;



public class Truck
{
	double rate, miles, weight, length;
	String category, originCity, destCity, comments;
	double RPM,RPW, RPWPL, maxWeight, maxLength;
	boolean pass;
	
   public Truck(double rate, double miles, double weight, double length, String category, String comments, String originCity, String destCity) {
	   this.rate = rate; 
	   this.miles = miles;
	   this.weight = weight;
	   this.length = length;
	   this.category = category;
	   this.comments = comments;
	   this.originCity = originCity;
	   this.destCity = destCity;
	   this.pass = false;
	   this.maxWeight = 10000;
	   this.maxLength = 48;
   }
   
  
   
}