package huji.ac.il.finderskeepers.data;

import java.io.Serializable;

/**
 * Created by Paz on 8/8/2015.
 */
public class User implements Serializable{
    String id = null; //this is set by the DB
    String username;

    /**
     * Ctor.
     * id will be set on user creation.
     *
     * @param username
     */
    public User(String username){
        this.username = username;
    }

    public String getUsername(){ return username;}
}
