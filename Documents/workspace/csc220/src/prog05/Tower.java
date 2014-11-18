package prog05;

import java.util.Stack;
import prog02.UserInterface;
import prog02.GUI;

public class Tower {
  static UserInterface ui = new GUI();

  static public void main (String[] args) {
    int n = getInt("How many disks?");
    if (n <= 0)
      return;
    Tower tower = new Tower(n);

    String[] commands = { "Human plays.", "Computer plays." };
    int c = ui.getCommand(commands);
    if (c == 0)
      tower.play();
    else
      tower.solve();
  }

  /** Get an integer from the user using prompt as the request.
   *  Return 0 if user cancels.  */
  static int getInt (String prompt) {
    while (true) {
      String number = ui.getInfo(prompt);
      if (number == null)
        return 0;
      try {
        return Integer.parseInt(number);
      } catch (Exception e) {
        ui.sendMessage(number + " is not a number.  Try again.");
      }
    }
  }

  int nDisks;
  StackInt<Integer>[] pegs = (StackInt<Integer>[]) new ArrayStack[3];

  Tower (int nDisks) {
    this.nDisks = nDisks;
    for (int i = 0; i < pegs.length; i++)
      pegs[i] = new ArrayStack<Integer>();
  
    
    // EXERCISE: Initialize game with pile of nDisks disks on peg 'a'
       for(int x =nDisks; x > 0; x--){
    	   pegs[0].push(x); 
       }
  }

  void play () {
    while (!(pegs[0].empty() && pegs[1].empty())) {
      displayPegs();
      String move = getMove();
      int from = move.charAt(0) - 'a';
      int to = move.charAt(1) - 'a';
      move(from, to);
    }

    ui.sendMessage("You win!");
  }

  String stackToString (StackInt<Integer> peg) {
    StackInt<Integer> helper = new ArrayStack<Integer>();
 String s = "";
    while (!peg.empty()){
    	helper.push(peg.pop());
    }
    
    while(!helper.empty()){
    	
    	 Integer value= helper.peek();
    	 s += value; 
    	 peg.push(helper.pop());
    }
    	
    return s;
  }

  void displayPegs () {
    String s = "";
    for (int i = 0; i < pegs.length; i++) {
      char abc = (char) ('a' + i);
      s = s + abc + stackToString(pegs[i]);
      if (i < pegs.length-1)
        s = s + "\n";
    }
    ui.sendMessage(s);
  }

  String getMove () {
    String[] moves = { "ab", "ac", "ba", "bc", "ca", "cb" };
    return moves[ui.getCommand(moves)];
  }

  void move (int from, int to) {
    
	  if(pegs[from].empty()){
		 ui.sendMessage("ERROR MOVE IS NOT LEGAL");
		 return;
	  }
	
	  if(!pegs[to].empty() && (pegs[from].peek() > pegs[to].peek())){
		 ui.sendMessage("ERROR 2");
		 return;
	  }
	  
	  pegs[to].push(pegs[from].pop());
	  
  }
  
  static String pegSelector[] = {"a", "b", "c"};
  
  class Goal {
		int howMany= nDisks;
		int fromPeg;  
		int toPeg;     
	
	 public Goal(int howMany, int theFromPeg, int theToPeg) {
			this.howMany= howMany;
			fromPeg= theFromPeg;
			toPeg= theToPeg;  
     }

	      public String toString(){
		    String move= howMany + " Disks from " + pegSelector[fromPeg] +  " to " + pegSelector[toPeg];
		    return(move);
	      }
  }

  
  void displayGoals (StackInt goalStack) {
	   StackInt helpStack = new ArrayStack();
	   Goal goal;
	   String s = "";
			 
	   while (!goalStack.empty()){
			 goal= (Goal)goalStack.pop();
			 helpStack.push(goal);
			 s += goal + "\n" ;
	   }
			    
	   while(!helpStack.empty()){
			goalStack.push(helpStack.pop());
	   }
			    	
	   ui.sendMessage(s);
	}	 
		  

  void solve () {
	 StackInt goalStack = new ArrayStack();
	 
	 // Push a big goal on the stack.
	 goalStack.push(new Goal(nDisks, 0, 2));
	 displayPegs();
	 
	  //While the stack is not empty:
	 while (!goalStack.empty()){   
		//   goalContents(StackInt<Goal> goalStack);
	    //Pop a goal.
	       displayGoals(goalStack);
		   Goal goal =(Goal)goalStack.pop();
		   
	    //If it is easy,
	    if (goal.howMany == 1){ 
	    //do it;
		  move(goal.fromPeg, goal.toPeg);
		  displayPegs();
	  } else {
		  //break it into subgoals
		  //and push the subgoals onto the stack.	  
		  displayPegs();
		  int n = 3-goal.fromPeg - goal.toPeg; 
		  goalStack.push( new Goal(goal.howMany -1, n, goal.toPeg));
		  goalStack.push(new Goal(1, goal.fromPeg, goal.toPeg));
		  goalStack.push(new Goal(goal.howMany-1, goal.fromPeg, n));
		  
		  
	  }	
	 
	}
  }        
}
