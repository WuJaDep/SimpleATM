import java.util.Scanner; 
public class ATM {
	
	public static void main(String[] args) {
		
		//to initialize the scanner
		Scanner sc = new Scanner(System.in);
		
		//init Bank
		Bank theBank = new Bank("Bank of World");
		
		
		//add a user, which also creates a saving account
		User aUser = theBank.addUser("John", "Doe", "1234");
		
		
		//add a checking account for our user
		Account newAccount = new Account("Checking", aUser, theBank);
		aUser.addAccount(newAccount);
		theBank.addAccount(newAccount);
		
		
		User curUser;
		while (true){
			
			//Stay in the login prompt until successful login
			curUser = ATM.mainMenuPrompt(theBank,sc);
			
			//stay in the main menu until user quits
			ATM.printUserMenu(curUser, sc);
			
		}
		
	}
	
	/**
	 * Print the ATM's login menu
	 * @param theBank the Bank object whose account to use
	 * @param sc	  the Scanner object to use for user's input
	 * @return		  the authenticated User object
	 */
	public static User mainMenuPrompt(Bank theBank, Scanner sc) {
		
		//inits
		String userID;
		String pin;
		User authUser;
		
		//prompt the user for user ID/pin combo until a correct one is matched
		do {
			System.out.printf("\n\nWelcome to %s\n\n", theBank.getName());
			System.out.print("Enter user ID: ");
			userID = sc.nextLine();
			System.out.print("Enter pin: ");
			pin=sc.nextLine();
			
			//Try to get the user object corresponding to the ID and pin combo
			authUser = theBank.userLogin(userID,pin);
			if(authUser == null) {
				System.out.println("Incorrect user ID/pin combination. " + 
						"Please Try again.");
			}
			
		}while( authUser == null); //continue looping until successful login
		
		return authUser;
		
	}
	
	public static void printUserMenu(User theUser, Scanner sc) {
		
		//print a summary of the user's accounts
		theUser.printAccountsSummary();
		
		//init
		int choice;
		
		//user's menu
		do {
			System.out.printf("Welcome %s, what would you like to do?\n", 
					theUser.getFirstName());
			System.out.println(" 1) Show transaction history");
			System.out.println(" 2) Withdraw");
			System.out.println(" 3) Deposit");
			System.out.println(" 4) Transfer");
			System.out.println(" 5) Quit");
			System.out.println();
			System.out.print("Enter Choice: ");
			choice = sc.nextInt();
			
			if(choice < 1 || choice > 5) {
				System.out.println("Invalid choices.Please choose 1-5");
			}
		}while(choice < 1 || choice > 5);
		
		//process the choice
		switch (choice) {
		
		case 1:
			ATM.showTransHistory(theUser,sc);
			break;
		case 2:
			ATM.withdrawFunds(theUser,sc);
			break;
		case 3:
			ATM.depositFunds(theUser,sc);
		break;
		case 4:
			ATM.transferFunds(theUser,sc);
			break;	
		case 5:
			//eliminate the rest of the previous input
			sc.nextLine();
			break;
		}
		//redisplay this menu  unless the user wants to quite
		if(choice !=5) {
			ATM.printUserMenu(theUser, sc);
		}
	}
	
	/**
	 * shows the transaction history for an account
	 * @param theUser 	the logged in User object
	 * @param sc 		the Scanner object used for user input
	 */
	public static void showTransHistory(User theUser, Scanner sc) {
		int theAcct;
		
		//get account whose transaction history to look at
		do {
			System.out.printf("Enter the number (1-%d) of the account\n "+ 
		"whose transaction you want to see: ", 
		theUser.numAccounts());
			
		theAcct= sc.nextInt() -1;
		if(theAcct < 0 || theAcct >= theUser.numAccounts()){
			System.out.println("Invalid account. Please try again");	
			}
		}while (theAcct < 0 || theAcct >= theUser.numAccounts());
		
		//print the transaction history
		theUser.printAcctTransHistory(theAcct);
	}
		
