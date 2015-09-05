package huji.ac.il.finderskeepers.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Paz on 8/8/2015.
 */
public class User implements Serializable{
    String id = null; //this is set by the DB
    String username;
    List<Item> reportedItems = null;
    List<Item> collectedItems = null;

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

    public List<Item> getReportedItems() {
        return reportedItems;
    }

    public void setReportedItems(List<Item> reportedItems) {
        this.reportedItems = reportedItems;
    }

    public List<Item> getCollectedItems() {
        return collectedItems;
    }

    public void setCollectedItems(List<Item> collectedItems) {
        this.collectedItems = collectedItems;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
