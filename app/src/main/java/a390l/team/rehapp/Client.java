package a390l.team.rehapp;



/**
 * Created by Paul Daccache on 2017-04-05.
 */

public class Client{

    protected String ClientName;
    protected int ClientMass;
    protected int ClientAge;
    protected int ClientID;
    protected int CaloriesAssigned;
    protected int CaloriesBurned;

    public Client(String Name,int Age,int Mass, int ID)
    {
        if(Name.contains("0")||Name.contains("1")||Name.contains("2")||Name.contains("3")||Name.contains("4")||Name.contains("5")||Name.contains("6")||Name.contains("7")||Name.contains("8")||Name.contains("9")){
            Name = null;
        }else this.ClientName = Name;

        this.ClientAge = Age;

        this.ClientMass = Mass;

        this.ClientID = ID;
    }

    public Client(String Name,int Age,int Mass, int ID, int CalsAssigned , int CalsBurned)
    {
        if(Name.contains("0")||Name.contains("1")||Name.contains("2")||Name.contains("3")||Name.contains("4")||Name.contains("5")||Name.contains("6")||Name.contains("7")||Name.contains("8")||Name.contains("9")){
            Name = null;
        }else this.ClientName = Name;

        this.ClientAge = Age;

        this.ClientMass = Mass;

        this.ClientID = ID;

        this.CaloriesAssigned = CalsAssigned;

        this.CaloriesBurned = CalsBurned;
    }


    public void updateClientData (String Name,int Age,int Mass)
    {
        if(Name.contains("1")||Name.contains("2")||Name.contains("3")||Name.contains("4")||Name.contains("5")||Name.contains("6")||Name.contains("7")||Name.contains("8")||Name.contains("9")){
            this.ClientName = this.ClientName;
        }else this.ClientName = Name;

        this.ClientAge = Age;

        this.ClientMass = Mass;
    }


    public void setCaloriesAssigned (int cals){
        this.CaloriesAssigned = cals;
    }

    public int getCaloriesAssigned(){
        return CaloriesAssigned;
    }

    public void SetCaloriesBurned(int Calories){
        this.CaloriesBurned = Calories;
    }

    public int getCaloriesBurned(){
        return CaloriesBurned;
    }

    public int getAge(){
        return ClientAge;
    }

    public int getMass(){
        return ClientMass;
    }

    public String getName(){
        return ClientName;
    }

    public int getID(){
       return ClientID;
    }


}





