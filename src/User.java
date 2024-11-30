public class User {
    private String userName;
    private int indexPos;
    private static int userCount;

    public User (String u){
        userName = u;
        indexPos = userCount;
        userCount++;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getIndexPos() {
        return indexPos;
    }

    public void setIndexPos(int indexPos){
        this.indexPos = indexPos;
    }

    public static int getUserCount() {
        return userCount;
    }

}
