import java.io.*;
import java.io.FileNotFoundException;
import java.util.*;




public class truckingMain {
	static ArrayList<Truck> trucks = new ArrayList<Truck>();
	final static double ZVALUE = 1.96, NINETYCONFIDENCE = 1.645, NINETYFIVECONFIDENCE = 1.96, EIGHTYCONFICDENCE = 1.28, NINETYEIGHTCONFIDENCE = 2.33, NINETYNINECONFIDENCE = 2.58;
	
	
	

	public static void main(String[] args) throws FileNotFoundException{
		readFile();
		//(1.3206421862526483-1.857298355846337)
		double[] rpm = calculateCI_RPM();
		
		//(3845.577411391522-4990.702308888199)
		double[] weight = calculateCI_W();
		
		//(1256.6215166836591-1549.7700917079492)
		double[] miles = calculateCI_M();
		
		//(1309.5341694506903-1677.0112850947642)
		double[] rate = calculateCI_R();
		
		//(0.5775653770166995-1.1868630633323782)
		double[] rpw = calculateCI_RPW();
		
		
		double[] rpwpl = calculateCI_RPWPL();
		
		//(13.822403508422873-18.023750337730974)
		double[] length = calculateCI_L();
		
		//Parse over the 
		for(Truck truck: trucks) {
			//If the rate is in the significantly higher then the mean rate then we pass
			if(truck.rate > rate[1]) {
				truck.pass = true;
				
				//However if we can check the Length then we check if the the load is at Maxlength and the RPM is significantly below the avgRPM then we will fail the load.
				if(truck.length != 0) {
					if(truck.RPM < rpm[0] && truck.length == truck.maxLength) 
						truck.pass = false;
				}
				
				//If the weight is 70% full and the rpm is significantly less than avg we fail
				if(truck.weight >= (truck.maxWeight*0.70) && truck.RPM < rpm[0]) {
					truck.pass = false;
				}

			}
			//Else for the loads that don't have the highest rates but still are good loads
			else {
				//If the loads RPM is significantly higher than the mean we pass
				if(truck.RPM > rpm[1]) 
					truck.pass = true;
				
				//We open up the range of passing RPM but shrink the scope by only including the values that have significantly lower weight values.
				if(truck.RPM > rpm[0] && truck.weight < weight[1] )
					truck.pass = true;
				
				//If the load is from CA we give it a larger range, so RPM that would be in the average range we now pass
				if(truck.originCity.equals("CA") && truck.RPM > rpm[0])
					truck.pass = true;
				
				//If the RPW is significantly higher and the weight is not significantly low then the mean then we pass as true
				if(truck.RPW > rpw[1]&& truck.weight > weight[0]) 
					truck.pass = true;
				
				//Now we open the range for RPW by including values that are within the avg but we decrease to only the values with weights significantly greater than the avg. 
				if(truck.RPW > rpw[0]&& truck.weight > weight[1]) 
					truck.pass = true;
				
				//If the rate RPW is significantly higher and the miles are avg and above pass
				if(truck.RPW > rpw[1] && truck.miles > miles[0])
					truck.pass = true;
				
				//If the RPM is low but the RPW is avg and above and the miles are good then we pass but inside we check if the rate is still significantly lower then avg then we fail if true
				if(truck.RPM < rpm[0] && truck.RPW > rpw[0] && truck.miles > miles[1]) {
					truck.pass = true;
					if(truck.rate < rate[0])
						truck.pass = false;		
				}
				
				
				//Use length as a parameter if we have it. trying to fail those loads that are small in rate and miles but are heavier than avg and are longer than avg. 
				if(truck.length != 0) {
					if(truck.rate < rate[1] && truck.miles < miles[0] && truck.RPW < rpw[0] && truck.length > length[0] && truck.weight > weight[1] && truck.RPM > rpm[1]) {
						truck.pass = false;
						if(truck.RPM > rpm[1] && truck.weight != truck.maxWeight && truck.length != truck.maxLength) {
							truck.pass = true;
						}
					}		
				}
				
				//Trying to fail those loads that have extremely small loads with small miles and above avg rpm
				if(truck.rate < rate[0]/2.0 && truck.miles < miles[0]/2.0 && truck.RPM < rpm[1]+rpm[0])
					truck.pass = false;		
				
				
				
					
			}
			
			
			//You can change true/false to see either which bad loads passed or which good loads failed
			if(truck.pass == true) {
				System.out.println("origin = " + truck.originCity + ", destination  = " + truck.destCity + ", category  = " + truck.category + ", comments  = " + truck.comments + ", RPM  = " + truck.RPM);
				
			}
			
			
				
			
		}
		//System.out.println(rpm[0]+"-"+rpm[1]);
		//System.out.println(rpw[0]+"-"+rpw[1]);
		//System.out.println(weight[0]+"-"+weight[1]);
		//System.out.println(miles[0]+"-"+miles[1]);
		//System.out.println(rate[0]+"-"+rate[1]);
		//System.out.println(length[0]+"-"+length[1]);
	
	}

