package singleserversimulation;

import java.text.DecimalFormat;
import java.io.FileReader;
import java.util.Scanner;



public class SingelServerSimulator
{
  int Queue_size = 1000;/* Limit on queue length. */
  int next_event_type; //next_event_type (1 for an arrival and 2 for a departure)
  int num_custs_delayed;
  int num_events;
  int num_in_q;
  int server_status;
  int num_of_customer;
  double area_num_in_q;
  double area_server_status;
  double sim_time; //the time of arrival of the customer who is just now arriving
  double time_last_event;
  double total_of_delays;
  double mean_interarrival;
  double mean_service;
 
  double[] time_arrival = new double[Queue_size];
  double[] time_next_event=new double[3];

  
  double random  = Math.random();
  /* Initialization method. */
  /*the timing method is first invoked to determine the type of the next event to occur and
   *  to advance the simulation clock to its time*/
	public  void initialize()
	{
		 /* Initialize the simulation clock. */
		sim_time = 0.0;
		
		 /* Initialize the state variables. */
		
		num_events=2; /* Specify the number of events for the timing function */
		server_status = 0;
		num_in_q = 0;
		time_last_event = 0.0;
		
		/* Initialize the statistical counters. */
		num_custs_delayed = 0;
		total_of_delays = 0.0;
		area_num_in_q = 0.0;
		area_server_status = 0.0;
		
		/* Initialize event list. Since no customers are present, the departure
		 (service completion) event is eliminated from consideration. */
		
		time_next_event[1] = sim_time + expon(mean_interarrival);
		time_next_event[2] = 1.0e+30;
	}
	 
	
	
	
	 /* Timing method. */
	public  void timing()
	{
		int i;
		double min_time_next_event =  1.0e+30;
		next_event_type=0;
		
		/* Determine the event type of the next event to occur. */
		for(i=1;i<=num_events;++i)
		{
			if (time_next_event[i] < min_time_next_event)
				{
					min_time_next_event=time_next_event[i];
					next_event_type=i;
				}
		}
		/* Check to see whether the event list is empty. */
		if(next_event_type==0)
		{
			System.out.println("Event List is Empty");
			System.exit(1);
		}
			
		 /* The event list is not empty, so advance the simulation clock. */
		sim_time = min_time_next_event;
	}
	 
	 /* Arrival event method. */
	public  void arrive()
	{
		double delay;
		/* Schedule next arrival. */
		
     time_next_event[1] = sim_time + expon(mean_interarrival); /*time of the first arrival, determined by adding an exponential 
      random variate with mean mean_interarrival, namely, expon(mean_interarrival), to the simulation clock, sim_time 5 0.
      
		/* Check to see whether server is busy. */
		if (server_status == 1)
		{
			/* Server is busy, so increment number of customers in queue. */
		
			++num_in_q;
			
			/* Check to see whether an overflow condition exists. */
			if(num_in_q>Queue_size)
			{
				/* The queue has overflowed, so stop the simulation. */
				System.out.println("Over flow of the queue time arrival at "+sim_time);
				System.exit(0);
				
			}
			/* There is still room in the queue, so store the time of arrival of the
			 arriving customer at the (new) end of time_arrival. */
			time_arrival[num_in_q] = sim_time;
	
		}
	 
		else
		{/* Server is idle, so arriving customer has a delay of zero. (The following two statements are for program clarity 
		and do not affect the results of the simulation.) */
			delay = 0;
			total_of_delays += delay;
			
			/* Increment the number of customers delayed, and make server busy. */
			++num_custs_delayed;
			server_status = 1;
			/* Schedule a departure (service completion). */
			time_next_event[2] = sim_time + expon(mean_service);
		}
	}
	 
	 /* Departure event method. */
	
	{/* Note that “sim_time” is the time of arrival of the customer who is just now arriving, 
	 and that the queue-overflow check is made by asking whether num_in_q is now greater than Q_LIMIT,
	  the length for which the array time_arrival was dimensioned.*/}
	public  void depart() 
	{
		int i;
		double delay;
		/* Check to see whether the queue is empty. */
		if (num_in_q == 0)
		{
			/* The queue is empty so make the server idle and eliminate the
			 departure (service completion) event from consideration. */
			server_status = 0;
			time_next_event[2] = 1.0e+30;
		}
		else
			/* The queue is nonempty, so decrement the number of customers in
			 queue. */
		{
			--num_in_q;
			/* Compute the delay of the customer who is beginning service and update
			 the total delay accumulator. */
			delay = sim_time-time_arrival[1];
			total_of_delays  += delay;
			/* Increment the number of customers delayed, and schedule departure. */
			++num_custs_delayed;
			
			time_next_event[2] = sim_time + expon(mean_service);
			
			/* Move each customer in queue (if any) up one place. */
			for ( i = 1; i <= num_in_q; ++i)
				time_arrival[i] = time_arrival[i+1];
		}
	}
	 
	/* Write report heading and input parameters. */
	public  void print()
	{   DecimalFormat dformat= new DecimalFormat("#.###");
		//System.out.println("Single-Server Queuing system simulation");
		System.out.println( "Total customer uses this server " + dformat.format(num_custs_delayed) + "\n");
		System.out.println( "Average delay in queue minutes  " + dformat.format(total_of_delays / num_custs_delayed) + "\n");
		System.out.println( "Average number in queue  " + dformat.format(area_num_in_q / sim_time) + "\n");
		System.out.println( "Server utilization  " + dformat.format(area_server_status / sim_time) + "\n");
		System.out.println( "Time simulation ended " + dformat.format(sim_time) + " minutes"+ "\n");
	}
	 
	public  void update_time_avg_stats()/* Update area accumulators for time-average
	 statistics. */
	{
		double time_since_last_event;
		 /* Compute time since last event, and update last-event-time marker. */
	 
		time_since_last_event = sim_time - time_last_event;
		time_last_event = sim_time;
		
		 /* Update area under number-in-queue method. */

		area_num_in_q += num_in_q * time_since_last_event;
		
		 /* Update area under server-busy indicator method. */

	 
		area_server_status += server_status * time_since_last_event;
	}
	/* Exponential variable generation method. */
	public  double expon(double  mean)
	{
		/* Return an exponential random variate with mean "mean". */
		return (-mean * Math.log(random));
	}
	public static void main(String[] args)
	{
		SingelServerSimulator s = new SingelServerSimulator();
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter mean inter arrival time = ");
		s.mean_interarrival=sc.nextDouble();
		
		System.out.println("\nEnter mean service time of server  = ");
		s.mean_service=sc.nextDouble();
		System.out.println("\nEnter number of customer  = ");
		s.num_of_customer= sc.nextInt();
	    s.initialize();
		
	    /* Run the simulation while more delays are still needed. */
		while(s.num_custs_delayed<s.num_of_customer)
		{
			/* Determine the next event. */
			s.timing();
			
			/* Update time-average statistical accumulators. */
			s.update_time_avg_stats();
			
			 /* Invoke the appropriate event function. */
			 
			switch (s.next_event_type)
			{
				case 1: s.arrive();
				break;
				case 2: s.depart();
				break;
			}	
		}
		/* Invoke the report generator and end the simulation. */
		s.print();
		}
}