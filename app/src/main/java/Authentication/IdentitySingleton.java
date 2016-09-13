package Authentication;

import ViewModels.Account;

/**
 * Created by Viktor on 5.9.2016 г..
 */
public class IdentitySingleton {
    private static IdentitySingleton ourInstance = new IdentitySingleton();
    private Account account;

    public static IdentitySingleton getInstance() {
        return ourInstance;
    }

    private IdentitySingleton() {
        //TODO:Get account from phone data
    }

    public Account getAccount(){ return  this.account; }

    public void login(Account account){
        this.account = account;
    }

    public boolean IsLogged(){
        if(account != null && account.access_token != null){
            return true;
        }

        return false;
    }
}