	/**
	 * Process transferring funds from one account to another
	 * @param theUser	the logged in User object
	 * @param sc		the Scanner object used for user input
	 */
	public static void transferFunds(User theUser, Scanner sc) {
		
		//inits
		int fromAcct;
		int toAcct;
		double amount;
		double acctBal;
		
		//get the account to transfer from
		do {
			System.out.printf("Enter the number (1-%d) from the account\n" +
		"to transfer from: ", theUser.numAccounts());
			fromAcct = sc.nextInt()-1;
			if(fromAcct < 0 || fromAcct >= theUser.numAccounts()) {
				System.out.println("Invalid account. Please try again.");
			}
		}while(fromAcct < 0 || fromAcct >= theUser.numAccounts());
		acctBal = theUser.getAcctBalance(fromAcct);
		
		//get the account to transfer to
		do {
			System.out.printf("Enter the number (1-%d) from the account\n" +
					"to transfer to: " , theUser.numAccounts());
			toAcct = sc.nextInt()-1;
			if(toAcct < 0 || toAcct >= theUser.numAccounts()) {
				System.out.println("Invalid account. Please try again.");
			}
		}while(toAcct < 0 || toAcct >= theUser.numAccounts());
		
		//get the amount to transfer
		do {
			System.out.printf("Enter the amount to transfer (max $%.02f): $", 
					acctBal);
			amount = sc.nextDouble();
			if(amount < 0) {
				System.out.println("Amount must be greater than zero.");
			} else if(amount > acctBal) {
				System.out.printf("Amount must not be greater than\n" +
						"balance of $%.02f.\n", acctBal);
			}
		} while (amount < 0 || amount > acctBal);
		
		// the transfer
		theUser.addAcctTransaction(fromAcct, -1*amount, String.format(
				"Transfer to account %s", theUser.getAcctUUID(toAcct)));
		theUser.addAcctTransaction(toAcct, amount, String.format(
				"Transfer to account %s", theUser.getAcctUUID(fromAcct)));
		
	}
	
	/**
	 * Process a fund from an account
	 * @param theUser	the logged in User object
	 * @param sc		the scanner object user for user input
	 */
	public static void withdrawFunds(User theUser, Scanner sc) {
		
		//inits
		int fromAcct;
		double amount;
		double acctBal;
		String memo;
		
		//get the account to transfer from
		do {
			System.out.printf("Enter the number (1-%d) from the account\n" +
		"to withdraw from: ", theUser.numAccounts());
			fromAcct = sc.nextInt()-1;
			if(fromAcct < 0 || fromAcct >= theUser.numAccounts()) {
				System.out.println("Invalid account. Please try again.");
			}
		}while(fromAcct < 0 || fromAcct >= theUser.numAccounts());
		acctBal = theUser.getAcctBalance(fromAcct);
		
		//get the amount to transfer
		do {
			System.out.printf("Enter the amount to withdraw (max $%.02f): $", 
					acctBal);
			amount = sc.nextDouble();
			if(amount < 0) {
				System.out.println("Amount must be greater than zero.");
			} else if(amount > acctBal) {
				System.out.printf("Amount must not be greater than\n" +
						"balance of $%.02f.\n", acctBal);
			}
		} while (amount < 0 || amount > acctBal);
		
		//eliminate the rest of the previous input
		sc.nextLine();
		
		//get the memo
		System.out.print("Enter a memo: ");
		memo = sc.nextLine();
		
		
		//do the withdraw
		theUser.addAcctTransaction(fromAcct, -1*amount, memo);
	}
	
	/**
	 * Process a fund deposit to an account
	 * @param theUser	the logged-in User object
	 * @param sc		the scanner object used for user input
	 */
	public static void depositFunds(User theUser, Scanner sc) {
		
		//inits
		int toAcct;
		double amount;
		double acctBal;
		String memo;
		
		//get the account to transfer from
		do {
			System.out.printf("Enter the number (1-%d) from the account\n" +
		"to deposite in: ", theUser.numAccounts());
			toAcct = sc.nextInt()-1;
			if(toAcct < 0 || toAcct >= theUser.numAccounts()) {
				System.out.println("Invalid account. Please try again.");
			}
		}while(toAcct < 0 || toAcct >= theUser.numAccounts());
		acctBal = theUser.getAcctBalance(toAcct);
		
		//get the amount to transfer
		do {
			System.out.printf("Enter the amount to deposit (max $%.02f): $", 
					acctBal);
			amount = sc.nextDouble();
			if(amount < 0) {
				System.out.println("Amount must be greater than zero.");
			}
		} while (amount < 0);
		//eliminate the rest of the previous input
		sc.nextLine();
		
		//get the memo
		System.out.print("Enter a memo: ");
		memo = sc.nextLine();
		
		
		//do the withdraw
		theUser.addAcctTransaction(toAcct,  amount, memo);
	}
}