	private static void readFile() {
		String csvFile = "sample-data.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "\",\"";
		
		 try {

	            br = new BufferedReader(new FileReader(csvFile));
	            br.readLine();
	            while ((line = br.readLine()) != null) {

	                // use comma as separator
	                String[] truck = line.split(cvsSplitBy);
	                
	                trucks.add(new Truck(Double.parseDouble(truck[0].replace("\"", "")), Double.parseDouble(truck[1]), Double.parseDouble(truck[2]), Double.parseDouble(truck[3]), truck[4], truck[5], truck[7].replace("\"", ""), truck[9].replace("\"", "")));
	                
	               

	            }

	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (br != null) {
	                try {
	                    br.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	}
	
	public static double calculateSD(double numArray[])
    {
        double sum = 0.0, standardDeviation = 0.0;
        int length = numArray.length;

        for(double num : numArray) {
            sum += num;
        }

        double mean = sum/length;

        for(double num: numArray) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        return Math.sqrt(standardDeviation/length);
        
        
    }
	
	public static double[] calculateCI_RPM() {
		double[] RPM = new double[trucks.size()];
		int i = 0;
		for(Truck truck: trucks) {
			 RPM[i] = (truck.rate/truck.miles);
			 truck.RPM = (truck.rate/truck.miles);
			 i++;
		}
		
		double avgRPM = calculateAVG(RPM);
		double sdRPM = calculateSD(RPM);
		
		double temp = ZVALUE * sdRPM / Math.sqrt(trucks.size());
		
		
		return new double[] {avgRPM - temp, avgRPM + temp};
	}
	
	public static double[] calculateCI_RPWPL() {
		int count = 0;
		for(Truck truck: trucks) {
			if(truck.length != 0)
				count++;
		}
		double[] RPWPL = new double[count];
		int i = 0;
		for(Truck truck: trucks) {
			if(truck.length != 0) {
			 RPWPL[i] = truck.rate/(truck.weight/truck.length);
			 truck.RPWPL = truck.rate/(truck.weight/truck.length);
			 i++;
			}
		}
		
		double avgRPWPL = calculateAVG(RPWPL);
		double sdRPWPL = calculateSD(RPWPL);
		
		double temp = ZVALUE * sdRPWPL / Math.sqrt(trucks.size());
		
		
		return new double[] {avgRPWPL - temp, avgRPWPL + temp};
	}
	
	public static double[] calculateCI_RPW() {
		double[] RPW = new double[trucks.size()];
		int i = 0;
		for(Truck truck: trucks) {
			 RPW[i] = (truck.rate/truck.weight);
			 truck.RPW = (truck.rate/truck.weight);
			 i++;
		}
		
		double avgRPW = calculateAVG(RPW);
		double sdRPW = calculateSD(RPW);
		
		double temp = ZVALUE * sdRPW / Math.sqrt(trucks.size());
		
		
		return new double[] {avgRPW - temp, avgRPW + temp};
	}
	
	public static double[] calculateCI_W() {
		double[] weights = new double[trucks.size()];
		int i = 0;
		for(Truck truck: trucks) {
			 weights[i] = truck.weight;
			 i++;
		}
		
		double avgW = calculateAVG(weights);
		double sdW = calculateSD(weights);
		
		double temp = ZVALUE * sdW / Math.sqrt(trucks.size());
		
		return new double[] {avgW - temp, avgW + temp};
	}
	
	public static double[] calculateCI_L() {
		int count = 0;
		for(Truck truck: trucks) {
			if(truck.length != 0)
				count++;
		}
		double[] length = new double[count];
		int i = 0;
		for(Truck truck: trucks) {
			if(truck.length != 0) {
			 length[i] = truck.length;
			 
			 i++;
			}
		}
		
		double avglength = calculateAVG(length);
		double sdlength = calculateSD(length);
		
		double temp = ZVALUE * sdlength / Math.sqrt(trucks.size());
		
		
		return new double[] {avglength - temp, avglength + temp};
	}
	
	public static double[] calculateCI_M() {
		double[] miles = new double[trucks.size()];
		int i = 0;
		for(Truck truck: trucks) {
			 miles[i] = truck.miles;
			 i++;
		}
		
		double avgM = calculateAVG(miles);
		double sdM = calculateSD(miles);
		
		double temp = ZVALUE * sdM / Math.sqrt(trucks.size());
		
		return new double[] {avgM - temp, avgM + temp};
		
	}
	
	public static double[] calculateCI_R() {
		double[] rates = new double[trucks.size()];
		int i = 0;
		for(Truck truck: trucks) {
			rates[i] = truck.rate;
			 i++;
		}
		
		double avgR = calculateAVG(rates);
		double sdR = calculateSD(rates);
		
		double temp = ZVALUE * sdR / Math.sqrt(trucks.size());
		
		return new double[] {avgR - temp, avgR + temp};
		
	}

	
	public static double calculateAVG(double numArray[]) {
		
		double sum = 0.0;
        int length = numArray.length;

        for(double num : numArray) {
            sum += num;
        }
		
		
		
		return (sum/trucks.size());
	}
	
	
}
